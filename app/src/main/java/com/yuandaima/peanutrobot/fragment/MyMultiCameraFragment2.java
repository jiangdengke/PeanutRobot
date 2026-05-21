package com.yuandaima.peanutrobot.fragment;

import static android.content.Context.CAMERA_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jiangdg.ausbc.camera.bean.CameraRequest;
import com.yuandaima.peanutrobot.databinding.FragmentMulticameraBinding;
import com.yuandaima.peanutrobot.manager.WebSocketManager;
import com.yuandaima.peanutrobot.manager.WebSocketManager2;
import com.yuandaima.peanutrobot.util.FastYuvConverter;
import com.yuandaima.peanutrobot.util.YuvToJpegCompressorUtil;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MyMultiCameraFragment2 extends Fragment {
    private static final String TAG = "MyMulti=====";
    private WebSocketManager webSocketManager;
    private WebSocketManager2 wsManager;
    private WebSocketManager2 wsManager2;
    private WebSocketManager2 wsManager3;
    private WebSocketManager2 wsManager4;
    private WebSocketManager webSocketManager2;
    private WebSocketManager webSocketManager3;
    private WebSocketManager webSocketManager4;
    private CameraManager manager = null;
    private int cameraNumber = 0;
    private ArrayList<String> idList = new ArrayList<>();

    private final Object sendLock = new Object();
    private String cameraOne="";
    private String cameraTwo="";
    private String cameraThree="";
    private String cameraFour="";
    private Surface mPreviewSurfaceOne = null;
    private Surface mPreviewSurfaceTwo = null;
    private Surface mPreviewSurfaceThree = null;
    private Surface mPreviewSurfaceFour = null;
    private CameraDevice mCameraDeviceOne = null;
    private CameraDevice mCameraDeviceTwo = null;
    private CameraDevice mCameraDeviceThree = null;
    private CameraDevice mCameraDeviceFour = null;
    private CameraCaptureSession mCameraCaptureSessionOne= null;
    private CameraCaptureSession mCameraCaptureSessionTwo= null;
    private CameraCaptureSession mCameraCaptureSessionThree= null;
    private CameraCaptureSession mCameraCaptureSessionFour= null;
    private ImageReader mImageReader;
    private ImageReader mImageReaderTwo;
    private ImageReader mImageReaderThree;
    private ImageReader mImageReaderFour;
    private byte[] mLastFrameData;
    private YuvToJpegCompressorUtil compressor = new YuvToJpegCompressorUtil();

    private FragmentMulticameraBinding mBinding;
    private Image image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding =  FragmentMulticameraBinding.inflate(inflater);
        initView();
        initWebSocketManager();
        startInitCamera();
        return mBinding.getRoot();
    }

    private void initView() {
//        mBinding.multiCameraTextureView.setAlpha(0);
//        mBinding.multiCameraTextureViewTwo.setAlpha(0);
//        mBinding.multiCameraTextureViewThr.setAlpha(0);
//        mBinding.multiCameraTextureViewFour.setAlpha(0);
    }


    private void startInitCamera() {
        manager = (CameraManager) getContext().getSystemService(CAMERA_SERVICE);
        try {
            cameraNumber = manager.getCameraIdList().length;

            for (String id : manager.getCameraIdList()) {
                Log.d("openCameraOne", "cameraid " + id);
                idList.add(id);
            }
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
        Log.d("openCameraOne", "cameraNumber " + cameraNumber+",idList="+new Gson().toJson(idList));

        setUpPreView();
    }
    private void setUpPreView() {
        if (cameraNumber > 0) {
            try {
                cameraOne = idList.get(0);
                openCameraOne();
            } catch (Exception e) {
                Log.d("openCameraOne","e="+e.getMessage());
                e.printStackTrace();
            }
        }
        if (cameraNumber > 1) {
            try {
                cameraTwo = idList.get(1);
                openCameraTwo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cameraNumber > 2) {
            try {
                cameraThree = idList.get(2);
                openCameraThree();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cameraNumber > 3) {
            try {
                cameraFour = idList.get(3);
                openCameraFour();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private ExecutorService mFrameExecutor = Executors.newFixedThreadPool(2);
   // private ExecutorService mFrameExecutor = Executors.newSingleThreadExecutor();
    private void openCameraOne() {
        mBinding.multiCameraTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d("openCameraOne", "openCameraOne width" + width + " height" + height);
                surface.setDefaultBufferSize(640, 480);
                mPreviewSurfaceOne = new Surface(surface);
                // 2. 创建 ImageReader 用于获取帧数据
                mImageReader = ImageReader.newInstance(640, 480,
                        ImageFormat.YUV_420_888, 2);
                mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        mFrameExecutor.execute(()->{
                            Image image = reader.acquireLatestImage();
                            if (image != null) {
                                // 获取帧数据
                                byte[] data = imageToByteArray2(image);
                                if (data != null) {
                                    //     mLastFrameData = data;
                                    Log.d("openCameraOne", "获取到帧数据1: " + data.length + " bytes");
                                    // 在这里处理帧数据
                                        processFrameData(data,wsManager,640, 480);
                                    //        sendFrame(data,webSocketManager);
                                }
                                image.close();
                            }
                        });

                    }
                }, null);
                try {
                    manager.openCamera(cameraOne, new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {

                            mCameraDeviceOne = cameraDevice;
                            try {
                                List<Surface> surfaces = Arrays.asList(mPreviewSurfaceOne, mImageReader.getSurface());
                                mCameraDeviceOne.createCaptureSession(
                                        surfaces,
//                                        Arrays.asList(mPreviewSurfaceOne, mImageReader.getSurface()),
                                        new CameraCaptureSession.StateCallback() {
                                            @Override
                                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                                mCameraCaptureSessionOne = session;
                                                try {
                                                    CaptureRequest.Builder builder = mCameraDeviceOne.createCaptureRequest(
                                                            CameraDevice.TEMPLATE_PREVIEW);
                                                    builder.addTarget(mPreviewSurfaceOne);
                                                    builder.addTarget(mImageReader.getSurface());
                                                    mCameraCaptureSessionOne.setRepeatingRequest(
                                                            builder.build(),
                                                            null,
                                                            null
                                                    );
                                                } catch (CameraAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                                // Handle configuration failure
                                            }
                                        },
                                        null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull CameraDevice cameraDevice, int error) {
                            // Handle error
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                            // Handle disconnect
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.d("openCameraOne", "onSurfaceTextureSizeChanged width" + width + " height" + height);
                // Handle size change
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d("openCameraOne", "onSurfaceTextureDestroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.d("openCameraOne", "onSurfaceTextureUpdated");
            }

        });
    }
    private void openCameraTwo() {
        mBinding.multiCameraTextureViewTwo.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d("openCameraOne", "opencameraTwo width" + width + " height" + height);
                surface.setDefaultBufferSize(640, 480);
                mPreviewSurfaceTwo = new Surface(surface);
                // 2. 创建 ImageReader 用于获取帧数据
                mImageReaderTwo = ImageReader.newInstance(640, 480,
                        ImageFormat.YUV_420_888, 2);
                mImageReaderTwo.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        mFrameExecutor.execute(()->{
                            Image image = reader.acquireLatestImage();
                            if (image != null) {
                                // 获取帧数据
                                byte[] data = imageToByteArray2(image);
                                if (data != null) {
                                    //     mLastFrameData = data;
                                    Log.d("openCameraOne", "获取到帧数据2: " + data.length + " bytes");
                                    // 在这里处理帧数据
                                    processFrameData(data,wsManager2,640, 480);
                                    //        sendFrame(data,webSocketManager);
                                }
                                image.close();
                            }
                        });

//                        Image image = reader.acquireLatestImage();
//                        if (image != null) {
//                            // 获取帧数据
//                            byte[] data = imageToByteArray3(image);
//                            if (data != null) {
//                             //   mLastFrameData = data;
//                                Log.d("openCameraOne", "获取到帧数据2: " + data.length + " bytes");
//                                // 在这里处理帧数据
//                                processFrameData(data,wsManager2);
//                            }
//                            image.close();
//                        }
                    }
                }, null);
                try {
                    manager.openCamera(cameraTwo, new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {

                            mCameraDeviceTwo = cameraDevice;
                            try {
                                List<Surface> surfaces = Arrays.asList(mPreviewSurfaceTwo, mImageReaderTwo.getSurface());
                                mCameraDeviceTwo.createCaptureSession(
                                        surfaces,
//                                        Arrays.asList(mPreviewSurfaceOne, mImageReader.getSurface()),
                                        new CameraCaptureSession.StateCallback() {
                                            @Override
                                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                                mCameraCaptureSessionTwo = session;
                                                try {
                                                    CaptureRequest.Builder builder = mCameraDeviceTwo.createCaptureRequest(
                                                            CameraDevice.TEMPLATE_PREVIEW);
                                                    builder.addTarget(mPreviewSurfaceTwo);
                                                    builder.addTarget(mImageReaderTwo.getSurface());
                                                    mCameraCaptureSessionTwo.setRepeatingRequest(
                                                            builder.build(),
                                                            null,
                                                            null
                                                    );
                                                } catch (CameraAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                                // Handle configuration failure
                                            }
                                        },
                                        null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull CameraDevice cameraDevice, int error) {
                            // Handle error
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                            // Handle disconnect
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Handle size change
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Handle update
            }
        });
    }
    private void openCameraThree() {
        mBinding.multiCameraTextureViewThr.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d("openCameraOne", "openCameraThree width" + width + " height" + height);
                surface.setDefaultBufferSize(640, 480);
                mPreviewSurfaceThree = new Surface(surface);
                // 2. 创建 ImageReader 用于获取帧数据
                mImageReaderThree = ImageReader.newInstance(640, 480,
                        ImageFormat.YUV_420_888, 2);
                mImageReaderThree.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        mFrameExecutor.execute(()->{
                            Image image = reader.acquireLatestImage();
                            if (image != null) {
                                // 获取帧数据
                                byte[] data = imageToByteArray2(image);
                                if (data != null) {
                                    //     mLastFrameData = data;
                                    Log.d("openCameraOne", "获取到帧数据3: " + data.length + " bytes");
                                    // 在这里处理帧数据
                                    processFrameData(data,wsManager3,640, 480);
                                    //        sendFrame(data,webSocketManager);
                                }
                                image.close();
                            }
                        });
//                        Image image = reader.acquireLatestImage();
//                        if (image != null) {
//                            // 获取帧数据
//                            byte[] data = imageToByteArray2(image);
//                            if (data != null) {
//                             //   mLastFrameData = data;
//                                Log.d("openCameraOne", "获取到帧数据3: " + data.length + " bytes");
//                                // 在这里处理帧数据
//                                processFrameData(data,wsManager3);
//                            }
//                            image.close();
//                        }
                    }
                }, null);
                try {
                    manager.openCamera(cameraThree, new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {

                            mCameraDeviceThree = cameraDevice;
                            try {
                                List<Surface> surfaces = Arrays.asList(mPreviewSurfaceThree, mImageReaderThree.getSurface());
                                mCameraDeviceThree.createCaptureSession(
                                        surfaces,
//                                        Arrays.asList(mPreviewSurfaceThree, mImageReader.getSurface()),
                                        new CameraCaptureSession.StateCallback() {
                                            @Override
                                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                                mCameraCaptureSessionThree = session;
                                                try {
                                                    CaptureRequest.Builder builder = mCameraDeviceThree.createCaptureRequest(
                                                            CameraDevice.TEMPLATE_PREVIEW);
                                                    builder.addTarget(mPreviewSurfaceThree);
                                                    builder.addTarget(mImageReaderThree.getSurface());
                                                    mCameraCaptureSessionThree.setRepeatingRequest(
                                                            builder.build(),
                                                            null,
                                                            null
                                                    );
                                                } catch (CameraAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                                // Handle configuration failure
                                            }
                                        },
                                        null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull CameraDevice cameraDevice, int error) {
                            // Handle error
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                            // Handle disconnect
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Handle size change
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Handle update
            }
        });
    }
    private void openCameraFour() {
        mBinding.multiCameraTextureViewFour.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d("openCameraOne", "openCameraFour width" + width + " height" + height);
                surface.setDefaultBufferSize(1920, 1080);
                mPreviewSurfaceFour = new Surface(surface);
                // 2. 创建 ImageReader 用于获取帧数据
                mImageReaderFour = ImageReader.newInstance(1920, 1080,
                        ImageFormat.YUV_420_888, 2);
                mImageReaderFour.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                            mFrameExecutor.execute(()->{
                             Image image = reader.acquireLatestImage();
             //           int height1 = image.getHeight();
                           byte[] data = imageToByteArray2(image);
                                if (data!=null){
                                    Log.d("openCameraOne", "获取到帧数据4: " + data.length + " bytes");
                                    processFrameData(data,wsManager4,1920, 1080);
                                }
                                if (image != null) {
                                    image.close();
                                }
//                                if (image != null) {
//                                    // 获取帧数据
//                                    byte[] data = imageToByteArray2(image);
//                                    if (data != null) {
//                                        //     mLastFrameData = data;
//                                        Log.d("openCameraOne", "获取到帧数据4: " + data.length + " bytes");
//                                        // 在这里处理帧数据
//                                    //    processFrameData(data,wsManager4);
//                                    }
//                                    image.close();
//                                }
                            });


//                        Image image = reader.acquireLatestImage();
//                        if (image != null) {
//                            // 获取帧数据
//                            byte[] data = imageToByteArray2(image);
//                            if (data != null) {
//                                mLastFrameData = data;
//                                Log.d("openCameraOne", "获取到帧数据4: " + data.length + " bytes");
//                                // 在这里处理帧数据
//                               // processFrameData(data,webSocketManager4);
//                            }
//                            image.close();
//                        }
                    }
                }, null);
                try {
                    manager.openCamera(cameraFour, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {
                            mCameraDeviceFour = cameraDevice;
                            try {
                                List<Surface> surfaces = Arrays.asList(mPreviewSurfaceFour, mImageReaderFour.getSurface());
                                mCameraDeviceFour.createCaptureSession(
                                        surfaces,
//                                        Arrays.asList(mPreviewSurfaceOne, mImageReader.getSurface()),
                                        new CameraCaptureSession.StateCallback() {
                                            @Override
                                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                                mCameraCaptureSessionFour = session;
                                                try {
                                                    CaptureRequest.Builder builder = mCameraDeviceFour.createCaptureRequest(
                                                            CameraDevice.TEMPLATE_PREVIEW);
                                                    builder.addTarget(mPreviewSurfaceFour);
                                                    builder.addTarget(mImageReaderFour.getSurface());
                                                    mCameraCaptureSessionFour.setRepeatingRequest(
                                                            builder.build(),
                                                            null,
                                                            null
                                                    );
                                                } catch (CameraAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                                // Handle configuration failure
                                            }
                                        },
                                        null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull CameraDevice cameraDevice, int error) {
                            // Handle error
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                            // Handle disconnect
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Handle size change
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Handle update
            }
        });
    }
    public static byte[] yuv420888ToNv21(Image image) {
        if (image == null || image.getFormat() != ImageFormat.YUV_420_888) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width * height;
        int uvSize = ySize / 2;
        byte[] nv21 = new byte[ySize + uvSize];

        // 获取三个平面
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // 获取步长信息
        int yRowStride = yPlane.getRowStride();
        int uvRowStride = vPlane.getRowStride();  // U 和 V 的 rowStride 相同
        int uvPixelStride = vPlane.getPixelStride();

        // ========== 1. 复制 Y 数据（处理行对齐）==========
        int pos = 0;
        if (yRowStride == width) {
            // 无填充，直接复制
            yBuffer.get(nv21, 0, ySize);
            pos = ySize;
        } else {
            // 有填充，逐行复制，跳过填充字节
            byte[] rowBuffer = new byte[yRowStride];
            for (int row = 0; row < height; row++) {
                yBuffer.position(row * yRowStride);
                yBuffer.get(rowBuffer, 0, yRowStride);
                System.arraycopy(rowBuffer, 0, nv21, pos, width);
                pos += width;
            }
        }

        // ========== 2. 复制并交错 UV 数据 ==========
        // NV21 格式：V 在前，U 在后（VU VU VU...）
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        for (int row = 0; row < uvHeight; row++) {
            int uvRowStart = row * uvRowStride;
            for (int col = 0; col < uvWidth; col++) {
                int uvIndex = uvRowStart + col * uvPixelStride;
                // V 在前
                nv21[pos++] = vBuffer.get(uvIndex);
                // U 在后
                nv21[pos++] = uBuffer.get(uvIndex);
            }
        }

        return nv21;
    }
    private byte[] imageToByteArray(Image image) {
        try {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private byte[] imageToByteArray2(Image image) {
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        // 获取三个平面
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int ySize = width * height;
        int uvSize = ySize / 2;  // NV21 中 UV 平面总大小

        byte[] nv21 = new byte[ySize + uvSize];

        // 1. 复制 Y 数据（处理行对齐问题）
        int yRowStride = yPlane.getRowStride();
        if (yRowStride == width) {
            // 理想情况：数据连续
            yBuffer.get(nv21, 0, ySize);
        } else {
            // 有行填充，逐行复制
            int pos = 0;
            for (int row = 0; row < height; row++) {
                yBuffer.position(row * yRowStride);
                yBuffer.get(nv21, pos, width);
                pos += width;
            }
        }

        // 2. 复制并交错 UV 数据（转换为 VU 交错格式）
        int uvRowStride = vPlane.getRowStride();  // V 和 U 的 rowStride 相同
        int uvPixelStride = vPlane.getPixelStride();
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        int uvPos = ySize;
        for (int row = 0; row < uvHeight; row++) {
            for (int col = 0; col < uvWidth; col++) {
                int uvIndex = row * uvRowStride + col * uvPixelStride;
                // NV21 格式：V 在前，U 在后
                nv21[uvPos++] = vBuffer.get(uvIndex);
                nv21[uvPos++] = uBuffer.get(uvIndex);
            }
        }

        return nv21;
    }
    private byte[] imageToByteArray3(Image image) {
        if (image == null) return null;

        int width = image.getWidth();   // 640
        int height = image.getHeight(); // 480

        // 关键：根据尺寸计算，不用 buffer.remaining()
        int ySize = width * height;
        int uvSize = ySize / 2;
        byte[] nv21 = new byte[ySize + uvSize];  // 460800

        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // 复制 Y 数据
        int yRowStride = yPlane.getRowStride();
        int pos = 0;

        if (yRowStride == width) {
            yBuffer.get(nv21, 0, ySize);
            pos = ySize;
        } else {
            byte[] rowBuffer = new byte[yRowStride];
            for (int row = 0; row < height; row++) {
                yBuffer.position(row * yRowStride);
                yBuffer.get(rowBuffer, 0, yRowStride);
                System.arraycopy(rowBuffer, 0, nv21, pos, width);
                pos += width;
            }
        }

        // 复制 UV 数据
        int uvRowStride = vPlane.getRowStride();
        int uvPixelStride = vPlane.getPixelStride();
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        for (int row = 0; row < uvHeight; row++) {
            int uvRowStart = row * uvRowStride;
            for (int col = 0; col < uvWidth; col++) {
                int uvIndex = uvRowStart + col * uvPixelStride;
                nv21[pos++] = vBuffer.get(uvIndex);
                nv21[pos++] = uBuffer.get(uvIndex);
            }
        }

        Log.d("YUV", "NV21 大小: " + nv21.length);  // 应该输出 460800
        return nv21;
    }
    private byte[] imageToNV21YuvImage(Image image) {
        if (image == null || image.getFormat() != ImageFormat.YUV_420_888) {
            return null;
        }

        try {
            // 将 Image 转换为 YuvImage
            YuvImage yuvImage = new YuvImage(
                    imageToByteArray(image),  // 需要先转换成 byte[]
                    ImageFormat.NV21,
                    image.getWidth(),
                    image.getHeight(),
                    null
            );

            // 或者直接使用 Image 转 YUV_420_888
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, outputStream);

        } catch (Exception e) {
            Log.e(TAG, "Conversion failed", e);
        }
        return null;
    }
    private void processFrameData(byte[] data,  WebSocketManager2 wsManager,int width,int height) {
            synchronized (sendLock){
                byte[] jpegData = compressor.compress(data, width, height, 60);
                wsManager.sendMessage(ByteString.of(jpegData));
            }


    }
    private long lastSendTime = 0;
    private static final long MIN_SEND_INTERVAL_MS = 50;  // 最快 20fps

    private void sendFrame(byte[] data, WebSocketManager wsManager) {
        long now = System.currentTimeMillis();
        if (now - lastSendTime < MIN_SEND_INTERVAL_MS) {
            return;  // 发送太快，跳过这一帧
        }
        lastSendTime = now;
        byte[] jpegData = compressor.compress(data, 640, 480, 60);
        wsManager.sendMessage(ByteString.of(jpegData));
    }
    public byte[] compressWithBitmap(byte[] yuvData, int width, int height, int quality) {
        try {
            // 步骤1: YUV -> Bitmap
            Bitmap bitmap = yuvToBitmap2(yuvData, width, height);
            if (bitmap == null) return null;

            // 步骤2: Bitmap -> JPEG (没有内存泄漏)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] jpegData = baos.toByteArray();

            // 步骤3: 回收Bitmap
            bitmap.recycle();

            return jpegData;

        } catch (Exception e) {
            Log.e(TAG, "压缩异常", e);
            return null;
        }
    }

    // YUV转Bitmap的辅助方法
    private Bitmap yuvToBitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = Math.max(16, y);

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                rgba[i * width + j] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(rgba, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void initWebSocketManager() {
         wsManager3 =new WebSocketManager2("ws://192.168.112.194:9090");
        wsManager = new WebSocketManager2("ws://192.168.112.194:9091");
        wsManager2 =new WebSocketManager2("ws://192.168.112.194:9092");
        wsManager4 =new WebSocketManager2("ws://192.168.112.194:9093");
         wsManager.setWebSocketListener(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("WebSocket", "连接成功");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("WebSocket", "连接失败", t);
            }
        });
        wsManager.connect();
        wsManager2.connect();
        wsManager3.connect();
        wsManager4.connect();
    }
    private FastYuvConverter converter;
    private void uploadPreviewAsJpeg(byte[] yuvData) {
        try {
            YuvImage yuvImage = new YuvImage(
                    yuvData,
                    ImageFormat.NV21,
                    1280,
                    720,
                    null
            );
            // 2. 压缩为 JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(
                    new Rect(0, 0, 1280, 720),
                    80,
                    baos
            );
            byte[] jpegData = baos.toByteArray();
            //  if (converter==null)converter = new FastYuvConverter(requireContext());
//            converter = new FastYuvConverter(requireContext());
//            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
//            mBinding.ivView.setImageBitmap(bitmap);

//            Bitmap bitmap2 = converter.convertYuvToBitmap(yuvData, 1280, 720);
//            mBinding.ivView.setImageBitmap(bitmap2);

          baos.close();
            Log.d(TAG, "jpegData ========= " + jpegData);

            Log.d(TAG, "JPEG 数据大小：" + jpegData.length + " 字节");

//            // 3. 通过 WebSocket 发送
            if (webSocketManager != null) {
//                webSocketManager.sendMessage(ByteArrayUtil.byteString(jpegData));
            }

        } catch (Exception e) {
            Log.e(TAG, "JPEG 转换失败", e);
        }
    }


    @Override
    public void onDestroy() {
        try {
            if (mFrameExecutor!=null){
                mFrameExecutor.shutdown();
            }
            if (mCameraDeviceOne!=null)
                mCameraDeviceOne.close();
            if (mCameraDeviceTwo!=null)
                mCameraDeviceTwo.close();
            if (mCameraDeviceThree!=null)
                mCameraDeviceThree.close();
            if (mCameraDeviceFour!=null)
                mCameraDeviceFour.close();

            if (mCameraCaptureSessionOne!=null)
              mCameraCaptureSessionOne.close();
            if (mCameraCaptureSessionTwo!=null)
              mCameraCaptureSessionTwo.close();
            if (mCameraCaptureSessionThree!=null)
              mCameraCaptureSessionThree.close();
            if (mCameraCaptureSessionFour!=null)
              mCameraCaptureSessionFour.close();
        }catch (Exception e){
            e.printStackTrace();
        }


        compressor.release();
        super.onDestroy();
    }


}
