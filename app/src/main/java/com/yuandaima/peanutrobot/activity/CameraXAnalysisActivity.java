package com.yuandaima.peanutrobot.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.yuandaima.peanutrobot.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
public class CameraXAnalysisActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageAnalysis imageAnalysis;
    private ProcessCameraProvider cameraProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);

        // 检查权限
        if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                // 1. 预览
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // 2. 图像分析 - 获取相机数据
                setupImageAnalysis();

                // 3. 选择摄像头
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                try {
                    if (!cameraProvider.hasCamera(cameraSelector)) {
                        Log.d("CameraSelector","没有后置摄像头");
                        // 尝试使用前置摄像头
                        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                        if (!cameraProvider.hasCamera(cameraSelector)) {
                            Log.d("CameraSelector","没有可用摄像头");
                            // 没有可用摄像头
                            return;
                        }
                    }
                } catch (CameraInfoUnavailableException e) {
                    Log.d("CameraSelector","CameraInfoUnavailableException");

                    throw new RuntimeException(e);
                }
                Log.d("CameraSelector","有可用摄像头");
                // 4. 绑定生命周期
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {

                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void setupImageAnalysis() {

        // 配置 ImageAnalysis
        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))  // 设置分辨率
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 策略
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888) // YUV格式
                .build();

        // 设置分析器
        imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor(), // 在后台线程执行
                new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        // 这里获取相机数据
                        processImage(image);
                        image.close(); // 必须关闭！
                    }
                });
    }

    private void processImage(ImageProxy image) {

        // 获取图像信息
        int width = image.getWidth();
        int height = image.getHeight();
        int format = image.getFormat(); // ImageFormat.YUV_420_888

        Log.d("CameraX", "图像尺寸: " + width + "x" + height + ", 格式: " + format);

        // 处理图像数据...
    }
}