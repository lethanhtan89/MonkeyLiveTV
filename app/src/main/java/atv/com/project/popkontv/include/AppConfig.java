package atv.com.project.popkontv.include;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Administrator on 4/12/2016.
 */
//Conecting Server Wowza
public class AppConfig {
    JSONParser jsonParser;
    public static final String STREAM_URL = "rtmp://192.168.100.100:1935/PopkonTV/myStream";
    public static final String PUBLISHER_USERNAME = "PopkonTV";
    public static final String PUBLISHER_PASSWORD = "123456";

    public static final String loginurl = "";
    public static final String registerurl = "";
    public static final String allVideosURL = "";
    public static final String addVideoURL = "";
    public static final String updateVideoURL = "";
    public static final String deleteVideoURL = "";

    String login_tag = "login";
    String register_tag = "register";


    Context context;

    public AppConfig(Context context){
        jsonParser = new JSONParser();
        this.context = context;
    }

    //Functions

    // Check login on data ?
    public boolean checkLogin(){
        SharedPreferences getSharedPreferences = context.getSharedPreferences(null,context.MODE_WORLD_READABLE);
        String emailLogined = getSharedPreferences.getString("emailLogined","Please Login");
        if(emailLogined.equals("Please Login"))
            return false;
        else
            return true;
    }

    // Get email logined
    public String getEmail(){
        SharedPreferences getSharedPreferences = context.getSharedPreferences(null, context.MODE_WORLD_READABLE);
        String emailLogined = getSharedPreferences.getString("emailLogined", "Please Login");
        return emailLogined;
    }

    //Set data for emailLogin to "Please Login"
    public boolean logOut(){
        SharedPreferences setSharedPreferences = context.getSharedPreferences(null, context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = setSharedPreferences.edit();
        editor.putString("emailLogined", "Please Login");
        editor.commit();
        return true;
    }

    //Logined, save to SharePreferrence
    public boolean setEmail(String email){
        SharedPreferences setSharedPreferences = context.getSharedPreferences(null,context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = setSharedPreferences.edit();
        editor.putString("emailLogined", email);
        editor.commit();
        return true;
    }

    public JSONObject loginUser(String email, String password){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", login_tag));
        nameValuePairs.add(new BasicNameValuePair("email",email));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        JSONObject jsonObject = jsonParser.getJSONFromURL(loginurl, nameValuePairs);
        //Set into Shared to remember
        setEmail(email);
        return jsonObject;
    }

    public JSONObject registerUser(String name, String email, String password){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag", register_tag));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        JSONObject jsonObject = jsonParser.getJSONFromURL(registerurl,nameValuePairs);
        return jsonObject;
    }

    //get All Video
    public JSONObject getAllVideos(){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        JSONObject jsonObject = jsonParser.getJSONFromURL(allVideosURL, nameValuePairs);
        return jsonObject;
    }

    public JSONObject addVideo(String name, String price, String decription){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("price", price));
        nameValuePairs.add(new BasicNameValuePair("decription", decription));
        JSONObject jsonObject = jsonParser.getJSONFromURL(addVideoURL, nameValuePairs);
        return jsonObject;
    }

    public JSONObject updateVideo(String name, String price, String decription){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("price", price));
        nameValuePairs.add(new BasicNameValuePair("decription", decription));
        JSONObject jsonObject = jsonParser.getJSONFromURL(updateVideoURL, nameValuePairs);
        return jsonObject;
    }

    public JSONObject deleteVideo(String id){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", id));
        JSONObject jsonObject = jsonParser.getJSONFromURL(deleteVideoURL, nameValuePairs);
        return jsonObject;
    }

}



