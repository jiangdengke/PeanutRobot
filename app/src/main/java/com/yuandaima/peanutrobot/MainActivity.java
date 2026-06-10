package com.yuandaima.peanutrobot;

import static com.keenon.sdk.external.PeanutSDK.SDK_INIT_SUCCESS;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keenon.common.constant.PeanutConstants;
import com.keenon.common.external.PeanutConfig;
import com.keenon.sdk.component.charger.PeanutCharger;
import com.keenon.sdk.component.charger.common.Charger;
import com.keenon.sdk.component.charger.common.ChargerInfo;
import com.keenon.sdk.component.navigation.PeanutNavigation;
import com.keenon.sdk.component.navigation.common.Navigation;
import com.keenon.sdk.component.navigation.route.RouteNode;
import com.keenon.sdk.component.runtime.PeanutRuntime;
import com.keenon.sdk.component.runtime.RuntimeInfo;
import com.keenon.sdk.constant.TopicName;
import com.keenon.sdk.external.IDataCallback;
import com.keenon.sdk.external.PeanutSDK;
import com.keenon.sdk.hedera.model.ApiError;
import com.yuandaima.peanutrobot.adapter.MediaAdapter;
import com.yuandaima.peanutrobot.adapter.PointAdapter;
import com.yuandaima.peanutrobot.bean.BannerModel;
import com.yuandaima.peanutrobot.bean.ChargeModel;
import com.yuandaima.peanutrobot.bean.DestModel;
import com.yuandaima.peanutrobot.bean.InfoModel;
import com.yuandaima.peanutrobot.bean.MediaModel;
import com.yuandaima.peanutrobot.bean.MyPoint;
import com.yuandaima.peanutrobot.bean.ScreenModel;
import com.yuandaima.peanutrobot.databinding.ActivityMainBinding;

import com.yuandaima.peanutrobot.fragment.MyMultiCameraFragment2;
import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
import com.yuandaima.peanutrobot.manager.NavManager;
import com.yuandaima.peanutrobot.manager.USBCameraManager;
import com.yuandaima.peanutrobot.manager.WebSocketManager;
import com.yuandaima.peanutrobot.presentation.PresentationCoucou;
import com.yuandaima.peanutrobot.server.WebServer;
import com.yuandaima.peanutrobot.server.WebSocketService;
import com.yuandaima.peanutrobot.util.GPIOUtil;
import com.yuandaima.peanutrobot.util.MmkvUtils;
import com.yuandaima.peanutrobot.util.ScrollAndSelectHelper;
import com.yuandaima.peanutrobot.util.TtsUntil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener, Navigation.Listener{
    private String TAG="MainActivity===";
    private static final int ARRIVE_STAY_DURATION = 3000;
    private static final int DEFAULT_NAVIGATION_SPEED = 30;
    private static final long IDLE_LOCK_DELAY_MS = 60 * 1000L;
    private static final long POINT_REFRESH_RETRY_DELAY_MS = 2000L;
    private static final int POINT_REFRESH_MAX_RETRY_COUNT = 10;
    private static final int REQUEST_PICK_IDLE_IMAGE = 2001;
    private static final String WAREHOUSE_TASK_WS = "ws://192.168.112.194:9098";
    private static final int WAREHOUSE_TASK_ROBOT_ID = 3;
    private static final int GO_CHARGE_TASK_ID = 789115;
    private static final int PATROL_WAREHOUSE_TASK_ID = 789110;
    private static final int RECALL_TASK_ID = 789116;
    private static final String GO_CHARGE_ROBOT_TASK_ID = "0000004529";
    private static final String PATROL_WAREHOUSE_ROBOT_TASK_ID = "0000004528";
    private static final int RECALL_ROBOT_TASK_ID = 4528;
    private static final long WAREHOUSE_TASK_RESPONSE_TIMEOUT_MS = 15000L;
    private static final long STARTUP_GO_CHARGE_DELAY_MS = 5000L;
    private static final String KEY_IDLE_IMAGE_URI = "idle_screen_image_uri";
    private static final String KEY_IDLE_IMAGE_ROTATION = "idle_screen_image_rotation";
    private static final String KEY_IDLE_IMAGE_MODE = "idle_screen_image_mode";
    private static final String KEY_IDLE_LOCK_PASSWORD = "idle_lock_password";
    private static final String DEFAULT_IDLE_LOCK_PASSWORD = "123456";
    private static final String IDLE_IMAGE_MODE_FIT = "fit";
    private static final String IDLE_IMAGE_MODE_FILL = "fill";
    private static final String IDLE_IMAGE_MODE_STRETCH = "stretch";
    private static final int IDLE_UNLOCK_TAP_COUNT = 5;
    private static final long IDLE_UNLOCK_TAP_WINDOW_MS = 2500L;
    private static final int IDLE_UNLOCK_HOTSPOT_DP = 120;
    private static final int NORMAL_SYSTEM_UI_FLAGS = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    private static final int IDLE_LOCK_SYSTEM_UI_FLAGS =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    private ActivityMainBinding mBinding;
    private PointAdapter mAdapter,verticalAdapter;
    private MediaAdapter mediaAdapter;
    private List<RouteNode> pointList=new ArrayList<>();
    private List<MyPoint> list = new ArrayList<>();
    private List<MediaModel> mediaModelList = new ArrayList<>();
    private TtsUntil ttsUntil;
    private boolean isPermissionRequested;
    private ScrollAndSelectHelper scrollHelper;


    private Runnable myRunnable;
    private WebSocketService webSocketService;
    private final OkHttpClient warehouseTaskWebSocketClient = new OkHttpClient.Builder().build();
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean idleLocked = false;
    private boolean unlockDialogShowing = false;
    private boolean idleConfigDialogShowing = false;
    private boolean coreInitialized = false;
    private boolean pointUiBound = false;
    private boolean warehouseTaskPending = false;
    private boolean startupGoChargeSent = false;
    private String pendingWarehouseTaskName = "";
    private WebSocket pendingWarehouseTaskWebSocket;
    private int warehouseTaskLoadingStep = 0;
    private int idleUnlockTapCount = 0;
    private long idleUnlockFirstTapTime = 0L;
    private int pointRefreshRetryCount = 0;
    private boolean pointAutoRefreshActive = false;
    private final Runnable idleLockRunnable = new Runnable() {
        @Override
        public void run() {
            showIdleLock();
        }
    };
    private final Runnable pointRefreshRetryRunnable = new Runnable() {
        @Override
        public void run() {
            refreshPointData(false);
        }
    };
    private final Runnable warehouseTaskTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            handleWarehouseTaskFailure(pendingWarehouseTaskName, "接口响应超时");
        }
    };
    private final Runnable warehouseTaskLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!warehouseTaskPending || TextUtils.isEmpty(pendingWarehouseTaskName)) {
                return;
            }
            warehouseTaskLoadingStep = (warehouseTaskLoadingStep + 1) % 4;
            StringBuilder loadingText = new StringBuilder("发送中");
            for (int i = 0; i < warehouseTaskLoadingStep; i++) {
                loadingText.append(".");
            }
            setWarehouseTaskButtonText(pendingWarehouseTaskName, loadingText.toString());
            handler.postDelayed(this, 500);
        }
    };
    private final Runnable startupGoChargeRunnable = new Runnable() {
        @Override
        public void run() {
            sendStartupGoChargeTask();
        }
    };
    private DestModel destModel=new DestModel();
    private List<DestModel.DataBean> testData;
    private List<RouteNode> routeNodes;
    private PeanutNavigation peanutNavigation;
    private DisplayManager displayManager;
    private USBCameraManager cameraManager ;
    private WebSocketManager webSocketManager;
    private String flag;
    private Runnable uploadRunnable = new Runnable() {
        @Override
        public void run() {
            uploadInfo();
            handler.postDelayed(this, 5000);
        }
    };
    private JSONObject jsonObject;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = getWindow();
        window.addFlags(-2147483648);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        window.getDecorView().setSystemUiVisibility(NORMAL_SYSTEM_UI_FLAGS);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mInitView();
        initListener();
        initIdleLock();
        requestPermission();
        if (checkPermissions()) {
            startRobotCore();
        }

