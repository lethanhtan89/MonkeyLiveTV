package atv.com.project.popkontv.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import atv.com.project.popkontv.Activity.UserDetailsListActivity;
import atv.com.project.popkontv.Application.Viewora;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.lib.StreamDrawable;

import it.gmariotti.cardslib.library.view.CardViewNative;


public class UserProfileFragment extends Fragment {

    private static final float GRADIENT_HEIGHT = 50;
    private ActionBar actionBar;
    private Button logoutButton;
    private TextView userName;
    private TextView following;
    private TextView followers;
    private TextView score;
    private View rootView;
    private TextView leaderBoard;
    private ImageView userImageView;
    private CardViewNative cardView;
    private TextView settingsTv;
    private TextView userHandle;
    private ImageView userCoverImageView;
//    private TextView streamsCountTv;
    private View followingShadow;
    private View followersShadow;
//    private View streamsCountShadow;
    private View leaderBoardShadow;
    private View settingsShadow;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
//        setUpActionBar();
//        ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
        initialiseViews();
        bindEvents();
        return rootView;
    }

    private void bindEvents() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Viewora.clearSharedPreferences();
                Viewora.restart();
            }
        });
//        leaderBoard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent leaderBoardIntent = new Intent(getActivity(), UserDetailsListActivity.class);
//                leaderBoardIntent.putExtra("fragmentToShow", UserDetailsListActivity.LEADER_BOARD);
//                startActivity(leaderBoardIntent);
//            }
//        });
        leaderBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        leaderBoardShadow.setBackground(null);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        leaderBoardShadow.setBackground(getResources().getDrawable(R.drawable.dropshadow));
                        Intent leaderBoardIntent = new Intent(getActivity(), UserDetailsListActivity.class);
                        leaderBoardIntent.putExtra("fragmentToShow", UserDetailsListActivity.LEADER_BOARD);
                        startActivity(leaderBoardIntent);
                        return true;
                }
                return false;
            }
        });
//        followers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent followersIntent = new Intent(getActivity(), UserDetailsListActivity.class);
//                followersIntent.putExtra("fragmentToShow", UserDetailsListActivity.FOLLOWERS);
//                startActivity(followersIntent);
//            }
//        });
        followers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        followersShadow.setBackground(null);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        followersShadow.setBackground(getResources().getDrawable(R.drawable.dropshadow));
                        Intent followersIntent = new Intent(getActivity(), UserDetailsListActivity.class);
                        followersIntent.putExtra("fragmentToShow", UserDetailsListActivity.FOLLOWERS);
                        startActivity(followersIntent);
                        return true;
                }
                return false;
            }
        });
//        following.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent followingIntent = new Intent(getActivity(), UserDetailsListActivity.class);
//                followingIntent.putExtra("fragmentToShow", UserDetailsListActivity.FOLLOWING);
//                startActivity(followingIntent);
//            }
//        });
        following.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        followingShadow.setBackground(null);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        followingShadow.setBackground(getResources().getDrawable(R.drawable.dropshadow));
                        Intent followingIntent = new Intent(getActivity(), UserDetailsListActivity.class);
                        followingIntent.putExtra("fragmentToShow", UserDetailsListActivity.FOLLOWING);
                        startActivity(followingIntent);
                        return true;
                }
        return false;
            }
        });
