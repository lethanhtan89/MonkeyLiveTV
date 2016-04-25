package io.kickflip.sdk.api.json;

import atv.com.project.popkontv.Application.Popkon;

/**
 * Created by davidbrodsky on 2/17/14.
 */
public class HlsStream {

    private String mBucket;

    private String mRegion;

    private String mPrefix;

    private String mAwsKey;

    private String mAwsSecret;

    private String mToken;

    private String mDuration;
    public String slug;

    public String getAwsS3Bucket() {

//        return "castasy-vcr";
        return Popkon.getStringPreference(Popkon.AWS_BUCKET_NAME, "hls");
//        return mBucket;
    }

    public String getRegion() {
        return "";

//        return mRegion;
    }

    public String getAwsS3Prefix() {

//                return "hls/";
        return Popkon.getStringPreference(Popkon.AWS_FOLDER, "hls") + "/" + slug + "/";
//        return mPrefix;
    }

    public String getAwsKey() {

        return "";

    }

    public String getAwsSecret() {
        return "";
//        return mAwsSecret;
    }

    public String getToken() {
        return mToken;
    }

    public String getDuration() {
        return mDuration;
    }

//    public String toString(){
//        return "Bucket: " + getAwsS3Bucket() + " streamUrl " + getStreamUrl();
//    }

}
