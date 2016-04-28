package atv.com.project.monkeylivetv.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioRecord;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import atv.com.project.monkeylivetv.Pojo.StreamFullDetails;
import atv.com.project.monkeylivetv.Adapters.CommentsAdapter;
import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Fragments.UserProfileDialogFragment;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Network.SocketMessageParser;
import atv.com.project.monkeylivetv.Pojo.Feed;
import atv.com.project.monkeylivetv.Pojo.MyResponse;
import atv.com.project.monkeylivetv.Pojo.NewComment;
import atv.com.project.monkeylivetv.Pojo.NewCommentDetails;
import atv.com.project.monkeylivetv.Pojo.PingForWatchersResponse;
import atv.com.project.monkeylivetv.Pojo.StartStreamContentStream;
import atv.com.project.monkeylivetv.Pojo.StartStreamDetails;
import atv.com.project.monkeylivetv.Pojo.StreamStatus;
import atv.com.project.monkeylivetv.Pojo.UserScore;
import atv.com.project.monkeylivetv.Pojo.UserTwitterActionsResponse;
import atv.com.project.monkeylivetv.Pojo.Watcher;
import atv.com.project.monkeylivetv.R;
import atv.com.project.monkeylivetv.lib.Faye;
import atv.com.project.monkeylivetv.lib.OnSwipeTouchListener;
import atv.com.project.monkeylivetv.lib.StreamDrawable;
import io.fabric.sdk.android.Fabric;
import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.activity.ImmersiveActivity;
import io.kickflip.sdk.av.Broadcaster;
import io.kickflip.sdk.av.FullFrameRect;
import io.kickflip.sdk.view.GLCameraEncoderView;

public class StreamingActivity extends ImmersiveActivity{
    private final static String LOG_TAG = "StreamingActivity";
    private PowerManager.WakeLock mWakeLock;
    private String ffmpeg_link = "";
//    private volatile FFmpegFrameRecorder recorder;
    boolean recording = false;
    long startTime = 0;
    private int sampleAudioRateInHz = 44100;
    private int imageWidth = 1280;
    private int imageHeight = 720;
    private int frameRate = 30;
    private Thread audioThread;
    volatile boolean runAudioThread = true;
    private AudioRecord audioRecord;
//    private AudioRecordRunnable audioRecordRunnable;
//    private CameraView cameraView;
//    private IplImage yuvIplimage = null;
    private ImageView recordButton;
//    private LinearLayout mainLayout;
    private ImageView settingsButton;
    private int bitRate = 512;
    private long linkSpeed;
    private int videoCodec;
    private TrafficStats trafficStats;
    private long initialBytesCount;
//    private TextView speed;
    private int uid;
    private Timer netWorkSpeedTimer;
    private TextView streamInfoTxt;
    private String url;
    private int streamId;
    private String tweetId;
    private String tweetMessage;
    private ImageButton switchCamera;


    private static int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private SurfaceHolder holder;
    private Camera camera;

    private static final String TWITTER_KEY = "OQczffwVTLCJIsuTZ4U7rXcJP";
    private static final String TWITTER_SECRET = "GnwwAFn6tIEjmhQnhmEiksEdaIPcY1Zy38B021FAAiEgzvTAPC";
    private long timeStamp = -1;
    private String userTweetMessage;
    private ImageButton toggleFlash;
    private boolean flashOn = false;
    private TextView userNameTv;
//    private TextView userHandleTv;
    private TextView streamViewStatusTv;
//    private TextView userLocationTv;
//    private TextView streamTweetTv;
    private ImageButton commentButton;
    private static StreamFullDetails currentStreamDetails = new StreamFullDetails();
    private ListView commentsListView;
    private CommentsAdapter commentsListAdapter;
    private ImageView userImage;
    private LinearLayout viewversLayout;
    private LinearLayout newCommentLayout;
    private EditText postCommentBox;
    private TextView postCommentButton;
    private Switch twitterReflectToggle;
//    private ImageView likeButton;
//    private ImageView reTweetButton;
    private SocketMessageParser.HandleMessage messageHandler;
    private ArrayList<Integer> duplicatedCheckList = new ArrayList<>();
    private TextView commentBoxHandle;
    private boolean keyboardShown = false;
    private ProgressBar fetchScoreProgressBar;
    private Timer pingTimer;
    private byte[] screenShot;
    private ImageButton recordControl;
    private long transferRate;
//    private StopStreamingThread stopThread;
    private boolean sendPicture = false;
    private File fileToUpload;
    private boolean onBoarding = true;
    private LinearLayout userActionsLayout;
    private RelativeLayout rootView;
    private LinearLayout userActionsContainerLayout;
    private LinearLayout streamEndLayout;
    private TextView scoreOnStreamEndTv;
    private ImageButton streamEndBackBt;

    private static Broadcaster mBroadcaster;        // Make static to survive Fragment re-creation
    private GLCameraEncoderView mCameraView;
    private static final boolean VERBOSE = false;
    private static LinearLayout initializeWaitLayout;
    private LinearLayout userDetailsLayout;
    private boolean isShowingDetails = true;
    private ImageButton userActionsControlButton;
//    private ImageButton zoomInCamera;
//    private ImageButton zoomOutCamera;
    private boolean isCommentsLayoutShowing = false;
    private TextView newCommentNotification;
    private int newCommentsCount = 0;
    private Faye faye;
    private JSONObject fayeJsonObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View decorView = getWindow().getDecorView();


        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            decorView.setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            );
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });

        userTweetMessage = getIntent().getStringExtra("tweetMsg");
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_stream);
        initializeWaitLayout = (LinearLayout) findViewById(R.id.initialise_wait_layout);

        initialiseViews();
        onBoarding = MonkeyLive.getBooleanPreference(MonkeyLive.USER_ONBOARDING, true);
//        showUserOnBoarding();
        bindEvents();
