package atv.com.project.popkontv.Network;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.HeadersCallback;
import com.koushikdutta.ion.HeadersResponse;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.koushikdutta.ion.future.ResponseFuture;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Pojo.MyResponse;

/**
 * Created by Administrator on 5/7/15.
 */
public class MyHttp<T> {

    public static final String POST = "POST";
    public static final String GET = "GET";
    private static final int AUTH_FAIL = 1;
    private static final int NO_ERROR = 0;
    private static final int AUTH_FAIL_CODE = 5001;
    public static final String PUT = "PUT";
    public final String url;
    public final String method;
    public MyHttp httpReference;
    Context context;

    Builders.Any.B ionLoad;
    public MyCallback<JsonObject> jsonObjectMyCallback;
    public MyCallback<JsonArray> jsonArrayMyCallback;

    public MyCallback<T> callback;
    public Class responseClass;
    private int errorCode;
    private String msg = "";
    public Object body;


    public MyHttp(Context context,String url,String method,Class responseClass) {
        ionLoad = Ion.with(context).load(method, url);
        this.context = context;
        this.url = url;
        this.method = method;
        this.responseClass =responseClass;

        Ion.getDefault(context).configure().getResponseCache().setCaching(true);
    }

    public MyHttp defaultHeaders(){
        ionLoad.addHeader("Content-Type","application/json");
        ionLoad.addHeader("access-token", Popkon.loggedInUserToken);
        return this;
    }

    public MyHttp setHeadersWithDefault(String key, String value){
        ionLoad.addHeader(key, value);
        return this;
    }
    public MyHttp addJson(Object body){
        this.body = body;
        ionLoad.setJsonPojoBody(body);
        return this;
    }

    public MyHttp sendJsonObjectRequest(final MyCallback<JsonObject> callback){
        httpReference = this;
        this.jsonObjectMyCallback = callback;
        ResponseFuture<JsonObject> jsonObjectResponseFuture = ionLoad.asJsonObject();
        setHeaderCallback(jsonObjectMyCallback,jsonObjectResponseFuture);
        jsonObjectMyCallback.onBefore();
        jsonObjectResponseFuture.
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) { //quit on errr
                            jsonObjectMyCallback.failure(e.getMessage());
                            return;
                        }else {
                            jsonObjectMyCallback.success(result);
                        }
                        jsonObjectMyCallback.onFinish();
                    }
                });

        //after
        return this;
    }


    public MyHttp send(){
        return send(callback);
    }

    public MyHttp send(final MyCallback<T> callback){
        httpReference = this;
        this.callback = callback;

        this.errorCode = MyHttp.NO_ERROR;

        final ResponseFuture<T> jsonObjectResponseFuture = ionLoad.as(responseClass);
        setHeaderCallback(callback,jsonObjectResponseFuture);
        callback.onBefore();
        jsonObjectResponseFuture.
                setCallback(new FutureCallback<T>() {
                    @Override
                    public void onCompleted(Exception e, T result) {
                        if (e != null || MyHttp.this.errorCode != MyHttp.NO_ERROR) { //quit on errr
                            if(MyHttp.this.errorCode != MyHttp.NO_ERROR){
                                MyResponse result1 = (MyResponse) result;

                                if(result1.code == MyHttp.AUTH_FAIL_CODE){
                                    //auth
                                    Popkon.addRequestToQueue(MyHttp.this);
                                    Auth.refreshApiToken();
                                    return; //return on auth fail
                                }
//                                MyHttp.this.msg = result1.message;
                                MyHttp.this.msg = "";
                            }else if(e!= null){
                                MyHttp.this.msg = e.getMessage() == null ? "" : e.getMessage();
                            }else{
                                MyHttp.this.msg = "";
                            }
                            callback.failure(MyHttp.this.msg);
                        }else {
                            callback.success(result);
                        }
                        callback.onFinish();
                    }
                });

        //after
        return this;
    }


    public MyHttp sendJsonArrayRequest(final MyCallback<JsonArray> callback){
        //Before
        httpReference = this;
        this.jsonArrayMyCallback = callback;
        ResponseFuture<JsonArray> jsonObjectResponseFuture = ionLoad.asJsonArray();
        setHeaderCallback(callback,jsonObjectResponseFuture);

        callback.onBefore();

        jsonObjectResponseFuture.
                setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) { //quit on errr
                            callback.failure(e.getMessage());
                        }else {
                            callback.success(result);
                        }
                        callback.onFinish();
                    }
                });

        //after
        return this;
    }
    private void setHeaderCallback(final MyCallback callback, final ResponseFuture jsonObjectResponseFuture) {
        ionLoad.onHeaders(new HeadersCallback() {
            @Override
            public void onHeaders(HeadersResponse headers) {
                if(headers.code() != 200){
                    if(headers.code() == 417){
                        MyHttp.this.errorCode = MyHttp.AUTH_FAIL;
                    }
//                    jsonObjectResponseFuture.setCallback(null);
//                    callback.failure( headers.message());
//                    callback.onFinish();
                }
            }
        });
    }
    public static void fetchBitmap(final Context context, final String url, final MyCallback<Bitmap> callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onBefore();


                if(url == null || url.isEmpty() || context == null){
                    callback.onFinish();
                    return;
                }


                Builders.Any.B load = Ion.with(context)
                        .load(url);


                try{
                        load.asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (result != null) {
                                    callback.success(result);
                                    callback.onFinish();
                                    return;
                                }else {
                                    callback.failure("");
                                }
                            }
                        });
                }catch (Exception ex){
                    callback.failure("");
                }



            }
        }).run();

    }

    public void reSend() {
        this.ionLoad = Ion.with(context).load(method, url);
        defaultHeaders()
        .addJson(body)
        .send(callback);
    }
}


