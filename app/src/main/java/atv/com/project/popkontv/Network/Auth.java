package atv.com.project.popkontv.Network;

import android.util.Log;

import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Viewora;
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
                Viewora.clearSharedPreferences();
                Viewora.restart();
            }
            TokenRefresh tokenRefresh = new TokenRefresh();
            tokenRefresh.uid = String.valueOf(Viewora.getLongPreference(Viewora.TWITTER_ID, 0l));
            tokenRefresh.accessToken = Viewora.getStringPreference(Viewora.API_TOKEN, "");
            new MyHttp(Viewora.context, EndPoints.refreshToken, MyHttp.POST, SessionCreationResponse.class)
                    .defaultHeaders()
                    .addJson(tokenRefresh)
                    .send(new MyCallback<SessionCreationResponse>() {
                        @Override
                        public void success(SessionCreationResponse data) {
                            Log.i(TAG, "Auth success" + data.toString());
                            authRefreshCount = 0;
                            Viewora.setStringPreferenceData(Viewora.API_TOKEN, data.message.accessToken);
                            Viewora.setIntPreferenceData(Viewora.USER_ID, data.message.id);
                            Viewora.updateGlobalData();
                            Viewora.executeAllRequests();
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