//        initLayout();
        if(MonkeyLive.getBooleanPreference(MonkeyLive.IS_SCHEDULED_STREAM, false)){
            MonkeyLive.setBooleanPreferenceData(MonkeyLive.IS_SCHEDULED_STREAM, false);
            startScheduledStream();
        }else {
            startRequests();
        }

        trafficStats = new TrafficStats();
        uid = android.os.Process.myUid();
        if(trafficStats.getUidRxBytes(uid) != trafficStats.UNSUPPORTED) {
            initialBytesCount = trafficStats.getUidTxBytes(uid) / 1024;
        } else{
            uid = -1;
        }


    }

    private void showUserOnBoarding() {
        if(onBoarding){
            MonkeyLive.setBooleanPreferenceData(MonkeyLive.USER_ONBOARDING, false);
            LayoutInflater onBoardingInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = onBoardingInflater.inflate(R.layout.user_onboarding_stream_activity, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.addRule(RelativeLayout.ALIGN_TOP, userActionsContainerLayout.getId());
            rootView.addView(view, layoutParams);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.removeView(view);
                }
            });
        }
    }

    private SensorEventListener mOrientationListener = new SensorEventListener() {

        final int SENSOR_CONFIRMATION_THRESHOLD = 5;
        int[] confirmations = new int[2];
        int orientation = -1;

        @Override
        public void onSensorChanged(SensorEvent event) {
//            if (getActivity() != null && getActivity().findViewById(R.id.rotateDeviceHint) != null) {
                //Log.i(TAG, "Sensor " + event.values[1]);
                if (event.values[1] > 10 || event.values[1] < -10) {
                    // Sensor noise. Ignore.
                } else if (event.values[1] < 5.5 && event.values[1] > -5.5) {
                    // Landscape
                    if (orientation != 1 && readingConfirmed(1)) {
                        if (event.values[0] > 0) {
                            findViewById(R.id.rotate_device_hint_image).setVisibility(View.GONE);
                            mBroadcaster.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.LANDSCAPE);
                        } else {
                            mBroadcaster.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.UPSIDEDOWN_LANDSCAPE);
                        }
                        orientation = 1;
                    }
                } else if (event.values[1] > 7.5 || event.values[1] < -7.5) {
                    // Portrait
                    if (orientation != 0 && readingConfirmed(0)) {
                        if (event.values[1] > 0) {
                            findViewById(R.id.rotate_device_hint_image).setVisibility(View.VISIBLE);
//                            mBroadcaster.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.VERTICAL);

                        } else {
//                            mBroadcaster.signalVerticalVideo(FullFrameRect.SCREEN_ROTATION.UPSIDEDOWN_VERTICAL);
                        }
                        orientation = 0;
                    }
                }
//            }
        }

        /**
         * Determine if a sensor reading is trustworthy
         * based on a series of consistent readings
         */
        private boolean readingConfirmed(int orientation) {
            confirmations[orientation]++;
            confirmations[orientation == 0 ? 1 : 0] = 0;
            return confirmations[orientation] > SENSOR_CONFIRMATION_THRESHOLD;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };




    protected void setupBroadcaster() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mBroadcaster == null) {
                if (VERBOSE)
                    Log.i(TAG, "Setting up Broadcaster for output " + Kickflip.getSessionConfig().getOutputPath() + " client key: " + Kickflip.getApiKey() + " secret: " + Kickflip.getApiSecret());
                // TODO: Don't start recording until stream start response, so we can determine stream type...
                Context context = MonkeyLive.context;
                try {
                    mBroadcaster = new Broadcaster(context, Kickflip.getSessionConfig(), Kickflip.getApiKey(), Kickflip.getApiSecret());
                    mBroadcaster.getEventBus().register(this);
//                    mBroadcaster.setBroadcastListener(Kickflip.getBroadcastListener());
                    Kickflip.clearSessionConfig();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to create Broadcaster. Could be trouble creating MediaCodec encoder.");
                    e.printStackTrace();
                }
            }
//        }
    }

    private void startScheduledStream() {
        StartStreamDetails startStreamDetails = new StartStreamDetails();
        startStreamDetails.stream = new StartStreamContentStream();
        startStreamDetails.stream.message = userTweetMessage;
        startStreamDetails.stream.recordable = getIntent().getBooleanExtra("doRecord", false);
        startStreamDetails.stream.categoryStreamAttributes.categoryId = getIntent().getIntExtra("categoryId", -1);

        MyHttp<StreamFullDetails> request = new MyHttp<>(StreamingActivity.this,
                EndPoints.startScheduledStream(MonkeyLive.getStringPreference(MonkeyLive.STREAM_SLUG, "")),
                MyHttp.PUT, StreamFullDetails.class)
                .defaultHeaders()
                .addJson(startStreamDetails)
                .send(new MyCallback<StreamFullDetails>() {
                    @Override
                    public void success(StreamFullDetails data) {
                        int count = MonkeyLive.getIntPreference(MonkeyLive.USER_STRAEMS_COUNT, 0);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_STRAEMS_COUNT, count + 1);
                        setDataToViewStartRecording(data);
                    }

                    @Override
                    public void failure(String msg) {
                        Toast.makeText(StreamingActivity.this, "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBefore() {
                        initializeWaitLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                    }
                });
    }

    private void bindEvents() {
        bindEventToUserActionsControllButton();
        bindEventsToToggleFlash();
        bindEventsToSwitchCamera();
//        bindEventToViewTree();
        bindEventToCommentButton();
        bindEventToPostComment();
        bindEventToLikeButton();
        bindEventToReTweetButton();
        bindEventToRecordControl();
        bindEventsToStreamEndBackButton();
//        bindEventsToZoomButtons();
    }

    private void bindSwipeEvent() {
        mCameraView.setOnTouchListener(new OnSwipeTouchListener(StreamingActivity.this) {
            public void onSwipeTop() {
//                Toast.makeText(StreamingActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                if(!isCommentsLayoutShowing) {
                    mShowCommentsLayout();
                }
            }

            public void onSwipeLeft() {
                if(isCommentsLayoutShowing) {
                    mHideCommentsLayout();
                }
            }

            public void onSwipeBottom() {
//                Toast.makeText(StreamingActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

//    private void bindEventsToZoomButtons() {
//        if(mBroadcaster.isZoomSupported()) {
//            zoomInCamera.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mBroadcaster.zoomInCamera();
//                }
//            });
//            zoomOutCamera.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mBroadcaster.zoomOutCamera();
//                }
//            });
//        }
//    else {
//            zoomOutCamera.setVisibility(View.GONE);
//            zoomInCamera.setVisibility(View.GONE);
//        }
//    }

    private void bindEventToUserActionsControllButton() {
        userActionsControlButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userActionsLayout.getVisibility() == View.VISIBLE) {
                    userActionsControlButton.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon));
                    userActionsLayout.setVisibility(View.GONE);
                } else {
                    userActionsControlButton.setImageDrawable(getResources().getDrawable(R.drawable.cancel_icon));
                    userActionsLayout.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void bindEventsToToggleFlash() {
        toggleFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBroadcaster.toggleFlash();
            }
        });
    }

    private void bindEventsToSwitchCamera() {
        if (Camera.getNumberOfCameras() == 1) {
            switchCamera.setVisibility(View.GONE);
        } else {
            switchCamera.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBroadcaster.requestOtherCamera();
                }
            });
        }
    }

    private void bindEventsToStreamEndBackButton() {
        streamEndBackBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void bindEventToRecordControl() {
        recordControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showStreamEndDialog();
            }
        });
    }

    private void bindEventToViewTree() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                int heightDiff = rootView.getRootView().getWidth() - rootView.getWidth();
