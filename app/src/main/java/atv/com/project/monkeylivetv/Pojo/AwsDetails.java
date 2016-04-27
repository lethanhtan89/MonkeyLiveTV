package atv.com.project.monkeylivetv.Pojo;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import atv.com.project.monkeylivetv.Application.EndPoints;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;

/**
 * Created by arjun on 7/7/15.
 */
public class AwsDetails extends MyResponse {
    @Expose
    public Message message;

    public class Message{
        @Expose
        @SerializedName("aws_access_key")
        public String awsAccessKey;

        @Expose
        @SerializedName("aws_secret_key")
        public String awsSecretKey;

        @Expose
        @SerializedName("aws_region")
        public String awsRegion;

        @Expose
        @SerializedName("aws_bucket_name")
        public String awsBucketName;

        @Expose
        @SerializedName("aws_recording_folder")
        public String awsRecordingFolder;
    }


    public static void getAwsDetails(Context context, final MyCallback myCallback) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("token", EndPoints.hlsToken);
        }catch (Exception e){
            Log.i("Erroraws", e.getMessage());
        }
        new MyHttp<AwsDetails>(context, EndPoints.awsDetails, MyHttp.POST, AwsDetails.class)
                .addJson(jsonObject)
                .defaultHeaders()
                .send(new MyCallback<AwsDetails>() {
                    @Override
                    public void success(AwsDetails data) {
                        Log.i("awsdet", data.toString());
                        myCallback.success(data);
                    }

                    @Override
                    public void failure(String msg) {
                        myCallback.failure(msg);
                    }

                    @Override
                    public void onBefore() {
                        myCallback.onBefore();
                    }

                    @Override
                    public void onFinish() {
                        myCallback.onFinish();
                    }
                });
    }
}
