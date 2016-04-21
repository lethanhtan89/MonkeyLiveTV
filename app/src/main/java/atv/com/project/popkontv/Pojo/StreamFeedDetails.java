package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by arjun on 5/20/15.
 */
public class StreamFeedDetails extends MyResponse {
    @Expose
    public ArrayList<Message> message;

    public class Message{
        @Expose
        public int id;
        @Expose
        public String slug;


        @Expose
        public Publisher publisher;
        public class Publisher{
            @Expose
            public Integer id;
            @Expose
            @SerializedName("name")
            public String publisherName;
            @Expose
            @SerializedName("image")
            public String publisherImage;
            @Expose
            @SerializedName("username")
            public String publisherUsername;
            @Expose
            @SerializedName("handle")
            public String publisherHandle;
            @Expose
            public String city;
            @Expose
            public Integer score;
        }

        @Expose
        @SerializedName("has_recording")
        public boolean isRecorded;

        @Expose
        @SerializedName("stream_type")
        public String streamType;

        @Expose
        @SerializedName("stream_recording")
        public StreamPlayLinks streamPlayLinks;

        public class StreamPlayLinks{
            @Expose
            @SerializedName("download_url")
            public String downloadUrl;

            @Expose
            @SerializedName("play_url")
            public String playUrl;
        }

        @Expose
        @SerializedName("stream_message")
        public String streamMessage;

        @Expose
        @SerializedName("total_number_of_subscribers")
        public int totalSubscribers;

        @Expose
        @SerializedName("number_of_live_subscribers")
        public int liveSubscribers;

        @Expose
        @SerializedName("number_of_comments")
        public int commentsCount;

        @Expose
        @SerializedName("number_of_likes")
        public int likesCount;

        @Expose
        @SerializedName("number_of_restreams")
        public int retweetsCount;

        @Expose
        public boolean reStreamed;

        @Expose
        public boolean liked;

        @Expose
        public String state;

        @Expose
        @SerializedName("start_time")
        public String startTime;

        @Expose
        public String url;

        @Expose
        @SerializedName("play_url")
        public String livePlayUrl;

        @Expose
        @SerializedName("uploaded_video")
        public uploadedVideo uploadedVideo;
        public class uploadedVideo{
            @Expose
            @SerializedName("play_url")
            public String playUrl;

            @Expose
            @SerializedName("stream_url")
            public String uploadedPlayUrl;

            @Expose
            @SerializedName("web_url")
            public String browserPlayUrl;

            @Expose
            @SerializedName("thumbnail")
            public String uploadedVideoThumbnail;

        }

        @Expose
        @SerializedName("cover_image")
        public String coverImage;
    }
}