//                if (heightDiff < 100) { // if more than 100 pixels, its probably a keyboard...
//                    Log.i("keyboard", "change layout");
//                    newCommentLayout.setVisibility(View.GONE);
//                } else {
//                    newCommentLayout.setVisibility(View.VISIBLE);
//                    postCommentBox.requestFocus();
//                }
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) {
                    // keyboard is opened
                    Log.i("Keyboard", "open");
                    newCommentLayout.setVisibility(View.VISIBLE);
                    postCommentBox.requestFocus();
                } else {
                    // keyboard is closed
                    Log.i("Keyboard", "close");
                    newCommentLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void bindEventToCommentButton() {
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCommentsLayoutShowing) {
                    mShowCommentsLayout();
                } else {
                    mHideCommentsLayout();
                }
            }
        });
    }

    private void mHideCommentsLayout() {
        newCommentsCount = 0;
        commentButton.setBackground(getResources().getDrawable(R.drawable.action_icon_background));
        userActionsContainerLayout.setVisibility(View.GONE);
        Animation slideOutAnimation = AnimationUtils.loadAnimation(StreamingActivity.this, R.anim.slide_out_left);
        userActionsContainerLayout.startAnimation(slideOutAnimation);
        isCommentsLayoutShowing = false;
    }

    private void mShowCommentsLayout() {
        newCommentNotification.setVisibility(View.GONE);
        newCommentsCount = 0;
        commentButton.setBackground(getResources().getDrawable(R.drawable.action_icon_background_live));
        userActionsContainerLayout.setVisibility(View.VISIBLE);
        Animation slideInAnimation = AnimationUtils.loadAnimation(StreamingActivity.this, R.anim.slide_in_left);
        userActionsContainerLayout.startAnimation(slideInAnimation);
        isCommentsLayoutShowing = true;
    }

    private void bindEventToReTweetButton() {
//        reTweetButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(StreamingActivity.this, EndPoints.reTweet(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
//                        .defaultHeaders()
//                        .send(new MyCallback<UserTwitterActionsResponse>() {
//                            @Override
//                            public void success(UserTwitterActionsResponse response) {
//                                Log.i("Succescomment", response.toString());
//                                if (duplicatedCheckList.contains(response.message.feedId)) return;
//                                duplicatedCheckList.add(response.message.feedId);
//                                currentStreamDetails.message.feed.add(response.message);
//                                commentsListAdapter.notifyDataSetChanged();
//                                commentsListView.smoothScrollToPosition(commentsListAdapter.getCount());
//                            }
//
//                            @Override
//                            public void failure(String msg) {
//                                Log.i("failcomment", msg);
//                                Toast.makeText(getApplicationContext(), "Request failed.Please try again", Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onBefore() {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//
//                            }
//                        });
//            }
//        });
    }

    private void bindEventToLikeButton() {
//        likeButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                likeButton.setVisibility(View.GONE);
//                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(StreamingActivity.this, EndPoints.makeFauvorite(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
//                        .defaultHeaders()
//                        .send(new MyCallback<UserTwitterActionsResponse>() {
//                            @Override
//                            public void success(UserTwitterActionsResponse response) {
//                                Log.i("Succescomment", response.toString());
//                                if (duplicatedCheckList.contains(response.message.feedId)) return;
//                                duplicatedCheckList.add(response.message.feedId);
//                                currentStreamDetails.message.feed.add(response.message);
//                                commentsListAdapter.notifyDataSetChanged();
//                                commentsListView.smoothScrollToPosition(commentsListAdapter.getCount());
//                            }
//
//                            @Override
//                            public void failure(String msg) {
//                                likeButton.setVisibility(View.VISIBLE);
//                                Log.i("failcomment", msg);
//                                Toast.makeText(getApplicationContext(), "Request failed.Please try again", Toast.LENGTH_LONG).show();
//
//                            }
//
//                            @Override
//                            public void onBefore() {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//
//                            }
//                        });
//            }
//        });
    }

    private void bindEventToPostComment() {
        postCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = postCommentBox.getText().toString();
                if (comment.equals("")) return;
                postCommentBox.setText("");
//                newCommentLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                NewComment newComment = new NewComment();
                newComment.stream_id = streamId;
                newComment.commentDetails = new NewCommentDetails();
                newComment.commentDetails.message = comment;
                newComment.commentDetails.reflect_in_twitter = twitterReflectToggle.isChecked();

                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(StreamingActivity.this, EndPoints.commentOnStream(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
                        .defaultHeaders()
                        .addJson(newComment)
                        .send(new MyCallback<UserTwitterActionsResponse>() {
                            @Override
                            public void success(UserTwitterActionsResponse response) {
                                Log.i("Succescomment", response.toString());
                                if (duplicatedCheckList.contains(response.message.feedId)) return;
                                newCommentsCount++;
                                if(userActionsContainerLayout.getVisibility() == View.GONE){
                                    newCommentNotification.setVisibility(View.VISIBLE);
                                    newCommentNotification.setText(newCommentsCount + "");
                                }
                                duplicatedCheckList.add(response.message.feedId);
                                currentStreamDetails.message.feed.add(response.message);
                                commentsListAdapter.notifyDataSetChanged();
                                commentsListView.smoothScrollToPosition(commentsListAdapter.getCount());
                            }

                            @Override
                            public void failure(String msg) {
                                Log.i("failcomment", msg);
                                Toast.makeText(getApplicationContext(), "Failed to comment. Try again", Toast.LENGTH_LONG).show();
//                                commentsListAdapter.remove(feed);
                            }

                            @Override
                            public void onBefore() {

                            }

                            @Override
                            public void onFinish() {

                            }
                        });
            }
        });
    }


    private void initialiseViews() {

        rootView = (RelativeLayout)findViewById(R.id.activityRootView);
        userActionsLayout = (LinearLayout) findViewById(R.id.userActionsLayout);
        userDetailsLayout = (LinearLayout) findViewById(R.id.publisher_details_layout);
        userActionsContainerLayout = (LinearLayout) findViewById(R.id.userActionsContainerLayout);
//        mainLayout = (LinearLayout) this.findViewById(R.id.record_layout);
        switchCamera = (ImageButton) findViewById(R.id.switch_camera);
        toggleFlash = (ImageButton) findViewById(R.id.toggleFlash);
        commentButton = (ImageButton) findViewById(R.id.postComment);
        newCommentNotification = (TextView) findViewById(R.id.new_comment_notifications);

        userActionsControlButton = (ImageButton) findViewById(R.id.action_buttons_controll);

        streamEndLayout = (LinearLayout) findViewById(R.id.video_view_stream_ended_container);
        scoreOnStreamEndTv = (TextView) findViewById(R.id.score_on_stream_end);
        streamEndBackBt = (ImageButton) findViewById(R.id.go_back_stream_end);
//        likeButton = (ImageView) findViewById(R.id.makeFauvorite);

//        reTweetButton = (ImageView) findViewById(R.id.retweet);
//        reTweetButton.setVisibility(View.GONE);

        userImage = (ImageView) findViewById(R.id.userImage);
        MyHttp.fetchBitmap(StreamingActivity.this, MonkeyLive.getStringPreference(MonkeyLive.TWITTER_USER_PICTURE, ""), new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable drawable = new StreamDrawable(data);
                userImage.setImageDrawable(drawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {

            }

            @Override
            public void onFinish() {

            }
        });

        userNameTv = (TextView) findViewById(R.id.userName);
        userNameTv.setText(MonkeyLive.getStringPreference(MonkeyLive.TWITTER_USER_NAME, ""));
//        userHandleTv = (TextView) findViewById(R.id.userHandle);
//        userHandleTv.setText(Viewora.getStringPreference(Viewora.TWITTER_HANDLE, ""));
        streamViewStatusTv = (TextView) findViewById(R.id.stream_view_status);
//        userLocationTv = (TextView) findViewById(R.id.userLocation);
//        userLocationTv.setText(Viewora.getStringPreference(Viewora.TWITTER_USER_LOCATION, ""));
//        streamTweetTv = (TextView) findViewById(R.id.streamTweet);
//        streamTweetTv.setText(userTweetMessage);

        viewversLayout = (LinearLayout) findViewById(R.id.viewersPictureLayout);
        newCommentLayout = (LinearLayout) findViewById(R.id.commentLayout);

        commentsListView = (ListView) findViewById(R.id.commentsList);

        postCommentBox = (EditText) findViewById(R.id.commentBox);
        commentBoxHandle = (TextView) findViewById(R.id.commentBoxHandle);
        commentBoxHandle.setText("@" + MonkeyLive.getStringPreference(MonkeyLive.TWITTER_HANDLE, ""));
        postCommentButton = (TextView) findViewById(R.id.postCommentButton);
        twitterReflectToggle = (Switch) findViewById(R.id.reflectInTwitter);
        twitterReflectToggle.setChecked(true);

        fetchScoreProgressBar = (ProgressBar)findViewById(R.id.streamEndProgressBar);

        recordControl = (ImageButton) findViewById(R.id.recorder_control);
//        zoomInCamera = (ImageButton) findViewById(R.id.zoom_in_camera);
//        zoomOutCamera = (ImageButton) findViewById(R.id.zoom_out_camera);

        userNameTv.setTypeface(MonkeyLive.racho);
//        userHandleTv.setTypeface(Viewora.racho);
        streamViewStatusTv.setTypeface(MonkeyLive.racho);
//        userLocationTv.setTypeface(Viewora.racho);
//        streamTweetTv.setTypeface(Viewora.racho);
        commentBoxHandle.setTypeface(MonkeyLive.racho);
        postCommentButton.setTypeface(MonkeyLive.racho);
    }


    private void startRequests() {

        StartStreamDetails startStreamDetails = new StartStreamDetails();
        startStreamDetails.stream = new StartStreamContentStream();
        startStreamDetails.stream.message = userTweetMessage;
        startStreamDetails.stream.recordable = getIntent().getBooleanExtra("doRecord", false);
        startStreamDetails.stream.categoryStreamAttributes.categoryId = getIntent().getIntExtra("categoryId", -1);
        startStreamDetails.stream.skipStart = true;

        MyHttp<StreamFullDetails> request = new MyHttp<>(StreamingActivity.this, EndPoints.baseStreamUrl, MyHttp.POST, StreamFullDetails.class)
                .defaultHeaders()
                .addJson(startStreamDetails)
                .send(new MyCallback<StreamFullDetails>() {
                    @Override
                    public void success(StreamFullDetails data) {
                        int count = MonkeyLive.getIntPreference(MonkeyLive.USER_STRAEMS_COUNT, 0);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_STRAEMS_COUNT, count + 1);
                        setDataToViewStartRecording(data);
                    }

                    @Override
                    public void failure(String msg) {
                        Toast.makeText(StreamingActivity.this, "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBefore() {
                        initializeWaitLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                    }
                });


    }

    private void setDataToViewStartRecording(StreamFullDetails data) {
        currentStreamDetails = data;
        url = currentStreamDetails.message.url;
//        url = "rtmp://192.168.1.16/castasy/stream";
        streamId = currentStreamDetails.message.id;
//        if(currentStreamDetails.message.liked){
//            likeButton.setVisibility(View.GONE);
//        }
//        userLocationTv.setText(currentStreamDetails.message.publisher.city);
        commentsListAdapter = new CommentsAdapter(this, -1, currentStreamDetails.message.feed);
        commentsListView.setAdapter(commentsListAdapter);
        commentsListView.setSelection(commentsListAdapter.getCount() - 1);
        fillViewers(currentStreamDetails.message.watchers);
        streamViewStatusTv.setText(currentStreamDetails.message.total_subscribers + " people . " + currentStreamDetails.message.live_subscribers + " here now");
        Log.i("result", data.toString());

        try {
            openSocket();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startPingingForLiveData();
//        initRecorder();
//        startRecording();
//        if (!Kickflip.readyToBroadcast()) {
//            Log.e(LOG_TAG, "Kickflip not properly prepared by BroadcastFragment's onCreate. SessionConfig: " + Kickflip.getSessionConfig() + " key " + Kickflip.getApiKey() + " secret " + Kickflip.getApiSecret());
//        } else {
            setupBroadcaster();
////            setupBroadcaster("rtmp://52.74.239.156/hls/150b3d833fc3c2538596d02b1a02e8e7");
            mCameraView = (GLCameraEncoderView) findViewById(R.id.cameraPreview);
            mCameraView.setVisibility(View.VISIBLE);
            mCameraView.setKeepScreenOn(true);
            mBroadcaster.setPreviewDisplay(mCameraView);
            startMonitoringOrientation();
            detectNetworkSpeed();
        initializeWaitLayout.setBackgroundColor(getResources().getColor(R.color.opacity30));
//        }
        mBroadcaster.startRecording(currentStreamDetails.message.slug);
    }

    private void bindEventsToCameraView() {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (isShowingDetails) {
                            userDetailsLayout.setVisibility(View.GONE);
                            isShowingDetails = false;
                        } else {
                            userDetailsLayout.setVisibility(View.VISIBLE);
                            isShowingDetails = true;
                        }
                }
                return false;
            }
        });
    }

    private void startPingingForLiveData() {
        pingTimer = new Timer();
        TimerTask pingTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    MyHttp<PingForWatchersResponse> myHttp = new MyHttp<PingForWatchersResponse>(StreamingActivity.this, EndPoints.pingForWatchers(currentStreamDetails.message.slug), MyHttp.POST, PingForWatchersResponse.class)
                            .defaultHeaders()
                            .send(new MyCallback<PingForWatchersResponse>() {
                                @Override
                                public void success(PingForWatchersResponse data) {
                                    fillViewers(data.message.watchers);
                                    streamViewStatusTv.setText(data.message.total_number_of_subscribers + " people . " + data.message.number_of_live_subscribers + " here now");
                                }

                                @Override
                                public void failure(String msg) {
                                    Log.i("getting watchers", msg);
                                }

                                @Override
                                public void onBefore() {

                                }

                                @Override
                                public void onFinish() {

                                }
                            });

                    if(transferRate < 20){
                        Log.i("TransferRate in if", transferRate + "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StreamingActivity.this);
                                builder.setMessage("Your network speed in low to continue streaming.");
                                builder.setTitle("Viewora");
                                builder.setPositiveButton("Stop Stream", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        stopBroadcasting();
                                        StreamingActivity.this.finish();
                                    }
                                });
                                builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                }catch (Exception e){
                    Log.i("Error", e.getMessage());
                }
                sendSnapShot();
            }

        };
        pingTimer.scheduleAtFixedRate(pingTask, 10000, 60000);
    }

    private void sendSnapShot() {
        if(mBroadcaster != null){
            mBroadcaster.takePicture(currentStreamDetails.message.slug);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pingTimer != null && netWorkSpeedTimer != null) {
            pingTimer.cancel();
            netWorkSpeedTimer.cancel();
        }
//        if (mBroadcaster.isRecording()) mBroadcaster.stopRecording();
    }


    private void openSocket() throws JSONException {
        messageHandler = getHandler();
        faye = new Faye(new Handler(Looper.getMainLooper()), Uri.parse(EndPoints.openSocket),"/streams/" + String.valueOf(currentStreamDetails.message.slug));
//        Faye faye = new Faye(new Handler(Looper.getMainLooper()), Uri.parse(EndPoints.openSocket),"/messages");
        fayeJsonObject = new JSONObject();
        fayeJsonObject.put("authToken", "asdfdsafsdfsdaf");
        faye.connectToServer(fayeJsonObject);
        faye.setFayeListener(new Faye.FayeListener() {
            @Override
            public void connectedToServer() {
                Log.d("WS", "Connected to server ");
            }

            @Override
            public void disconnectedFromServer() {
                Log.d("WS", "dis Connected to server ");
            }

            @Override
            public void subscribedToChannel(String subscription) {
                Log.d("WS", "Subscribed " + subscription);
            }

            @Override
            public void subscriptionFailedWithError(String error) {
                Log.d("WS", "Failed " + error);
            }

            @Override
            public void messageReceived(JSONObject json) {
                Log.d("WS", "Message received " + json.toString());
                SocketMessageParser.parseMessage(json, messageHandler);
            }
        });

    }

    public SocketMessageParser.HandleMessage getHandler(){
        return new SocketMessageParser.HandleMessage() {
            @Override
            public void userOnline(Watcher watcher) {
                //todo: update subscriber data change logic
//                if(!currentStreamDetails.message.watchers.contains(watcher)) {
//                    currentStreamDetails.message.watchers.add(watcher);
//                    fillViewers(currentStreamDetails.message.watchers);
//                }
            }

            @Override
            public void userOffline(Watcher watcher) {

            }

            @Override
            public void streamStateChanged(StreamStatus currentStatus) {

            }

            @Override
            public void newFeedAdded(final Feed feed) {
                if(duplicatedCheckList.contains(feed.feedId)) return;
                duplicatedCheckList.add(feed.feedId);
                newCommentsCount++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(userActionsContainerLayout.getVisibility() == View.GONE){
                            newCommentNotification.setVisibility(View.VISIBLE);
                            newCommentNotification.setText(newCommentsCount + "");
                        }
                        currentStreamDetails.message.feed.add(feed);
                        commentsListAdapter.notifyDataSetChanged();
                        commentsListView.smoothScrollToPosition(commentsListAdapter.getCount() - 1);
                    }
                });
            }
        };
    }

    private void fillViewers(ArrayList<Watcher> watchers) {
        viewversLayout.removeAllViews();
        for(final Watcher watcher : watchers) {
//        for(int i = 0 ; i < 20 ; i ++){
            LayoutInflater inflater = (LayoutInflater) StreamingActivity.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.viewer_picture, null);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", watcher.watcher_id);
                    userProfileDialogFragment.setArguments(bundle);
                    userProfileDialogFragment.show(StreamingActivity.this.getSupportFragmentManager(), "UserPrfileView");
                }
            });
            viewversLayout.addView(view);
            final ImageView img = (ImageView) view.findViewById(R.id.viewersPicture);
