package atv.com.project.popkontv.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.ArrayList;

import atv.com.project.popkontv.Activity.UserProfileActivity;
import atv.com.project.popkontv.Adapters.StreamFeedAdapter;
import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.LoginDetails;
import atv.com.project.popkontv.Pojo.MyResponse;
import atv.com.project.popkontv.Pojo.StreamCategoryResponse;
import atv.com.project.popkontv.Pojo.StreamFeedDetails;
import atv.com.project.popkontv.Pojo.User;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.lib.FeedCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;

//import org.videolan.vlc.audio.AudioService;
//import org.videolan.vlc.audio.AudioServiceController;
//import org.videolan.vlc.gui.audio.AudioPlayer;
//import org.videolan.vlc.gui.video.VideoPlayerActivity;
//import org.videolan.vlc.util.VLCInstance;
//import org.videolan.vlc.gui.video.VideoPlayerActivity;

/**
 * Created by arjun on 5/16/15.
 */
public class MainFragment extends Fragment {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "249222288878";
    private View rootView;
    private TextView tweetText;
    private Button tweetButton;
    ActionBar actionBar;
    private RelativeLayout contentsLayout;
    private ProgressBar mainProgressBar;
    private Button scheduleButton;
    private ListView streamFeedList;
    private SwipeRefreshLayout streamFeedSwipe;
//    public StreamFeedAdapter streamFeedAdapter;
//    private AudioPlayer mAudioPlayer;
//    private AudioServiceController mAudioController;
    private CardArrayRecyclerViewAdapter mCardArrayAdapter;
//    private ArrayList<Card> cards;
    private ArrayList<StreamFeedDetails.Message> feedsList;
    private FloatingActionButton newStreamBt;
    private TextView noStreamTv;
    private GoogleCloudMessaging gcm;
    private String regId;
    private boolean onBoarding = true;
    private StreamFeedAdapter feedAdapter;
    private int pageNo = 1;
    private boolean requestInProgress = false;
    private boolean noMoreData = false;
    private ArrayList<StreamCategoryResponse.Message> categoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
  //      ((AppCompatActivity)getActivity()).getSupportActionBar().show();
   //     setUpActionBar();
//        registerScoreUpdateReceiver();
//        cards = new ArrayList<>();
        initialiseViews();
        onBoarding = Popkon.getBooleanPreference(Popkon.USER_ONBOARDING, true);
        if(onBoarding){
            Popkon.setBooleanPreferenceData(Popkon.USER_ONBOARDING, false);
            LayoutInflater onBoardingInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = onBoardingInflater.inflate(R.layout.newstream_user_show, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, newStreamBt.getId());
            layoutParams.addRule(RelativeLayout.LEFT_OF, newStreamBt.getId());
            contentsLayout.addView(view, layoutParams);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentsLayout.removeView(view);
                }
            });
        }
        if(feedsList == null){
            feedsList = new ArrayList<>();
            feedAdapter = new StreamFeedAdapter(getActivity(), feedsList);
            streamFeedList.setAdapter(feedAdapter);
            fetchFeeds();
        } else {
            feedAdapter = new StreamFeedAdapter(getActivity(), feedsList);
            streamFeedList.setAdapter(feedAdapter);
        }
        bindEvents();
//        try {
//            // Start LibVLC
//            VLCInstance.getLibVlcInstance();
//            mAudioPlayer = new AudioPlayer();
//            mAudioController = AudioServiceController.getInstance();
//        }catch (java.lang.UnsatisfiedLinkError libErr){
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage("Cant view streams on your phone");
//            builder.setTitle("Viewora");
//            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    getActivity().finish();
//                    return ;
//                }
//            });
//            builder.show();
//            return null;
//        }
//        catch (Exception ex){
//            Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();
//            Log.d("Exception","Some error occured.");
//            getActivity().finish();
//            return null;
//        }

