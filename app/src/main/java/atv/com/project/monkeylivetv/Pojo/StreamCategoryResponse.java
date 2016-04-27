package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by arjun on 7/16/15.
 */
public class StreamCategoryResponse extends MyResponse {

    @Expose
    @SerializedName("message")
    public ArrayList<Message> categories = new ArrayList<>();
    public class Message{
        @Expose
        @SerializedName("id")
        public Integer categoryId;

        @Expose
        @SerializedName("name")
        public String categoryName;
    }
}
