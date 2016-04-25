package atv.com.project.popkontv.Gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import atv.com.project.popkontv.Activity.UserDetailsListActivity;
import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.R;
import sdk.Kickflip;

//import org.videolan.vlc.gui.video.VideoPlayerActivity;

/**
 * Created by arjun on 8/4/15.
 */
public class GcmIntentService extends GcmListenerService {

    private static final String TAG = "PlayServices";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private Uri alarmSound;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(data.getString("code") == null) return;
        Log.i("GCM", data.getString("code"));
        String code = data.getString("code");
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(code.equalsIgnoreCase("2001") && Popkon.getBooleanPreference(Popkon.SHOW_NEW_STREAM_NOTIFICATION, true)){
            showNewStreamNotification(data);
        } else if(code.equalsIgnoreCase("2002")) {
            int count = Popkon.getIntPreference(Popkon.USER_FOLLOWERS_COUNT, 0);
            Popkon.setIntPreferenceData(Popkon.USER_FOLLOWERS_COUNT, count + 1);
            if(Popkon.getBooleanPreference(Popkon.SHOW_FOLLOW_NOTIFICATION, true)) {
                showNewFollowerNotification(data);
            }
        } else if(code.equalsIgnoreCase("2003")){
            int count = Popkon.getIntPreference(Popkon.USER_FOLLOWERS_COUNT, 0);
            Popkon.setIntPreferenceData(Popkon.USER_FOLLOWERS_COUNT, count - 1);
        }
    }

    private void showNewFollowerNotification(Bundle data) {
        String followerId = data.getString("id");
        String followerHandle = data.getString("handle");
        String followerName = data.getString("name");
        String followerImage = data.getString("images");

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent showNewFollowerIntent = new Intent(getApplicationContext(), UserDetailsListActivity.class);
        showNewFollowerIntent.putExtra("followerId", followerId);
        showNewFollowerIntent.putExtra("fragmentToShow", UserDetailsListActivity.FROM_GCM_NOTIFICATION);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 98765,
                showNewFollowerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Viewora New Follower")
                        .setVibrate(new long[] {1000})
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setContentText(followerName + " started following you");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void showNewStreamNotification(Bundle data) {
        String streamState = data.getString("state");
        if(streamState != null &&streamState.equalsIgnoreCase("scheduled")){
            return;
        }
        String publisherName = data.getString("name");
        String streamUrl = data.getString("url");
        String streamSlug = data.getString("slug");
        String streamMessage = data.getString("stream_message");
        String streamType = data.getString("video_type");
        String message = "";
        PendingIntent contentIntent = null;
        if(streamType != null && streamType.equalsIgnoreCase("uploaded")){
            message = publisherName + " uploaded a video";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(streamUrl));
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 98765,
                    browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else if(streamType != null && streamType.equalsIgnoreCase("stream")) {
            message = publisherName + " started a new stream";
            contentIntent = Kickflip.startMediaPlayerActivity(getApplicationContext(), streamUrl, streamSlug);
        }

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


//        Intent playStreamIntent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
//        playStreamIntent.setAction(VideoPlayerActivity.PLAY_FROM_RTMP);
//        playStreamIntent.putExtra("itemLocation", streamUrl);
//        playStreamIntent.putExtra("itemTitle", "");
//        playStreamIntent.putExtra("dontParse", false);
//        playStreamIntent.putExtra("fromStart", true);
//        playStreamIntent.putExtra("itemPosition", -1);
//        playStreamIntent.putExtra("streamSlug", streamSlug);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Viewora New Stream")
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentText(message+ " " + streamMessage);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}