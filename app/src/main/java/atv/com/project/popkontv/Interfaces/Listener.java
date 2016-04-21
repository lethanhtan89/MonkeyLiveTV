package atv.com.project.popkontv.Interfaces;

/**
 * Created by Administrator on 4/21/2016.
 */
public interface Listener {
    void onConnect();

    void onMessage(String message);

    void onMessage(byte[] data);

    void onDisconnect(int code, String reason);

    void onError(Exception error);
}
