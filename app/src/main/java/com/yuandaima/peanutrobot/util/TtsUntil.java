package com.yuandaima.peanutrobot.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.yuandaima.peanutrobot.interFace.RequestSuccess;

import java.util.Locale;

public class TtsUntil extends UtteranceProgressListener implements LifecycleObserver  {
    private TextToSpeech textToSpeech;
    private Boolean flag = false;
    private static final String TAG = "ChineseToSpeech";
    private RequestSuccess requestSuccess;

    private TtsUntil() {

    }


    // 静态内部类，持有单例实例
    private static class Holder {
        private static final TtsUntil INSTANCE = new TtsUntil();
    }

    // 获取单例实例
    public static TtsUntil getInstance() {
        return Holder.INSTANCE;
    }

    public void initTts(Context context, RequestSuccess requestSuccess){
        this.requestSuccess=requestSuccess;
        this.textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    Log.d(TAG, "SUCCESS" );
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    textToSpeech.setPitch(2.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(1.0f);//用来控制语速
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context,"不支持语音朗读",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Log.d(TAG,"status3 = "+status);
                }
            }
        });
        textToSpeech.setOnUtteranceProgressListener(this);
    }




    public void speech(String text,boolean flag) {
        this.flag = flag;
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null   ,"myUtterance");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stop(){

        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    @Override
    public void onStart(String s) {
    }

    @Override
    public void onDone(String s) {
        if (flag) {
            requestSuccess.success();
        }
    }

    @Override
    public void onError(String s) {
    }
}
