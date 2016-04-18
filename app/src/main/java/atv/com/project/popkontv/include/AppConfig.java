package atv.com.project.popkontv.include;

import android.content.Context;

/**
 * Created by Administrator on 4/12/2016.
 */
//Conecting Server Wowza
public class AppConfig {
    JSONParser jsonParser;
    public static final String STREAM_URL = "rtmp://192.168.100.100:1935/PopkonTV/myStream";
    public static final String PUBLISHER_USERNAME = "PopkonTV";
    public static final String PUBLISHER_PASSWORD = "123456";

    Context context;

    public AppConfig(Context context){
        jsonParser = new JSONParser();
        this.context = context;
    }
}

//Functions

