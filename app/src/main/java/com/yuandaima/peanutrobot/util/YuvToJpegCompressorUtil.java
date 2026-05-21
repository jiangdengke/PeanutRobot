package com.yuandaima.peanutrobot.util;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class YuvToJpegCompressorUtil {
    private static final String TAG = "YuvToJpeg";

    private YuvImage yuvImage;
    private ByteArrayOutputStream outputStream;

    public YuvToJpegCompressorUtil() {
        outputStream = new ByteArrayOutputStream();
    }


    public byte[] compress(byte[] yuvData, int width, int height, int quality) {
        if (yuvData == null || yuvData.length == 0) {
            Log.e(TAG, "YUV 数据为空");
            return null;
        }

        try {
            yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);

            outputStream.reset();

            boolean success = yuvImage.compressToJpeg(
                    new Rect(0, 0, width, height),
                    quality,
                    outputStream
            );

            if (!success) {
                Log.e(TAG, "JPEG 压缩失败");
                return null;
            }

            byte[] jpegData = outputStream.toByteArray();

            // 打印压缩率
            Log.d(TAG, String.format("压缩: %d -> %d 字节, 压缩率: %.1f%%",
                    yuvData.length, jpegData.length,
                    (jpegData.length * 100.0 / yuvData.length)));

            return jpegData;

        } catch (Exception e) {
            Log.e(TAG, "压缩异常", e);
            return null;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "关闭流失败", e);
        }
        yuvImage = null;
    }
}