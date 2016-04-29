package atv.com.project.monkeylivetv.Application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;

import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import atv.com.project.monkeylivetv.Activity.LauncherActivity;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Services.LocationServices;

/**
 * Created by Administrator on 5/7/15.
 */
public class MonkeyLive extends Application {
    public static final String PREFERENCE_NAME = "App_Preferences";
    public static final String TWITTER_TOKEN = "twitter_token";
    public static final String TWITTER_SECRET = "secret";
    public static final String TWITTER_ID = "twitter_id";
    public static final String TWITTER_HANDLE = "twitter_handle";
    public static final String USER_ID = "app_user_id";//same as twitter id for now
    public static final String API_TOKEN = "api_token";
    public static final String TWITTER_USER_NAME = "twitter_user_name";
    public static final String TWITTER_USER_LOCATION = "twitter_user_location";
    public static final String TWITTER_USER_PICTURE = "twitter_user_picture";
    public static final String FACEBOOK_USER_NAME = "facebook_user_name";
    public static final String FACEBOOK_USER_LOCATION = "facebook_user_location";
    public static final String FACEBOOK_USER_PICTURE = "facebook_user_picture";
    public static final String USER_SCORE = "user_score";
    public static final String USER_FOLLOWERS_COUNT = "number_of_followers";
    public static final String USER_FOLLOWING_COUNT = "number_of_following";
    public static final String IS_SCHEDULED_STREAM = "is_scheduled_stream";
    public static final String STREAM_SLUG = "stream_slug";
    public static final String SCORE_UPDATED = "score_updated";
    public static final String USER_FOLLOWED = "user_followed";
    public static final String USER_UNFOLLOWED = "user_unfollowed";
    public static final String PROPERTY_REG_ID = "gcm_id";
    public static final String SHOW_FOLLOW_NOTIFICATION = "follow_notification";
    public static final String SHOW_NEW_STREAM_NOTIFICATION = "new_stream_notification";
    public static final String USER_ONBOARDING = "user_onboarding";
    public static final String USER_STRAEMS_COUNT = "user_streams_count";
    public static final String AWS_FOLDER = "aws_recording_folder";
    public static final String AWS_ACCESS_KEY = "aws_access_key";
    public static final String AWS_SECRET_KEY = "aws_secret";
    public static final String AWS_BUCKET_NAME = "aws_bucket";
    public static final String AWS_REGION = "aws_region";
    public static Context context;
    public static ArrayList<MyHttp> httpQueue = new ArrayList<>();
    public static int loggedInUserId;
    public static String loggedInUserToken;
    public static Location currentLocation;
    private static int loggedInUserScore;
    public static Typeface racho;

    @Override
    public void onCreate() {
        super.onCreate();
        MonkeyLive.context = getApplicationContext();
        updateGlobalData();
        initFonts();
        Log.i("AppInfo", loggedInUserId + " " + loggedInUserToken);
        currentLocation = LocationServices.getInstance().getCurrentLocation();
        Ion.getDefault(this).configure().setLogging("MyHttp",Log.VERBOSE);
    }

    private void initFonts() {
        racho = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Rancho-Regular.ttf");
    }

    public static void updateGlobalData() {
        loggedInUserId = getIntPreference(USER_ID, 0);
        loggedInUserToken = getStringPreference(API_TOKEN, "");
        loggedInUserScore = getIntPreference(USER_SCORE, 1);
    }

    public static void setStringPreferenceData(String key, String value){
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPreference(String key, String defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }
    public static void setIntPreferenceData(String key, int value){
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static void setBooleanPreferenceData(String key, boolean value){
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanPreference(String key, boolean defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }
    public static int getIntPreference(String key, int defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }
    public static void setLongPreferenceData(String key, long value){
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLongPreference(String key, long defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

    public static void addRequestToQueue(MyHttp httpRequest){
        httpQueue.add(httpRequest);
    }
    public static void executeAllRequests(){
        try{
            for(MyHttp httpReq : httpQueue){
                httpReq.reSend();
            }
        } catch (Exception e){ //ignore exception

        }
        httpQueue.clear();
    }

    public static void restart() {
        Intent mStartActivity = new Intent(context, LauncherActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static void clearSharedPreferences(){
        setIntPreferenceData(MonkeyLive.USER_ID, 0);
        setStringPreferenceData(MonkeyLive.PROPERTY_REG_ID, "");
        setBooleanPreferenceData(MonkeyLive.USER_ONBOARDING, true);
        Log.i("AppInfo", getIntPreference(USER_ID, 0) + "");
    }
    public static String timeElapsedAsRelativeTime(String dateTime){

        Calendar cal = Calendar.getInstance();
        /**
         * Set the format in SimpleDateFormat
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        if(dateTime != null && !dateTime.isEmpty()) {
            try {
                /**
                 * Parse the passed date in the given format and set it to the calander
                 */
                Date parse = simpleDateFormat.parse(dateTime);
                cal.setTime(parse);
            } catch (ParseException e) {
                /**
                 * On Error Return an empty string
                 */
                e.printStackTrace();
                return "";
            }

        }

        /**
         * Return a formated string
         */
        return DateUtils.getRelativeTimeSpanString(cal.getTimeInMillis(), Calendar.getInstance().getTimeInMillis(), 1000, DateUtils.FORMAT_ABBREV_ALL).toString();
    }
}
