package app.coolweather.com.coolweather.util;

/**
 * Created by sunbin on 15-8-5.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}