package atv.com.project.monkeylivetv.Pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by arjun on 5/7/15.
 */

public class LoginDetails {

    public User user;

    @SerializedName("twitter_credential")
    public TwitterCredential twitterCredential;

}


