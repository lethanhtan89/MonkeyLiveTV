package atv.com.project.popkontv.Application;

/**
 * Created by arjun on 5/7/15.
 */
public class EndPoints {

//    Production Api
    public static String openSocket = "ws://52.74.20.82/faye";
//    public static String openSocket = "ws://52.74.246.24/faye"; //for wowza
//    public static String openSocket = "ws://192.168.1.10:9292/faye";

//    public static String baseUrl = "http://192.168.1.10:3000/api/v1";
    public static String baseUrl = "http://52.74.20.82/api/v1";
//    public static String baseUrl = "http://52.74.246.24/api/v1"; //for wowza


    public static String hlsToken = "x3wygi4hrkuqx71827av3mbsj3bqpym0a37t0corexl9hrn79q71rt8qy7501i6o12d06oc7plrboh1ml62zeqlfy0t1udz3nsks9t7hdtvayok092vxt978deexa49fnmfyk8769a593b6fyf3f6xj2u565xy9efo44f67sv57zkf3uqyt6x9rnmgzm9x5f3xr4op7pj720gzhlqjz6chgmdg6kgqd4rspiovqpoioxxwww879zqhoixm1vbevc";
//    public static String hlsToken = "638gcpwx8wxxnbjncxnn2qij9ok6618prs60lgigc0yopk1ghnrqyec5zdoyb8ber6mvhe417ro3tnxvw13vsagvcsbg6go99l6hr6n4a3xd17hbm5cd1qqitc27ue21nnborbbfy75tg832rybihb5ju3czvoilh6my45mc4gq3sfow851yinq0ak7z60hzvxgkpjnd8jkyjetzgrbqfsvq0w62kdfxo94et1omtxlamjbxdipgbzcyz4oq8ahd";
    public static String baseSessionUrl = baseUrl + "/sessions";
    public static String baseStreamUrl = baseUrl + "/streams/";
    public static String baseUserUrl = baseUrl + "/users/";
    public static String getLeaderBoard = baseUrl + "/leaderboard";
    public static String getFollowers = baseUrl + "/followers";
    public static String getFollowing = baseUrl + "/following";
    public static String follow = baseUrl + "/follow";
    public static String unFollow = baseUrl + "/unfollow";
    public static String getStreamFeed = baseUrl + "/feed";
    public static String awsDetails = baseUrl + "/client_auth_tokens/aws_detail";
    public static String streamCategories = baseUrl + "/categories";

    public static String getAllStreams(int pageNo){
        return baseUrl + "/live?page=" + pageNo;
    }

    public static String sendTweetId(int streamId){
        return baseUrl + "/streams/" + streamId + "/tweets";
    }
    public static String refreshToken = baseSessionUrl + "/refresh";
    public static String viewProfile(Integer userId){
        return baseUserUrl + userId;
    }

    public static String updateUser(int userId){
        return baseUserUrl + userId;
    }

    public static String commentOnStream(int streamId){
           return baseStreamUrl + streamId + "/comments";
    }
    public static String reTweet(int streamId){
        return baseStreamUrl + streamId + "/restreams";
    }
    public static String makeFauvorite(int streamId){
        return baseStreamUrl + streamId + "/likes";
    }

    public static String pingForWatchers(String slug){
        return baseStreamUrl + slug + "/ping";
    }

    public static String fetchScore(int userId) {
        return baseUserUrl + userId;
    }

    public static String stopStream(String slug) {
        return baseStreamUrl + slug + "/stop";
    }
    public static String startStream(String slug) {
        return baseStreamUrl + slug + "/start";
    }

    public static String startScheduledStream(String slug) {
        return baseStreamUrl + slug;
    }
}
