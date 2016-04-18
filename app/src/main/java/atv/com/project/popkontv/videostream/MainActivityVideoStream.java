package atv.com.project.popkontv.videostream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atv.com.project.popkontv.R;
import atv.com.project.popkontv.include.AppConfig;

public class MainActivityVideoStream extends Activity implements RtspClient.Callback,
        Session.Callback, SurfaceHolder.Callback {
        // log tag
        public final static String TAG = MainActivityVideoStream.class.getSimpleName();

        // surfaceview
        private static SurfaceView mSurfaceView;
        private ProgressDialog progressDialog;

        private Camera mCamera;

        // Rtsp session
        private Session mSession;
        private static RtspClient mClient;
        private ImageView camera_led, camera_type, camera_settings, camera_option;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                requestWindowFeature(Window.FEATURE_NO_TITLE);

                setContentView(R.layout.activity_videostream);

                camera_led = (ImageView) findViewById(R.id.pop_camera_led);
                camera_type = (ImageView) findViewById(R.id.pop_camera_type);
                camera_settings = (ImageView) findViewById(R.id.pop_camera_settings);
                camera_option = (ImageView) findViewById(R.id.pop_camera_option);

                mSurfaceView = (SurfaceView) findViewById(R.id.surface);

                mSurfaceView.getHolder().addCallback(this);

                camera_led.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Finishing", Toast.LENGTH_LONG).show();
                        }
                });

                camera_type.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                openFrontFacingCamera().startPreview();
                                Toast.makeText(getApplicationContext(), "Finishing", Toast.LENGTH_LONG).show();
                        }
                });

                camera_settings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Finishing", Toast.LENGTH_LONG).show();
                        }
                });

                camera_option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                progressDialog = ProgressDialog.show(MainActivityVideoStream.this, "Please wait ...", "Task in progress ...", true);
                                progressDialog.setCancelable(true);
                                new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                                //initRtspClient();
                                                try {
                                                        //Do some stuff that take some time...
                                                       /* Intent intent = new Intent(getApplicationContext(), MainActivityVideoStreamConnected.class);
                                                        startActivity(intent);
                                                        finish();*/
                                                        Toast.makeText(getApplicationContext(),"Finishing",Toast.LENGTH_LONG).show();
                                                        Thread.sleep(1000); // Let's wait for some time
                                                } catch (Exception e) {

                                                }
                                                progressDialog.dismiss();
                                        }
                                }).start();
                        }

                  });

                // Initialize RTSP client
                initRtspClient();

        }

        private Camera openFrontFacingCamera()
        {
                int cameraCount = 0;
                Camera cam = null;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();
                for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                        Camera.getCameraInfo( camIdx, cameraInfo );
                        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                                try {
                                        cam = Camera.open( camIdx );
                                } catch (RuntimeException e) {
                                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                                }
                        }
                }

                return cam;
        }

        

        @Override
        protected void onResume() {
                super.onResume();

                toggleStreaming();
        }

        @Override
        protected void onPause(){
                super.onPause();

                toggleStreaming();
        }

        private void initRtspClient() {
                // Configures the SessionBuilder
                mSession = SessionBuilder.getInstance()
                        .setContext(getApplicationContext())
                        .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                        .setAudioQuality(new AudioQuality(8000, 16000))
                        .setVideoEncoder(SessionBuilder.VIDEO_H264)
                        .setSurfaceView(mSurfaceView).setPreviewOrientation(0)
                        .setCallback(this).build();

                // Configures the RTSP client
                mClient = new RtspClient();
                mClient.setSession(mSession);
                mClient.setCallback(this);
                mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
                String ip, port, path;

                // We parse the URI written in the Editext
                Pattern uri = Pattern.compile("rtmp://(.+):(\\d+)/(.+)");
                Matcher m = uri.matcher(AppConfig.STREAM_URL);
                m.find();
                ip = m.group(1);
                port = m.group(2);
                path = m.group(3);

                mClient.setCredentials(AppConfig.PUBLISHER_USERNAME,
                        AppConfig.PUBLISHER_PASSWORD);
                mClient.setServerAddress(ip, Integer.parseInt(port));
                mClient.setStreamPath("/" + path);
        }

        private void toggleStreaming() {
                if (!mClient.isStreaming()) {
                        // Start camera preview
                        mSession.startPreview();

                        // Start video stream
                        mClient.startStream();
                } else {
                        // already streaming, stop streaming
                        // stop camera preview
                        mSession.stopPreview();

                        // stop streaming
                        mClient.stopStream();
                }
        }

        @Override
        public void onDestroy() {
                super.onDestroy();
                mClient.release();
                mSession.release();
                mSurfaceView.getHolder().removeCallback(this);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.main_menu, menu);
                return true;
        }

        @Override
        public void onBitrateUpdate(long bitrate) {

        }

        @Override
        public void onSessionError(int reason, int streamType, Exception e) {
                switch (reason) {
                        case Session.ERROR_CAMERA_ALREADY_IN_USE:
                                break;
                        case Session.ERROR_CAMERA_HAS_NO_FLASH:
                                break;
                        case Session.ERROR_INVALID_SURFACE:
                                break;
                        case Session.ERROR_STORAGE_NOT_READY:
                                break;
                        case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                                break;
                        case Session.ERROR_OTHER:
                                break;
                }

                if (e != null) {
                        //alertError(e.getMessage());
                        e.printStackTrace();
                }
        }

        private void alertError(final String msg) {
                final String error = (msg == null) ? "Unknown error: " : msg;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityVideoStream.this);
                builder.setMessage(error).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                        });
                AlertDialog dialog = builder.create();
                //dialog.show();
        }

        @Override
        public void onRtspUpdate(int message, Exception exception) {
                switch (message) {
                        case RtspClient.ERROR_CONNECTION_FAILED:
                        case RtspClient.ERROR_WRONG_CREDENTIALS:
                                alertError(exception.getMessage());
                                exception.printStackTrace();
                                break;
                }
        }

        @Override
        public void onPreviewStarted() {
        }

        @Override
        public void onSessionConfigured() {
        }

        @Override
        public void onSessionStarted() {
        }

        @Override
        public void onSessionStopped() {
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void onBitrareUpdate(long bitrate) {
        }


}