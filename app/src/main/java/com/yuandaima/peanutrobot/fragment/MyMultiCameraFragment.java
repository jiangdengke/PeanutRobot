package com.yuandaima.peanutrobot.fragment;

import static android.content.Context.CAMERA_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.jiangdg.ausbc.MultiCameraClient;
import com.jiangdg.ausbc.base.MultiCameraFragment;
import com.jiangdg.ausbc.callback.ICameraStateCallBack;
import com.jiangdg.ausbc.callback.IPreviewDataCallBack;
import com.jiangdg.ausbc.camera.bean.CameraRequest;
import com.yuandaima.peanutrobot.databinding.FragmentMulticameraBinding;
import com.yuandaima.peanutrobot.manager.WebSocketManager;
import com.yuandaima.peanutrobot.util.ByteArrayUtil;
import com.yuandaima.peanutrobot.util.FastYuvConverter;
import com.yuandaima.peanutrobot.util.YuvToJpegCompressorUtil;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okio.ByteString;

public class MyMultiCameraFragment extends MultiCameraFragment {
    private static final String TAG = "MyMulti=====";
    private FragmentMulticameraBinding mBinding;
    private WebSocketManager webSocketManager;
    private WebSocketManager webSocketManager2;
    private WebSocketManager webSocketManager3;
    private CameraManager manager = null;
    private int cameraNumber = 0;
    private ArrayList<String> idList = new ArrayList<>();

    private final Object sendLock = new Object();
    private String cameraOne="";
    private Surface mPreviewSurfaceOne = null;
    private CameraDevice mCameraDeviceOne = null;
    private CameraCaptureSession mCameraCaptureSessionOne= null;
    private ImageReader mImageReader;
    private byte[] mLastFrameData;
    private YuvToJpegCompressorUtil compressor = new YuvToJpegCompressorUtil();
    @Nullable
    @Override
    protected View getRootView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        Log.d(TAG,"getRootView");
        mBinding = FragmentMulticameraBinding.inflate(getLayoutInflater());
        initWebSocketManager();
      //  startInitCamera();
        return mBinding.getRoot();
    }

    @Override
    protected void onCameraAttached(@NonNull MultiCameraClient.Camera camera) {
        Log.d(TAG,"onCameraAttached"+camera.getUsbDevice().getDeviceId());
    }

    @Override
    protected void onCameraConnected(@NonNull MultiCameraClient.Camera camera) {
        Log.d("onCameraConnected=====","onCameraConnected=="+camera.getUsbDevice().getDeviceId()+",name==="+camera.getUsbDevice().getDeviceName()+",class==="+camera.getUsbDevice().getDeviceClass());
            if (camera.getUsbDevice().getDeviceId()==1003){
                camera.openCamera(mBinding.multiCameraTextureView, getCameraRequest());
            }else if (camera.getUsbDevice().getDeviceId()==1005){
                camera.openCamera(mBinding.multiCameraTextureViewTwo, getCameraRequest());
            }else if (camera.getUsbDevice().getDeviceId()==1004){
                camera.openCamera(mBinding.multiCameraTextureViewThr, getCameraRequest());
            }
            else if (camera.getUsbDevice().getDeviceId()==4003){
                camera.openCamera(mBinding.multiCameraTextureViewFour, getCameraRequest());
            }
        camera.setCameraStateCallBack(new ICameraStateCallBack() {
            @Override
            public void onCameraState(@NonNull MultiCameraClient.Camera camera, @NonNull State state, @Nullable String msg) {
                Log.d(TAG, "Camera state: " + state + ", msg: " + msg);
                if (state == ICameraStateCallBack.State.OPENED) {
                    Log.d(TAG,  "摄像头 " + camera.getUsbDevice().getDeviceId() + " 已打开");
                }
            }
        });
        camera.addPreviewDataCallBack(new IPreviewDataCallBack() {
            @Override
            public void onPreviewData(@Nullable byte[] bytes, @NonNull DataFormat dataFormat) {
                Log.d(TAG,"bytes===="+bytes.length+",name===="+dataFormat.name()+",DeviceId==="+camera.getUsbDevice().getDeviceId()+",");
//                synchronized (sendLock) {
//                    if (webSocketManager!=null&&camera.getUsbDevice().getDeviceId()==1005){
//                        byte[] jpegData = compressor.compress(bytes, 640, 480, 60);
//                        webSocketManager.sendMessage(ByteString.of(jpegData));
//                    }
////                    if (webSocketManager2!=null&&camera.getUsbDevice().getDeviceId()==1006){
////                        byte[] jpegData = compressor.compress(bytes, 640, 480, 60);
////                        webSocketManager2.sendMessage(ByteString.of(jpegData));
////                    }
////                    if (webSocketManager3!=null&&camera.getUsbDevice().getDeviceId()==4003){
////                        byte[] jpegData = compressor.compress(bytes, 640, 480, 60);
////                        webSocketManager3.sendMessage(ByteString.of(jpegData));
////                    }
//                }

                //   uploadPreviewAsJpeg(bytes);

            }
        });
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
        try {
            cameraOne= idList.get(3);
            openCameraOne();
        }catch (Exception e){
            Log.d("openCameraOne", "e= " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void openCameraOne() {
        mBinding.multiCameraTextureViewFour.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
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
                        Image image = reader.acquireLatestImage();
                        if (image != null) {
                            // 获取帧数据
                            byte[] data = imageToByteArray(image);
                            if (data != null) {
                                mLastFrameData = data;
                                Log.d("openCameraOne", "获取到帧数据: " + data.length + " bytes");
                                // 在这里处理帧数据
                          //      processFrameData(data);
                            }
                            image.close();
                        }
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
    private void processFrameData(byte[] data) {
        byte[] jpegData = compressor.compress(data, 640, 480, 60);
        webSocketManager3.sendMessage(ByteString.of(jpegData));
    }
    private void initWebSocketManager() {
//        webSocketManager=new WebSocketManager("ws://192.168.112.194:9091");
        webSocketManager=new WebSocketManager("ws://192.168.112.161:9091");
        webSocketManager2=new WebSocketManager("ws://192.168.112.194:9092");
        webSocketManager3=new WebSocketManager("ws://192.168.112.194:9090");
//        webSocketManager=new WebSocketManager("wss://echo.websocket.org");
//        webSocketManager = new WebSocketManager("wss://echo.websocket.org");

        webSocketManager.connect();
        webSocketManager2.connect();
        webSocketManager3.connect();

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
    protected void onCameraDetached(@NonNull MultiCameraClient.Camera camera) {
        Log.d(TAG,"onCameraDetached");
    }

    @Override
    protected void onCameraDisConnected(@NonNull MultiCameraClient.Camera camera) {
        Log.d(TAG,"onCameraDisConnected");
    }

    private CameraRequest getCameraRequest() {
        return new CameraRequest.Builder()
                .setPreviewWidth(640)
                .setPreviewHeight(480)
                .create();
    }

    @Override
    public void onDestroy() {
        compressor.release();
        super.onDestroy();
    }

    // RenderScript方式（性能更好）
    public Bitmap yuvToBitmap(byte[] yuvData, int width, int height) {
        RenderScript rs = RenderScript.create(requireContext());

        ScriptIntrinsicYuvToRGB yuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvData.length);
        Allocation input = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation output = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        input.copyFrom(yuvData);
        yuvToRgb.setInput(input);
        yuvToRgb.forEach(output);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        output.copyTo(bitmap);

        // 释放资源
        input.destroy();
        output.destroy();
        yuvToRgb.destroy();
        rs.destroy();

        return bitmap;
    }
}
