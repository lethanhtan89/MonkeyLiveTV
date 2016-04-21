package atv.com.project.popkontv.Fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import atv.com.project.popkontv.R;

/**
 * Created by Administrator on 4/7/2016.
 */
public class YouTubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";

    private String videoId;

    //Empty constructor
    public YouTubeFragment() {
    }

    /**
     * Returns a new instance of this Fragment
     *
     * @param videoId The ID of the video to play
     */
    public static YouTubeFragment newInstance(final String videoId){
        final YouTubeFragment youTubeFragment = new YouTubeFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_VIDEO_ID, videoId);
        youTubeFragment.setArguments(bundle);
        return youTubeFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final Bundle arguments = getArguments();
        if(bundle != null && bundle.containsKey(KEY_VIDEO_ID)){
            videoId = bundle.getString(KEY_VIDEO_ID);
        }
        else {
            if(arguments != null && arguments.containsKey(KEY_VIDEO_ID)){
                videoId = arguments.getString(KEY_VIDEO_ID);
            }
        }
        initialize("" + R.string.DEVELOPER_KEY, this);
    }

    // set videoId
    public void setVideoId(final String videoId){
        this.videoId = videoId;
        initialize("" + R.string.DEVELOPER_KEY,this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        if(videoId != null){
            if(restored) {
                youTubePlayer.play();
            }
            else {
                youTubePlayer.loadVideo(videoId);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if(result.isUserRecoverableError()){
            result.getErrorDialog(getActivity(),RECOVERY_DIALOG_REQUEST);
        }
        else {
            Toast.makeText(getActivity(), "Unable load video", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(KEY_VIDEO_ID, videoId);
    }
}
