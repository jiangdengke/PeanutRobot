package com.yuandaima.peanutrobot.fragment

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.MultiCameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.yuandaima.peanutrobot.databinding.FragmentMulticameraBinding
import com.yuandaima.peanutrobot.manager.WebSocketManager

//
//class MyMultiCameraktFragment  : MultiCameraFragment(), ICameraStateCallBack {
//    private val TAG="MyMultiCameraFragment===="
//    private lateinit var mBinding: FragmentMulticameraBinding;
//    private val webSocketManager: WebSocketManager? = null
//    override fun generateCamera(ctx: Context, device: UsbDevice): MultiCameraClient.ICamera {
//        return CameraUVC(ctx, device)
//    }
//
//    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
//        mBinding = FragmentMulticameraBinding.inflate(inflater, container, false)
//        return mBinding.root
//    }
//
//    override fun onCameraAttached(camera: MultiCameraClient.ICamera) {
//        Log.d(TAG,"onCameraAttached");
//    }
//
//    override fun onCameraConnected(camera: MultiCameraClient.ICamera) {
//        Log.d(TAG,"onCameraConnected");
//        camera.openCamera(mBinding.multiCameraTextureView, getCameraRequest());
//        camera.addPreviewDataCallBack(object : IPreviewDataCallBack {
//
//            override fun onPreviewData(
//                data: ByteArray?,
//                width: Int,
//                height: Int,
//                format: IPreviewDataCallBack.DataFormat
//            ) {
//                Log.d(TAG, "bytes=${data?.size ?: 0},name====${format.name},DeviceId===${camera.getUsbDevice().deviceId}")
//            }
//        })
//    }
//
//
//    override fun onCameraDetached(camera: MultiCameraClient.ICamera) {
//        Log.d(TAG,"onCameraDetached");
//
//    }
//
//    override fun onCameraDisConnected(camera: MultiCameraClient.ICamera) {
//        Log.d(TAG,"onCameraDisConnected");
//        camera.closeCamera()
//    }
//
//    override fun onCameraState(
//        self: MultiCameraClient.ICamera,
//        code: ICameraStateCallBack.State,
//        msg: String?
//    ) {
//        Log.d(TAG,"onCameraState");
//
//    }
//
//    private fun getCameraRequest(): CameraRequest {
//        return CameraRequest.Builder()
//            .setPreviewWidth(640)
//            .setPreviewHeight(480)
//            .create()
//    }
//}