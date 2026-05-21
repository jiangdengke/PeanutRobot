package com.yuandaima.peanutrobot.activity;

import static com.keenon.common.external.PeanutConfig.getContext;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.yuandaima.peanutrobot.R;
import com.yuandaima.peanutrobot.databinding.ActivityCameraBinding;
import com.yuandaima.peanutrobot.databinding.ActivityMainBinding;
import com.yuandaima.peanutrobot.databinding.ActivityMulticameraBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private CameraManager manager = null;
    private int cameraNumber = 0;
    private ArrayList<String> idList = new ArrayList<>();
    private List<String> requiredPermissions = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private ActivityMulticameraBinding mBinding;
    @RequiresApi(api = Build.VERSION_CODES.R)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multicamera);

        if (checkPermissions()) {
            startInitCamera();
        } else {
            requestPermissions();
        }
    }
    private void startInitCamera() {
        Log.d("openCameraOne", "startInitCamera " );
        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraNumber = manager.getCameraIdList().length;
            for (String id : manager.getCameraIdList()) {
                Log.d("openCameraOne", "cameraid " + id);
                idList.add(id);
            }
        } catch (CameraAccessException e) {
            Log.d("openCameraOne", "CameraAccessException= " + e.getMessage());
            throw new RuntimeException(e);
        }
        Log.d("openCameraOne", "cameraNumber222 " + cameraNumber+",idList="+new Gson().toJson(idList));
        try {

        }catch (Exception e){
            Log.d("openCameraOne", "e= " + e.getMessage());
            e.printStackTrace();
        }
    }
    private boolean checkPermissions() {
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("openCameraOne", "false " );
                return false;
            }
        }
        Log.d("openCameraOne", "true " );
        return true;
    }
    private static final int REQUEST_PERMISSIONS = 1001;
    private void requestPermissions() {
        String[] permissionsArray = requiredPermissions.toArray(new String[0]);
        ActivityCompat.requestPermissions(this, permissionsArray, REQUEST_PERMISSIONS);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d("openCameraOne", "allGranted " );
            } else {
                Log.d("openCameraOne", "showPermissionDeniedDialog " );
            }
        }
    }
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("需要摄像头、录音和存储权限才能使用本应用")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

}

