package atv.com.project.popkontv.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 4/6/2016.
 */
public class YouTubeContent {

    // An array Youtube Video, by Id
    public static List<YouTubeVideo> ITEMS = new ArrayList<>();

    // A map Youtube Video, by Id
    public static Map<String, YouTubeVideo> ITEMMAP = new HashMap<>();

    static {
        addItem(new YouTubeVideo("H_3nrx7B8_s", "Open in the YouTube App"));
        addItem(new YouTubeVideo("K5O1ItvHatM", "Open in the YouTube App in fullscreen"));
        addItem(new YouTubeVideo("0_Bp_sp3LYg", "Open in the Standalone player in fullscreen"));
        addItem(new YouTubeVideo("WwJuGbEnr7Y", "Open in the Standalone player in \"Light Box\" mode"));
        addItem(new YouTubeVideo("gUl9PREP1W8", "Open in the YouTubeFragment"));
        addItem(new YouTubeVideo("TTh_qYMzSZk", "Hosting the YouTubeFragment in an Activity"));
        addItem(new YouTubeVideo("xSEUJsrPljI", "Open in the YouTubePlayerView"));
        addItem(new YouTubeVideo("fSy9XOLjHuc", "Custom \"Light Box\" player with fullscreen handling"));
        addItem(new YouTubeVideo("E7UlemBSoYw", "Custom player controls"));
    }

    private static void addItem(final YouTubeVideo item){
        ITEMS.add(item);
        ITEMMAP.put(item.id, item);
    }

    //A POJO representing a YouTube video
    public static class YouTubeVideo{
        public String id;
        public String title;

        public YouTubeVideo(String id, String content){
            this.id = id;
            this.title = content;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
