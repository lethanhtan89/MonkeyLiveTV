package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.Expose;

/**
 * Created by arjun on 5/9/15.
 */
public class StreamStartResponse extends MyResponse{

    @Expose
    public Message message;

    public class Message{
        @Expose
        public String url;

        @Expose
        public int id;

        @Expose
        public String tweet;

    }


}
