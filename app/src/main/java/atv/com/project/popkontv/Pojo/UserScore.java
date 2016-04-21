package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;

/**
 * Created by arjun on 5/18/15.
 */
public class UserScore extends MyResponse{
    @Expose
    public Message message;

    public class Message{
        @Expose
        public int score;
    }
}