//        Intent intent = new Intent(getActivity(), AudioService.class);
//        intent.setAction(AudioService.AUDIO_SERVICE);
//        getActivity().startService(intent);

        if(checkGooglePlayServices()){
            gcm = GoogleCloudMessaging.getInstance(getActivity());
            regId = getRegistrationId();

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i("Play services", "No valid Google Play Services APK found.");
        }

        if(categoriesList == null)
            fetchCategories();

        return rootView;
    }

    private void fetchCategories() {
        new MyHttp<StreamCategoryResponse>(getActivity(), EndPoints.streamCategories, MyHttp.GET, StreamCategoryResponse.class)
                .defaultHeaders()
                .send(new MyCallback<StreamCategoryResponse>() {
                    @Override
                    public void success(StreamCategoryResponse data) {
                        categoriesList = data.categories;
                    }

                    @Override
                    public void failure(String msg) {

                    }

                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
                    }
//                    regId = gcm.register(SENDER_ID);
                    InstanceID instanceID = InstanceID.getInstance(getActivity());
                    regId = instanceID.getToken(getString(R.string.gcmsenderid),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToServer();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;

            }

            @Override
            protected void onPreExecute() {
//                super.onPreExecute();
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToServer() {
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.user = new User();
        loginDetails.user.device_registration_id = regId;

        new MyHttp<MyResponse>(getActivity(), EndPoints.updateUser(Popkon.loggedInUserId), MyHttp.PUT, MyResponse.class)
                .defaultHeaders()
                .addJson(loginDetails)
                .send(new MyCallback<MyResponse>() {
                    @Override
                    public void success(MyResponse data) {

                    }

                    @Override
                    public void failure(String msg) {

                    }

                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    private void storeRegistrationId(){
        Popkon.setStringPreferenceData(Popkon.PROPERTY_REG_ID, regId);
    }
    private String getRegistrationId() {
        String registrationId = Popkon.getStringPreference(Popkon.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCM", "Registration not found.");
            return "";
        }
//        int registeredVersion = MyFunctions.getSharedPrefs(applicationContext, PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = MyFunctions.getAppVersion(applicationContext);
//        if (registeredVersion != currentVersion) {
//            Log.i(TAG, "App version changed.");
//            return "";
//        }
        return registrationId;
    }

    private boolean checkGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Play services error", "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }
    private ArrayList<Card> initCard() {

        //Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < feedsList.size(); i++) {
//            Card card = new Card(this.getActivity());
            FeedCard card = new FeedCard(getActivity());
            final StreamFeedDetails.Message currentFeed = feedsList.get(i);
            card.setStreamUserName(currentFeed.publisher.publisherName);
            card.setStreamUserHandle(currentFeed.publisher.publisherHandle);
            card.setStreamMessage(currentFeed.streamMessage);
            card.setStreamLocation(currentFeed.publisher.city);
            card.setStreamLikesCount(currentFeed.likesCount);
            card.setStreamCommentsCount(currentFeed.commentsCount);
            card.setStreamReTweetsCount(currentFeed.retweetsCount);
            card.setStreamType(currentFeed.liveSubscribers);
            card.setStreamUserImage(currentFeed.publisher.publisherImage);
            card.setStreamBackground(currentFeed.coverImage);
            card.setStreamStatus(currentFeed.isRecorded);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    if (currentFeed.state.equalsIgnoreCase("scheduled")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Scheduled Stream")
                                .setMessage("The stream is scheduled " + Popkon.timeElapsedAsRelativeTime(currentFeed.startTime))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    } else {
//                        VideoPlayerActivity.start(getActivity(), currentFeed.url, true, currentFeed.slug);
                    }
                }
            });
            card.setCardElevation(R.dimen.card_elevation);
            cards.add(card);
        }

        return cards;
    }


    private void registerScoreUpdateReceiver() {
        BroadcastReceiver scoreUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setUpActionBar();
                fetchFeeds();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(scoreUpdateReceiver, new IntentFilter(Popkon.SCORE_UPDATED));
    }

    private void fetchFeeds() {
        new MyHttp<StreamFeedDetails>(getActivity(), EndPoints.getAllStreams(pageNo), MyHttp.GET, StreamFeedDetails.class)
                .defaultHeaders()
                .send(new MyCallback<StreamFeedDetails>() {
                    @Override
                    public void success(StreamFeedDetails data) {
//                        streamFeedAdapter.clear();
                        if (data.message.size() == 0) {
                            noStreamTv.setVisibility(View.VISIBLE);
                        } else {
                            noStreamTv.setVisibility(View.GONE);
                        }
                        feedsList.addAll(data.message);
                        feedAdapter.notifyDataSetChanged();
//                        cards = initCard();
//                        updateAdapter(cards);
                        Log.i("Feed response", data.toString());
//                        streamFeedAdapter = new StreamFeedAdapter(Castasy.context, data.message);
//                        streamFeedList.setAdapter(streamFeedAdapter);
                    }

                    @Override
                    public void failure(String msg) {
                        if (getActivity() != null) {
                            Snackbar.make(rootView, "Could not contact server", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchFeeds();
                                        }
                                    })
                                    .setActionTextColor(getActivity().getResources().getColor(R.color.primarybutton))
                                    .show();
                        }
//                            Toast.makeText(getActivity().getApplicationContext(), "Cannot Load Streams", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBefore() {
                        streamFeedSwipe.setRefreshing(true);
                    }

                    @Override
                    public void onFinish() {
                        streamFeedSwipe.setRefreshing(false);
                    }
                });
    }

    private void fetchMoreFeeds() {
        new MyHttp<StreamFeedDetails>(getActivity(), EndPoints.getAllStreams(pageNo), MyHttp.GET, StreamFeedDetails.class)
                .defaultHeaders()
                .send(new MyCallback<StreamFeedDetails>() {
                    @Override
                    public void success(StreamFeedDetails data) {
                        if(data.message.size() == 0){
                            noMoreData = true;
                        }
                        feedsList.addAll(data.message);
                        feedAdapter.notifyDataSetChanged();
                        Log.i("Feed response", data.toString());
                    }

                    @Override
                    public void failure(String msg) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity().getApplicationContext(), "Cannot Load Streams", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBefore() {
                        requestInProgress = true;
                    }

                    @Override
                    public void onFinish() {
                        requestInProgress = false;
                    }
                });
    }

    private void setUpActionBar() {
        try {
            actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.show();
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_white));
            actionBar.setDisplayShowCustomEnabled(true);
    //        actionBar.setDisplayHomeAsUpEnabled();
            actionBar.setTitle("");
    //        actionBar.setLogo(getResources().getDrawable(R.drawable.applogo));
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.actionbar_view, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            ImageView profileLayout = (ImageView) view.findViewById(R.id.profileLayout);
            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getFragmentManager().beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                            .replace(R.id.launcherContainer, new UserProfileFragment())
