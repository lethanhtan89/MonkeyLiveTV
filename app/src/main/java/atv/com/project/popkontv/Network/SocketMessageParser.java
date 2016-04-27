package atv.com.project.popkontv.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import atv.com.project.popkontv.Pojo.Feed;
import atv.com.project.popkontv.Pojo.StreamStatus;
import atv.com.project.popkontv.Pojo.Watcher;

/**
 * Created by Administrator on 5/13/15.
 */
public class SocketMessageParser {
    public static final int NEW_FEED_ITEM = 1;
    public static final int USER_ONLINE = 2;
    public static final int USER_OFFLINE = 3;
    public static final int STREAM_STATE_CHANGE = 4;

    public static int getMessageType(String code) {
        if(code.contentEquals("NEW_FEED_ITEM")){
            return NEW_FEED_ITEM;
        } else if(code.contentEquals("USER_ONLINE")){
            return USER_ONLINE;
        } else if(code.contentEquals("USER_OFFLINE")){
            return USER_OFFLINE;
        } else if(code.contentEquals("STREAM_STATE_CHANGE")){
            return STREAM_STATE_CHANGE;
        }
        else {
            return 0;
        }
    }

    @Deprecated
    public static void parseMessage(JSONObject obj,HandleMessage handle){
        try {
            parseMessage(obj.getString("code"),obj.getJSONObject("message").toString(),handle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseMessage(JsonObject obj,HandleMessage handle){
        parseMessage(obj.get("code").getAsString(),obj.getAsJsonObject("message"),handle);
    }

    @Deprecated
    public  static void parseMessage(String type,String message,HandleMessage handler){
        parseMessage(type,new Gson().fromJson(message,JsonObject.class),handler);
    }

    public  static void parseMessage(String type,JsonObject message,HandleMessage handler){
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            switch(SocketMessageParser.getMessageType(type)){
                case SocketMessageParser.NEW_FEED_ITEM:
                    Feed feed = gson.fromJson(message,Feed.class);
                    handler.newFeedAdded(feed);
                    break;

                case SocketMessageParser.USER_OFFLINE:
                    Watcher watcherOffline = gson.fromJson(message, Watcher.class);
                    handler.userOffline(watcherOffline);
                    break;

                case SocketMessageParser.USER_ONLINE:
                    Watcher watcherOnline = gson.fromJson(message, Watcher.class);
                    handler.userOnline(watcherOnline);
                    break;

                case SocketMessageParser.STREAM_STATE_CHANGE:
                    StreamStatus currentStatus = gson.fromJson(message, StreamStatus.class);
                    handler.streamStateChanged(currentStatus);
                    break;
            }
    }

    public interface HandleMessage {
        void userOnline(Watcher watcher);
        void userOffline(Watcher watcher);
        void newFeedAdded(Feed feed);
        void streamStateChanged(StreamStatus currentStatus);
    }
}
