package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/10/15.
 */
public class SessionCreationResponse extends MyResponse {

    @Expose
    public Message message;

    public class Message{
        @Expose
        public String uid;

        @Expose
        public int id;

        @Expose
        @SerializedName("access-token")
        public String accessToken;

        @Expose
        public int score;

        @Expose
        @SerializedName("followers_count")
        public int numberOfFollowers;

        @Expose
        @SerializedName("following_users_count")
        public int numberOfFollowing;

        @Expose
        @SerializedName("streams_count")
        public int numberOfStreams;
    }
}