//            MyHttp.fetchBitmap(StreamingActivity.this, Viewora.getStringPreference(getApplicationContext(), Viewora.TWITTER_USER_PICTURE, ""), new MyCallback<Bitmap>() {
            MyHttp.fetchBitmap(StreamingActivity.this, watcher.watcher_image, new MyCallback<Bitmap>() {
                @Override
                public void success(Bitmap data) {
                    StreamDrawable streamDrawable = new StreamDrawable(data);
                    img.setImageDrawable(streamDrawable);
                }

                @Override
                public void failure(String msg) {

                }

                @Override
                public void onBefore() {

                }

                @Override
                public void onFinish() {

                }
            });
        }
    }

    private void detectNetworkSpeed() {
//        speed = (TextView) findViewById(R.id.speedInfo);
        startTime = System.currentTimeMillis();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                linkSpeed = trafficStats.getUidTxBytes(uid) / 1024 - initialBytesCount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transferRate = 0;
                        long recordedTime = (System.currentTimeMillis() - startTime) / 1000;
                        try {
                            transferRate = linkSpeed / recordedTime;
                            Log.i("TransferRate", transferRate + "");
                        }catch (Exception e){

                        }
//                        speed.setText(String.valueOf(linkSpeed) + " Kb " + transferRate + "Kbps");
                    }
                });
            }
        };
        netWorkSpeedTimer = new Timer();
        netWorkSpeedTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            if(faye != null){
                faye.connectToServer(fayeJsonObject);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, LOG_TAG);
            mWakeLock.acquire();
        }
            if (mBroadcaster != null) {
                mBroadcaster.onHostActivityResumed();
                startMonitoringOrientation();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            faye.disconnectFromServer();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
            if (mBroadcaster != null) {
                stopMonitoringOrientation();
                mBroadcaster.onHostActivityPaused();
//                mBroadcaster = null;
            }
        }

    public void stopBroadcasting() {
        if (mBroadcaster != null && mBroadcaster.isRecording()) {
            stopMonitoringOrientation();
            mBroadcaster.stopRecording();
            mBroadcaster.release();
            mBroadcaster = null;
        }
        if(pingTimer != null && netWorkSpeedTimer != null) {
            pingTimer.cancel();
            netWorkSpeedTimer.cancel();
        }
    }

    protected void startMonitoringOrientation() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mOrientationListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//        bindSwipeEvent();
    }

    protected void stopMonitoringOrientation() {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mOrientationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recording = false;
//        stopBroadcasting();
        if (mBroadcaster != null && !mBroadcaster.isRecording())
            mBroadcaster.release();
        sendStopEventToServer(true);
    }

