package atv.com.project.monkeylivetv.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import atv.com.project.monkeylivetv.Pojo.MyResponse;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.Pojo.UserDetails;
import atv.com.project.monkeylivetv.Pojo.UserProfileDetails;
import atv.com.project.monkeylivetv.R;
import atv.com.project.monkeylivetv.lib.StreamDrawable;

/**
 * Created by arjun on 5/19/15.
 */
public class LeaderBoardAdapter extends ArrayAdapter<UserDetails.Message>{
    private ArrayList<UserDetails.Message> leadersList;
    private Context context;

    public LeaderBoardAdapter(Context context, ArrayList<UserDetails.Message> objects) {
        super(context, -1, objects);
        this.context = context;
        this.leadersList = objects;
    }

    static class ViewHolder{
        public TextView leaderName;
        public TextView leaderHandle;
        public TextView leaderScore;
        public ImageView leaderImage;
        public ImageView followButton;
        public ProgressBar followProgressBar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserDetails.Message currentUser = leadersList.get(position);
        final ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.leaderboard_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.leaderName = (TextView)convertView.findViewById(R.id.leaderName);
            viewHolder.leaderHandle = (TextView)convertView.findViewById(R.id.leaderHandle);
            viewHolder.leaderScore = (TextView)convertView.findViewById(R.id.leaderScore);
            viewHolder.leaderImage = (ImageView)convertView.findViewById(R.id.leaderImage);
            viewHolder.followButton = (ImageView)convertView.findViewById(R.id.leaderFollowBt);
            viewHolder.followProgressBar = (ProgressBar)convertView.findViewById(R.id.follow_progress_bar);
            viewHolder.leaderName.setTypeface(MonkeyLive.racho);
            viewHolder.leaderHandle.setTypeface(MonkeyLive.racho);
            viewHolder.leaderScore.setTypeface(MonkeyLive.racho);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.leaderName.setText(currentUser.name);
        viewHolder.leaderHandle.setText(currentUser.handle);
        viewHolder.leaderScore.setText(String.valueOf(currentUser.score));
        if(currentUser.id == MonkeyLive.loggedInUserId){
            viewHolder.followButton.setVisibility(View.GONE);
//            if(Build.VERSION.SDK_INT > 15){
//                convertView.setBackground(context.getResources().getDrawable(R.drawable.primaryround));
//            }else {
//                convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.whiteround));
//            }
        }else {
            viewHolder.followButton.setVisibility(View.VISIBLE);
//            if(Build.VERSION.SDK_INT > 15){
//                convertView.setBackground(null);
//            }else {
//                convertView.setBackgroundDrawable(null);
//            }
        }
        if(currentUser.followed_by_current_user){
            viewHolder.followButton.setImageDrawable(context.getResources().getDrawable(R.drawable.following));
        } else {
            viewHolder.followButton.setImageDrawable(context.getResources().getDrawable(R.drawable.follow));
        }
        MyHttp.fetchBitmap(context, currentUser.image, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable streamDrawable = new StreamDrawable(data);
                viewHolder.leaderImage.setImageDrawable(streamDrawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                viewHolder.leaderImage.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
            }

            @Override
            public void onFinish() {

            }
        });
        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.followed_by_current_user) {
                    unFollow(viewHolder, currentUser.id, position);
                } else {
                    follow(viewHolder, currentUser.id, position);
                }
            }
        });
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("userId", currentUser.id);
//                bundle.putInt("userIdPosition", position);
//                userProfileDialogFragment.setArguments(bundle);
//                userProfileDialogFragment.show(((ActionBarActivity)context).getSupportFragmentManager(), "UserProfileView");
//            }
//        });
        return convertView;
    }

    private void follow(final ViewHolder viewHolder, int currentUserId, int position) {
        UserProfileDetails.follow(context, currentUserId, position, new MyCallback<MyResponse>() {
            @Override
            public void success(MyResponse data) {
                viewHolder.followProgressBar.setVisibility(View.GONE);
                viewHolder.followButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                viewHolder.followProgressBar.setVisibility(View.VISIBLE);
                viewHolder.followButton.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void unFollow(final ViewHolder viewHolder, int currentUserId, int position) {
        UserProfileDetails.unFollow(context, currentUserId, position, new MyCallback<MyResponse>() {
            @Override
            public void success(MyResponse data) {
                viewHolder.followProgressBar.setVisibility(View.GONE);
                viewHolder.followButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                viewHolder.followProgressBar.setVisibility(View.VISIBLE);
                viewHolder.followButton.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {

            }
        });
    }

}
