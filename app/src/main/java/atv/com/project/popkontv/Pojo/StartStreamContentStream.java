package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/7/15.
 */
public class StartStreamContentStream {
    public String message;
    public String state;
    public String start_time_difference;
    public boolean recordable;
    @Expose
    @SerializedName("skip_start")
    public boolean skipStart;
    @Expose
    @SerializedName("category_stream_attributes")
    public CategoryStreamAttributes categoryStreamAttributes = new CategoryStreamAttributes();

    public class CategoryStreamAttributes{
        @Expose
        @SerializedName("category_id")
        public Integer categoryId;

    }
}
