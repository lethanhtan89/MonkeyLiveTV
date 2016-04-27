package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/13/15.
 */
public class NewComment {
    public int stream_id;

    @SerializedName("comment")
    public NewCommentDetails commentDetails;

}
