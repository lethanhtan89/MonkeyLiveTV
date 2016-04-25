package atv.com.project.popkontv.Adapters;

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

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.MyResponse;
import atv.com.project.popkontv.Pojo.UserDetails;
import atv.com.project.popkontv.Pojo.UserProfileDetails;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.lib.StreamDrawable;
/**
 * Created by arjun on 5/19/15.
 */
public class UserViewAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<UserDetails.Message> usersList;

    public UserViewAdapter(Context context, ArrayList<UserDetails.Message> objects) {
        super(context, -1, objects);
        this.context = context;
        this.usersList = objects;
    }

    static class ViewHolder{
        public TextView userName;
        public TextView userHandle;
        public ImageView userImage;
        public ImageView followButton;
        public ProgressBar followProgressBar;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserDetails.Message currentUser = usersList.get(position);
        final ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.userview_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView)convertView.findViewById(R.id.userName);
            viewHolder.userHandle = (TextView)convertView.findViewById(R.id.userHandle);
            viewHolder.userImage = (ImageView)convertView.findViewById(R.id.userImage);
            viewHolder.followButton = (ImageView)convertView.findViewById(R.id.userFollowBt);
            viewHolder.followProgressBar = (ProgressBar)convertView.findViewById(R.id.follow_progress_bar);
            viewHolder.userName.setTypeface(Popkon.racho);
            viewHolder.userHandle.setTypeface(Popkon.racho);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userName.setText(currentUser.name);
        viewHolder.userHandle.setText(currentUser.handle);
        if(currentUser.followed_by_current_user){
            viewHolder.followButton.setImageDrawable(context.getResources().getDrawable(R.drawable.following));
        } else {
            viewHolder.followButton.setImageDrawable(context.getResources().getDrawable(R.drawable.follow));
        }
        MyHttp.fetchBitmap(context, currentUser.image, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable streamDrawable = new StreamDrawable(data);
                viewHolder.userImage.setImageDrawable(streamDrawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                viewHolder.userImage.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
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
