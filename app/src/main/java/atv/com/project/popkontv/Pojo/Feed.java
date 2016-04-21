package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/13/15.
 */
public class Feed extends MyResponse{
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_LIKE = 2;
    public static final int TYPE_RESTREAM = 3;
    @Expose
    @SerializedName("id")
    public int feedId;
    @Expose
    public String type;
    @Expose
    public String handle;
    @Expose
    public String message;

    public int getType(){
        if(type.contentEquals("comment")) {
            return TYPE_COMMENT;
        } else if(type.contentEquals("like")){
            return TYPE_LIKE;
        }else if(type.contentEquals("restream")) {
            return TYPE_RESTREAM;
        }
        return 0;
    }
}

