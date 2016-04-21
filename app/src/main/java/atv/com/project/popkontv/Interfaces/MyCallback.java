package atv.com.project.popkontv.Interfaces;

public interface MyCallback<T>{


    public void success(T data);

    public void failure(String msg);

    public void onBefore();

    public void onFinish();
}