//        if (checkPermissions()) {
//            initFragmentLayout();
//        } else {
//            requestPermissions();
//        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(uploadRunnable, 10000);
        if (idleLocked) {
            enterIdleLockFullscreen();
        }
        resetIdleLockTimer();
    }
    boolean mFlag=false;

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(uploadRunnable);
        handler.removeCallbacks(idleLockRunnable);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!idleLocked && ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            resetIdleLockTimer();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && idleLocked) {
            enterIdleLockFullscreen();
        }
    }

    private void initUSBCameraManager() {
        //  cameraManager  = USBCameraManager.getInstance();
    }
    MediaPlayer mediaPlayer;
    private  void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        //  mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    private void initFragmentLayout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MyMultiCameraFragment2())
                .commit();
    }

    private void startRobotCore() {
        if (coreInitialized) {
            return;
        }
        coreInitialized = true;
        initSubscribe();
        if (!initSDK(PeanutConstants.REMOTE_LINK_PROXY)) {
            coreInitialized = false;
            return;
        }
        initTts();
        initUSBCameraManager();
        initWebSocket();
        initMediaPlayer();
        initCharger();
    }

    private void initIdleLock() {
        applyIdleScreenConfig();
        mBinding.flIdleLock.setOnTouchListener((v, event) -> handleIdleLockTouch(event));
        mBinding.tvIdleUnlockHint.setVisibility(View.GONE);
        mBinding.tvIdleScreenLock.setOnClickListener(v -> showIdleLock());
        mBinding.tvIdleScreenSettings.setOnClickListener(v -> showIdleScreenSettingsDialog());
        hideIdleLock();
    }

    private void showIdleLock() {
        enterIdleLockFullscreen();
        idleLocked = true;
        resetHiddenUnlockTapState();
        handler.removeCallbacks(idleLockRunnable);
        applyIdleScreenConfig();
        mBinding.flIdleLock.setVisibility(View.VISIBLE);
        mBinding.flIdleLock.bringToFront();
    }

    private void hideIdleLock() {
        idleLocked = false;
        resetHiddenUnlockTapState();
        mBinding.flIdleLock.setVisibility(View.GONE);
        exitIdleLockFullscreen();
        resetIdleLockTimer();
    }

    private void resetIdleLockTimer() {
        handler.removeCallbacks(idleLockRunnable);
        if (!idleLocked && !idleConfigDialogShowing) {
            handler.postDelayed(idleLockRunnable, IDLE_LOCK_DELAY_MS);
        }
    }

    private void applyIdleScreenConfig() {
        String imageUri = MmkvUtils.decodeString(KEY_IDLE_IMAGE_URI);
        try {
            if (!TextUtils.isEmpty(imageUri)) {
                mBinding.ivIdleScreen.setImageURI(Uri.parse(imageUri));
            } else {
                mBinding.ivIdleScreen.setImageResource(R.mipmap.ic_launcher);
            }
        } catch (Exception e) {
            Log.e(TAG, "屏保图片加载失败: " + e.getMessage());
            mBinding.ivIdleScreen.setImageResource(R.mipmap.ic_launcher);
        }

        mBinding.ivIdleScreen.setRotation(MmkvUtils.decodeFloat(KEY_IDLE_IMAGE_ROTATION));
        String mode = MmkvUtils.decodeString(KEY_IDLE_IMAGE_MODE);
        if (IDLE_IMAGE_MODE_STRETCH.equals(mode)) {
            mBinding.ivIdleScreen.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if (IDLE_IMAGE_MODE_FIT.equals(mode)) {
            mBinding.ivIdleScreen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            mBinding.ivIdleScreen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private boolean handleIdleLockTouch(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_UP) {
            return true;
        }

        if (!isInHiddenUnlockHotspot(event)) {
            resetHiddenUnlockTapState();
            return true;
        }

        long now = System.currentTimeMillis();
        if (idleUnlockFirstTapTime == 0L || now - idleUnlockFirstTapTime > IDLE_UNLOCK_TAP_WINDOW_MS) {
            idleUnlockFirstTapTime = now;
            idleUnlockTapCount = 1;
        } else {
            idleUnlockTapCount++;
        }

        if (idleUnlockTapCount >= IDLE_UNLOCK_TAP_COUNT) {
            resetHiddenUnlockTapState();
            showUnlockDialog();
        }
        return true;
    }

    private boolean isInHiddenUnlockHotspot(MotionEvent event) {
        int hotspotSize = dp(IDLE_UNLOCK_HOTSPOT_DP);
        return event.getX() >= 0
                && event.getY() >= 0
                && event.getX() <= hotspotSize
                && event.getY() <= hotspotSize;
    }

    private void resetHiddenUnlockTapState() {
        idleUnlockTapCount = 0;
        idleUnlockFirstTapTime = 0L;
    }

    private void enterIdleLockFullscreen() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(IDLE_LOCK_SYSTEM_UI_FLAGS);
    }

    private void exitIdleLockFullscreen() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));
        window.setNavigationBarColor(Color.BLACK);
        window.getDecorView().setSystemUiVisibility(NORMAL_SYSTEM_UI_FLAGS);
    }

    private void showUnlockDialog() {
        if (unlockDialogShowing) {
            return;
        }
        EditText passwordInput = new EditText(this);
        passwordInput.setSingleLine(true);
        passwordInput.setHint("请输入密码");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int padding = dp(24);
        passwordInput.setPadding(padding, padding / 2, padding, padding / 2);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("输入密码")
                .setView(passwordInput)
                .setPositiveButton("进入", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            unlockDialogShowing = true;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String password = passwordInput.getText().toString();
                if (getIdleLockPassword().equals(password)) {
                    dialog.dismiss();
                    hideIdleLock();
                } else {
                    passwordInput.setError("密码错误");
                    passwordInput.selectAll();
                }
            });
        });
        dialog.setOnDismissListener(dialogInterface -> unlockDialogShowing = false);
        dialog.show();
    }

    private String getIdleLockPassword() {
        String password = MmkvUtils.decodeString(KEY_IDLE_LOCK_PASSWORD);
        return TextUtils.isEmpty(password) ? DEFAULT_IDLE_LOCK_PASSWORD : password;
    }

    private void showIdleScreenSettingsDialog() {
        handler.removeCallbacks(idleLockRunnable);
        idleConfigDialogShowing = true;

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(24);
        content.setPadding(padding, padding / 2, padding, 0);

        TextView imageStatus = new TextView(this);
        imageStatus.setText(TextUtils.isEmpty(MmkvUtils.decodeString(KEY_IDLE_IMAGE_URI))
                ? "当前图片：默认图片"
                : "当前图片：已选择");
        imageStatus.setTextSize(16);
        content.addView(imageStatus);

        Button chooseImageButton = new Button(this);
        chooseImageButton.setText("选择屏保图片");
        chooseImageButton.setAllCaps(false);
        chooseImageButton.setOnClickListener(v -> openIdleImagePicker());
        content.addView(chooseImageButton);

        TextView rotationLabel = new TextView(this);
        rotationLabel.setText("旋转角度");
        rotationLabel.setTextSize(16);
        rotationLabel.setPadding(0, dp(12), 0, 0);
        content.addView(rotationLabel);

        EditText rotationInput = new EditText(this);
        rotationInput.setSingleLine(true);
        rotationInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        rotationInput.setText(String.valueOf(MmkvUtils.decodeFloat(KEY_IDLE_IMAGE_ROTATION)));
        content.addView(rotationInput);

        TextView modeLabel = new TextView(this);
        modeLabel.setText("显示模式");
        modeLabel.setTextSize(16);
        modeLabel.setPadding(0, dp(12), 0, 0);
        content.addView(modeLabel);

        RadioGroup modeGroup = new RadioGroup(this);
        modeGroup.setOrientation(RadioGroup.VERTICAL);
        int fitId = View.generateViewId();
        int fillId = View.generateViewId();
        int stretchId = View.generateViewId();
        RadioButton fitButton = new RadioButton(this);
        fitButton.setId(fitId);
        fitButton.setText("自适应");
        RadioButton fillButton = new RadioButton(this);
        fillButton.setId(fillId);
        fillButton.setText("铺满");
        RadioButton stretchButton = new RadioButton(this);
        stretchButton.setId(stretchId);
        stretchButton.setText("拉伸");
        modeGroup.addView(fitButton);
        modeGroup.addView(fillButton);
        modeGroup.addView(stretchButton);

        String mode = MmkvUtils.decodeString(KEY_IDLE_IMAGE_MODE);
        if (IDLE_IMAGE_MODE_STRETCH.equals(mode)) {
            modeGroup.check(stretchId);
        } else if (IDLE_IMAGE_MODE_FIT.equals(mode)) {
            modeGroup.check(fitId);
        } else {
            modeGroup.check(fillId);
        }
        content.addView(modeGroup);

        TextView passwordLabel = new TextView(this);
        passwordLabel.setText("修改进入密码");
        passwordLabel.setTextSize(16);
        passwordLabel.setPadding(0, dp(12), 0, 0);
        content.addView(passwordLabel);

        EditText passwordInput = new EditText(this);
        passwordInput.setSingleLine(true);
        passwordInput.setHint("留空则不修改");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        content.addView(passwordInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("屏保设置")
                .setView(content)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                String rotationText = rotationInput.getText().toString().trim();
                float rotation = TextUtils.isEmpty(rotationText) ? 0f : Float.parseFloat(rotationText);
                MmkvUtils.encode(KEY_IDLE_IMAGE_ROTATION, rotation);
            } catch (NumberFormatException e) {
                rotationInput.setError("请输入数字");
                return;
            }

            int checkedId = modeGroup.getCheckedRadioButtonId();
            if (checkedId == fillId) {
                MmkvUtils.encode(KEY_IDLE_IMAGE_MODE, IDLE_IMAGE_MODE_FILL);
            } else if (checkedId == stretchId) {
                MmkvUtils.encode(KEY_IDLE_IMAGE_MODE, IDLE_IMAGE_MODE_STRETCH);
            } else {
                MmkvUtils.encode(KEY_IDLE_IMAGE_MODE, IDLE_IMAGE_MODE_FIT);
            }

            String newPassword = passwordInput.getText().toString();
            if (!TextUtils.isEmpty(newPassword)) {
                MmkvUtils.encode(KEY_IDLE_LOCK_PASSWORD, newPassword);
            }

            applyIdleScreenConfig();
            dialog.dismiss();
        }));
        dialog.setOnDismissListener(dialogInterface -> {
            idleConfigDialogShowing = false;
            resetIdleLockTimer();
        });
        dialog.show();
    }

    private void openIdleImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PICK_IDLE_IMAGE);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void initWebSocketManager() {
        webSocketManager=new WebSocketManager("ws://192.168.78.19:9096");
        webSocketManager.connect();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static String getUsbPathPrefix(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        if (storageManager != null) {
            for (StorageVolume volume : storageManager.getStorageVolumes()) {
                if (volume.isRemovable() && volume.getState().equals(Environment.MEDIA_MOUNTED)) {
                    if (volume.getDirectory() != null) {
                        return volume.getDirectory().getAbsolutePath();  // 如：/storage/XXXX-XXXX
                    }

                    try {
                        Method getPathMethod = StorageVolume.class.getMethod("getPath");
                        String path = (String) getPathMethod.invoke(volume);
                        return path;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
    public List<StorageVolume> getStorageVolumesCompat() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 新版本直接调用
            return storageManager.getStorageVolumes();
        } else {
            // 低版本使用反射
            try {
                Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
                Object[] volumeList = (Object[]) getVolumeList.invoke(storageManager);

                // 将 Object[] 转换为 List<StorageVolume>
                for (Object obj : volumeList) {
                    if (obj instanceof StorageVolume) {
                        volumes.add((StorageVolume) obj);
                    }
                }
            } catch (Exception e) {
                Log.d("usbPath","e="+e.getMessage());

                e.printStackTrace();
                // 反射失败时的备用方案
            }
        }
        return volumes;
    }


    private void initSubscribe() {
        PeanutSDK.getInstance().subscribe(TopicName.BUTTON_STATUS,buttonStatusCallback);
        PeanutSDK.getInstance().subscribe(TopicName.CHARGE_MATCH_TIMES,chargeCallback );
        PeanutSDK.getInstance().subscribe(TopicName.NAVIGATION_PATH,navigationCallback );

    }



    private List<String> requiredPermissions = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private static final int REQUEST_PERMISSIONS = 1001;
    private boolean checkPermissions() {
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
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
                startRobotCore();
            } else {

                showPermissionDeniedDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IDLE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                if (takeFlags != 0) {
                    getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                }
            } catch (Exception e) {
                Log.e(TAG, "保存屏保图片访问权限失败: " + e.getMessage());
            }
            MmkvUtils.encode(KEY_IDLE_IMAGE_URI, imageUri.toString());
            applyIdleScreenConfig();
            tip("屏保图片已更新");
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



    public static enum MediaType {
        IMAGE,
        VIDEO
    }
    private WebServer server;
    private List<RouteNode> response=new ArrayList();

    private void initWebSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                server = new WebServer(9095,MainActivity.this);
                try {
                    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    server.setWebCallback(new WebServer.WebCallback() {
                        @Override
                        public void onMessage(String text,String url) {
                            Log.d("MyServer","url=="+url+",text=="+text);
                            switch (url){
                                case "/robot_task/go_to_charge":
                                    flag="go_to_charge";
                                    ChargeModel chargeModel = new Gson().fromJson(text, new TypeToken<ChargeModel>(){}.getType());
                                    Log.d("Charger===","ChargeModel=="+new Gson().toJson(chargeModel));
                                    NavManager.getInstance().stop();
                                    //   NavManager.getInstance().release();
                                    mPeanutCharger.setPile(Integer.parseInt(chargeModel.getData().get(0).getId()));
                                    mPeanutCharger.execute();
                                    mPeanutCharger.performAction(PeanutCharger.CHARGE_ACTION_AUTO);
                                    break;
                                case "/robot_task/send_point":
                                    if (mPeanutCharger!=null){
                                        Log.d("navigatenext","CHARGE_ACTION_STOP");
                                        mPeanutCharger.performAction(PeanutCharger.CHARGE_ACTION_STOP);
                                    }
                                    flag="send_point";
                                    routeNodes = new Gson().fromJson(text, new TypeToken<List<RouteNode>>(){}.getType());
                                    Log.d("navigatenext","send_poin=="+new Gson().toJson(routeNodes));
                                    prepareNav(routeNodes);
                                    break;
                                case "/robot_task/send_stop":
                                    Log.d("MyServer","send_stop==");
                                    NavManager.getInstance().stop();
                                    break;
                                case "/robot_task/screen_control":
                                    ScreenModel screenModel = new Gson().fromJson(text, new TypeToken<ScreenModel>(){}.getType());
                                    Log.d("presentation","screenModel="+new Gson().toJson(screenModel));
                                    startHardwareTests(screenModel,null,MediaType.VIDEO);
                                    break;
                                case "/robot_task/screen_control_img_display":
                                    BannerModel bannerModelList = new Gson().fromJson(text, new TypeToken<BannerModel>(){}.getType());
                                    Log.d("presentation","  bannerModelList="+new Gson().toJson( bannerModelList));
                                    startHardwareTests(null,bannerModelList,MediaType.IMAGE);
                                    break;
                                case "/robot_task/single_point_speed":
                                    jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(text);
                                        int speed = jsonObject.optInt("speed",-1);
                                        if (speed!=-1){
                                            MmkvUtils.encode("single_point_speed",speed);
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                case "/robot_task/multiple_point_speed":
                                    jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(text);
                                        int speed2 = jsonObject.optInt("speed",-1);
                                        if (speed2!=-1){
                                            MmkvUtils.encode("multiple_point_speed",speed2);
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                case "/robot_task/save_voice_address":
                                    jsonObject= null;
                                    Log.d("navigatenext","save_voice_address_text="+text);
                                    try {
                                        jsonObject = new JSONObject(text);
                                        String video = jsonObject.optString("video","");
                                        Log.d("navigatenext","save_voice_address="+video);
                                        if (!TextUtils.isEmpty(video)){
                                            MmkvUtils.encode("save_voice_address",video);
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                            }

                        }
                    });
                } catch (IOException e) {
                    Log.e("WebServer", "启动失败", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 显示错误信息
                            // statusTextView.setText("服务器启动失败");
                        }
                    });
                }
            }
        }).start();



        bindService(new Intent(this, WebSocketService.class), serviceConnection, BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webSocketService = ((WebSocketService.LocalBinder) service).getService();
            webSocketService.setWebSocketCallback(webSocketCallback);
            //  uploadInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            webSocketService = null;
        }
    };

    private WebSocketService.WebSocketCallback webSocketCallback = new WebSocketService.WebSocketCallback() {
        @Override
        public void onMessage(final String responseData) {

        }

        @Override
        public void onMessage(ByteString bytes) {
            Log.d("websocket=======","onMessage=="+bytes.toString());
            //  startHardwareTests(null,null,null);
        }

        @Override
        public void onOpen() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"onOpen");
                }
            });
        }

        @Override
        public void onClosed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"onClosed");
                }
            });
        }
    };
    private void initNavManager() {
        Log.d("routeNodes","initNavManager");
        NavManager.getInstance().init(MainActivity.this, 3000, 2, true);
        // routeNodes = NavManager.getInstance().getRouteNodes();
        startPointAutoRefresh();
//
    }

    private void initTts() {
        ttsUntil = TtsUntil.getInstance();
        ttsUntil.initTts(MainActivity.this, () -> {

        });
        getLifecycle().addObserver(ttsUntil);
    }

    private void initListener() {
        if (pointUiBound) {
            return;
        }
        pointUiBound = true;
        mBinding.tvNavigate.setOnClickListener(this);
        mBinding.tvSecondaryScreenDisplay.setOnClickListener(this);
        mBinding.tvRefreshPoints.setOnClickListener(this);
        mBinding.tvGoCharge.setOnClickListener(this);
        mBinding.tvPatrolWarehouse.setOnClickListener(this);
        mBinding.tvRecall.setOnClickListener(this);

        PeanutRuntime.getInstance().registerListener(mRuntimeListener);
        mAdapter.setOnClickItemListener(new OnItemClickListener() {
            @Override
            public void onClick(int position,boolean isSelect) {
                // 1. 平滑滚动到指定位置
                //        mBinding.rvVerticalPoint.smoothScrollToPosition(position);
                scrollHelper.smoothScrollToPositionAndSelect(position, isSelect,()->{
                    View viewByPosition = verticalAdapter.getViewByPosition(position, R.id.tv_point);
                    viewByPosition.setSelected(isSelect);
                    verticalAdapter.notifyDataSetChanged();
                });

            }
        });


        verticalAdapter.setOnClickItemListener(new OnItemClickListener() {
            @Override
            public void onClick(int position,boolean isSelect) {
                View viewByPosition = mAdapter.getViewByPosition(position, R.id.tv_point);
                viewByPosition.setSelected(isSelect);
                verticalAdapter.notifyDataSetChanged();
            }
        });





    }


    private void mInitView() {
        List<DestModel.DataBean> displayData = getDisplayPointData();
        boolean hasPointData = !displayData.isEmpty();
        mBinding.tvPointEmpty.setVisibility(hasPointData ? View.GONE : View.VISIBLE);

        // List<RouteNode> nodeList = Arrays.asList(routeNodes);
        //pointList
        if (mAdapter == null) {
            mAdapter = new PointAdapter(new ArrayList<>(displayData));
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
            mBinding.rvPoint.setLayoutManager(gridLayoutManager);
            mBinding.rvPoint.setAdapter(mAdapter);
        } else {
            replacePointData(mAdapter, displayData);
        }
        // mediaAdapter=new MediaAdapter(mediaModelList,MainActivity.this);
        //  mAdapter= new PointAdapter(testData);
        if (verticalAdapter == null) {
            verticalAdapter = new PointAdapter(new ArrayList<>(displayData));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mBinding.rvVerticalPoint.setLayoutManager(linearLayoutManager);
            mBinding.rvVerticalPoint.setAdapter(verticalAdapter);
        } else {
            replacePointData(verticalAdapter, displayData);
        }
        scrollHelper = new ScrollAndSelectHelper(
                mBinding.rvVerticalPoint,
                verticalAdapter
        );
        //    verticalAdapter= new PointAdapter(testData);
        mAdapter.setTtsUtil(ttsUntil);
    }

    private List<DestModel.DataBean> getDisplayPointData() {
        if (destModel != null && destModel.getData() != null && !destModel.getData().isEmpty()) {
            return destModel.getData();
        }
        return new ArrayList<>();
    }

    private List<DestModel.DataBean> parseRemotePointData(String destList) {
        List<DestModel.DataBean> result = new ArrayList<>();
        if (TextUtils.isEmpty(destList)) {
            return result;
        }
        try {
            DestModel remoteModel = new Gson().fromJson(destList, DestModel.class);
            if (remoteModel != null && remoteModel.getData() != null) {
                result.addAll(remoteModel.getData());
            }
        } catch (Exception e) {
            Log.e(TAG, "解析机器人点位失败: " + e.getMessage(), e);
        }
        return result;
    }

    private void startPointAutoRefresh() {
        pointRefreshRetryCount = 0;
        pointAutoRefreshActive = true;
        handler.removeCallbacks(pointRefreshRetryRunnable);
        handler.postDelayed(pointRefreshRetryRunnable, 1000);
    }

    private void refreshPointData(boolean manualRefresh) {
        if (manualRefresh) {
            pointRefreshRetryCount = 0;
            pointAutoRefreshActive = true;
            handler.removeCallbacks(pointRefreshRetryRunnable);
            mBinding.tvPointEmpty.setText("正在刷新点位");
            tip("正在刷新点位");
        }

        String destList = null;
        RuntimeInfo runtimeInfo = PeanutRuntime.getInstance().getRuntimeInfo();
        if (runtimeInfo != null) {
            destList = runtimeInfo.getDestList();
        }

        List<DestModel.DataBean> remotePointData = parseRemotePointData(destList);
        if (!remotePointData.isEmpty()) {
            destModel.setData(remotePointData);
            mBinding.tvPointEmpty.setText("未获取到点位");
            pointAutoRefreshActive = false;
            handler.removeCallbacks(pointRefreshRetryRunnable);
            if (manualRefresh) {
                tip("点位已刷新：" + remotePointData.size() + "个");
            }
        } else {
            destModel.setData(new ArrayList<>());
            mBinding.tvPointEmpty.setText("未获取到点位");
            if (manualRefresh) {
                tip("未获取到点位");
            }
            if (pointAutoRefreshActive && pointRefreshRetryCount < POINT_REFRESH_MAX_RETRY_COUNT) {
                pointRefreshRetryCount++;
                handler.postDelayed(pointRefreshRetryRunnable, POINT_REFRESH_RETRY_DELAY_MS);
            } else {
                pointAutoRefreshActive = false;
            }
        }

        mInitView();
        initListener();
        Log.d("routeNodes", "refreshPointData manual=" + manualRefresh
                + ", retry=" + pointRefreshRetryCount
                + ", destList=" + destList);
    }

    private void replacePointData(PointAdapter adapter, List<DestModel.DataBean> data) {
        if (adapter == null) {
            return;
        }
        List<DestModel.DataBean> target = adapter.getData();
        target.clear();
        if (data != null) {
            target.addAll(data);
        }
        adapter.notifyDataSetChanged();
    }


    private boolean initSDK(String ip) {
        try {
            PeanutConfig.getConfig()
                    .setLinkType(PeanutConstants.REMOTE_LINK_PROXY.equals(ip) ? PeanutConstants.LinkType.COAP : PeanutConstants.LinkType.COM_COAP)
                    .setLinkIP(ip)
                    .enableLog(true)
                    .setLogLevel(Log.DEBUG)
                    .setAppId("353110c9f77947da8fd26a908a80d866")
                    .setSecret("nPlQERTP4qKBBpfoMxMFtgNJG1eC1nU6U7nA2g0eUNVS/iKjcTxL3ZtGLebwINKLAx/kACtCq7UBvt1QCODovm2gq7dsXAK4pgjBRK2OqQF0SMvBNMxjqVFh73zzI8tCP12D+eCfG2WMCUu4EVyaBYg6sD7FzGWL")
                    .enableUMLog(false);
            PeanutSDK.getInstance().init(this.getApplicationContext(), mErrorListener);
            return true;
        } catch (RuntimeException e) {
            Log.e(TAG, "PeanutSDK init failed: " + e.getMessage(), e);
            tip("启动失败，请先授权存储权限");
            return false;
        }



    }

    /**
     * 初始化充电模组
     */
    private void initCharger() {
        //初始化
        mPeanutCharger = new PeanutCharger.Builder()
                .setListener(listener)
                //.setPile(Integer.parseInt(chargeModel.getData().get(0).getId()))
                .build();
    }



    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.READ_PHONE_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), REQUEST_PERMISSIONS);
            }
        }
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(idleLockRunnable);
        handler.removeCallbacks(pointRefreshRetryRunnable);
        PeanutSDK.getInstance().release();
        NavManager.getInstance().stop();
        NavManager.getInstance().release();
        PeanutRuntime.getInstance().removeListener(mRuntimeListener);
        if (mPeanutCharger != null) {
            mPeanutCharger.release();
        }
        super.onDestroy();
    }

    private void addPoint(TextView editText, boolean manualNext) {
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            MyPoint point = new MyPoint();
            point.setManualControl(manualNext);
            point.setDuration(ARRIVE_STAY_DURATION);
            RouteNode node = new RouteNode();
            node.setId(Integer.parseInt(editText.getText().toString()));
            node.setName("Point:" + editText.getText().toString());
            point.setRouteNode(node);
            list.add(point);
        }
    }
    private PeanutSDK.ErrorListener mErrorListener = errorCode -> {
        Log.d(TAG, "onInit:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (errorCode == SDK_INIT_SUCCESS) {
                    Log.d("routeNodes","SDK_INIT_SUCCESS:"+errorCode);
                    initNavManager();
                    scheduleStartupGoChargeTask();
                    tip("SDK_INIT_SUCCESS");
                    PeanutRuntime.getInstance().start(new PeanutRuntime.Listener() {
                        @Override
                        public void onEvent(int event, Object obj) {

                        }

                        @Override
                        public void onHealth(Object content) {

                        }

                        @Override
                        public void onHeartbeat(Object content) {

                        }
                    });
                } else {
                    Log.d("routeNodes","SDK_INIT_FAIL:"+errorCode);
                    tip("SDK_INIT_FAIL:"+errorCode);
                }
            }
        });
    };
    private PeanutRuntime.Listener mRuntimeListener = new PeanutRuntime.Listener() {
        @Override
        public void onEvent(int event, Object obj) {
            Log.d(TAG, "onEvent:" + event + ", content: " + obj);


        }

        @Override
        public void onHealth(Object content) {
            Log.d(TAG, "onHealth:" + content);

        }

        @Override
        public void onHeartbeat(Object content) {
            Log.d(TAG, "onHeartbeat:" + content);

        }
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==mBinding.tvNavigate.getId()){
            if (mAdapter == null || mAdapter.getData().isEmpty()) {
                tip("未获取到点位");
                return;
            }
            flag="";
            mBinding.tvNavigate.setEnabled(false);
            List<DestModel.DataBean> routeNodeList = mAdapter.getData();


//            List<RouteNode> routeNodes = IntStream.range(0, routeNodeList.size())
//                    .filter(i -> {
//                        View view = mAdapter.getViewByPosition(i, R.id.tv_point);
//                        return view != null && view.isSelected();
//                    })
//                    .mapToObj(i -> {
//                        DestModel.DataBean dataBean = routeNodeList.get(i);
//                        RouteNode routeNode = new RouteNode();
//                        routeNode.setId(dataBean.getId());
//                        routeNode.setName(dataBean.getName());
//                        // routeNode.setOtherProperty(dataBean.getOtherProperty());
//                        return routeNode;
//                    })
//                    .collect(Collectors.toList());


            routeNodes = new ArrayList<>();

            for (DestModel.DataBean dataBean : routeNodeList) {
                int position = routeNodeList.indexOf(dataBean);
                View view = mAdapter.getViewByPosition(position, R.id.tv_point);

                if (view != null && view.isSelected()) {
                    RouteNode routeNode = new RouteNode();
                    routeNode.setId(dataBean.getId());
                    routeNode.setName(dataBean.getName());
                    routeNodes.add(routeNode);
                }
            }

            if (routeNodes.isEmpty()){
                tip("请选择点位");
                return;
            }
            Log.d("navigatenext","routeNodes===="+new Gson().toJson(routeNodes)+",size="+routeNodes.size());
//            RouteNode node = new RouteNode();
//            node.setId(Integer.parseInt(editText.getText().toString()));
//            node.setName("Point:" + editText.getText().toString());
//            point.setRouteNode(node);
            prepareNav(routeNodes);

        }else if (id==mBinding.tvSecondaryScreenDisplay.getId()){
            //   startHardwareTests(null);
        }else if (id==mBinding.tvRefreshPoints.getId()){
            refreshPointData(true);
        }else if (id==mBinding.tvGoCharge.getId()){
            sendGoChargeTask();
        }else if (id==mBinding.tvPatrolWarehouse.getId()){
            sendPatrolWarehouseTask();
        }else if (id==mBinding.tvRecall.getId()){
            sendRecallTask();
        }
    }

    private void sendGoChargeTask() {
        sendGoChargeTask(false);
    }

    private void sendGoChargeTask(boolean startupAutoCharge) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("robot_id", WAREHOUSE_TASK_ROBOT_ID);
            payload.put("task_id", GO_CHARGE_TASK_ID);
            payload.put("robot_task_id", GO_CHARGE_ROBOT_TASK_ID);
            sendWarehouseTask("回充", payload.toString(), startupAutoCharge ? "回充发送完毕" : null);
        } catch (JSONException e) {
            Log.e(TAG, "创建回充指令失败", e);
            tip("回充指令创建失败");
        }
    }

    private void scheduleStartupGoChargeTask() {
        if (startupGoChargeSent) {
            return;
        }
        handler.removeCallbacks(startupGoChargeRunnable);
        handler.postDelayed(startupGoChargeRunnable, STARTUP_GO_CHARGE_DELAY_MS);
    }

    private void sendStartupGoChargeTask() {
        if (startupGoChargeSent) {
            return;
        }
        if (warehouseTaskPending) {
            Log.d(TAG, "skip startup go charge: warehouse task pending");
            return;
        }

        startupGoChargeSent = true;
        Log.d(TAG, "send startup go charge task");
        sendGoChargeTask(true);
    }

    private void sendPatrolWarehouseTask() {
        try {
            JSONObject payload = new JSONObject();
            payload.put("robot_id", WAREHOUSE_TASK_ROBOT_ID);
            payload.put("task_id", PATROL_WAREHOUSE_TASK_ID);
            payload.put("is_return", true);
            payload.put("robot_task_id", PATROL_WAREHOUSE_ROBOT_TASK_ID);
            sendWarehouseTask("巡仓", payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "创建巡仓指令失败", e);
            tip("巡仓指令创建失败");
        }
    }

    private void sendRecallTask() {
        try {
            JSONObject payload = new JSONObject();
            payload.put("robot_id", WAREHOUSE_TASK_ROBOT_ID);
            payload.put("task_id", RECALL_TASK_ID);
            payload.put("is_return", true);
            payload.put("robot_task_id", RECALL_ROBOT_TASK_ID);
            sendWarehouseTask("召回", payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "创建召回指令失败", e);
            tip("召回指令创建失败");
        }
    }

    private void sendWarehouseTask(String taskName, String payload) {
        sendWarehouseTask(taskName, payload, null);
    }

    private void sendWarehouseTask(String taskName, String payload, String successMessage) {
        if (warehouseTaskPending) {
            tip(pendingWarehouseTaskName + "请求发送中，请稍候");
            return;
        }

        beginWarehouseTaskLoading(taskName);
        Log.d(TAG, taskName + "指令发送到 " + WAREHOUSE_TASK_WS + ": " + payload);
        Request request = new Request.Builder()
                .url(WAREHOUSE_TASK_WS)
                .build();

        warehouseTaskWebSocketClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                pendingWarehouseTaskWebSocket = webSocket;
                boolean sent = webSocket.send(payload);
                Log.d(TAG, taskName + "指令" + (sent ? "发送成功" : "发送失败") + ": " + payload);
                if (sent) {
                    showWarehouseTaskStatus(taskName + "请求已发送，等待接口确认...");
                } else {
                    handleWarehouseTaskFailure(taskName, "请求发送失败");
                    webSocket.close(1000, taskName + " send failed");
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, String text) {
                Log.d(TAG, taskName + "指令收到响应: " + text);
                handleWarehouseTaskResponse(taskName, text, successMessage);
                webSocket.close(1000, taskName + " response received");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                String text = bytes.utf8();
                Log.d(TAG, taskName + "指令收到二进制响应: " + text);
                handleWarehouseTaskResponse(taskName, text, successMessage);
                webSocket.close(1000, taskName + " response received");
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, taskName + "指令连接失败: " + t.getMessage(), t);
                handleWarehouseTaskFailure(taskName, "连接失败：" + t.getMessage());
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, taskName + "指令连接关闭 code=" + code + ", reason=" + reason);
            }
        });
    }

    private void beginWarehouseTaskLoading(String taskName) {
        warehouseTaskPending = true;
        pendingWarehouseTaskName = taskName;
        pendingWarehouseTaskWebSocket = null;
        warehouseTaskLoadingStep = 0;
        setWarehouseTaskButtonsEnabled(false);
        setWarehouseTaskButtonText(taskName, "发送中...");
        showWarehouseTaskStatus(taskName + "请求发送中...");
        handler.removeCallbacks(warehouseTaskLoadingRunnable);
        handler.post(warehouseTaskLoadingRunnable);
        handler.removeCallbacks(warehouseTaskTimeoutRunnable);
        handler.postDelayed(warehouseTaskTimeoutRunnable, WAREHOUSE_TASK_RESPONSE_TIMEOUT_MS);
    }

    private void handleWarehouseTaskResponse(String taskName, String responseText) {
        handleWarehouseTaskResponse(taskName, responseText, null);
    }

    private void handleWarehouseTaskResponse(String taskName, String responseText, String successMessage) {
        if (TextUtils.isEmpty(responseText)) {
            handleWarehouseTaskFailure(taskName, "接口返回为空");
            return;
        }

        try {
            JSONObject responseJson = new JSONObject(responseText);
            if (!responseJson.has("ok")) {
                handleWarehouseTaskFailure(taskName, "接口返回缺少 ok 字段");
                return;
            }

            boolean accepted = responseJson.optBoolean("ok", false);
            String message = responseJson.optString("msg", "");
            if (TextUtils.isEmpty(message)) {
                message = accepted ? "指令已入队" : "接口返回失败";
            }

            if (accepted) {
                completeWarehouseTask(taskName, TextUtils.isEmpty(successMessage) ? taskName + "请求已受理：" + message : successMessage);
            } else {
                handleWarehouseTaskFailure(taskName, message);
            }
        } catch (JSONException e) {
            handleWarehouseTaskFailure(taskName, "接口返回不是合法 JSON");
        }
    }

    private void completeWarehouseTask(String taskName, String message) {
        if (!warehouseTaskPending) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finishWarehouseTaskUi(taskName);
                mBinding.tvWarehouseTaskStatus.setText(message);
                mBinding.tvWarehouseTaskStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blue));
                tip(message);
            }
        });
    }

    private void handleWarehouseTaskFailure(String taskName, String message) {
        if (!warehouseTaskPending) {
            return;
        }
        Log.e(TAG, taskName + "指令失败: " + message);
        closePendingWarehouseTaskWebSocket();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finishWarehouseTaskUi(taskName);
                String displayMessage = taskName + "失败：" + message;
                mBinding.tvWarehouseTaskStatus.setText(displayMessage);
                mBinding.tvWarehouseTaskStatus.setTextColor(Color.RED);
                tip(displayMessage);
            }
        });
    }

    private void finishWarehouseTaskUi(String taskName) {
        handler.removeCallbacks(warehouseTaskTimeoutRunnable);
        handler.removeCallbacks(warehouseTaskLoadingRunnable);
        warehouseTaskPending = false;
        pendingWarehouseTaskName = "";
        pendingWarehouseTaskWebSocket = null;
        setWarehouseTaskButtonText(taskName, taskName);
        setWarehouseTaskButtonsEnabled(true);
    }

    private void setWarehouseTaskButtonsEnabled(boolean enabled) {
        mBinding.tvGoCharge.setEnabled(enabled);
        mBinding.tvPatrolWarehouse.setEnabled(enabled);
        mBinding.tvRecall.setEnabled(enabled);
    }

    private void setWarehouseTaskButtonText(String taskName, String text) {
        if ("回充".equals(taskName)) {
            mBinding.tvGoCharge.setText(text);
        } else if ("巡仓".equals(taskName)) {
            mBinding.tvPatrolWarehouse.setText(text);
        } else if ("召回".equals(taskName)) {
            mBinding.tvRecall.setText(text);
        }
    }

    private void showWarehouseTaskStatus(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.tvWarehouseTaskStatus.setText(message);
                mBinding.tvWarehouseTaskStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.grey_700));
            }
        });
    }

    private void closePendingWarehouseTaskWebSocket() {
        if (pendingWarehouseTaskWebSocket != null) {
            pendingWarehouseTaskWebSocket.close(1000, "warehouse task finished");
            pendingWarehouseTaskWebSocket = null;
        }
    }

    /**
     * 准备导航
     * @param routeNodes
     */
    private void prepareNav(List<RouteNode> routeNodes) {
        NavManager.getInstance().stop();
//        NavManager.getInstance().release();
        peanutNavigation = NavManager.getInstance().getmPeanutNavigation();
        peanutNavigation.setTargets(routeNodes);

 //       NavManager.getInstance().setSpeed(100);
        NavManager.getInstance().setSpeed(routeNodes.size()==1
                ? MmkvUtils.decodeInt("single_point_speed", DEFAULT_NAVIGATION_SPEED)
                : MmkvUtils.decodeInt("multiple_point_speed", DEFAULT_NAVIGATION_SPEED));
        NavManager.getInstance().prepare();
    }

    public  List<DestModel.DataBean> getRouteNodesList(){

        return  mAdapter.getData();
    }

        @Override
    public void onStateChanged(int state, int schedule) {
        Log.d("navigatenext","state="+state);
        switch (state) {
            case Navigation.STATE_DESTINATION:
                arrived();
                break;
        }
    }
    private void arrived() {
        NavManager.getInstance().readyGo(false);
//        Log.d("navigatenext","getQueueName="+(response.get(0).getQueueName()));
        Log.d("navigatenext","getCurrentPosition="+peanutNavigation.getCurrentPosition()+",size=="+routeNodes.size());
        if (routeNodes!=null&&flag.equals("send_point")&&!TextUtils.isEmpty(routeNodes.get(peanutNavigation.getCurrentPosition()).getQueueName())){
            mediaPlayerShow(routeNodes.get(peanutNavigation.getCurrentPosition()).getQueueName());
        }
        //   ttsUntil.speech(peanutNavigation.getCurrentNode().getQueueName(),false);
        navigatenext();
        //  Toast.makeText(this,"已经到达目的地", Toast.LENGTH_SHORT).show();
    }

    private void mediaPlayerShow(String dataSource) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("navigatenext","onPrepared");
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("navigatenext","mediaPlayernError what="+what+",extra="+extra);
                    return true;
                }
            });
        } catch (IOException e) {
            Log.d("navigatenext","IOException="+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void navigatenext() {
        Log.d("navigatenext","lastNode="+NavManager.getInstance().isLastNode());
        Log.d("navigatenext","NextNode="+new Gson().toJson(NavManager.getInstance().getNextNode()));
        Log.d("navigatenext","Targets="+new Gson().toJson(NavManager.getInstance().getTargets()));
        if ( NavManager.getInstance().isLastNode()) {
            if (peanutNavigation.getRouteNodes().length==1&&TextUtils.isEmpty(flag)){
                myRunnable = () -> {
                    RouteNode routeNode = new RouteNode();
                    routeNode.setId(2);
                    routeNode.setName("出餐口");
                    routeNodes.add(routeNode);
                    prepareNav(routeNodes);

                };
                handler.postDelayed(myRunnable, 20000);
                String voiceAddress = MmkvUtils.decodeString("save_voice_address");
                Log.d("navigatenext","get_voice_address="+voiceAddress);
                if (!TextUtils.isEmpty(voiceAddress)){
                    mediaPlayerShow(voiceAddress);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(flag)){

                        webSocketService.send(flag+"已完成");

                        Log.d("websocket=======", "flag =============" + flag);
                        flag="";
                    }
                    //   ttsUntil.speech("已经没有下一个目的地了",false);
                    mBinding.tvNavigate.setEnabled(true);
                }
            });
        } else {
            NavManager.getInstance().nextDes();
            NavManager.getInstance().readyGo(true);
        }
    }
    @Override
    public void onRouteNode(int i, RouteNode routeNode) {

    }
    PresentationCoucou pres;
    private void startHardwareTests(ScreenModel screenModel, BannerModel bannerModelList,MediaType mediaType) {
        Log.d("presentation","=== DÉMARRAGE DES TESTS ===");
        GPIOUtil.IOCtrl(GPIOUtil.GPIO, GPIOUtil.OPEN_SCEEN);//打开
//        GPIOUtil.IOCtrl(GPIOUtil.GPIO, GPIOUtil.CLOSE_SCEEN);//关闭
        try {
            // Test 1 : Liste des écrans disponibles
            Log.d("presentation","\n[Test 1] Recherche des écrans disponibles...");
            Display[] displays = displayManager.getDisplays();
            Log.d("presentation","Nombre d’écrans détectés : " + displays.length);
            for (int i = 0; i < displays.length; i++) {
                Log.d("presentation"," - Display[" + i + "] : " + displays[i].getName());
            }

            // Si plusieurs écrans, afficher “Coucou” sur le secondaire
            if (displays.length > 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pres != null) pres.cancel();
                        pres = new PresentationCoucou(MainActivity.this, displays[1],screenModel,  bannerModelList, mediaType);
                        pres.show();
                    }
                });
                Log.d("presentation","\n[Test 2] Tentative d’affichage sur le second écran...");

                Log.d("presentation","✅ Présentation affichée sur : " + displays[1].getName());
            } else {
                Log.d("presentation","⚠️ Aucun écran secondaire détecté.");
            }

        } catch (Exception e) {
            Log.d("presentation","❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }

        Log.d("presentation","\n=== FIN DES TESTS ===");
    }
    /**
     * 回传数据
     */
    private void uploadInfo(){
        RuntimeInfo runtimeInfo = PeanutRuntime.getInstance().getRuntimeInfo();
        if (runtimeInfo == null) {
            Log.w(TAG, "skip uploadInfo: runtimeInfo is null");
            return;
        }
        //工作模式
        int workMode = runtimeInfo.getWorkMode();
        //电量
        int power = runtimeInfo.getPower();
        //电机状态
        int motorStatus = runtimeInfo.getMotorStatus();

        String destList = runtimeInfo.getDestList();

        InfoModel infoModel=new InfoModel(workMode,power,motorStatus,destList,isCharging?"正在充电":"",(working==2||working==3)?"正在执行任务":"空闲");
        if (webSocketService != null) {
            Log.d("websocket=======","uploadInfo=="+new Gson().toJson(infoModel));
            webSocketService.send(new Gson().toJson(infoModel));
        }

    }

    PeanutCharger mPeanutCharger;




    public void tip(final String str) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView messageTextView = new TextView(MainActivity.this);
                messageTextView.setText(str);
                messageTextView.setTextColor(Color.WHITE);
                messageTextView.setTextSize(18);
                messageTextView.setGravity(Gravity.CENTER);
                int horizontalPadding = dp(20);
                int verticalPadding = dp(12);
                messageTextView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

                android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
                background.setColor(0xCC000000);
                background.setCornerRadius(dp(8));
                messageTextView.setBackground(background);

                Toast toast = new Toast(MainActivity.this);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(messageTextView);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    IDataCallback buttonStatusCallback = new IDataCallback() {
        @Override
        public void success(String result) {
            // NavManager.getInstance().readyGo(false);
            Log.d(TAG,"buttonStatusCallback success=="+result);
        }

        @Override
        public void error(ApiError error) {
            Log.d(TAG,"buttonStatusCallback error=="+error.toString());
        }
    };
    IDataCallback chargeCallback= new IDataCallback() {
        @Override
        public void success(String response) {
            Log.d("Charger===", "success = " + response);
        }

        @Override
        public void error(ApiError error) {
            Log.d("Charger===", "error = " + error);
        }
    };
    IDataCallback navigationCallback= new IDataCallback() {
        @Override
        public void success(String response) {
            Log.d("schedule====", "success = " + response);
        }

        @Override
        public void error(ApiError error) {
            Log.d("schedule====", "error = " + error);
        }
    };

    @Override
    public void onRoutePrepared(RouteNode... routeNodes) {
        Log.d("navigatenext","readyGo=====");
        NavManager.getInstance().readyGo(true);
    }

    @Override
    public void onDistanceChanged(float v) {
        Log.d("navigatenext","onDistanceChanged="+v);
    }

    @Override
    public void onError(int i) {
        mBinding.tvNavigate.setEnabled(true);
        Log.d("navigatenext","onerror="+i);
        flag="";
    }

    @Override
    public void onEvent(int i) {
        Log.d("navigatenext","onEvent="+i);
    }
    private boolean isCharging;
    private int working;

    //充电回调
    Charger.Listener listener=new Charger.Listener() {
        @Override
        public void onChargerInfoChanged(int event, ChargerInfo chargerInfo) {

            Log.d("Charger===", "event = " + event +
                    " Power = " + chargerInfo.getPower() + " ChargeEvent = " + chargerInfo.getEvent());
        }

        @Override
        public void onChargerStatusChanged(int status) {
            if (status==4&&webSocketService!=null){
                isCharging=true;
                webSocketService.send("开始充电");
            }else if(status==1||status==6){
                isCharging=false;
            }
            Log.d("Charger===", "status = " + status);
        }

        @Override
        public void onError(int errorCode) {
            Log.d("Charger===", "errorCode = " + errorCode);
        }
    };
}
