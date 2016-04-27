package atv.com.project.popkontv.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Fragments.LoginFragment;
import atv.com.project.popkontv.Fragments.MainFragment;
import atv.com.project.popkontv.MainActivity;
import atv.com.project.popkontv.R;
import io.fabric.sdk.android.Fabric;


public class LauncherActivity extends AppCompatActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "OQczffwVTLCJIsuTZ4U7rXcJP";
    private static final String TWITTER_SECRET = "GnwwAFn6tIEjmhQnhmEiksEdaIPcY1Zy38B021FAAiEgzvTAPC";
    private int loggedInUserId;
    private String loggedInUserToken;
    private Fragment fragmentInView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(LauncherActivity.this, new Twitter(authConfig));
        setUpActionBar();
        loggedInUserId = Popkon.getIntPreference(Popkon.USER_ID, 0);
        loggedInUserToken = Popkon.getStringPreference(Popkon.API_TOKEN, "");
        if(getIntent() != null){
            Popkon.setBooleanPreferenceData(Popkon.IS_SCHEDULED_STREAM,
                    getIntent().getBooleanExtra(Popkon.IS_SCHEDULED_STREAM, false));
        }
        Log.i("AppInfo", loggedInUserId + " " + loggedInUserToken);
        if(!(Popkon.loggedInUserId == 0)){
            fragmentInView = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.launcherContainer, fragmentInView)
                    .commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else {
            fragmentInView = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.launcherContainer, fragmentInView)
                    .commit();
        }
    }
    private void setUpActionBar() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar != null) {
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayShowCustomEnabled(true);
                //        actionBar.setDisplayHomeAsUpEnabled();
                actionBar.setTitle("");
                //        actionBar.setLogo(getResources().getDrawable(R.drawable.applogo));

//                ImageView profileLayout = (ImageView) findViewById(R.id.profileLayout);
//                profileLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent profileIntent = new Intent(LauncherActivity.this, UserProfileActivity.class);
//                        startActivity(profileIntent);
//                    }
//                });
                TextView appName = (TextView) findViewById(R.id.appNameAb);
//                ImageView backButton = (ImageView) findViewById(R.id.actionbarBack);
                appName.setTypeface(Popkon.racho);
//                backButton.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.i("error", e.getMessage());
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragmentInView != null){
            fragmentInView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_view_profile){
            Intent profileIntent = new Intent(LauncherActivity.this, UserProfileActivity.class);
            startActivity(profileIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
