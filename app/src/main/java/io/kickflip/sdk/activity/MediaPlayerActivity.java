package io.kickflip.sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import atv.com.project.monkeylivetv.*;

import atv.com.project.monkeylivetv.MainActivity;
import io.kickflip.sdk.fragment.MediaPlayerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @hide
 */
public class MediaPlayerActivity extends ImmersiveActivity {
    private static final String TAG = "MediaPlayerActivity";
    MediaPlayerFragment mediaPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUseImmersiveMode(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_playback);
        mediaPlayerFragment = new MediaPlayerFragment();
        // This must be setup before
        //Uri intentData = getIntent().getData();
        //String mediaUrl = isKickflipUrl(intentData) ? intentData.toString() : getIntent().getStringExtra("mediaUrl");

        String streamSlug;
        String mediaUrl;

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//        if(Intent.ACTION_VIEW.equals(action) && type != null){

//        }else{
            streamSlug = getIntent().getStringExtra(MediaPlayerFragment.STREAM_SLUG);
            mediaUrl = getIntent().getStringExtra("mediaUrl");
//        }


        checkNotNull(mediaUrl, new IllegalStateException("MediaPlayerActivity started without a mediaUrl"));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, MediaPlayerFragment.newInstance(mediaUrl, streamSlug))
                    .commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.media_playback, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
