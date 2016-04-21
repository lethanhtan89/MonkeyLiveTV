package atv.com.project.popkontv.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Viewora;
import sdk.av.Broadcaster;

/**
 * Created by arjun on 6/9/15.
 */
public class FileUploadService extends IntentService{
    private static final int ACTION_UPLOAD_PICTURE = 1;
    private static final int ACTION_UPLOAD_VIDEO = 2;
    private static File fileToUpload;
    private static String slugOfFileToUpload;
    private static Broadcaster mBroadCaster;

    public FileUploadService() {
        super("Thumbnail upload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        final int action = Integer.parseInt(intent.getAction());
        switch(action){
            case ACTION_UPLOAD_PICTURE:
                handlePictureUpload();
                break;
            case ACTION_UPLOAD_VIDEO:
                handlePictureVideo();
                break;
        }
    }

    private void handlePictureVideo() {
        mBroadCaster.submitQueuedUploadsToS3();
    }

    private void handlePictureUpload() {
        File tempConversation;

//        try {
//            Bitmap bitmap = decodeFile(fileToUpload.getAbsoluteFile());
//            tempConversation = File.createTempFile("Castasy-", "Upload");
//            final FileOutputStream fileOutputStream = new FileOutputStream(tempConversation);
//            bitmap.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
//            fileOutputStream.close();
//        }catch (Exception ex){
//            tempConversation = null;
//        }


//        if(tempConversation != null) {
//            fileToUpload = tempConversation;
//        }

        Ion.with(getApplicationContext())
                .load("PUT", EndPoints.startScheduledStream(slugOfFileToUpload))
//                        .setHeader("Content-Type", "image/jpeg")
                        .setHeader("access-token", Viewora.loggedInUserToken)
                        .uploadProgress(new ProgressCallback() {
                            @Override
                            public void onProgress(long downloaded, long total) {
                                Log.d("Profile Pic upload", "Total: " + total + " Uploaded " + downloaded);
                            }
                        })
                        .setMultipartFile("stream[cover_image]", getMimeType(fileToUpload.getName()), fileToUpload)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
//                                if (isResponseSuccess(e, result)) {
//
//                                } else {
//
//                                }
                                try{
                                    Log.i("upload", result.toString());
                                }catch(Exception ex) {

                                }
                            }
                        });

    }

    public static Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=70;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private static String getMimeType(String url) {
        try {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getMimeTypeFromExtension(extension);
            }
            if (type.isEmpty()) {
                type = "image/jpeg";
            }
            return type;
        } catch (Exception ex) {
            return "image/jpeg";
        }
    }
    private boolean isResponseSuccess(Exception e, JsonObject result) {
        if(e != null || result == null || result.toString().contains("error")){
            return false;
        }
        return true;
    }

    public static void uploadPicture(File tempPhoto, String slug){
        if(tempPhoto == null) {
            return;
        }
        slugOfFileToUpload = slug;
        fileToUpload = tempPhoto;
        Intent intent = new Intent(Viewora.context, FileUploadService.class);
        intent.setAction(String.valueOf(ACTION_UPLOAD_PICTURE));
        Viewora.context.startService(intent);
    }
    public static void uploadVideoSegment(Broadcaster broadcaster){
        if(mBroadCaster == null) {
            mBroadCaster = broadcaster;
        };
        Intent intent = new Intent(Viewora.context, FileUploadService.class);
        intent.setAction(String.valueOf(ACTION_UPLOAD_VIDEO));
        intent.setFlags(START_NOT_STICKY);
        Viewora.context.startService(intent);
    }
}
