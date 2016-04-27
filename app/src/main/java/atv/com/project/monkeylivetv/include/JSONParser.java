package atv.com.project.monkeylivetv.include;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


/**
 * Created by Administrator on 4/18/2016.
 */
public class JSONParser {
    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String json = "" ;

    public JSONParser(){

    }

    public JSONObject getJSONFromURL(String url, List<NameValuePair> nameValuePairs){
        try{
            //Connecting
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Get data into InputStream
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();

            //Read inputStream --> JSONObject
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
            StringBuilder stringBuilder = new StringBuilder();
            String line =  null;
            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line + "\n");
            }
            inputStream.close();
            json = stringBuilder.toString();
            jsonObject = new JSONObject(json);

        }catch (Exception e){

        }
        return jsonObject;
    }
}
