package atv.com.project.monkeylivetv.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.twitter.MyTwitterApiClient;
import atv.com.project.monkeylivetv.R;


public class TweetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new TweetComposer());
        setContentView(R.layout.activity_tweet);
//        TweetComposer.Builder builder = new TweetComposer.Builder(this)
//                .text("just setting up my Fabric.");
//        builder.show();
        final TwitterSession session =
                Twitter.getSessionManager().getActiveSession();
        final EditText text = (EditText) findViewById(R.id.tweetText);
        Button button = (Button) findViewById(R.id.tweetButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterApiClient client = TwitterCore.getInstance().getApiClient(session);
                StatusesService statusesService = client.getStatusesService();
                statusesService.update(text.getText().toString(), 0L, false, 0.0, 0.0, "", true, true, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> tweetResult) {
                        Log.i("tweet", tweetResult.toString());
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.i("error", e.getMessage());
                    }
                });
            }
        });
        Button retweet = (Button) findViewById(R.id.retweetButton);
        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterApiClient client = new TwitterApiClient(session);
                StatusesService statusesService = client.getStatusesService();
                statusesService.retweet(595915322097475585L, true, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> tweetResult) {
                        Log.i("reTweet", tweetResult.toString());
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.i("error", e.getMessage());
                    }
                });
            }
        });
        Button markFav = (Button) findViewById(R.id.favButton);
        markFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterApiClient client = new TwitterApiClient(session);
                FavoriteService favoriteService = client.getFavoriteService();
                favoriteService.create(595915322097475585L, false, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> tweetResult) {
                        Log.i("fav", tweetResult.toString());
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.i("fav", e.getMessage());
                    }
                });
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences(MonkeyLive.PREFERENCE_NAME, Context.MODE_PRIVATE);
        Long userId = pref.getLong(MonkeyLive.TWITTER_ID, 0L);
        MyTwitterApiClient userClient = new MyTwitterApiClient(session);
        userClient.getUsersService().show(userId,null,true,new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                Log.i("user", userResult.toString());
            }

            @Override
            public void failure(TwitterException e) {
                Log.i("user", e.getMessage());
            }
        });
    }

//595915322097475585
    //4730
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
