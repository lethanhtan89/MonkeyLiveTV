package atv.com.project.monkeylivetv.Network;

import android.util.Log;

import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Pojo.SessionCreationResponse;
import atv.com.project.monkeylivetv.Pojo.TokenRefresh;

/**
 * Created by arjun on 5/8/15.
 */
public class Auth {
    private static final String TAG = "ApiAuth";
    private static boolean authInProgress = false;
//    private Context context = Castasy.context;
    public static int authRefreshCount = 0;

    public static void refreshApiToken() {
        if(!authInProgress) {
            authRefreshCount++;

            if(authRefreshCount > 3){
                MonkeyLive.clearSharedPreferences();
                MonkeyLive.restart();
            }
            TokenRefresh tokenRefresh = new TokenRefresh();
            tokenRefresh.uid = String.valueOf(MonkeyLive.getLongPreference(MonkeyLive.TWITTER_ID, 0l));
            tokenRefresh.accessToken = MonkeyLive.getStringPreference(MonkeyLive.API_TOKEN, "");
            new MyHttp(MonkeyLive.context, EndPoints.refreshToken, MyHttp.POST, SessionCreationResponse.class)
                    .defaultHeaders()
                    .addJson(tokenRefresh)
                    .send(new MyCallback<SessionCreationResponse>() {
                        @Override
                        public void success(SessionCreationResponse data) {
                            Log.i(TAG, "Auth success" + data.toString());
                            authRefreshCount = 0;
                            MonkeyLive.setStringPreferenceData(MonkeyLive.API_TOKEN, data.message.accessToken);
                            MonkeyLive.setIntPreferenceData(MonkeyLive.USER_ID, data.message.id);
                            MonkeyLive.updateGlobalData();
                            MonkeyLive.executeAllRequests();
                        }

                        @Override
                        public void failure(String msg) {
                            Log.i(TAG, "auth fails" + msg);
                            refreshApiToken();
                        }

                        @Override
                        public void onBefore() {
                            Auth.authInProgress = true;
                        }

                        @Override
                        public void onFinish() {
                            Auth.authInProgress = false;
                        }
                    });
        }
    }
}