//    private void initLayout() {
////        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
////        imageWidth = displayMetrics.widthPixels + 100;
////        imageHeight = displayMetrics.heightPixels + 100;
//        cameraView = new CameraView(this);
//        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
////        mainLayout.addView(cameraView, layoutParam);
//        Log.v(LOG_TAG, "added cameraView to mainLayout");
//    }

//    private void initRecorder() {
//        try {
////            rtmp://52.74.249.221:1935/live?doPublish=08062015/79b4d1bf12f7b9b3f3bc5d5904fc051c
//            ffmpeg_link = url;
////            ffmpeg_link = "rtmp://192.168.2.3/securestream?doPublish=12345/testing";
//            Log.w(LOG_TAG, "initRecorder");
//
////            if (yuvIplimage == null) {
////// Recreated after frame size is set in surface change method
//////            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
////                yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
////                Log.v(LOG_TAG, "IplImage.create");
////            }
//            recorder = new FFmpegFrameRecorder(ffmpeg_link, 320, 240, 1);
//            Log.v(LOG_TAG, "FFmpegFrameRecorder: " + ffmpeg_link + " imageWidth: " + imageWidth + " imageHeight " + imageHeight);
////            recorder.setSampleRate(sampleAudioRateInHz);
////            Log.v(LOG_TAG, "recorder.setSampleRate(sampleAudioRateInHz)");
////// re-set in the surface changed method as well
//            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1);
//            recorder.setVideoOption("preset", "fast");
//            recorder.setPixelFormat(0);
//            recorder.setSampleRate(44100);
//            recorder.setVideoBitrate(400000);
//            recorder.setAudioBitrate(64000);
//            recorder.setFormat("flv");
//            Log.v(LOG_TAG, "recorder.setFormat(\"flv\")");
//
//
//            Log.v(LOG_TAG, "recorder.setFrameRate(frameRate)");
//// Create audio recording thread
//            audioRecordRunnable = new AudioRecordRunnable();
//            audioThread = new Thread(audioRecordRunnable);
//        }catch (java.lang.UnsatisfiedLinkError libErr){
//            AlertDialog.Builder builder = new AlertDialog.Builder(StreamingActivity.this);
//            builder.setMessage("Your phone does not support streaming.");
//            builder.setTitle("Viewora");
//            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    StreamingActivity.this.finish();
//                    return;
//                }
//            });
//            builder.show();
//        }
//        catch (Exception ex){
//            Toast.makeText(StreamingActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();
//            Log.d("Exception","Some error occured.");
//            StreamingActivity.this.finish();
//            return;
//        }
//    }
    // Start the capture
