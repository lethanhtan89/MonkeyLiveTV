package atv.com.project.popkontv.Pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/8/15.
 */
public class TokenRefresh {
    public String uid;

    @SerializedName("access-token")
    public String accessToken;
}
