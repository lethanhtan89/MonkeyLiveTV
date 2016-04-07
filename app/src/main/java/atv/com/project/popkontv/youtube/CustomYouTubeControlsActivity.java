package atv.com.project.popkontv.youtube;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import atv.com.project.popkontv.R;

/**
 * Created by Administrator on 4/7/2016.
 */
public class CustomYouTubeControlsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    private YouTubePlayer player;
    private String videoId;
    private Button playButton;
    private Button pauseButton;
    private RadioGroup styleRadioGroup;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_controls);

        final Bundle arguments = getIntent().getExtras();
        if (arguments != null && arguments.containsKey(KEY_VIDEO_ID)) {
            videoId = arguments.getString(KEY_VIDEO_ID);
        }

        final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
        playerView.initialize("AIzaSyA34h-7yPCNSBkBdJ9cAhLHozGn4JwHtWs", this);

        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(this);

        styleRadioGroup = (RadioGroup) findViewById(R.id.style_radio_group);
        ((RadioButton) findViewById(R.id.style_default)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.style_minimal)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.style_chromeless)).setOnCheckedChangeListener(this);

        setControlsEnabled(false);
    }

    private void setControlsEnabled(boolean enabled){
        playButton.setEnabled(enabled);
        pauseButton.setEnabled(enabled);
        for(int i = 0; i < styleRadioGroup.getChildCount(); i++){
            styleRadioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked && player != null){
            switch (buttonView.getId()){
                case R.id.style_default:
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    break;
                case R.id.style_minimal:
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                    break;
                case R.id.style_chromeless:
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        // Null check a player
        if(player != null){
            if(v == playButton){
                player.play();
            }
            else {
                player.pause();
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        player = youTubePlayer;

        if(videoId != null){
            if(restored){
                player.play();
            }
            else {
                player.loadVideo(videoId);
            }
        }
        setControlsEnabled(true);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if(result.isUserRecoverableError()){
            result.getErrorDialog(this, RECOVERY_DIALOG_REQUEST);
        }
        else {
            Toast.makeText(this, "Unable to play video", Toast.LENGTH_LONG).show();
        }
    }
}