//    public void startRecording() {
//        try {
//            if(recorder == null){
//                initRecorder();
//            }
//            if(uid != -1) {
//                detectNetworkSpeed();
//            }
//            recorder.start();
//            startTime = System.currentTimeMillis();
//            recording = true;
//            audioThread.start();
//        } catch (FFmpegFrameRecorder.Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public void stopRecording() {
//// This should stop the audio thread from running
//        runAudioThread = false;
//        if (recorder != null && recording) {
//            recording = false;
//            Log.v(LOG_TAG,"Finishing recording, calling stop and release on recorder");
//            try {
//                camera.setPreviewCallback(null);
//                camera.stopPreview();
//                camera.release();
//                camera = null;
//                recorder.stop();
//                recorder.release();
//            } catch (FFmpegFrameRecorder.Exception e) {
//                e.printStackTrace();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//            recorder = null;
//        }
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//// Quit when back button is pushed
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            showStreamEndDialog();
//            if(event.getAction() == KeyEvent.ACTION_UP) {
//                if (newCommentLayout.getVisibility() == View.VISIBLE) {
//                    newCommentLayout.setVisibility(View.GONE);
//                }
//            }
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onBackPressed() {
        if(mBroadcaster != null && mBroadcaster.isRecording()) {
            showStreamEndDialog();
            return;
        }
        super.onBackPressed();
    }

    //    @Override
