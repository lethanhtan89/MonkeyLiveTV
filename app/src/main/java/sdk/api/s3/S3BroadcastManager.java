package sdk.api.s3;

import android.util.Log;
import android.util.Pair;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import atv.com.project.popkontv.Activity.StreamingActivity;
import sdk.av.Broadcaster;
import sdk.event.S3UploadEvent;

/**
 * Manages a sequence of S3 uploads on behalf of
 * a single instance of {@link AWSCredentials}.
 */
public class S3BroadcastManager implements Runnable {
    private static final String TAG = "S3Manager";
    private static final boolean VERBOSE = true;
    private final AWSCredentials creds;

    private LinkedBlockingQueue<Pair<PutObjectRequest, Boolean>> mQueue;
    private TransferManager mTransferManager;
    private Broadcaster mBroadcaster;
    private Set<WeakReference<S3RequestInterceptor>> mInterceptors;
    private boolean firstUploadComplete = false;


    public interface S3RequestInterceptor{
        public void interceptRequest(PutObjectRequest request);
    }

    public S3BroadcastManager(Broadcaster broadcaster, AWSCredentials creds) {

        // XXX - Need to determine what's going wrong with MD5 computation
        System.setProperty("com.amazonaws.services.s3.disableGetObjectMD5Validation", "true");
        this.creds = creds;
        mTransferManager = new TransferManager(creds);
        mBroadcaster = broadcaster;
        mQueue = new LinkedBlockingQueue<>();
        mInterceptors = new HashSet<>();
        new Thread(this).start();
    }

    /**
     * Add an interceptor to be called on requests before they're submitted.
     * This is a good point to add request headers e.g: Cache-Control.
     *
     * WeakReferences are held on the S3RequestInterceptor so it
     * will be active as long as an external reference is held.
     */
    public void addRequestInterceptor(S3RequestInterceptor interceptor) {
        mInterceptors.add(new WeakReference<S3RequestInterceptor>(interceptor));
    }

    public void setRegion(String regionStr) {
        if (regionStr == null || regionStr.equals("")) return;
        Region region = Region.getRegion(Regions.fromName(regionStr));
        mTransferManager.getAmazonS3Client().setRegion(region);
    }


    public void queueUpload(final String bucket, final String key, final File file, boolean lastUpload) {
        if (VERBOSE) Log.i(TAG, "Queueing upload " + key);

        final PutObjectRequest por = new PutObjectRequest(bucket, key, file);
        por.setGeneralProgressListener(new ProgressListener() {
            final String url = "https://" + bucket + ".s3.amazonaws.com/" + key;
            private long uploadStartTime;

            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                try {
                    Log.i(TAG,"Event " + progressEvent.getEventCode());
                    Log.i(TAG,"Event transfered" + progressEvent.getBytesTransferred());
                    Log.i(TAG,"Event name " + file.getName());
                    if (progressEvent.getEventCode() == ProgressEvent.STARTED_EVENT_CODE) {
                        uploadStartTime = System.currentTimeMillis();
                    } else if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                        if(!firstUploadComplete){
                            StreamingActivity.sendStartEventToServer();
                            Log.i(TAG, "First Upload complete.");
                            firstUploadComplete = true;
                        }
                        long uploadDurationMillis = System.currentTimeMillis() - uploadStartTime;
                        int bytesPerSecond = (int) (file.length() / (uploadDurationMillis / 1000.0));
                        if (VERBOSE)
                            Log.i(TAG, "Uploaded " + file.length() / 1000.0 + " KB in " + (uploadDurationMillis) + "ms (" + bytesPerSecond / 1000.0 + " KBps)");
                        mBroadcaster.onS3UploadComplete(new S3UploadEvent(file, url, bytesPerSecond));
                    } else if (progressEvent.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
                        Log.w(TAG, "Upload failed for " + url);
                    }
                } catch (Exception excp) {
                    Log.e(TAG, "ProgressListener error");
                    excp.printStackTrace();
                }
            }
        });
        por.setCannedAcl(CannedAccessControlList.PublicRead);
        for (WeakReference<S3RequestInterceptor> ref : mInterceptors) {
            S3RequestInterceptor interceptor = ref.get();
            if (interceptor != null) {
                interceptor.interceptRequest(por);
            }
        }
        mQueue.add(new Pair<>(por, lastUpload));
    }

    @Override
    public void run() {
        boolean lastUploadComplete = false;
        while (!lastUploadComplete) {
            try {
                Pair<PutObjectRequest, Boolean> requestPair = mQueue.poll(mBroadcaster.getSessionConfig().getHlsSegmentDuration() * 2, TimeUnit.SECONDS);
                if (requestPair != null) {
                    final PutObjectRequest request = requestPair.first;
                    Upload upload = mTransferManager.upload(request);
                    upload.waitForCompletion();
                    lastUploadComplete = requestPair.second;
                    if (!lastUploadComplete && VERBOSE ) {

                        Log.i(TAG, "Upload complete." + requestPair.first.getFile().getAbsolutePath());
                    }
                    else if (VERBOSE) {
//                        new StreamingActivity().sendStopEventToServer(false);
                        Log.i(TAG, "Last Upload complete.");
                        mTransferManager.shutdownNow();
                        mTransferManager = new TransferManager(creds);
                    }
                } else {
                    if (VERBOSE)
                        Log.e(TAG, "Reached end of Queue before processing last segment!");
                    lastUploadComplete = true;
                }
            } catch (InterruptedException e) {
                Log.w(TAG, "InterruptedException. retrying.");
                e.printStackTrace();
            } catch (AmazonS3Exception s3e) {
                // Possible Bad Digest. Retry
                Log.w(TAG, "AmazonS3Exception. retrying.");
            }
        }
        if (VERBOSE) Log.i(TAG, "Shutting down");
    }
}
