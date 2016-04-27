package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/13/15.
 */
public class Watcher{
    @Expose
    @SerializedName("id")
    public int watcher_id;

    @Expose
    @SerializedName("image")
    public String watcher_image;
}