//    public void onClick(View v) {
//                if (!recording) {
//            startRecording();
////            streamInfoTxt.setText(ffmpeg_link);
//            Log.w(LOG_TAG, "Start Button Pushed");
////            if(Build.VERSION.SDK_INT > 15) {
////                recordButton.setBackground(getResources().getDrawable(R.drawable.stop));
////            }else {
////                recordButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.stop));
////            }
//        } else {
////        netWorkSpeedTimer.cancel();
//////        streamInfoTxt.setText("");
////        stopRecording();
////        Log.w(LOG_TAG, "Stop Button Pushed");
//////            if(Build.VERSION.SDK_INT > 15) {
//////                recordButton.setBackground(getResources().getDrawable(R.drawable.record));
//////            }else {
//////                recordButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.record));
//////            }
////        if(uid != -1) {
////            initialBytesCount = trafficStats.getUidTxBytes(uid) / 1024;
////            speed.setText("0");
//
//                    showStreamEndDialog();
//        }
//    }

    private void showStreamEndDialog() {
        AlertDialog.Builder streamEndDialogBuilder =  new AlertDialog.Builder(StreamingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        streamEndDialogBuilder.setMessage("Are you sure you want to end this stream?")
                .setCancelable(false)
                .setTitle("End Stream")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fetchScoreProgressBar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
//                        if (stopThread == null) stopThread = new StopStreamingThread();
                        sendStopEventToServer(false);
                        stopBroadcasting();
                        Log.w(LOG_TAG, "Stop Button Pushed");
//                        if(uid != -1) {
//                           ev initialBytesCount = trafficStats.getUidTxBytes(uid) / 1024;
//                            speed.setText("0");
//                        }

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        streamEndDialogBuilder.show();
    }

    public void sendStopEventToServer(final boolean interrupted){
        final Handler handler = new Handler();
        try {
            new MyHttp<UserScore>(MonkeyLive.context, EndPoints.stopStream(currentStreamDetails.message.slug), MyHttp.POST, UserScore.class)
                    .defaultHeaders()
                    .send(new MyCallback<UserScore>() {
                        @Override
                        public void success(final UserScore data) {
                            Log.i("Stream status", "Stopped");
                            Log.i("Score ", String.valueOf(data.message.score));
//                        Toast.makeText(StreamingActivity.this, "Score " + data.message.score, Toast.LENGTH_SHORT).show();
                            MonkeyLive.setIntPreferenceData(MonkeyLive.USER_SCORE, data.message.score);
//                            LocalBroadcastManager.getInstance(StreamingActivity.this).sendBroadcast(new Intent(Viewora.SCORE_UPDATED));
                            if (!interrupted) {
                                fetchScoreProgressBar.setVisibility(View.GONE);
                                scoreOnStreamEndTv.setText("Score " + data.message.score);
                            }
                        }

                        @Override
                        public void failure(String msg) {
                            Log.i("Score ", msg);
                        }

                        @Override
                        public void onBefore() {
                            Log.i("Stream status", "Before stop");
                            streamEndLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendStartEventToServer() {
        new MyHttp<MyResponse>(MonkeyLive.context, EndPoints.startStream(currentStreamDetails.message.slug), MyHttp.POST, MyResponse.class)
                .defaultHeaders()
                .send(new MyCallback<MyResponse>() {
                    @Override
                    public void success(MyResponse data) {
                        initializeWaitLayout.setVisibility(View.GONE);
                        Log.i("Stream status", "Started");
                    }

                    @Override
                    public void failure(String msg) {
                        Log.i("Stream status", "Start failed");
                    }

                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }


    //---------------------------------------------
// audio thread, gets and encodes audio data
//---------------------------------------------
//    class AudioRecordRunnable implements Runnable {
//        @Override
//        public void run() {
//// Set the thread priority
//            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
//// Audio
//            int bufferSize;
//            short[] audioData;
//            int bufferReadResult;
//            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
//            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//            audioData = new short[bufferSize];
//            Log.d(LOG_TAG, "audioRecord.startRecording()");
//            audioRecord.startRecording();
//// Audio Capture/Encoding Loop
//            while (runAudioThread) {
//// Read from audioRecord
//                bufferReadResult = audioRecord.read(audioData, 0, audioData.length);
//                if (bufferReadResult > 0) {
////Log.v(LOG_TAG,"audioRecord bufferReadResult: " + bufferReadResult);
//// Changes in this variable may not be picked up despite it being "volatile"
//                    if (recording) {
//
//                        try {
//// Write to FFmpegFrameRecorder
//                            Buffer[] buffer = {ShortBuffer.wrap(audioData, 0, bufferReadResult)};
////                            recorder.setTimestamp(timeStamp);
//                            recorder.record(buffer);
//
//                        } catch (FFmpegFrameRecorder.Exception e) {
//                            Log.v(LOG_TAG,e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            Log.v(LOG_TAG,"AudioThread Finished");
///* Capture/Encoding finished, release recorder */
//            if (audioRecord != null) {
//                audioRecord.stop();
//                audioRecord.release();
//                audioRecord = null;
//                Log.v(LOG_TAG,"audioRecord released");
//            }
//        }
//    }
//    class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
//        private boolean previewRunning = false;
//        private byte[] previewBuffer;
//        long videoTimestamp = 0;
//        //Bitmap bitmap;
//        Canvas canvas;
//        private opencv_core.CvSize sizeRotated;
//        private CameraHandlerThread mThread;
//        private int cameraWidth;
//        private int cameraHeight;
//
//        public CameraView(Context _context) {
//            super(_context);
//            holder = this.getHolder();
//            holder.addCallback(this);
//            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            holder.setFixedSize(metrics.widthPixels, metrics.heightPixels);
//        }
//
//        public void setCameraDisplayOrientation(Activity activity,
//                                                int cameraId, android.hardware.Camera camera) {
//            android.hardware.Camera.CameraInfo info =
//                    new android.hardware.Camera.CameraInfo();
//            android.hardware.Camera.getCameraInfo(cameraId, info);
//            int rotation = activity.getWindowManager().getDefaultDisplay()
//                    .getRotation();
//            int degrees = 0;
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    degrees = 0;
//                    break;
//                case Surface.ROTATION_90:
//                    degrees = 90;
//                    break;
//                case Surface.ROTATION_180:
//                    degrees = 180;
//                    break;
//                case Surface.ROTATION_270:
//                    degrees = 270;
//                    break;
//            }
//
//            int result;
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                result = (info.orientation + degrees) % 360;
//                result = (360 - result) % 360;  // compensate the mirror
//            } else {  // back-facing
//                result = (info.orientation - degrees + 360) % 360;
//            }
////                    result = 0;
//            camera.setDisplayOrientation(result);
//        }
//
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
////            camera = Camera.open(cameraId);
//////            camera.setDisplayOrientation(90);
////            setCameraDisplayOrientation(StreamingActivity.this, cameraId, camera);
//            try {
////                camera = Camera.open(cameraId);
//                if (mThread == null) mThread = new CameraHandlerThread();
//
//                synchronized (mThread) {
//                    mThread.openCamera();
//                }
//                setCameraDisplayOrientation(StreamingActivity.this, cameraId, camera);
//                camera.setPreviewDisplay(holder);
//                camera.setPreviewCallback(this);
//                Camera.Parameters currentParams = camera.getParameters();
//                if (flashOn) {
//                    currentParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                } else {
//                    currentParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                }
//                Log.v(LOG_TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
//                Log.v(LOG_TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: " + currentParams.getPreviewSize().height);
//// Use these values
//                List<Camera.Size> sl = currentParams.getSupportedPreviewSizes();
//                cameraWidth = 0;
//                cameraHeight = 0;
//                for(Camera.Size s : sl){
//                    if(cameraWidth <= s.width && cameraHeight <= s.height) {
//                        cameraWidth = s.width;
//                        cameraHeight = s.height;
//                    }
//                }
//
//                imageWidth = cameraWidth;
//                imageHeight = cameraHeight;
//
//                yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
//                currentParams.setPreviewSize(cameraWidth, cameraHeight);
//                frameRate = currentParams.getPreviewFrameRate();
//                camera.setParameters(currentParams);
////                bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ALPHA_8);
////                camera.startPreview();
//                camera.startPreview();
//                previewRunning = true;
//                setKeepScreenOn(true);
//            } catch (Exception e) {
//                Log.v(LOG_TAG, e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(Viewora.context, "Please Try Again", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//
//        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            setCameraDisplayOrientation(StreamingActivity.this, cameraId, camera);
//            Log.v(LOG_TAG, "Surface Changed: width " + width + " height: " + height);
//// Get the current parameters
//            Camera.Parameters currentParams = camera.getParameters();
//            Log.v(LOG_TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
//            Log.v(LOG_TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: " + currentParams.getPreviewSize().height);
//// Use these values
//            currentParams.setPreviewSize(cameraWidth, cameraHeight);
////            imageWidth = currentParams.getPreviewSize().width;
////            imageHeight = currentParams.getPreviewSize().height;
//            frameRate = currentParams.getPreviewFrameRate();
//            camera.setParameters(currentParams);
//
//// Create the yuvIplimage if needed
//            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
////yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
//
//
//            ///roate
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            try {
//                camera.setPreviewCallback(null);
//                previewRunning = false;
//                camera.release();
//                setKeepScreenOn(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onPreviewFrame(byte[] data, Camera camera) {
////            screenShot = data;
//            if(sendPicture){
//                try {
//                    Bitmap bitmap = getBitmapImageFromYUV(data, imageWidth, imageHeight);
//                    Matrix matrix = new Matrix();
//                    if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                        matrix.postRotate(-90);
//                    }else {
//                        matrix.postRotate(90);
//                    }
//                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
//                    fileToUpload = File.createTempFile("Viewora-", "-upload.jpeg");
//                    FileOutputStream fileOutputStream = new FileOutputStream(fileToUpload);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
//                    fileOutputStream.close();
//                    FileUploadService.uploadPicture(fileToUpload, currentStreamDetails.message.slug);
//                    sendPicture = false;
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            if (yuvIplimage != null && recording) {
////                Log.v(LOG_TAG, "Width : " + imageWidth + " Height : " + imageHeight);
//                videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
//                StreamingActivity.this.timeStamp = videoTimestamp;
//// Put the camera preview frame right into the yuvIplimage object
//                yuvIplimage.getByteBuffer().put(data);
//
//
////                //rotate
////                IplImage img = IplImage.create(yuvIplimage.height(),yuvIplimage.width(),yuvIplimage.depth(),yuvIplimage.nChannels());
////                opencv_core.cvTranspose(yuvIplimage, img);
////                opencv_core.cvFlip(img,img,-90);
//
////                yuvIplimage = img;
//                //rotate
//                IplImage img = IplImage.create(yuvIplimage.height(), yuvIplimage.width(), yuvIplimage.depth(), yuvIplimage.nChannels());
//                opencv_core.cvTranspose(yuvIplimage, img);
//                opencv_core.cvFlip(img, img, -90);
//
//                yuvIplimage = img;
//                //rotate ends
//
////Log.v(LOG_TAG,"Writing Frame");
//                try {
//// Get the correct time
//                    recorder.setTimestamp(videoTimestamp);
//// Record the image into FFmpegFrameRecorder
//
//
//                    videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
//                    yuvIplimage = IplImage.create(imageWidth, imageHeight * 3 / 2, IPL_DEPTH_8U, 1);
//                    yuvIplimage.getByteBuffer().put(data);
//
//                    final IplImage rgbimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 3);
//                    opencv_imgproc.cvCvtColor(yuvIplimage, rgbimage, opencv_imgproc.CV_YUV2BGR_NV21);
//
//                    IplImage rotateimage = null;
//                    int degrees;
//                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                        degrees = 180;
//                    } else {
//                        degrees = 0;
//                    }
//
//                    recorder.setTimestamp(videoTimestamp);
//                    int rot = 0;
//                    switch (degrees) {
//                        case 0:
//                            rot = 1;
//                            rotateimage = rotate(rgbimage, rot);
//                            break;
//                        case 180:
//                            rot = -1;
//                            rotateimage = rotate(rgbimage, rot);
//                            break;
//                        default:
//                            rotateimage = rgbimage;
//                    }
//                    recorder.record(rotateimage);
//
//
////                    recorder.record(yuvIplimage);
////                    opencv_core.cvReleaseImage(yuvIplimage);
////                    opencv_core.cvReleaseImage(img);
//
////                    yuvIplimage.getByteBuffer().clear();
//                } catch (FFmpegFrameRecorder.Exception e) {
//                    Log.v(LOG_TAG, e.getMessage());
//                    e.printStackTrace();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        private Bitmap getBitmapImageFromYUV(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//        {
//            YuvImage localYuvImage = new YuvImage(paramArrayOfByte, 17, paramInt1, paramInt2, null);
//            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
//            localYuvImage.compressToJpeg(new Rect(0, 0, paramInt1, paramInt2), 80, localByteArrayOutputStream);
//            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
//            BitmapFactory.Options localOptions = new BitmapFactory.Options();
//            localOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//            return BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length, localOptions);
//        }
//        IplImage rotate(IplImage IplSrc, int angle) {
//            IplImage img = IplImage.create(IplSrc.height(), IplSrc.width(), IplSrc.depth(), IplSrc.nChannels());
//            cvTranspose(IplSrc, img);
//            cvFlip(img, img, angle);
//            return img;
//        }
//
//        private class CameraHandlerThread extends HandlerThread {
//            Handler mHandler;
//
//            public CameraHandlerThread() {
//                super("CameraHandlerThread");
//                start();
//                mHandler = new Handler(getLooper());
//            }
//
//            synchronized void notifyCameraOpened() {
//                notify();
//            }
//
//            void openCamera() {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        openNewCamera();
//                        notifyCameraOpened();
//                    }
//                });
//                try {
//                    wait();
//                } catch (InterruptedException e) {
//                    Log.w(LOG_TAG, "wait was interrupted");
//                }
//            }
//        }
//
//
//        private void openNewCamera() {
//            try {
//                camera = Camera.open(cameraId);
//            } catch (RuntimeException e) {
//                Log.e(LOG_TAG, "failed to open front camera");
//            }
//        }
//    }
//    private class StopStreamingThread extends HandlerThread {
//        Handler mHandler;
//
//        public StopStreamingThread() {
//            super("StopStreamingThread");
//            start();
//            mHandler = new Handler(getLooper());
//        }
//
//        synchronized void notifyStreamStopped() {
//            notify();
//        }
//
//        void stopStream() {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    stopRecording();
//                    notifyStreamStopped();
//                }
//            });
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                Log.w(LOG_TAG, "wait was interrupted");
//            }
//        }
//    }
}

