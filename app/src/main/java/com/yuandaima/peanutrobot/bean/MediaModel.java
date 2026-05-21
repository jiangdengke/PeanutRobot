package com.yuandaima.peanutrobot.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MediaModel implements MultiItemEntity {
    private int itemType=1;
    private String imageUrl;
    private String videoUrl;
    private String title;
    private String duration;

    public MediaModel(int itemType,String url) {
        this.itemType = itemType;
        if (itemType==1){
            this.imageUrl=url;
        }else if (itemType==2){
            this.videoUrl=url;
        }
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
