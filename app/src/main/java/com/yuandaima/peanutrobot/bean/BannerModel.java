package com.yuandaima.peanutrobot.bean;

import java.util.List;

public class BannerModel {
    private List<String> url;
    private String title;
    private int time_sleep;

    public BannerModel(List<String> imageUrl, String title) {
        this.url = imageUrl;
        this.title = title;
    }

    public int getTime_sleep() {
        return time_sleep;
    }

    public void setTime_sleep(int time_sleep) {
        this.time_sleep = time_sleep;
    }

    public List<String> getImageUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}