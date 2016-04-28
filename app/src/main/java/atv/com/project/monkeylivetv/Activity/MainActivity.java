/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package atv.com.project.monkeylivetv.Activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import atv.com.project.monkeylivetv.Fragments.LeaderBoardFragment;
import atv.com.project.monkeylivetv.Fragments.MainFragment;
import atv.com.project.monkeylivetv.Fragments.SettingsFragment;
import atv.com.project.monkeylivetv.Adapters.MainAdapter;
import atv.com.project.monkeylivetv.R;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private DrawerLayout mDrawerLayout;
    private FragmentTransaction ft;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
/*
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
*/
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // menu.clear();
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Share
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareIntent(createShareIntent());

        //Search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                break;
            case R.id.action_view_profile:
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Share
    private void setShareIntent(Intent shareIntent){
        if(shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "http://monkeylivetv.com");
        return shareIntent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Set up ViewPager
    private void setupViewPager(ViewPager viewPager) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "LIVE");
        adapter.addFragment(new MainFragment(), "VIDEO");
        adapter.addFragment(new LeaderBoardFragment(), "FAVORITE");
        adapter.addFragment(new SettingsFragment(), "SETTINGS");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /*
        private void showNavigation(){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        // Set up DrawerContent
        private void setupDrawerContent(NavigationView navigationView) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    Fragment fg = null;
                    if(menuItem.isChecked()){
                        menuItem.setChecked(false);
                    }
                    else {
                        menuItem.setChecked(true);
                    }
                    mDrawerLayout.closeDrawers();
                    switch (id){
                        case R.id.nav_video:

                            Toast.makeText(getApplicationContext(), "Video", Toast.LENGTH_LONG).show();
                            fg = new PopkonListFragment();
                            break;
                    }
                    return true;
                }
            });
        }
    */

}
