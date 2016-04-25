package atv.com.project.popkontv.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Fragments.FollowersFragment;
import atv.com.project.popkontv.Fragments.FollowingFragment;
import atv.com.project.popkontv.Fragments.LeaderBoardFragment;
import atv.com.project.popkontv.Fragments.SettingsFragment;
import atv.com.project.popkontv.R;


public class UserDetailsListActivity extends AppCompatActivity {

    public static final int LEADER_BOARD = 1;
    public static final int FOLLOWERS = 2;
    public static final int FOLLOWING = 3;
    public static final int SETTINGS = 4;
    public static final int FROM_GCM_NOTIFICATION = 5;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
        setContentView(R.layout.activity_user_details_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            toolbar.setTitle("Profile");
            name = (TextView) findViewById(R.id.appNameAb);
            name.setTypeface(Popkon.racho);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);

        }
        int fragmentToShow = getIntent().getIntExtra("fragmentToShow", -1);
        switch (fragmentToShow){
            case LEADER_BOARD:
                name.setText("LeaderBoard");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.userDetailsListsContainer, new LeaderBoardFragment())
                        .commit();
                break;
            case FOLLOWERS:
                name.setText("Followers");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.userDetailsListsContainer, new FollowersFragment())
                        .commit();
                break;
            case FOLLOWING:
                name.setText("Following");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.userDetailsListsContainer, new FollowingFragment())
                        .commit();
                break;
            case SETTINGS:
                name.setText("Settings");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.userDetailsListsContainer, new SettingsFragment())
                        .commit();
                break;
            case FROM_GCM_NOTIFICATION:
                FollowersFragment fragment = new FollowersFragment();
                Bundle bundle = new Bundle();
                bundle.putString("followerId", getIntent().getStringExtra("followerId"));
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.userDetailsListsContainer, fragment)
                        .commit();
                break;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_details_list, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
    }
}
