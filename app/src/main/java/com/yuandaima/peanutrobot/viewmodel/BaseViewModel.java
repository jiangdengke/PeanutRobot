package com.yuandaima.peanutrobot.viewmodel;


import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.ToastUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuandaima.peanutrobot.manager.RetrofitManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.http.Url;

public class BaseViewModel extends ViewModel {



    //统一配置 RetrofitManager.getInstance().setBaseUrl(Url.getHttp()+Url.getIP()).getStringRetrofit();
    public static Retrofit retrofit;

    public static void resetRetrofit() {
     //   retrofit = RetrofitManager.getInstance().setBaseUrl(Url.getHttp() + Url.getIP()).getStringRetrofit();
    }






}
