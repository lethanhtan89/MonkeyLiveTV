package io.kickflip.sdk.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.chilicat.m3u8.Element;
import net.chilicat.m3u8.Playlist;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import atv.com.project.monkeylivetv.Adapters.CommentsAdapter;
import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Fragments.UserProfileDialogFragment;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Activity.MainActivity;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Network.SocketMessageParser;
import atv.com.project.monkeylivetv.Pojo.Feed;
import atv.com.project.monkeylivetv.Pojo.NewComment;
import atv.com.project.monkeylivetv.Pojo.NewCommentDetails;
import atv.com.project.monkeylivetv.Pojo.PingForWatchersResponse;
import atv.com.project.monkeylivetv.Pojo.StreamFullDetails;
import atv.com.project.monkeylivetv.Pojo.StreamStatus;
import atv.com.project.monkeylivetv.Pojo.UserTwitterActionsResponse;
import atv.com.project.monkeylivetv.Pojo.Watcher;
import atv.com.project.monkeylivetv.R;
import atv.com.project.monkeylivetv.lib.Faye;
import atv.com.project.monkeylivetv.lib.OnSwipeTouchListener;
import atv.com.project.monkeylivetv.lib.StreamDrawable;
import io.kickflip.sdk.av.Broadcaster;
import io.kickflip.sdk.av.M3u8Parser;
import io.kickflip.sdk.view.GLCameraEncoderView;

/**
 * MediaPlayerFragment demonstrates playing an HLS Stream, and fetching
 * stream metadata via the .m3u8 manifest to decorate the display for Live streams.
 */
public class MediaPlayerFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {
    private static final String TAG = "MediaPlayerFragment";
    private static final String ARG_URL = "url";
    private final static String LOG_TAG = "StreamingActivity";
    public static final String STREAM_SLUG = "slug";
//    private KickflipApiClient mKickflip;

    private ProgressBar mProgress;
    private TextureView mTextureView;
//    private TextView mLiveLabel;
    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;
    private String mMediaUrl;

    private String userTweetMessage;
    private ImageButton likeStreamButton;
    private boolean flashOn = false;
    private TextView userNameTv;
    //    private TextView userHandleTv;
    private TextView streamViewStatusTv;
    //    private TextView userLocationTv;
//    private TextView streamTweetTv;
    private ImageButton commentButton;
    private StreamFullDetails currentStreamDetails = new StreamFullDetails();
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
//    private TextView scoreOnStreamEndTv;
    private ImageButton streamEndBackBt;

    private static Broadcaster mBroadcaster;        // Make static to survive Fragment re-creation
    private GLCameraEncoderView mCameraView;
    private static final boolean VERBOSE = false;
    private LinearLayout initializeWaitLayout;
    private LinearLayout userDetailsLayout;
    private boolean isShowingDetails = true;
    private ImageButton userActionsControlButton;
//    private ImageButton zoomInCamera;
//    private ImageButton zoomOutCamera;
    private boolean isCommentsLayoutShowing = false;
    private TextView newCommentNotification;
    private int newCommentsCount = 0;

    // M3u8 Media properties inferred from .m3u8
    private int mDurationMs;
    private boolean mIsLive;

    private Surface mSurface;

    private M3u8Parser.M3u8ParserCallback m3u8ParserCallback = new M3u8Parser.M3u8ParserCallback() {
        @Override
        public void onSuccess(Playlist playlist) {
            updateUIWithM3u8Playlist(playlist);
            setupMediaPlayer(mSurface);
        }

        @Override
        public void onError(Exception e) {
            if (VERBOSE) Log.i(TAG, "m3u8 parse failed " + e.getMessage());
        }
    };

