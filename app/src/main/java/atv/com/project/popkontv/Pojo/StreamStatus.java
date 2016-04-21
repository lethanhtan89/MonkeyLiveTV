package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/28/15.
 */
public class StreamStatus {
    @Expose
    public int id;

    @Expose
    @SerializedName("state")
    public String streamStatus;
}