//                            .addToBackStack("mainfragment")
//                            .commit();
                    Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(profileIntent);
                }
            });
//            TextView userNameView = (TextView) view.findViewById(R.id.toolBarUserName);
            TextView appName = (TextView) view.findViewById(R.id.appNameAb);
//            TextView userScoreView = (TextView) view.findViewById(R.id.toolBarUserScore);
            ImageView backButton = (ImageView) view.findViewById(R.id.actionbarBack);
//            userNameView.setTypeface(Castasy.racho);
//            userScoreView.setTypeface(Castasy.racho);
            appName.setTypeface(Popkon.racho);
            backButton.setVisibility(View.GONE);
//            userScoreView.setText("Score " + Viewora.getIntPreference(Viewora.USER_SCORE, 0));
//            userNameView.setText("@" + Viewora.getStringPreference(Viewora.TWITTER_HANDLE, ""));
            actionBar.setCustomView(view);
        }catch (Exception e){
            Log.i("error", e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mainProgressBar.setVisibility(View.GONE);
        contentsLayout.setVisibility(View.VISIBLE);

//        mAudioController.addAudioPlayer(mAudioPlayer);
//        AudioServiceController.getInstance().bindAudioService(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
//        mAudioController.removeAudioPlayer(mAudioPlayer);
//        AudioServiceController.getInstance().unbindAudioService(getActivity());
    }

    private void bindEvents() {
        streamFeedSwipe.setColorSchemeColors(getResources().getColor(R.color.new_primary_color), getResources().getColor(R.color.infobutton));
        streamFeedSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                noMoreData = false;
                feedsList.clear();
                feedAdapter.clear();
                fetchFeeds();
            }
        });

        newStreamBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartNewStreamDialogFragment startNewStreamDialogFragment = new StartNewStreamDialogFragment();
                startNewStreamDialogFragment.show(getFragmentManager(), "New stream");
                startNewStreamDialogFragment.populateCategories(categoriesList);
            }
        });

        streamFeedList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(noMoreData || requestInProgress || feedAdapter.isEmpty() || (firstVisibleItem + visibleItemCount < (totalItemCount - 5))) return;
                pageNo += 1;
                fetchMoreFeeds();
            }
        });
    }

    private void initialiseViews() {
//        tweetText = (TextView) rootView.findViewById(R.id.tweetMessage);
//        tweetButton = (Button) rootView.findViewById(R.id.streamBtn);
//        scheduleButton = (Button) rootView.findViewById(R.id.scheduleBtn);
        contentsLayout = (RelativeLayout) rootView.findViewById(R.id.contentsLayout);
        mainProgressBar = (ProgressBar) rootView.findViewById(R.id.mainProgress);

//        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        streamFeedList = (ListView) rootView.findViewById(R.id.streamFeedList);
//        streamFeedList.setHasFixedSize(false);
//        streamFeedList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        if (streamFeedList != null) {
//            streamFeedList.setAdapter(mCardArrayAdapter);
//        }
        streamFeedSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.streamFeedSwipe);
        newStreamBt = (FloatingActionButton) rootView.findViewById(R.id.newStream);
        noStreamTv = (TextView) rootView.findViewById(R.id.noStreamText);
        noStreamTv.setTypeface(Popkon.racho);

        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        streamFeedSwipe.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
    }

    private void updateAdapter(ArrayList<Card> cards) {
        if (cards != null) {
            mCardArrayAdapter.addAll(cards);
        }
    }
}
