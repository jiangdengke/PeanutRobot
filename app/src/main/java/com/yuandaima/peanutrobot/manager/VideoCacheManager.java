package com.yuandaima.peanutrobot.manager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCacheManager {
    private Context context;
    private List<File> cachedFiles  = new ArrayList<>();
    private Map<String, File> videoMap = new HashMap<>();  // 用 ID 或名称索引

    private static volatile VideoCacheManager instance;

    private VideoCacheManager(Context context) {
        this.context = context.getApplicationContext();
    }


    public static VideoCacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (VideoCacheManager.class) {
                if (instance == null) {
                    instance = new VideoCacheManager(context);
                }
            }
        }
        return instance;
    }


    public File cacheVideo(String fileId, byte[] videoData) {
        try {
            // 创建缓存文件
            File cacheFile = new File(context.getCacheDir(), "video_" + fileId + ".mp4");

            // 写入数据
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(videoData);
            fos.close();

            // 加入缓存管理
            cachedFiles .add(cacheFile);
            videoMap.put(fileId, cacheFile);

            Log.d("CacheManager", "文件缓存成功: " + fileId + ", 大小: " + videoData.length);
            return cacheFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取所有图片
    public List<File> getCachedImages() {
        List<File> images = new ArrayList<>();
        for (File file : cachedFiles) {
            if (file.getName().endsWith(".jpg") ||
                    file.getName().endsWith(".png") ||
                    file.getName().endsWith(".gif")) {
                images.add(file);
            }
        }
        return images;
    }

    // 获取所有视频
    public List<File> getcachedFiles () {
        List<File> videos = new ArrayList<>();
        for (File file : cachedFiles) {
            if (file.getName().endsWith(".mp4") || file.getName().endsWith(".3gp")) {
                videos.add(file);
            }
        }
        return videos;
    }
    public File getCachedVideo(String videoId) {
        return videoMap.get(videoId);
    }

    public List<File> getAllcachedFiles () {
        return new ArrayList<>(cachedFiles );
    }

    public boolean isVideoCached(String videoId) {
        return videoMap.containsKey(videoId) && videoMap.get(videoId).exists();
    }


    public void playCachedVideo(String videoId, VideoView videoView) {
        File videoFile = videoMap.get(videoId);
        if (videoFile != null && videoFile.exists()) {
            videoView.setVideoURI(Uri.fromFile(videoFile));
            videoView.start();
        } else {
            Log.e("CacheManager", "视频未缓存: " + videoId);
        }
    }


    public void clearAllCache() {
        for (File file : cachedFiles ) {
            if (file.exists()) {
                file.delete();
            }
        }
        cachedFiles .clear();
        videoMap.clear();
        Log.d("CacheManager", "所有缓存已清理");
    }


    public void removeCache(String videoId) {
        File file = videoMap.remove(videoId);
        if (file != null && file.exists()) {
            file.delete();
            cachedFiles .remove(file);
        }
    }


    public long getCacheSize() {
        long totalSize = 0;
        for (File file : cachedFiles ) {
            totalSize += file.length();
        }
        return totalSize;
    }


    public static void releaseInstance() {
        if (instance != null) {
            instance.clearAllCache();
            instance.context = null;
            instance = null;
        }
    }
}
