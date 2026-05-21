package com.yuandaima.peanutrobot.manager;

public class USBCameraManager {
//    public enum CameraState {
//        DISCONNECTED,    // 未连接
//        CONNECTING,      // 连接中
//        CONNECTED,       // 已连接
//        PREVIEWING,      // 预览中
//        RECORDING,       // 录制中
//        ERROR           // 错误状态
//    }
//    private static volatile USBCameraManager instance;
//
//    private USBCameraManager() {}
//
//    public static USBCameraManager getInstance() {
//        if (instance == null) {
//            synchronized (USBCameraManager.class) {
//                if (instance == null) {
//                    instance = new USBCameraManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//
//    private UVCCameraHelper cameraHelper;
//    private CameraState currentState = CameraState.DISCONNECTED;
//    private List<OnStateChangeListener> stateListeners = new ArrayList<>();
//
//    public interface OnStateChangeListener {
//        void onStateChange(CameraState newState);
//    }
//
//    public interface OnPreviewDataCallback {
//        void onPreviewData(byte[] data);
//    }
//
//    private OnPreviewDataCallback previewDataCallback;
//
//
//    public void setOnPreviewDataCallback(OnPreviewDataCallback callback) {
//        this.previewDataCallback = callback;
//    }
//
//
//    public boolean initialize(Activity context, UVCCameraTextureView textureView) {
//        try {
//            cameraHelper = UVCCameraHelper.getInstance();
//
//            cameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
//            cameraHelper.setDefaultPreviewSize(1280, 720);
//            cameraHelper.initUSBMonitor(context, textureView, deviceConnectListener);
//            cameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
//                @Override
//                public void onPreviewResult(byte[] bytes) {
//                    if (previewDataCallback != null) {
//                        previewDataCallback.onPreviewData(bytes);
//                    }
//                }
//            });
//            return true;
//        } catch (Exception e) {
//            Log.e("USBCameraManager", "初始化失败", e);
//            return false;
//        }
//    }
//
//
//
//    public void stopPreview() {
//        if (cameraHelper != null) {
//            cameraHelper.stopPreview();
//        }
//        updateState(CameraState.CONNECTED);
//    }
//
//
//
//
//    public void stopRecording() {
//        if (cameraHelper != null) {
//            cameraHelper.stopPusher();
//        }
//        updateState(CameraState.PREVIEWING);
//    }
//
//
//    public interface OnCaptureCallback {
//        void onCaptureResult(boolean success, String path);
//    }
//
//    /**
//     * 释放资源
//     */
//    public void release() {
//        if (cameraHelper != null) {
//            cameraHelper.release();
//            cameraHelper = null;
//        }
//        updateState(CameraState.DISCONNECTED);
//    }
//
//    // 设备连接监听器
//    private UVCCameraHelper.OnMyDevConnectListener deviceConnectListener = new UVCCameraHelper.OnMyDevConnectListener() {
//        @Override
//        public void onAttachDev(UsbDevice device) {
//            Log.d("USBCameraManager", "检测到USB设备: " + device.getDeviceName());
//            // 自动请求权限
//            if (cameraHelper != null) {
//                cameraHelper.requestPermission(0);
//            }else {
//                Log.d("USBCameraManager", "未检测到USB摄像头设备");
//            }
//        }
//
//        @Override
//        public void onDettachDev(UsbDevice device) {
//            Log.d("USBCameraManager", "USB设备已拔出: " + device.getDeviceName());
//            if (cameraHelper != null) {
//                cameraHelper.closeCamera();
//            }
//            updateState(CameraState.DISCONNECTED);
//        }
//
//        @Override
//        public void onConnectDev(UsbDevice device, boolean isConnected) {
//            if (isConnected) {
//                Log.d("USBCameraManager", "USB摄像头连接成功");
//                updateState(CameraState.CONNECTED);
//            } else {
//                Log.e("USBCameraManager", "USB摄像头连接失败");
//                updateState(CameraState.ERROR);
//            }
//        }
//
//        @Override
//        public void onDisConnectDev(UsbDevice device) {
//            Log.d("USBCameraManager", "USB摄像头断开连接");
//            updateState(CameraState.DISCONNECTED);
//        }
//    };
//
//    private AbstractUVCCameraHandler.OnEncodeResultListener encodeListener = new AbstractUVCCameraHandler.OnEncodeResultListener() {
//        @Override
//        public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
//            // 处理编码数据（H.264视频流或AAC音频流）
//            switch (type) {
//                case 0:
//                    handleAudioData(data, offset, length, timestamp);
//                    break;
//                case 1:
//                    handleVideoData(data, offset, length, timestamp);
//                    break;
//            }
//        }
//
//        @Override
//        public void onRecordResult(String videoPath) {
//            Log.d("USBCameraManager", "视频录制完成: " + videoPath);
//            // 这里可以通知UI层录制完成
//        }
//    };
//
//    private void handleAudioData(byte[] data, int offset, int length, long timestamp) {
//    }
//
//    private void handleVideoData(byte[] data, int offset, int length, long timestamp) {
//    }
//
//    private void updateState(CameraState newState) {
//        if (currentState != newState) {
//            currentState = newState;
//            for (OnStateChangeListener listener : stateListeners) {
//                listener.onStateChange(newState);
//            }
//        }
//    }
//
//    public void addStateListener(OnStateChangeListener listener) {
//        stateListeners.add(listener);
//    }
//
//    public void removeStateListener(OnStateChangeListener listener) {
//        stateListeners.remove(listener);
//    }
//
//    public CameraState getCurrentState() {
//        return currentState;
//    }
}