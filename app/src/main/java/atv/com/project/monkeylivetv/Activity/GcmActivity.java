package atv.com.project.monkeylivetv.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Fragments.FollowersFragment;
import atv.com.project.monkeylivetv.R;


public class GcmActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm);
        FollowersFragment fragmentInView = new FollowersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("followerId", getIntent().getStringExtra("followerId"));
        fragmentInView.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.gcmContainer, fragmentInView)
                .addToBackStack("followers")
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MonkeyLive.restart();
    }
}
