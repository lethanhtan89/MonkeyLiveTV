package atv.com.project.popkontv.Fragments;

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

import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.MainActivity;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.AwsDetails;
import atv.com.project.popkontv.Pojo.LoginDetails;
import atv.com.project.popkontv.Pojo.SessionCreationResponse;
import atv.com.project.popkontv.Pojo.TwitterCredential;
import atv.com.project.popkontv.Pojo.User;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.twitter.MyTwitterApiClient;

/**
 * Created by arjun on 5/16/15.
 */
public class LoginFragment extends Fragment {
    private View rootView;
    private static final String TWITTER_KEY = "OQczffwVTLCJIsuTZ4U7rXcJP";
    private static final String TWITTER_SECRET = "GnwwAFn6tIEjmhQnhmEiksEdaIPcY1Zy38B021FAAiEgzvTAPC";
    private TwitterLoginButton loginButton;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;
    private ProgressBar progressBar;
    private LinearLayout introLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        initialiseViews();
/*
        //Login facebook
        FacebookSdk.sdkInitialize(this.getActivity());
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

*/
        //Login Twiiter
        loginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
//        bindEvents();

        loginButton.setCallback(new Callback<TwitterSession>() {
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
                userClient.getUsersService().show(Popkon.getLongPreference(Popkon.TWITTER_ID, 0l), null, true, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        Log.i("user", userResult.toString());
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_NAME, userResult.data.name);
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_LOCATION, userResult.data.location.equals("") ? "Getting location.." : userResult.data.location);
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_PICTURE, userResult.data.profileImageUrl);
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
        loginButton.setCallback(new Callback<TwitterSession>() {
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
                userClient.getUsersService().show(Popkon.getLongPreference(Popkon.TWITTER_ID, 0l), null, true, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        Log.i("user", userResult.toString());
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_NAME, userResult.data.name);
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_LOCATION, userResult.data.location.equals("") ? "Getting location.." : userResult.data.location);
                        Popkon.setStringPreferenceData(Popkon.TWITTER_USER_PICTURE, userResult.data.profileImageUrl);
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
        if(Popkon.currentLocation != null) {
            loginDetails.user.latitude = Popkon.currentLocation.getLatitude();
            loginDetails.user.longitude = Popkon.currentLocation.getLongitude();
        }else {
            loginDetails.user.latitude = 0.0;
            loginDetails.user.longitude = 0.0;
        }

        loginDetails.twitterCredential = new TwitterCredential();
        loginDetails.twitterCredential.access_token = Popkon.getStringPreference(Popkon.TWITTER_TOKEN, "");
        loginDetails.twitterCredential.reference = String.valueOf(Popkon.getLongPreference(Popkon.TWITTER_ID, 0l));
        loginDetails.twitterCredential.secret = Popkon.getStringPreference(Popkon.TWITTER_SECRET, "");
        MyHttp<SessionCreationResponse> requestHttp = new MyHttp(getActivity(), EndPoints.baseSessionUrl, MyHttp.POST, SessionCreationResponse.class)
                .defaultHeaders()
                .addJson(loginDetails)
                .send(new MyCallback<SessionCreationResponse>() {
                    @Override
                    public void success(SessionCreationResponse data) {
                        Popkon.setIntPreferenceData(Popkon.USER_ID, data.message.id);
                        Popkon.setStringPreferenceData(Popkon.API_TOKEN, data.message.accessToken);
                        Popkon.setIntPreferenceData(Popkon.USER_SCORE, data.message.score);
                        Popkon.setIntPreferenceData(Popkon.USER_FOLLOWERS_COUNT, data.message.numberOfFollowers);
                        Popkon.setIntPreferenceData(Popkon.USER_FOLLOWING_COUNT, data.message.numberOfFollowing);
                        Popkon.setIntPreferenceData(Popkon.USER_STRAEMS_COUNT, data.message.numberOfStreams);
                        Popkon.updateGlobalData();
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
                Popkon.setStringPreferenceData(Popkon.AWS_ACCESS_KEY, data.message.awsAccessKey);
                Popkon.setStringPreferenceData(Popkon.AWS_SECRET_KEY, data.message.awsSecretKey);
                Popkon.setStringPreferenceData(Popkon.AWS_BUCKET_NAME, data.message.awsBucketName);
                Popkon.setStringPreferenceData(Popkon.AWS_FOLDER, data.message.awsRecordingFolder);
                Popkon.setStringPreferenceData(Popkon.AWS_REGION, data.message.awsRegion);
                getFragmentManager().beginTransaction()
                        .replace(R.id.launcherContainer, new MainFragment())
                        .commit();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(String msg) {
                Log.i("failureapi", msg);
                Toast.makeText(getActivity(), "Login Failed. Please Try Again", Toast.LENGTH_LONG).show();
                Popkon.clearSharedPreferences();
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

        Popkon.setStringPreferenceData(Popkon.TWITTER_TOKEN, token);
        Popkon.setStringPreferenceData(Popkon.TWITTER_SECRET, secret);
        Popkon.setStringPreferenceData(Popkon.TWITTER_HANDLE, session.getUserName());
        Popkon.setLongPreferenceData(Popkon.TWITTER_ID, session.getUserId());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}

