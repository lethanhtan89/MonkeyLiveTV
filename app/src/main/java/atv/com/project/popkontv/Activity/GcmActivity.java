package atv.com.project.popkontv.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Fragments.FollowersFragment;
import atv.com.project.popkontv.R;


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
        Popkon.restart();
    }
}
