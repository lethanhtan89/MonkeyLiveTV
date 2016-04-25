package atv.com.project.popkontv.Pojo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;

/**
 * Created by arjun on 6/18/15.
 */
public class UserProfileDetails extends MyResponse{
    public static final String USER_FOLLOW_STATUS_CHANGE = "user_follow_status_change";
    @Expose
    public Message message;
    public class Message {
        @Expose
        @SerializedName("id")
        public Integer userId;

        @Expose
        @SerializedName("score")
        public Integer userScore;

        @Expose
        @SerializedName("name")
        public String userName;

        @Expose
        @SerializedName("handle")
        public String userHandle;

        @Expose
        @SerializedName("image")
        public String userImage;

        @Expose
        @SerializedName("city")
        public String userCity;

        @Expose
        @SerializedName("streams_count")
        public Integer userStreamsCount;

        @Expose
        @SerializedName("followers_count")
        public Integer userFollowersCount;

        @Expose
        @SerializedName("following_count")
        public Integer userFollowingCount;

        @Expose
        @SerializedName("followed_by_current_user")
        public boolean followedByMe;
    }

    public static void follow(final Context context, Integer userId, final Integer userIdPosition, final MyCallback<MyResponse> callback){
        JsonObject object = new JsonObject();
        object.addProperty("user_id", userId);
        new MyHttp<MyResponse>(context, EndPoints.follow, MyHttp.POST, MyResponse.class)
                .defaultHeaders()
                .addJson(object)
                .send(new MyCallback<MyResponse>() {
                    @Override
                    public void success(MyResponse data) {
                        Intent broadCastFollowIntent = new Intent(USER_FOLLOW_STATUS_CHANGE);
                        broadCastFollowIntent.putExtra("followStatusChangedPosition", userIdPosition);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastFollowIntent);
                        int count = Popkon.getIntPreference(Popkon.USER_FOLLOWING_COUNT, 0);
                        Popkon.setIntPreferenceData(Popkon.USER_FOLLOWING_COUNT, count + 1);
                        callback.success(data);
                    }

                    @Override
                    public void failure(String msg) {
                        callback.failure(msg);
                    }

                    @Override
                    public void onBefore() {
                        Log.i("follow", "done");
                        callback.onBefore();
                    }

                    @Override
                    public void onFinish() {
                        callback.onFinish();
                    }
                });
    }
    public static void unFollow(final Context context, Integer userId, final Integer userIdPosition, final MyCallback<MyResponse> callback){
        JsonObject object = new JsonObject();
        object.addProperty("user_id", userId);
        new MyHttp<MyResponse>(context, EndPoints.unFollow, MyHttp.POST, MyResponse.class)
                .defaultHeaders()
                .addJson(object)
                .send(new MyCallback<MyResponse>() {
                    @Override
                    public void success(MyResponse data) {
                        Intent broadCastFollowIntent = new Intent(USER_FOLLOW_STATUS_CHANGE);
                        broadCastFollowIntent.putExtra("followStatusChangedPosition", userIdPosition);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(broadCastFollowIntent);
                        int count = Popkon.getIntPreference(Popkon.USER_FOLLOWING_COUNT, 0);
                        Popkon.setIntPreferenceData(Popkon.USER_FOLLOWING_COUNT, count - 1);
                        callback.success(data);
                    }

                    @Override
                    public void failure(String msg) {
                        callback.failure(msg);
                    }

                    @Override
                    public void onBefore() {
                        Log.i("follow", "done");
                        callback.onBefore();
                    }

                    @Override
                    public void onFinish() {
                        callback.onFinish();
                    }
                });
    }
}
