package atv.com.project.monkeylivetv.Fragments;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import atv.com.project.monkeylivetv.Adapters.UserViewAdapter;
import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Pojo.MyResponse;
import atv.com.project.monkeylivetv.Pojo.UserDetails;
import atv.com.project.monkeylivetv.Pojo.UserProfileDetails;
import atv.com.project.monkeylivetv.R;


/**
 * Created by arjun on 5/19/15.
 */
public class FollowingFragment extends Fragment {

    private View rootView;
    private ListView followingListView;
    public UserViewAdapter followingAdapter;
    public ArrayList<UserDetails.Message> followingList;
    private ProgressBar progressBar;
    private LinearLayout contentsLayout;
//    private TextView headerView;
    private TextView emptyDataMessage;
    private int currentFollowingCount;
//    private ImageView backButton;
    private BroadcastReceiver followUnFollowReceiver;
    private BroadcastReceiver followReceiver;
    private BroadcastReceiver unFollowReceiver;
    private ActionBar actionBar;
//    private TextView headerView;
//    private ImageView profileLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_following, container, false);
//        setUpActionBar();
        initialiseViews();
//        registerFollowCountChangeReceiver();
        registerFollowStateChangeReceiver();
        if(followingList == null){
            followingList = new ArrayList<>();
            followingAdapter = new UserViewAdapter(getActivity(), followingList);
            followingListView.setAdapter(followingAdapter);
            fetchFollowing();
        } else {
            followingAdapter = new UserViewAdapter(getActivity(), followingList);
            followingListView.setAdapter(followingAdapter);
        }
        bindEvents();
        return rootView;
    }

    private void registerFollowStateChangeReceiver() {
        followUnFollowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer viewPositionToUpdate = intent.getIntExtra("followStatusChangedPosition", -1);
                if(followingList.get(viewPositionToUpdate).followed_by_current_user){
//                    headerView.setText("Following (" + (--currentFollowingCount) + ")");
                    followingList.get(viewPositionToUpdate).followed_by_current_user = false;
                } else {
//                    headerView.setText("Following (" + (++currentFollowingCount) + ")");
                    followingList.get(viewPositionToUpdate).followed_by_current_user = true;
                }
                int start = followingListView.getFirstVisiblePosition();
                for(int i=start, j=followingListView.getLastVisiblePosition();i<=j;i++)
                    if(viewPositionToUpdate == i){
                        View view = followingListView.getChildAt(i-start);
                        followingListView.getAdapter().getView(i, view, followingListView);
                        break;
                    }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(followUnFollowReceiver,
                new IntentFilter(UserProfileDetails.USER_FOLLOW_STATUS_CHANGE));
    }

    private void registerFollowCountChangeReceiver() {
        followReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                headerView.setText("Following (" + (++currentFollowingCount) + ")");
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(followReceiver, new IntentFilter(MonkeyLive.USER_FOLLOWED));

        unFollowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                headerView.setText("Following (" + (--currentFollowingCount) + ")");
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(unFollowReceiver, new IntentFilter(MonkeyLive.USER_UNFOLLOWED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(followUnFollowReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(followReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(unFollowReceiver);
    }


    private void bindEvents() {
//        final Drawable drawable = headerView.getCompoundDrawables()[2];
//        headerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int fuzz = 20;
//                if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
//                    final int x = (int) event.getX();
//                    final int y = (int) event.getY();
//                    final Rect bounds = drawable.getBounds();
//                    if (x >= (v.getRight() - bounds.width() - fuzz) && x <= (v.getRight() - v.getPaddingRight() + fuzz)
//                            && y >= (v.getPaddingTop() - fuzz) && y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {
//                        //clicked drawable click
//                        showSearchDialog();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });

        followingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", followingList.get(position).id);
                bundle.putInt("userIdPosition", position);
                userProfileDialogFragment.setArguments(bundle);
                userProfileDialogFragment.show(getActivity().getSupportFragmentManager(), "UserProfileView");
            }
        });
    }

    private void showSearchDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
        final View popupView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.searchdialog_view, null);
        final EditText userSearchText = (EditText) popupView.findViewById(R.id.userSearchText);
        final TextView cancelBt = (TextView) popupView.findViewById(R.id.cancelSearch);
        final TextView followBt = (TextView) popupView.findViewById(R.id.followFromSearch);
        final ProgressBar searchProgress = (ProgressBar) popupView.findViewById(R.id.searchProgress);
        final LinearLayout searchLayout = (LinearLayout) popupView.findViewById(R.id.searchLayout);

        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(popupView);
        final AlertDialog dialog = dialogBuilder.show();
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        followBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchKey = userSearchText.getText().toString();
                if(searchKey.equals("")) return;
                JsonObject object = new JsonObject();
                object.addProperty("user_name", searchKey);
                new MyHttp<MyResponse>(getActivity(), EndPoints.follow, MyHttp.POST, MyResponse.class)
                        .defaultHeaders()
                        .addJson(object)
                        .send(new MyCallback<MyResponse>() {
                            @Override
                            public void success(MyResponse data) {
                                searchProgress.setVisibility(View.GONE);
                                searchLayout.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Following " + searchKey, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(String msg) {
                                Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_LONG).show();
                                searchProgress.setVisibility(View.GONE);
                                searchLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBefore() {
                                Log.i("follow", "done");
                                searchProgress.setVisibility(View.VISIBLE);
                                searchLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFinish() {

                            }
                        });

            }
        });
    }

    private void fetchFollowing() {
        new MyHttp<UserDetails>(getActivity(), EndPoints.getFollowing, MyHttp.GET, UserDetails.class)
                .defaultHeaders()
                .send(new MyCallback<UserDetails>() {
                    @Override
                    public void success(UserDetails data) {
                        if(data.message.size() == 0){
                            emptyDataMessage.setVisibility(View.VISIBLE);
                            followingListView.setVisibility(View.GONE);
                        }
                        followingList.addAll(data.message);
                        followingAdapter.notifyDataSetChanged();
//                        followingAdapter = new UserViewAdapter(getActivity(), data.message);
                        currentFollowingCount = followingAdapter.getCount();
//                        followingListView.setAdapter(followingAdapter);
//                        headerView.setText("Following (" + followingAdapter.getCount() + ")");
                    }

                    @Override
                    public void failure(String msg) {
                        if (getActivity() != null) {
                            Snackbar.make(rootView, "Could not contact server", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchFollowing();
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
        followingListView = (ListView) rootView.findViewById(R.id.followingList);
        emptyDataMessage = (TextView) rootView.findViewById(R.id.dataEmptyFollowing);
        emptyDataMessage.setTypeface(MonkeyLive.racho);
//        headerView = (TextView) rootView.findViewById(R.id.followingCount);
//        headerView.setTypeface(Castasy.racho);
        progressBar = (ProgressBar) rootView.findViewById(R.id.followingProgress);
        contentsLayout = (LinearLayout) rootView.findViewById(R.id.followingListContents);
//        backButton = (ImageView) rootView.findViewById(R.id.actionBarBackButton);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user_details_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            showSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
