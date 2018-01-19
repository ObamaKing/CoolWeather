package wolf.coolweather.util;

import android.support.annotation.NonNull;

import java.net.URL;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 遍历全国省市县网络请求数据工具类
 * */

public class HttpUtil {


    public static void sendOkHttpRequest(@NonNull String address, @NonNull Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest(@NonNull URL url, @NonNull Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest(@NonNull HttpUrl httpUrl, @NonNull Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(httpUrl).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