//        settingsTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent settingsIntent = new Intent(getActivity(), UserDetailsListActivity.class);
//                settingsIntent.putExtra("fragmentToShow", UserDetailsListActivity.SETTINGS);
//                startActivity(settingsIntent);
//            }
//        });
        settingsTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        settingsShadow.setBackground(null);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        settingsShadow.setBackground(getResources().getDrawable(R.drawable.dropshadow));
                        Intent settingsIntent = new Intent(getActivity(), UserDetailsListActivity.class);
                        settingsIntent.putExtra("fragmentToShow", UserDetailsListActivity.SETTINGS);
                        startActivity(settingsIntent);
                        return true;
                }
                return false;
            }
        });
    }
    public Bitmap addGradient(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap overlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);

        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,  h - GRADIENT_HEIGHT, 0, h, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, h - GRADIENT_HEIGHT, w, h, paint);

        return overlay;
    }
    private void initialiseViews() {
//        cardView = (CardViewNative) rootView.findViewById(R.id.card_largeimage);
//        cardView.setCardElevation(R.dimen.card_elevation);
        String imageUrl = (Viewora.getStringPreference(Viewora.TWITTER_USER_PICTURE, "")).replace("_normal", "");
        logoutButton = (Button) rootView.findViewById(R.id.logOut);
        logoutButton.setTypeface(Viewora.racho);

        userName = (TextView) rootView.findViewById(R.id.profileUserNameTv);
        userName.setTypeface(Viewora.racho);
        userName.setText(Viewora.getStringPreference(Viewora.TWITTER_USER_NAME, ""));

        userHandle = (TextView) rootView.findViewById(R.id.profileUserHandleTv);
        userHandle.setTypeface(Viewora.racho);
        userHandle.setText(Viewora.getStringPreference(Viewora.TWITTER_HANDLE, ""));

        userImageView = (ImageView) rootView.findViewById(R.id.profileUserIv);

        userCoverImageView = (ImageView) rootView.findViewById(R.id.profileUserCoverImage);

        score = (TextView) rootView.findViewById(R.id.profileUserScoreTv);
        score.setTypeface(Viewora.racho);
        score.setText("Streams(" + Viewora.getIntPreference(Viewora.USER_STRAEMS_COUNT, 0) + ") " +
                "Score(" + Viewora.getIntPreference(Viewora.USER_SCORE, 0) + ")");

        followers = (TextView) rootView.findViewById(R.id.profileUserFollowers);
        followers.setTypeface(Viewora.racho);
        followers.setText("Followers " + Viewora.getIntPreference(Viewora.USER_FOLLOWERS_COUNT, 0));

        following = (TextView) rootView.findViewById(R.id.profileUserFollowing);
        following.setTypeface(Viewora.racho);
        following.setText("Following " + Viewora.getIntPreference(Viewora.USER_FOLLOWING_COUNT, 0));

//        streamsCountTv = (TextView) rootView.findViewById(R.id.profileUserStreamsCount);
//        streamsCountTv.setTypeface(Castasy.racho);
//        streamsCountTv.setText("Streams " + Viewora.getIntPreference(Viewora.USER_STRAEMS_COUNT, 0));

        leaderBoard = (TextView) rootView.findViewById(R.id.profileUserLeaderBoard);
        leaderBoard.setTypeface(Viewora.racho);
        settingsTv = (TextView) rootView.findViewById(R.id.settings);
        settingsTv.setTypeface(Viewora.racho);

        MyHttp.fetchBitmap(getActivity(), imageUrl, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                Drawable streamDrawable = new BitmapDrawable(getActivity().getResources(), addGradient(data));
                StreamDrawable drawable = new StreamDrawable(data);
                userImageView.setBackground(drawable);
                userCoverImageView.setImageDrawable(streamDrawable);
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

        followingShadow = rootView.findViewById(R.id.following_card_shadow);
        followersShadow = rootView.findViewById(R.id.followers_card_shadow);
//        streamsCountShadow = rootView.findViewById(R.id.streams_count_card_shadow);
        leaderBoardShadow = rootView.findViewById(R.id.leader_board_card_shadow);
        settingsShadow = rootView.findViewById(R.id.settings_card_shadow);

    }

    private void setUpActionBar() {
        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
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
        profileLayout.setVisibility(View.GONE);
        TextView appName = (TextView) view.findViewById(R.id.appNameAb);
        appName.setTypeface(Viewora.racho);
        ImageView backButton = (ImageView) view.findViewById(R.id.actionbarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        actionBar.setCustomView(view);
    }

}
