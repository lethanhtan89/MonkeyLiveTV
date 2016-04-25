package atv.com.project.popkontv.Network;

import android.util.Log;

import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Pojo.SessionCreationResponse;
import atv.com.project.popkontv.Pojo.TokenRefresh;

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
                Popkon.clearSharedPreferences();
                Popkon.restart();
            }
            TokenRefresh tokenRefresh = new TokenRefresh();
            tokenRefresh.uid = String.valueOf(Popkon.getLongPreference(Popkon.TWITTER_ID, 0l));
            tokenRefresh.accessToken = Popkon.getStringPreference(Popkon.API_TOKEN, "");
            new MyHttp(Popkon.context, EndPoints.refreshToken, MyHttp.POST, SessionCreationResponse.class)
                    .defaultHeaders()
                    .addJson(tokenRefresh)
                    .send(new MyCallback<SessionCreationResponse>() {
                        @Override
                        public void success(SessionCreationResponse data) {
                            Log.i(TAG, "Auth success" + data.toString());
                            authRefreshCount = 0;
                            Popkon.setStringPreferenceData(Popkon.API_TOKEN, data.message.accessToken);
                            Popkon.setIntPreferenceData(Popkon.USER_ID, data.message.id);
                            Popkon.updateGlobalData();
                            Popkon.executeAllRequests();
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
