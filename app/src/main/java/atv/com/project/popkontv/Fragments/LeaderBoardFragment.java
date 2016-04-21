package atv.com.project.popkontv.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import atv.com.project.popkontv.Adapters.LeaderBoardAdapter;
import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.UserDetails;
import atv.com.project.popkontv.Pojo.UserProfileDetails;
import atv.com.project.popkontv.R;

/**
 * Created by arjun on 5/19/15.
 */
public class LeaderBoardFragment extends Fragment {
    private View rootView;
    private ListView leaderBoardList;
    public LeaderBoardAdapter leaderBoardAdapter;
    public ArrayList<UserDetails.Message> leaders;
    private ProgressBar progressBar;
    private LinearLayout contentsLayout;
//    private ImageView backButton;
//    private TextView headerView;
    private BroadcastReceiver followUnFollowReceiver;
    private ActionBar actionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
//        ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
        initialiseViews();
//        setUpActionBar();
        bindEvents();
        registerReceiver();
        if(leaders == null){
            leaders = new ArrayList<>();
            leaderBoardAdapter = new LeaderBoardAdapter(getActivity(), leaders);
            leaderBoardList.setAdapter(leaderBoardAdapter);
            fetchLeadingUsers();
        }else {
            leaderBoardAdapter = new LeaderBoardAdapter(getActivity(), leaders);
            leaderBoardList.setAdapter(leaderBoardAdapter);
        }
        return rootView;
    }

    private void setUpActionBar() {
        actionBar  = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("LeaderBoard");
    }

    private void registerReceiver() {
        followUnFollowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer viewPositionToUpdate = intent.getIntExtra("followStatusChangedPosition", -1);
                if(leaders.get(viewPositionToUpdate).followed_by_current_user){
                    leaders.get(viewPositionToUpdate).followed_by_current_user = false;
                } else {
                    leaders.get(viewPositionToUpdate).followed_by_current_user = true;
                }
                int start = leaderBoardList.getFirstVisiblePosition();
                for(int i=start, j=leaderBoardList.getLastVisiblePosition();i<=j;i++)
                    if(viewPositionToUpdate == i){
                        View view = leaderBoardList.getChildAt(i-start);
                        leaderBoardList.getAdapter().getView(i, view, leaderBoardList);
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
        if(followUnFollowReceiver != null){
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(followUnFollowReceiver);
        }
    }

    private void bindEvents() {
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });

        leaderBoardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", leaders.get(position).id);
                bundle.putInt("userIdPosition", position);
                userProfileDialogFragment.setArguments(bundle);
                userProfileDialogFragment.show(getActivity().getSupportFragmentManager(), "UserProfileView");
            }
        });
    }
    private void fetchLeadingUsers() {
        new MyHttp<UserDetails>(getActivity(), EndPoints.getLeaderBoard, MyHttp.GET, UserDetails.class)
                .defaultHeaders()
                .send(new MyCallback<UserDetails>() {
                    @Override
                    public void success(UserDetails data) {
//                        leaders = data.message;
                        leaders.addAll(data.message);
                        leaderBoardAdapter.notifyDataSetChanged();
//                        leaderBoardAdapter = new LeaderBoardAdapter(getActivity(), data.message);
//                        leaderBoardList.setAdapter(leaderBoardAdapter);

                    }

                    @Override
                    public void failure(String msg) {
                        if (getActivity() != null) {
                            Snackbar.make(rootView, "Could not contact server", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchLeadingUsers();
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
        leaderBoardList = (ListView) rootView.findViewById(R.id.leaderBoardList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.leaderLoadProgress);
        contentsLayout = (LinearLayout) rootView.findViewById(R.id.listContents);
//        backButton = (ImageView) rootView.findViewById(R.id.actionBarBackButton);
//        headerView = (TextView) rootView.findViewById(R.id.leaderBoardHeaderView);
//        headerView.setTypeface(Castasy.racho);
    }
}
