package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by arjun on 5/14/15.
 */
public class PingForWatchersResponse extends MyResponse{
    @Expose
    public Message message;

    public class Message{
        @Expose
        public ArrayList<Watcher> watchers = new ArrayList<>();

        @Expose
        public int total_number_of_subscribers;

        @Expose
        public int number_of_live_subscribers;
    }
}
