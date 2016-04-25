package atv.com.project.popkontv.Fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Pojo.MyResponse;
import atv.com.project.popkontv.Pojo.UserProfileDetails;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.lib.StreamDrawable;


/**
 * Created by Administrator on 6/18/15.
 */
public class UserProfileDialogFragment extends DialogFragment {
    private View rootView;
    private TextView mUserNameTv;
//    private TextView mHandleTv;
    private TextView mScoreTv;
    private TextView mFollowersCountTv;
    private TextView mFollowingCountTv;
    private TextView mFollowButtonTv;
    private ImageView mUserImageIv;
    private Integer userId;
    private UserProfileDetails currentUser;
    private TextView mFollowerTextTv;
    private TextView mFollowingTextTv;
    private Integer userIdPosition;
    private ProgressBar mUserProfilePb;
//    private TextView mErrorDataTv;
//    private LinearLayout mUserProfileLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_user_profile, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        userId = getArguments().getInt("userId", 0);
        userIdPosition = getArguments().getInt("userIdPosition", 0);
        initialiseView();
        fetchUserData();
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    private void fetchUserData() {
        new MyHttp<UserProfileDetails>(getActivity(), EndPoints.viewProfile(userId), MyHttp.GET, UserProfileDetails.class)
                .defaultHeaders()
                .send(new MyCallback<UserProfileDetails>() {
                    @Override
                    public void success(UserProfileDetails data) {
                        currentUser = data;
                        if (isAdded()) {
                            updateViews();
                        }
                    }

                    @Override
                    public void failure(String msg) {
//                        mErrorDataTv.setVisibility(View.VISIBLE);
                        getDialog().dismiss();
                    }

                    @Override
                    public void onBefore() {
                        mUserProfilePb.setVisibility(View.VISIBLE);
//                        mUserProfileLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        mUserProfilePb.setVisibility(View.GONE);
//                        mUserProfileLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void updateViews() {
        mUserNameTv.setText(currentUser.message.userName);
//        mHandleTv.setText(currentUser.message.userHandle);
        mScoreTv.setText(currentUser.message.userScore + "");
        mFollowersCountTv.setText(currentUser.message.userFollowersCount + "");
        mFollowingCountTv.setText(currentUser.message.userFollowingCount + "");
        MyHttp.fetchBitmap(getActivity(), currentUser.message.userImage.replace("_normal", "_bigger"), new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable streamDrawable = new StreamDrawable(data);
                mUserImageIv.setImageDrawable(streamDrawable);
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
        if(currentUser.message.userId == Popkon.loggedInUserId){
            mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.filled_primary_border));
            mFollowButtonTv.setText("ME");
            mFollowButtonTv.setTextColor(getResources().getColor(R.color.white));
        } else {
            bindEvents();
            if (currentUser.message.followedByMe) {
                mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.filled_primary_border));
                mFollowButtonTv.setText("FOLLOWING");
                mFollowButtonTv.setTextColor(getResources().getColor(R.color.white));
            } else {
                mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.transparent_primary_border));
                mFollowButtonTv.setText("FOLLOW");
                mFollowButtonTv.setTextColor(getResources().getColor(R.color.new_primary_color));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if(getDialog() == null){
            return;
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width;
        if(Build.VERSION.SDK_INT > 12) {
            display.getSize(size);
            width = size.x;
        }else{
            //noinspection deprecation
            width = display.getWidth();
        }
        int dialogWidth;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            dialogWidth = width - 500;
        } else {
            dialogWidth = width - 150;
        }
        int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        Window currentWindow =  getDialog().getWindow();
        currentWindow.setLayout(dialogWidth, dialogHeight);
    }

    private void bindEvents() {
        mFollowButtonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.message.followedByMe){
                    currentUser.unFollow(getActivity(), currentUser.message.userId, userIdPosition, new MyCallback<MyResponse>() {
                        @Override
                        public void success(MyResponse data) {

                        }

                        @Override
                        public void failure(String msg) {
                            currentUser.message.followedByMe = true;
                            mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.filled_primary_border));
                            mFollowButtonTv.setText("FOLLOWING");
                            mFollowButtonTv.setTextColor(getResources().getColor(R.color.white));
                        }

                        @Override
                        public void onBefore() {
                            currentUser.message.followedByMe = false;
                            mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.transparent_primary_border));
                            mFollowButtonTv.setText("FOLLOW");
                            mFollowButtonTv.setTextColor(getResources().getColor(R.color.new_primary_color));
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
                } else {
                    currentUser.follow(getActivity(), currentUser.message.userId, userIdPosition, new MyCallback<MyResponse>() {
                        @Override
                        public void success(MyResponse data) {

                        }

                        @Override
                        public void failure(String msg) {
                            currentUser.message.followedByMe = false;
                            mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.transparent_primary_border));
                            mFollowButtonTv.setText("FOLLOW");
                            mFollowButtonTv.setTextColor(getResources().getColor(R.color.new_primary_color));
                        }

                        @Override
                        public void onBefore() {
                            currentUser.message.followedByMe = true;
                            mFollowButtonTv.setBackground(getResources().getDrawable(R.drawable.filled_primary_border));
                            mFollowButtonTv.setText("FOLLOWING");
                            mFollowButtonTv.setTextColor(getResources().getColor(R.color.white));
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
                }
            }
        });
    }

    private void initialiseView() {
        mUserNameTv = (TextView) rootView.findViewById(R.id.userNameTv);
        mUserNameTv.setTypeface(Popkon.racho);
//        mHandleTv = (TextView) rootView.findViewById(R.id.userHandleTv);
//        mHandleTv.setTypeface(Castasy.racho);
        mScoreTv = (TextView) rootView.findViewById(R.id.userScoreTv);
        mScoreTv.setTypeface(Popkon.racho);
        mFollowersCountTv = (TextView) rootView.findViewById(R.id.followersCountTv);
        mFollowersCountTv.setTypeface(Popkon.racho);
        mFollowingCountTv = (TextView) rootView.findViewById(R.id.followingCountTv);
        mFollowingCountTv.setTypeface(Popkon.racho);
        mFollowButtonTv = (TextView) rootView.findViewById(R.id.followUserButtonTv);
        mFollowButtonTv.setTypeface(Popkon.racho);
        mFollowerTextTv = (TextView) rootView.findViewById(R.id.followersPhtv);
        mFollowerTextTv.setTypeface(Popkon.racho);
        mFollowingTextTv = (TextView) rootView.findViewById(R.id.followingPhtv);
        mFollowingTextTv.setTypeface(Popkon.racho);
//        mErrorDataTv = (TextView) rootView.findViewById(R.id.errorDataTv);
        mUserImageIv = (ImageView) rootView.findViewById(R.id.userProfileIv);
        mUserProfilePb = (ProgressBar) rootView.findViewById(R.id.user_profile_dialog_progress_bar);
//        mUserProfileLayout = (LinearLayout) rootView.findViewById(R.id.userProfileLayout);
    }
}
