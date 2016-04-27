package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by arjun on 5/12/15.
 */
public class StreamFullDetails extends MyResponse {
    @Expose
    public Message message;

    public class Message{
        @Expose
        public int id;

        @Expose
        public Publisher publisher;
        public class Publisher{
            @Expose
            public Integer id;
            @Expose
            @SerializedName("name")
            public String publisher_name;
            @Expose
            @SerializedName("image")
            public String publisher_image;
            @Expose
            @SerializedName("username")
            public String publisher_username;
            @Expose
            @SerializedName("handle")
            public String publisher_handle;
            @Expose
            public String city;
        }

        @Expose
        public String slug;


        @Expose
        public String stream_message;

        @Expose
        @SerializedName("total_number_of_subscribers")
        public int total_subscribers;

        @Expose
        @SerializedName("number_of_live_subscribers")
        public int live_subscribers;

        @Expose
        public ArrayList<Watcher> watchers = new ArrayList<>();


        @Expose
        public ArrayList<Feed> feed = new ArrayList<>();


        @Expose
        public boolean restreamed;

        @Expose
        public boolean liked;

        @Expose
        public String url;
    }
}
