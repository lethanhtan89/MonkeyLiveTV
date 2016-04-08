package atv.com.project.popkontv.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import atv.com.project.popkontv.R;
import atv.com.project.popkontv.content.YouTubeContent;
import atv.com.project.popkontv.youtube.CustomLightboxActivity;
import atv.com.project.popkontv.youtube.CustomYouTubeControlsActivity;
import atv.com.project.popkontv.youtube.VideoListAdapter;
import atv.com.project.popkontv.youtube.YouTubeActivity;

/**
 * Created by Administrator on 4/7/2016.
 */
public class VideoListFragment extends ListFragment {

    public VideoListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new VideoListAdapter(getActivity()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Context context = getActivity();
        final String DEVELOPER_KEY = "" + R.string.DEVELOPER_KEY;
        final YouTubeContent.YouTubeVideo video = YouTubeContent.ITEMS.get(position);

        switch (position) {
            case 0:
                //Check whether we can actually open YT
                if (YouTubeIntents.canResolvePlayVideoIntent(context)) {
                    startActivity(YouTubeIntents.createPlayVideoIntent(context, video.id));
                }
                break;
            case 1:
                //Opens in the YouTube app in fullscreen and returns to this app once the video finishes
                if (YouTubeIntents.canResolvePlayVideoIntentWithOptions(context)) {
                    startActivity(YouTubeIntents.createPlayVideoIntentWithOptions(context, video.id, true, true));
                }
                break;
            case 2:
                //Issue #3 - Need to resolve StandalonePlayer as well
                if (YouTubeIntents.canResolvePlayVideoIntent(context)) {
                    startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(), DEVELOPER_KEY, video.id));
                }
                break;
            case 3:
                //Issue #3 - Need to resolve StandalonePlayer as well
                if (YouTubeIntents.canResolvePlayVideoIntentWithOptions(context)) {
                    startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(), DEVELOPER_KEY, video.id));
                }
                break;
            case 4:
                //Open in the YouTubeSupportFragment
                final YouTubeFragment fragment = YouTubeFragment.newInstance(video.id);
                getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
                break;
            case 5:
                //Opens is Custom Activity
                final Intent intent = new Intent(context, YouTubeFragment.class);
                intent.putExtra(YouTubeFragmentActivity.KEY_VIDEO_ID, video.id);
                startActivity(intent);
                break;
            case 6:
                //Opens in the YouTubePlayerView
                final Intent actIntent = new Intent(context, YouTubeActivity.class);
                actIntent.putExtra(YouTubeActivity.KEY_VIDEO_ID, video.id);
                startActivity(actIntent);
                break;
            case 7:
                //Opens in the the custom Lightbox activity
                final Intent lightboxIntent = new Intent(context, CustomLightboxActivity.class);
                lightboxIntent.putExtra(CustomLightboxActivity.KEY_VIDEO_ID, video.id);
                startActivity(lightboxIntent);
                break;
            case 8:
                //Custom player controls
                final Intent controlsIntent = new Intent(context, CustomYouTubeControlsActivity.class);
                controlsIntent.putExtra(CustomLightboxActivity.KEY_VIDEO_ID, video.id);
                startActivity(controlsIntent);
                break;
        }
    }
}
