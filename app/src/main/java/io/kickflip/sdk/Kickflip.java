package io.kickflip.sdk;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import atv.com.project.popkontv.Activity.StreamingActivity;
import io.kickflip.sdk.activity.BroadcastActivity;
import io.kickflip.sdk.activity.MediaPlayerActivity;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.fragment.MediaPlayerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is a top-level manager class for all the fundamental SDK actions. Herer you can register
 * your Kickflip account credentials, start a live broadcast or play one back.
 * <p/>
 * <h2>Setup</h2>
 * Before use Kickflip must be setup with your Kickflip Client ID and Client Secret with
 * account dashboard.
 * <h2>Example Usage</h2>
 * <b>Starting a single live broadcast</b>
 * <p/>
 * <ol>
 * <li>(Optional) {@link #setSessionConfig(SessionConfig)}</li>
 * </ol>
 * The {@link BroadcastActivity} will present a standard camera UI with controls
 * for starting and stopping the broadcast. When the broadcast is stopped, BroadcastActivity will finish
 * <p/>
 * <br/>
 * <b>Customizing broadcast parameters</b>
 * <p/>
 * As noted above, you can optionally call {@link #setSessionConfig(SessionConfig)} before
 * Here's an example of how to build a {@link SessionConfig} with {@link SessionConfig.Builder}:
 * <p/>
 * <code>
 *   SessionConfig config = new SessionConfig.Builder(mRecordingOutputPath)
 *     <br/>&nbsp.withTitle(Util.getHumanDateString())
 *     <br/>&nbsp.withDescription("Example Description")
 *     <br/>&nbsp.withVideoResolution(1280, 720)
 *     <br/>&nbsp.withVideoBitrate(2 * 1000 * 1000)
 *     <br/>&nbsp.withAudioBitrate(192 * 1000)
 *     <br/>&nbsp.withAdaptiveStreaming(true)
 *     <br/>&nbsp.withVerticalVideoCorrection(true)
 *     <br/>&nbsp.withExtraInfo(extraDataMap)
 *     <br/>&nbsp.withPrivateVisibility(false)
 *     <br/>&nbsp.withLocation(true)
 *
 *     <br/>&nbsp.build();
 *    <br/>Kickflip.setSessionConfig(config);
 *
 * </code>
 * <br/>
 * Note that SessionConfig is initialized with sane defaults for a 720p broadcast. Every parameter is optional.
 */
public class Kickflip {
    public static final String TAG = "Kickflip";
    public static Context sContext;
    private static String sClientKey;
    private static String sClientSecret;

//    private static KickflipApiClient sKickflip;

    // Per-Stream settings
    private static SessionConfig sSessionConfig;          // Absolute path to root storage location

    /**
     * Register with Kickflip, creating a new user identity per app installation.
     *
     * @param context the host application's {@link Context}
     * @param key     your Kickflip Client Key
     * @param secret  your Kickflip Client Secret
     * @return a {@link io.kickflip.sdk.api.KickflipApiClient} used to perform actions on behalf of a
     * {@link io.kickflip.sdk.api.json.User}.
     */
//    public static KickflipApiClient setup(Context context, String key, String secret) {
//        return setup(context, key, secret, null);
//    }

    /**
     * Register with Kickflip, creating a new user identity per app installation.
     *
     */
//    public static KickflipApiClient setup(Context context, String key, String secret, KickflipCallback cb) {
//        sContext = context;
//        setApiCredentials(key, secret);
//        return getApiClient(context, cb);
//    }

    private static void setApiCredentials(String key, String secret) {
        sClientKey = key;
        sClientSecret = secret;
    }

    /**
     * Start {@link BroadcastActivity}. This Activity
     * facilitates control over a single live broadcast.
     * <p/>
     *  @param host     the host {@link Activity} initiating this action
     *                 broadcast events
     * @param s
     * @param checked
     * @param selectedCategoryId
     */
    public static void startBroadcastActivity(Activity host, String s, boolean checked, Integer selectedCategoryId) {
//        checkNotNull(listener, host.getString(R.string.error_no_broadcastlistener));
        if (sSessionConfig == null) {
            setupDefaultSessionConfig();
        }
//        checkNotNull(sClientKey);
//        checkNotNull(sClientSecret);
//        sBroadcastListener = listener;
        Intent broadcastIntent = new Intent(host, StreamingActivity.class);
        broadcastIntent.putExtra("tweetMsg", s);
        broadcastIntent.putExtra("doRecord", checked);
        broadcastIntent.putExtra("categoryId", selectedCategoryId);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        host.startActivity(broadcastIntent);
    }

//    public static void startGlassBroadcastActivity(Activity host, BroadcastListener listener) {
////        checkNotNull(listener, host.getString(R.string.error_no_broadcastlistener));
//        if (sSessionConfig == null) {
//            setupDefaultSessionConfig();
//        }
//        checkNotNull(sClientKey);
//        checkNotNull(sClientSecret);
//        sBroadcastListener = listener;
//        Log.i(TAG, "startGlassBA ready? " + readyToBroadcast());
//        Intent broadcastIntent = new Intent(host, GlassBroadcastActivity.class);
//        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        host.startActivity(broadcastIntent);
//    }

    /**
     * Start {@link MediaPlayerActivity}. This Activity
     * facilitates playing back a Kickflip broadcast.
     * <p/>
     *  @param host      the host {@link Activity} initiating this action
     * @param streamUrl a path of format https://kickflip.io/<stream_id> or https://xxx.xxx/xxx.m3u8
     * @param slug
     * @param newTask   Whether this Activity should be started as part of a new task. If so, when this Activity finishes
     */
    public static void startMediaPlayerActivity(AppCompatActivity host, String streamUrl, String slug, boolean newTask) {
        Intent playbackIntent = new Intent(host, MediaPlayerActivity.class);
        playbackIntent.putExtra("mediaUrl", streamUrl);
        playbackIntent.putExtra(MediaPlayerFragment.STREAM_SLUG, slug);
        if (newTask) {
            playbackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        host.startActivity(playbackIntent);
    }
    public static PendingIntent startMediaPlayerActivity(Context context, String streamUrl, String slug) {
        Intent playbackIntent = new Intent(context, MediaPlayerActivity.class);
        playbackIntent.putExtra("mediaUrl", streamUrl);
        playbackIntent.putExtra(MediaPlayerFragment.STREAM_SLUG, slug);
        return PendingIntent.getActivity(context, 98765,
                playbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Convenience method for attaching the current reverse geocoded device location to a given
     * {@link io.kickflip.sdk.api.json.Stream}
     *
     * @param context  the host application {@link Context}
     * @param stream   the {@link io.kickflip.sdk.api.json.Stream} to attach location to
     * @param eventBus an {@link com.google.common.eventbus.EventBus} to be notified of the complete action
     */
//    public static void addLocationToStream(final Context context, final Stream stream, final EventBus eventBus) {
//        DeviceLocation.getLastKnownLocation(context, false, new DeviceLocation.LocationResult() {
//            @Override
//            public void gotLocation(Location location) {
//                stream.setLatitude(location.getLatitude());
//                stream.setLongitude(location.getLongitude());
//
//                try {
//                    Geocoder geocoder = new Geocoder(context);
//                    Address address = geocoder.getFromLocation(location.getLatitude(),
//                            location.getLongitude(), 1).get(0);
//                    stream.setCity(address.getLocality());
//                    stream.setCountry(address.getCountryName());
//                    stream.setState(address.getAdminArea());
//                    if (eventBus != null) {
//                        eventBus.post(new StreamLocationAddedEvent());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }



    /**
     * Get the provided Kickflip Client Key
     *
     * @return the provided Kickflip Client Key
     */
    public static String getApiKey() {
        return sClientKey;
    }

    /**
     * Get the provided Kickflip Client Secret
     *
     * @return the provided Kickflip Client Secret
     */
    public static String getApiSecret() {
        return sClientSecret;
    }

    /**
     * Return the {@link SessionConfig} responsible for configuring this broadcast.
     *
     * @return the {@link SessionConfig} responsible for configuring this broadcast.
     * @hide
     */
    public static SessionConfig getSessionConfig() {
//        SessionConfig sessionConfig = new SessionConfig();
        return sSessionConfig;
    }

    /**
     * Clear the current SessionConfig, marking it as in use by a Broadcaster.
     * This is typically safe to do after constructing a Broadcaster, as it will
     * hold reference.
     *
     * @hide
     */
    public static void clearSessionConfig() {
        Log.i(TAG, "Clearing SessionConfig");
        sSessionConfig = null;
    }

    /**
     * Set the {@link SessionConfig} responsible for configuring this broadcast.
     *
     * @param config the {@link SessionConfig} responsible for configuring this broadcast.
     */
    public static void setSessionConfig(SessionConfig config) {
        sSessionConfig = config;
    }

    /**
     * Check whether credentials required for broadcast are provided
     *
     * @return true if credentials required for broadcast are provided. false otherwise
     */
    public static boolean readyToBroadcast() {
        return sClientKey != null && sClientSecret != null && sSessionConfig != null;
    }

    /**
     * Return whether the given Uri belongs to the kickflip.io authority.
     *
     * @param uri uri to test
     * @return true if the uri is of the kickflip.io authority.
     */
    public static boolean isKickflipUrl(Uri uri) {
//        return uri != null && uri.getAuthority().contains("kickflip.io");
        return true;
    }

    /**
     * Given a Kickflip.io url, return the stream id.
     * <p/>
     * e.g: https://kickflip.io/39df392c-4afe-4bf5-9583-acccd8212277/ returns
     * "39df392c-4afe-4bf5-9583-acccd8212277"
     *
     * @param uri the uri to test
     */
    public static String getStreamIdFromKickflipUrl(Uri uri) {
        if (uri == null) throw new IllegalArgumentException("uri cannot be null");
        return uri.getLastPathSegment().toString();
    }

    /**
     * Create a new instance of the KickflipApiClient if one hasn't
     * yet been created, or the provided API keys don't match
     * the existing client.
     *
     * @param context the context of the host application
     * @return
     */
//    public static KickflipApiClient getApiClient(Context context) {
//        return getApiClient(context, null);
//    }

    /**
     * Create a new instance of the KickflipApiClient if one hasn't
     * yet been created, or the provided API keys don't match
     * the existing client.
     *
     *                 corresponding to the provided API keys.
     * @return
     */
//    public static KickflipApiClient getApiClient(Context context, KickflipCallback callback) {
//        checkNotNull(sClientKey);
//        checkNotNull(sClientSecret);
//        if (sKickflip == null || !sKickflip.getConfig().getClientId().equals(sClientKey)) {
//            sKickflip = new KickflipApiClient(context, sClientKey, sClientSecret, callback);
//        } else if (callback != null) {
//            callback.onSuccess(sKickflip.getActiveUser());
//        }
//        return sKickflip;
//    }

    private static void setupDefaultSessionConfig() {
        Log.i(TAG, "Setting default SessonConfig");
        checkNotNull(sContext);
        String outputLocation = new File(sContext.getFilesDir(), "index.m3u8").getAbsolutePath();
        Kickflip.setSessionConfig(new SessionConfig.Builder(outputLocation)
                .withVideoBitrate(1024 * 1024)
                .withPrivateVisibility(false)
                .withLocation(true)
                .withVideoResolution(720, 480)
                .build());
    }

    /**
     * Returns whether the current device is running Android 4.4, KitKat, or newer
     *
     * KitKat is required for certain Kickflip features like Adaptive bitrate streaming
     */
    public static boolean isKitKat() {
//        return Build.VERSION.SDK_INT >= 19;
        return false;
    }

}
