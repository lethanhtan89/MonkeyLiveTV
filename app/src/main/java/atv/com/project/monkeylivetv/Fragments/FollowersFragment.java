package atv.com.project.monkeylivetv.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import atv.com.project.monkeylivetv.Adapters.UserViewAdapter;
import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Pojo.UserDetails;
import atv.com.project.monkeylivetv.Pojo.UserProfileDetails;
import atv.com.project.monkeylivetv.R;

/**
 * Created by Administrator on 5/19/15.
 */
public class FollowersFragment extends Fragment {
    private View rootView;
    private ListView followersListView;
    public UserViewAdapter followersAdapter;
    public ArrayList<UserDetails.Message> followersList;
    private ProgressBar progressBar;
    private LinearLayout contentsLayout;
//    private TextView headerView;
    private TextView dataEmptyMessage;
//    private ImageView backButton;
    private BroadcastReceiver followUnFollowReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_followers, container, false);
//        ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
        initialiseViews();
        bindEvents();
        registerFollowStateChangeReceiver();
        if(followersList == null){
            followersList = new ArrayList<>();
            followersAdapter = new UserViewAdapter(getActivity(), followersList);
            followersListView.setAdapter(followersAdapter);
            fetchFollowers();
        } else {
            followersAdapter = new UserViewAdapter(getActivity(), followersList);
            followersListView.setAdapter(followersAdapter);
        }
        if(getArguments() != null) {
            String followerId = getArguments().getString("followerId", "");
            if(!followerId.equals("")){
                showFollowerProfile(followerId);
            }
        }
        return rootView;
    }

    private void registerFollowStateChangeReceiver() {
        followUnFollowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer viewPositionToUpdate = intent.getIntExtra("followStatusChangedPosition", -1);
                if(followersList.get(viewPositionToUpdate).followed_by_current_user){
                    followersList.get(viewPositionToUpdate).followed_by_current_user = false;
                } else {
                    followersList.get(viewPositionToUpdate).followed_by_current_user = true;
                }
                int start = followersListView.getFirstVisiblePosition();
                for(int i=start, j=followersListView.getLastVisiblePosition();i<=j;i++)
                    if(viewPositionToUpdate == i){
                        View view = followersListView.getChildAt(i-start);
                        followersListView.getAdapter().getView(i, view, followersListView);
                        break;
                    }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(followUnFollowReceiver,
                new IntentFilter(UserProfileDetails.USER_FOLLOW_STATUS_CHANGE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(followUnFollowReceiver);
    }

    private void showFollowerProfile(String followerId) {
        UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", Integer.parseInt(followerId));
        userProfileDialogFragment.setArguments(bundle);
        userProfileDialogFragment.show(getActivity().getSupportFragmentManager(), "UserProfileView");
    }

    private void bindEvents() {
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });

        followersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", followersList.get(position).id);
                bundle.putInt("userIdPosition", position);
                userProfileDialogFragment.setArguments(bundle);
                userProfileDialogFragment.show(getActivity().getSupportFragmentManager(), "UserProfileView");
            }
        });
    }

    private void fetchFollowers() {
        new MyHttp<UserDetails>(getActivity(), EndPoints.getFollowers, MyHttp.GET, UserDetails.class)
                .defaultHeaders()
                .send(new MyCallback<UserDetails>() {
                    @Override
                    public void success(UserDetails data) {
                        if(data.message.size() == 0){
                            dataEmptyMessage.setVisibility(View.VISIBLE);
                            followersListView.setVisibility(View.GONE);
                        }
                        followersList.addAll(data.message);
                        followersAdapter.notifyDataSetChanged();
//                        followersAdapter = new UserViewAdapter(getActivity(), data.message);
//                        followersListView.setAdapter(followersAdapter);
//                        headerView.setText("Followers (" + followersAdapter.getCount() + ")");
                    }

                    @Override
                    public void failure(String msg) {
                        if (getActivity() != null) {
                            Snackbar.make(rootView, "Could not contact server", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchFollowers();
                                        }
                                    })
                                    .setActionTextColor(getActivity().getResources().getColor(R.color.primarybutton))
                                    .show();
                        }
                    }

                    @Override
                    public void onBefore() {
                        progressBar.setVisibility(View.VISIBLE);
                        contentsLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        progressBar.setVisibility(View.GONE);
                        contentsLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initialiseViews() {
        followersListView = (ListView) rootView.findViewById(R.id.followersList);
//        headerView = (TextView) rootView.findViewById(R.id.followersCount);
//        headerView.setTypeface(Castasy.racho);
        progressBar = (ProgressBar) rootView.findViewById(R.id.followerProgress);
        contentsLayout = (LinearLayout) rootView.findViewById(R.id.followerListContents);
        dataEmptyMessage = (TextView) rootView.findViewById(R.id.dataEmptyMessage);
        dataEmptyMessage.setTypeface(MonkeyLive.racho);
//        backButton = (ImageView) rootView.findViewById(R.id.actionBarBackButton);
    }
}
