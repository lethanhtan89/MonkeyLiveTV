package atv.com.project.monkeylivetv.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import atv.com.project.monkeylivetv.Activity.MainActivity;
import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Pojo.AwsDetails;
import atv.com.project.monkeylivetv.Pojo.LoginDetails;
import atv.com.project.monkeylivetv.Pojo.SessionCreationResponse;
import atv.com.project.monkeylivetv.Pojo.TwitterCredential;
import atv.com.project.monkeylivetv.Pojo.User;
import atv.com.project.monkeylivetv.R;
import atv.com.project.monkeylivetv.twitter.MyTwitterApiClient;

/**
 * Created by arjun on 5/16/15.
 */
public class LoginFragment extends Fragment {
    private View rootView;
    private static final String TWITTER_KEY = "OQczffwVTLCJIsuTZ4U7rXcJP";
    private static final String TWITTER_SECRET = "GnwwAFn6tIEjmhQnhmEiksEdaIPcY1Zy38B021FAAiEgzvTAPC";

    private static final String FACEBOOK_KEY = "";
    private static final String FACEBOOK_SERECT = "";
    private TwitterLoginButton twloginButton;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;
    private ProgressBar progressBar;
    private LinearLayout introLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        initialiseViews();
     /*   // Login using Faacebook API
        FacebookSdk.sdkInitialize(getActivity());
        CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.GONE);
                Log.i("succes", loginResult.toString());
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

*/
        //Login using Twiiter API
        twloginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
//        bindEvents();

        twloginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                progressBar.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.GONE);
                Log.i("succes", result.toString());
                final TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                saveDetails(session, authToken);
                MyTwitterApiClient userClient = new MyTwitterApiClient(session);
                userClient.getUsersService().show(MonkeyLive.getLongPreference(MonkeyLive.TWITTER_ID, 0l), null, true, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        Log.i("user", userResult.toString());
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_NAME, userResult.data.name);
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_LOCATION, userResult.data.location.equals("") ? "Getting location.." : userResult.data.location);
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_PICTURE, userResult.data.profileImageUrl);
                        sendDetailsToServer(session, userResult);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.i("user", e.getMessage());
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("failure", exception.toString());
                Toast.makeText(getActivity(), "Login Failed. Please Try Again", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    private void bindEvents() {
        twloginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                progressBar.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.GONE);
                Log.i("succes", result.toString());
                final TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                saveDetails(session, authToken);
                MyTwitterApiClient userClient = new MyTwitterApiClient(session);
                userClient.getUsersService().show(MonkeyLive.getLongPreference(MonkeyLive.TWITTER_ID, 0l), null, true, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        Log.i("user", userResult.toString());
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_NAME, userResult.data.name);
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_LOCATION, userResult.data.location.equals("") ? "Getting location.." : userResult.data.location);
                        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_USER_PICTURE, userResult.data.profileImageUrl);
                        sendDetailsToServer(session, userResult);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.i("user", e.getMessage());
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("failure", exception.toString());
                Toast.makeText(getActivity(), "Login Failed. Please Try Again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initialiseViews() {
        introLayout = (LinearLayout) rootView.findViewById(R.id.introLayout);

        progressBar = (ProgressBar) rootView.findViewById(R.id.loginProgress);
    }
    private void sendDetailsToServer(final TwitterSession session, Result<com.twitter.sdk.android.core.models.User> userResult){
//        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.user = new User();
        loginDetails.user.email = userResult.data.email;//todo:check for null
        loginDetails.user.name = userResult.data.name;
        loginDetails.user.username = userResult.data.screenName;
        loginDetails.user.image = userResult.data.profileImageUrl;
        if(MonkeyLive.currentLocation != null) {
            loginDetails.user.latitude = MonkeyLive.currentLocation.getLatitude();
            loginDetails.user.longitude = MonkeyLive.currentLocation.getLongitude();
        }else {
            loginDetails.user.latitude = 0.0;
            loginDetails.user.longitude = 0.0;
        }

        loginDetails.twitterCredential = new TwitterCredential();
        loginDetails.twitterCredential.access_token = MonkeyLive.getStringPreference(MonkeyLive.TWITTER_TOKEN, "");
        loginDetails.twitterCredential.reference = String.valueOf(MonkeyLive.getLongPreference(MonkeyLive.TWITTER_ID, 0l));
        loginDetails.twitterCredential.secret = MonkeyLive.getStringPreference(MonkeyLive.TWITTER_SECRET, "");
        MyHttp<SessionCreationResponse> requestHttp = new MyHttp(getActivity(), EndPoints.baseSessionUrl, MyHttp.POST, SessionCreationResponse.class)
                .defaultHeaders()
                .addJson(loginDetails)
                .send(new MyCallback<SessionCreationResponse>() {
                    @Override
                    public void success(SessionCreationResponse data) {
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_ID, data.message.id);
                        MonkeyLive.setStringPreferenceData(MonkeyLive.API_TOKEN, data.message.accessToken);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_SCORE, data.message.score);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_FOLLOWERS_COUNT, data.message.numberOfFollowers);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_FOLLOWING_COUNT, data.message.numberOfFollowing);
                        MonkeyLive.setIntPreferenceData(MonkeyLive.USER_STRAEMS_COUNT, data.message.numberOfStreams);
                        MonkeyLive.updateGlobalData();
                        getAwsDetails();
                    }

                    @Override
                    public void failure(String msg) {
                        Log.i("failureapi", msg);
                        Toast.makeText(getActivity(), "Login Failed. Please Try Again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        introLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    private void getAwsDetails() {
        AwsDetails.getAwsDetails(getActivity(), new MyCallback<AwsDetails>() {
            @Override
            public void success(AwsDetails data) {
                MonkeyLive.setStringPreferenceData(MonkeyLive.AWS_ACCESS_KEY, data.message.awsAccessKey);
                MonkeyLive.setStringPreferenceData(MonkeyLive.AWS_SECRET_KEY, data.message.awsSecretKey);
                MonkeyLive.setStringPreferenceData(MonkeyLive.AWS_BUCKET_NAME, data.message.awsBucketName);
                MonkeyLive.setStringPreferenceData(MonkeyLive.AWS_FOLDER, data.message.awsRecordingFolder);
                MonkeyLive.setStringPreferenceData(MonkeyLive.AWS_REGION, data.message.awsRegion);
                //getFragmentManager().beginTransaction()
                //      .replace(R.id.launcherContainer, new MainFragment())
                //.commit();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(String msg) {
                Log.i("failureapi", msg);
                Toast.makeText(getActivity(), "Login Failed. Please Try Again", Toast.LENGTH_LONG).show();
                MonkeyLive.clearSharedPreferences();
                progressBar.setVisibility(View.GONE);
                introLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBefore() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void saveDetails(TwitterSession session, TwitterAuthToken authToken) {
        String token = authToken.token;
        String secret = authToken.secret;

        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_TOKEN, token);
        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_SECRET, secret);
        MonkeyLive.setStringPreferenceData(MonkeyLive.TWITTER_HANDLE, session.getUserName());
        MonkeyLive.setLongPreferenceData(MonkeyLive.TWITTER_ID, session.getUserId());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twloginButton.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, requestCode, data);
    }
}

