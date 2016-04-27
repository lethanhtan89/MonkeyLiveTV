package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by arjun on 5/19/15.
 */
public class UserDetails extends MyResponse{
    @Expose
    public ArrayList<Message> message = new ArrayList<>();

    public class Message{
        @Expose
        public int id;
        @Expose
        public String name;
        @Expose
        public String handle;
        @Expose
        public String image;
        @Expose
        public int score;
        @Expose
        public boolean followed_by_current_user;
    }
}