    private View.OnTouchListener mTextureViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaController != null && isResumed()) {
                mMediaController.show();
            }
            return false;
        }
    };
    private View root;
    private ImageButton retweetStreamButton;
    private int streamId;
    private String publisherImage;
    private String publisherName;
    private String streamSlug;
    private TextView streamEndShowText;

    public static MediaPlayerFragment newInstance(String mediaUrl, String streamSlug) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, mediaUrl);
        args.putString(STREAM_SLUG, streamSlug);
        fragment.setArguments(args);
        return fragment;
    }

    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMediaUrl = getArguments().getString(ARG_URL);
            if (mMediaUrl.substring(mMediaUrl.lastIndexOf(".") + 1).equals("m3u8")) {
                parseM3u8FromMediaUrl();
            } else if (mMediaUrl.substring(mMediaUrl.lastIndexOf(".") + 1).equals("mp4")) {
                setupMediaPlayer(mSurface);
                throw new IllegalArgumentException("Unknown HLS media url format: " + mMediaUrl);
            }
        }
    }

    private void updateUIWithM3u8Playlist(Playlist playlist) {
        int durationSec = 0;
        for (Element element : playlist.getElements()) {
            durationSec += element.getDuration();
        }
        mIsLive = !playlist.isEndSet();
        mDurationMs = durationSec * 1000;
        mDurationMs = durationSec * 1000;
    }

    private void setupMediaPlayer(Surface displaySurface) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(displaySurface);
            mMediaPlayer.setDataSource(mMediaUrl);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (VERBOSE) Log.i(TAG, "media player prepared");
                    mProgress.setVisibility(View.GONE);
                    mMediaController.setEnabled(true);
                    mTextureView.setOnTouchListener(mTextureViewTouchListener);
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (getActivity() != null) {
                        if (VERBOSE) Log.i(TAG, "media player complete. finishing");
                        streamEndLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    streamEndLayout.setVisibility(View.VISIBLE);
                    streamEndShowText.setText("Video cannot be played. Please check your internet connection and try again");
                    return false;
                }
            });

            mMediaController = new MediaController(getActivity());
            mMediaController.setAnchorView(mTextureView);
            mMediaController.setMediaPlayer(this);

            mMediaPlayer.prepareAsync();
        } catch (IOException ioe) {
            Log.i("exp", ioe.getMessage());
        }catch (Exception e) {
            Log.i("exp", e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_media_player, container, false);
        if (root != null) {
            mTextureView = (TextureView) root.findViewById(R.id.textureView);
            mTextureView.setSurfaceTextureListener(this);
            mTextureView.setKeepScreenOn(true);
            mProgress = (ProgressBar) root.findViewById(R.id.progress);
        }


        streamSlug = getArguments().getString(STREAM_SLUG);
        initialiseViews();
        bindEvents();
        getStreamData();
//        try {
//            openSocket();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        startPingingForLiveData();
        return root;
    }

    private void parseM3u8FromMediaUrl() {
        M3u8Parser.parseM3u8(mMediaUrl, m3u8ParserCallback);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return !mIsLive;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void initialiseViews() {

        rootView = (RelativeLayout) root.findViewById(R.id.fragment_root_view);
        userActionsLayout = (LinearLayout) root.findViewById(R.id.userActionsLayout);
        userDetailsLayout = (LinearLayout) root.findViewById(R.id.publisher_details_layout);
        userActionsContainerLayout = (LinearLayout) root.findViewById(R.id.userActionsContainerLayout);
        likeStreamButton = (ImageButton) root.findViewById(R.id.stream_like_button);
        retweetStreamButton = (ImageButton) root.findViewById(R.id.stream_retweet_button);
        commentButton = (ImageButton) root.findViewById(R.id.postComment);
        newCommentNotification = (TextView) root.findViewById(R.id.new_comment_notifications);
        streamEndShowText = (TextView) root.findViewById(R.id.stream_end_show_text);

        userActionsControlButton = (ImageButton) root.findViewById(R.id.action_buttons_controll);

        streamEndLayout = (LinearLayout) root.findViewById(R.id.video_view_stream_ended_container);
//        scoreOnStreamEndTv = (TextView) root.findViewById(R.id.score_on_stream_end);
        streamEndBackBt = (ImageButton) root.findViewById(R.id.go_back_stream_end);
        userImage = (ImageView) root.findViewById(R.id.userImage);
        
        userNameTv = (TextView) root.findViewById(R.id.userName);
        streamViewStatusTv = (TextView) root.findViewById(R.id.stream_view_status);

        viewversLayout = (LinearLayout) root.findViewById(R.id.viewersPictureLayout);
        newCommentLayout = (LinearLayout) root.findViewById(R.id.commentLayout);

        commentsListView = (ListView) root.findViewById(R.id.commentsList);

        postCommentBox = (EditText) root.findViewById(R.id.commentBox);
        commentBoxHandle = (TextView) root.findViewById(R.id.commentBoxHandle);
        commentBoxHandle.setText("@" + MonkeyLive.getStringPreference(MonkeyLive.TWITTER_HANDLE, ""));
        postCommentButton = (TextView) root.findViewById(R.id.postCommentButton);
        twitterReflectToggle = (Switch) root.findViewById(R.id.reflectInTwitter);
        twitterReflectToggle.setChecked(true);

        fetchScoreProgressBar = (ProgressBar)root.findViewById(R.id.streamEndProgressBar);

        recordControl = (ImageButton) root.findViewById(R.id.recorder_control);
//        zoomOutCamera = (ImageButton) root.findViewById(R.id.zoom_out_camera);
//        zoomInCamera = (ImageButton) root.findViewById(R.id.zoom_in_camera);

        userNameTv.setTypeface(MonkeyLive.racho);
        streamViewStatusTv.setTypeface(MonkeyLive.racho);
        commentBoxHandle.setTypeface(MonkeyLive.racho);
        postCommentButton.setTypeface(MonkeyLive.racho);
    }
    private void bindEvents() {
        bindEventToUserActionsControllButton();
        bindEventToCommentButton();
        bindEventToPostComment();
        bindEventToLikeButton();
        bindEventToReTweetButton();
        bindEventToRecordControl();
        bindEventsToStreamEndBackButton();
    }

    private void bindSwipeEvent() {
        mCameraView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
//                Toast.makeText(StreamingActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                if (!isCommentsLayoutShowing) {
                    mShowCommentsLayout();
                }
            }

            public void onSwipeLeft() {
                if (isCommentsLayoutShowing) {
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



    private void bindEventToUserActionsControllButton() {
        userActionsControlButton.setOnClickListener(new View.OnClickListener() {
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

    private void bindEventsToStreamEndBackButton() {
        streamEndBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void bindEventToRecordControl() {
        recordControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void bindEventToCommentButton() {
        commentButton.setOnClickListener(new View.OnClickListener() {
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
        Animation slideOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        userActionsContainerLayout.startAnimation(slideOutAnimation);
        isCommentsLayoutShowing = false;
    }

    private void mShowCommentsLayout() {
        newCommentNotification.setVisibility(View.GONE);
        newCommentsCount = 0;
        commentButton.setBackground(getResources().getDrawable(R.drawable.action_icon_background_live));
        userActionsContainerLayout.setVisibility(View.VISIBLE);
        Animation slideInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        userActionsContainerLayout.startAnimation(slideInAnimation);
        isCommentsLayoutShowing = true;
    }

    private void bindEventToReTweetButton() {
        retweetStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retweetStreamButton.setVisibility(View.GONE);
                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(getActivity(), EndPoints.reTweet(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
                        .defaultHeaders()
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
                                retweetStreamButton.setVisibility(View.VISIBLE);
                                if(getActivity() != null)
                                    Toast.makeText(getActivity(), "Request failed.Please try again", Toast.LENGTH_LONG).show();
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

    private void bindEventToLikeButton() {
        likeStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeStreamButton.setVisibility(View.GONE);
                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(getActivity(), EndPoints.makeFauvorite(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
                        .defaultHeaders()
                        .send(new MyCallback<UserTwitterActionsResponse>() {
                            @Override
                            public void success(UserTwitterActionsResponse response) {
                                Log.i("Succescomment", response.toString());
                                if (duplicatedCheckList.contains(response.message.feedId)) return;
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
                                likeStreamButton.setVisibility(View.VISIBLE);
                                if(getActivity() != null)
                                    Toast.makeText(getActivity(), "Request failed.Please try again", Toast.LENGTH_LONG).show();
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

    private void bindEventToPostComment() {
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = postCommentBox.getText().toString();
                if (comment.equals("")) return;
                postCommentBox.setText("");
//                newCommentLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                NewComment newComment = new NewComment();
                newComment.stream_id = streamId;
                newComment.commentDetails = new NewCommentDetails();
                newComment.commentDetails.message = comment;
                newComment.commentDetails.reflect_in_twitter = twitterReflectToggle.isChecked();

                MyHttp<UserTwitterActionsResponse> http = new MyHttp<UserTwitterActionsResponse>(getActivity(), EndPoints.commentOnStream(streamId), MyHttp.POST, UserTwitterActionsResponse.class)
                        .defaultHeaders()
                        .addJson(newComment)
                        .send(new MyCallback<UserTwitterActionsResponse>() {
                            @Override
                            public void success(UserTwitterActionsResponse response) {
                                Log.i("Succescomment", response.toString());
                                if (duplicatedCheckList.contains(response.message.feedId)) return;
                                newCommentsCount++;
                                if (userActionsContainerLayout.getVisibility() == View.GONE) {
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
                                if (getActivity() != null)
                                    Toast.makeText(getActivity(), "Failed to comment. Try again", Toast.LENGTH_LONG).show();
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

    private void openSocket() throws JSONException {
        messageHandler = getHandler();
        Faye ste = new Faye(new Handler(Looper.getMainLooper()), Uri.parse(EndPoints.openSocket),"/streams/" + String.valueOf(streamSlug));
//        Faye ste = new Faye(new Handler(Looper.getMainLooper()), Uri.parse(EndPoints.openSocket),"/messages");
        JSONObject ext = new JSONObject();
        ext.put("authToken", "asdfdsafsdfsdaf");
        ste.connectToServer(ext);
        ste.setFayeListener(new Faye.FayeListener() {
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
                Log.i("Streamstate", currentStatus.streamStatus);
                if(currentStatus.streamStatus.equalsIgnoreCase("ended")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                streamEndLayout.setVisibility(View.VISIBLE);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void newFeedAdded(final Feed feed) {
                if(duplicatedCheckList.contains(feed.feedId)) return;
                duplicatedCheckList.add(feed.feedId);
                newCommentsCount++;
                getActivity().runOnUiThread(new Runnable() {
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

    private void startPingingForLiveData() {
        //marudhu
        if(currentStreamDetails.message == null ) return;

        pingTimer = new Timer();
        TimerTask pingTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    MyHttp<PingForWatchersResponse> myHttp = new MyHttp<PingForWatchersResponse>(getActivity(), EndPoints.pingForWatchers(currentStreamDetails.message.slug), MyHttp.POST, PingForWatchersResponse.class)
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
                }catch(Exception e){
                    Log.d("Error in ping", e.getMessage());
                }
            }

        };
        pingTimer.scheduleAtFixedRate(pingTask, 60000, 60000);
    }


    private void fillViewers(ArrayList<Watcher> watchers) {
        viewversLayout.removeAllViews();
        for(final Watcher watcher : watchers) {
//        for(int i = 0 ; i < 20 ; i ++){
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.viewer_picture, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", watcher.watcher_id);
                    userProfileDialogFragment.setArguments(bundle);
                    userProfileDialogFragment.show(((AppCompatActivity)getActivity()).getSupportFragmentManager(), "UserPrfileView");
                }
            });
            viewversLayout.addView(view);
            final ImageView img = (ImageView) view.findViewById(R.id.viewersPicture);
//            MyHttp.fetchBitmap(StreamingActivity.this, Viewora.getStringPreference(getApplicationContext(), Viewora.TWITTER_USER_PICTURE, ""), new MyCallback<Bitmap>() {
            MyHttp.fetchBitmap(getActivity(), watcher.watcher_image, new MyCallback<Bitmap>() {
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

    private void getStreamData() {
        MyHttp<StreamFullDetails> request = new MyHttp<>(getActivity(), EndPoints.baseStreamUrl + streamSlug, MyHttp.GET, StreamFullDetails.class)
                .defaultHeaders()
                .send(new MyCallback<StreamFullDetails>() {
                    @Override
                    public void success(StreamFullDetails data) {
                        setDataToViewStartRecording(data);
                    }

                    @Override
                    public void failure(String msg) {
                        Toast.makeText(MonkeyLive.context, "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBefore() {
                    }

                    @Override
                    public void onFinish() {
                    }
                });
    }
    private void setDataToViewStartRecording(StreamFullDetails data) {
        currentStreamDetails = data;
//        url = "rtmp://192.168.1.16/castasy/stream";
        streamId = currentStreamDetails.message.id;
        if(currentStreamDetails.message.liked){
            likeStreamButton.setVisibility(View.GONE);
        }
        if(currentStreamDetails.message.restreamed){
            retweetStreamButton.setVisibility(View.GONE);
        }
        publisherImage = currentStreamDetails.message.publisher.publisher_image;
        publisherName = currentStreamDetails.message.publisher.publisher_name;
//        publisherHandle = currentStreamDetails.message.publisher.publisher_handle;
//        publisherTweetMessage = currentStreamDetails.message.stream_message;
//        userLocationTv.setText(currentStreamDetails.message.publisher.city);

        MyHttp.fetchBitmap(getActivity(), publisherImage, new MyCallback<Bitmap>() {
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

        userNameTv.setText(publisherName);
//        userHandleTv.setText(publisherHandle);
//        streamTweetTv.setText(publisherTweetMessage);

        commentsListAdapter = new CommentsAdapter(getActivity(), -1, currentStreamDetails.message.feed);
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
    }

    private void showRecordEndDialog() {
        AlertDialog.Builder streamEndDialogBuilder =  new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        streamEndDialogBuilder.setMessage("Are you sure you want to end this record?")
                .setCancelable(false)
                .setTitle("End Record")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fetchScoreProgressBar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
//                        if (stopThread == null) stopThread = new StopStreamingThread();
                        //sendStopEventToServer(false);
                        //stopBroadcasting();
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
}
