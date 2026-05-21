package com.yuandaima.peanutrobot.manager;

import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitManager {

    private static String baseUrl;
    private static String identity;
    private static RetrofitManager mInstance;

    private static Map<String, Retrofit> retrofitMap = new HashMap<>();

    private RetrofitManager() {

    }

    public static void cleanRetrofitMap() {
        retrofitMap.clear();
    }

    public static RetrofitManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 返回全局对象
     * <p>
     * 解析为对象
     */
    public Retrofit getRetrofit() {
        checkBaseUrl();
        Retrofit retrofit = retrofitMap.get(baseUrl);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }

    /**
     * 返回局部对象
     * <p>
     * 解析为对象
     */
    public Retrofit getRetrofit(String baseUrl) {
        checkBaseUrl(baseUrl);
        Retrofit retrofit = retrofitMap.get(baseUrl);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }

    OkHttpClient httpClient = new OkHttpClient.Builder()
            //   .connectTimeout(10,TimeUnit.SECONDS)
            // 设置拦截器，添加统一的请求头
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    // 以拦截到的请求为基础创建一个新的请求对象，然后插入Header
                    Request request = chain.request().newBuilder()
                            .addHeader("identity", identity)
                            .build();
                    // 开始请求
                    return chain.proceed(request);
                }
            }).build();

    /**
     * 返回全局对象
     * <p>
     * 解析为字符串
     */
    public Retrofit getStringRetrofit() {
        checkBaseUrl();

        Retrofit retrofit = retrofitMap.get(baseUrl);
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .client(httpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }

    /**
     * 返回局部对象
     * <p>
     * 解析为字符串
     */
    public Retrofit getStringRetrofit(String baseUrl) {
        checkBaseUrl();
        Retrofit retrofit = retrofitMap.get(baseUrl);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }

    private void checkBaseUrl(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("baseUrl is null");
        }
    }


    private void checkBaseUrl() {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("please set setBaseUrl first：RetrofitManager.getInstance().setBaseUrl(url)");
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置全局的url
     *
     * @param baseUrl
     * @return
     */
    public RetrofitManager setBaseUrl(String baseUrl) {

        RetrofitManager.baseUrl = baseUrl;
        return this;
    }
}
