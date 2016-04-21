package sdk.api;//package io.kickflip.sdk.api;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import com.google.api.client.http.HttpRequestFactory;
//import com.google.api.client.http.HttpResponse;
//
//import java.util.ArrayDeque;
//import java.util.Date;
//
///**
// * Manage the OAuth Client Credentials authentication
// * to be negotiated prior to any API Requests being performed
// *
// * @hide
// */
//public abstract class OAuthClient {
//    private static final String TAG = "OAuthClient";
//    private static final boolean VERBOSE = false;
//
//    // For SharedPreferences storage
//    private final String ACCESS_TOKEN_KEY = "AT";
//    private final String ACCESS_TOKEN_EXP_KEY = "EXP";
//    private final String CLIENT_ID = "CID";
//
//    private HttpRequestFactory mRequestFactory;         // RequestFactory cached for life of mOAuthAccessToken
//    private String mOAuthAccessToken;
//    private OAuthConfig mConfig;                        // Immutable OAuth Configuration
//    private SharedPreferences mStorage;
//    private Context mContext;                           // Application Context
//    private ArrayDeque<OAuthCallback> mCallbackQueue;   // Queued callbacks awaiting OAuth registration
//    private boolean mOauthInProgress;                   // Is an OAuth authentication flow in progress
//
//    public OAuthClient(Context context, OAuthConfig config) {
//        mConfig = config;
//        mContext = context;
//        mStorage = context.getSharedPreferences(mConfig.getCredentialStoreName(), Context.MODE_PRIVATE);
//        mOauthInProgress = false;
//        mCallbackQueue = new ArrayDeque<>();
//    }
//
//    public Context getContext() {
//        return mContext;
//    }
//
//    public OAuthConfig getConfig() {
//        return mConfig;
//    }
//
//    public SharedPreferences getStorage() {
//        return mStorage;
//    }
//
//    /**
//     * Force clear and re-acquire an OAuth Acess Token
//     */
//    protected void refreshAccessToken() {
//        refreshAccessToken(null);
//    }
//
//    /**
//     * Force clear and re-acquire an OAuth Acess Token
//     * cb is always called on a background thread
//     */
//    protected void refreshAccessToken(final OAuthCallback cb) {
//        clearAccessToken();
//        acquireAccessToken(cb);
//    }
//
//    /**
//     * Asynchronously attempt to jsonRequest an OAuth Access Token
//     */
//    protected void acquireAccessToken() {
//        acquireAccessToken(null);
//    }
//
//    /**
//     * Asynchronously attempt to acquire an OAuth Access Token
//     *
//     * @param cb called when AccessToken is acquired. Always called
//     *           on a background thread suitable for networking.
//     */
//    protected void acquireAccessToken(final OAuthCallback cb) {
//
//    }
//
//    protected HttpRequestFactory getRequestFactoryFromCachedCredentials() {
//        return getRequestFactoryFromAccessToken(mStorage.getString(ACCESS_TOKEN_KEY, null));
//    }
//
//    private HttpRequestFactory getRequestFactoryFromAccessToken(String accessToken) {
////        if (accessToken == null) {
////            throw new NullPointerException("getRequestFactoryFromAccessToken got null Access Token");
////        }
////        if (mRequestFactory == null || !accessToken.equals(mOAuthAccessToken)) {
////            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
////            NetHttpTransport mHttpTransport = new NetHttpTransport.Builder().build();
////            mRequestFactory = mHttpTransport.createRequestFactory(credential);
////            mOAuthAccessToken = accessToken;
////        }
//        return mRequestFactory;
//    }
//
//    protected boolean isAccessTokenCached() {
//        // An unexpired Access Token is stored along with a Client ID that matches what's currently provided via mConfig
//        long now = new Date().getTime();
//        long tokenExpiryTime = getStorage().getLong(ACCESS_TOKEN_EXP_KEY, 0);
//        boolean validCredentialsStored = (mStorage.contains(ACCESS_TOKEN_KEY) &&
//                mStorage.getString(CLIENT_ID, "").equals(mConfig.getClientId())) &&
//                now < tokenExpiryTime;
//
//        if (!validCredentialsStored)
//            clearAccessToken();
//
//        return validCredentialsStored;
//    }
//
////    protected void storeAccessToken(TokenResponse tokenResponse) {
////        Calendar cal = Calendar.getInstance();
////        cal.add(Calendar.SECOND, (int) (tokenResponse.getExpiresInSeconds() - 60));
////        long tokenExpirtyTime = cal.getTimeInMillis();
////
////        getContext().getSharedPreferences(mConfig.getCredentialStoreName(), mContext.MODE_PRIVATE).edit()
////                .putString(ACCESS_TOKEN_KEY, tokenResponse.getAccessToken())
////                .putLong(ACCESS_TOKEN_EXP_KEY, tokenExpirtyTime)
////                .putString(CLIENT_ID, mConfig.getClientId())
////                .apply();
////    }
//
//    protected void clearAccessToken() {
//        getContext().getSharedPreferences(mConfig.getCredentialStoreName(), mContext.MODE_PRIVATE).edit()
//                .clear()
//                .apply();
//    }
//
//    protected boolean isSuccessResponse(HttpResponse response) {
//        if (VERBOSE) Log.i(TAG, "Response status code: " + response.getStatusCode());
//        return response.getStatusCode() == 200;
//    }
//
//    /**
//     * Execute queued callbacks once valid OAuth
//     * credentials are acquired.
//     */
//    protected void executeQueuedCallbacks() {
//        if (VERBOSE)
//            Log.i(TAG, String.format("Executing %d queued callbacks", mCallbackQueue.size()));
//        for (OAuthCallback cb : mCallbackQueue) {
//            cb.onSuccess(getRequestFactoryFromCachedCredentials());
//        }
//    }
//
//    private void postExceptionToCallback(final OAuthCallback cb, final Exception e) {
//        if (cb != null) {
//            cb.onFailure(new OAuthException(e.getMessage()));
//        }
//    }
//
//    public class OAuthException extends Exception {
//        public OAuthException(String detail) {
//            super(detail);
//        }
//    }
//
//}
