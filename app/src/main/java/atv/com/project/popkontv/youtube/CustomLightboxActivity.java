package atv.com.project.popkontv.youtube;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import atv.com.project.popkontv.R;

/**
 * Created by Administrator on 4/7/2016.
 */
public class CustomLightboxActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    //Keys
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";

    private YouTubePlayer Player;
    private boolean isFullscreen;

    private int millis;
    private String VideoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_lightbox);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_youtube_activity);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
        playerView.initialize("" + R.string.DEVELOPER_KEY, this);

        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
        }

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_VIDEO_ID)) {
            VideoId = extras.getString(KEY_VIDEO_ID);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        boolean finish = true;
        try {
            if (Player != null) {
                if (isFullscreen) {
                    finish = false;
                    Player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            //Wait until we are out of fullscreen before finishing this activity
                            if (!b) {
                                finish();
                            }
                        }
                    });
                    Player.setFullscreen(false);
                }
                Player.pause();
            }
        } catch (final IllegalStateException e) {
            e.printStackTrace();
        }

        if (finish) {
            super.onBackPressed();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Player = youTubePlayer;
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                isFullscreen = b;
            }
        });

        if (VideoId != null && !wasRestored) {
            youTubePlayer.loadVideo(VideoId);
        }

        if (wasRestored) {
            youTubePlayer.seekToMillis(millis);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Unable to load video", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if (Player != null) {
            outState.putInt(KEY_VIDEO_TIME, Player.getCurrentTimeMillis());
        }
    }
}
