package com.yuandaima.peanutrobot;

import static com.keenon.sdk.external.PeanutSDK.SDK_INIT_SUCCESS;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.yuandaima.peanutrobot.bean.DestModel;
import com.yuandaima.peanutrobot.bean.InfoModel;
import com.yuandaima.peanutrobot.bean.MediaModel;
import com.yuandaima.peanutrobot.bean.MapPointConfig;
import com.yuandaima.peanutrobot.bean.MyPoint;
import com.yuandaima.peanutrobot.bean.ScreenModel;
import com.yuandaima.peanutrobot.databinding.ActivityMainBinding;

import com.yuandaima.peanutrobot.fragment.MyMultiCameraFragment2;
import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
import com.yuandaima.peanutrobot.manager.NavManager;
import com.yuandaima.peanutrobot.manager.USBCameraManager;
import com.yuandaima.peanutrobot.manager.WebSocketManager;
import com.yuandaima.peanutrobot.presentation.PresentationCoucou;
import com.yuandaima.peanutrobot.server.PickupStatusServer;
import com.yuandaima.peanutrobot.server.WebServer;
import com.yuandaima.peanutrobot.server.WebSocketService;
import com.yuandaima.peanutrobot.util.DiagnosticLogRecorder;
import com.yuandaima.peanutrobot.util.GPIOUtil;
import com.yuandaima.peanutrobot.util.MapPointConfigSanitizer;
import com.yuandaima.peanutrobot.util.MmkvUtils;
import com.yuandaima.peanutrobot.util.TtsUntil;
import com.yuandaima.peanutrobot.util.UpstreamChargeTaskParser;
import com.yuandaima.peanutrobot.view.MapPointOverlayView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Navigation.Listener, NavManager.SessionNavigationListener {
    private String TAG="MainActivity===";
    private static final int ARRIVE_STAY_DURATION = 3000;
    private static final int DEFAULT_NAVIGATION_SPEED = 30;
    private static final long IDLE_LOCK_DELAY_MS = 60 * 1000L;
    private static final long POINT_REFRESH_RETRY_DELAY_MS = 2000L;
    private static final int POINT_REFRESH_MAX_RETRY_COUNT = 10;
    private static final int COMPARTMENT_COUNT = 3;
    private static final int PICKUP_STATUS_SERVER_PORT = 9088;
    private static final long ARRIVAL_WAIT_TIMEOUT_MS = 5 * 60 * 1000L;
    private static final long DELIVERY_COUNTDOWN_UPDATE_INTERVAL_MS = 1000L;
    private static final long NAVIGATION_PREPARE_TIMEOUT_MS = 10 * 1000L;
    private static final String DELIVERY_STATUS_QUEUED = "等待配送";
    private static final String DELIVERY_STATUS_TRAVELING = "正在前往";
    private static final String DELIVERY_STATUS_WAITING = "等待取餐";
    private static final String DELIVERY_STATUS_PICKED_UP = "已完成取餐";
    private static final String DELIVERY_STATUS_TIMED_OUT = "等待超时";
    private static final String DELIVERY_STATUS_CANCELLED = "配送已取消";
    private static final String NAV_ARRIVE_URL = "http://192.168.112.194:9088/nav_arrive";
    private static final okhttp3.MediaType JSON_MEDIA_TYPE =
            okhttp3.MediaType.get("application/json; charset=utf-8");
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
    private static final long WAREHOUSE_TASK_STATUS_CLEAR_DELAY_MS = 5000L;
    private static final String DEFAULT_DELIVERY_VOICE_URL = "http://192.168.112.194:9089/delivery.wav";
    private static final String KEY_IDLE_IMAGE_URI = "idle_screen_image_uri";
    private static final String KEY_IDLE_IMAGE_ROTATION = "idle_screen_image_rotation";
    private static final String KEY_IDLE_IMAGE_MODE = "idle_screen_image_mode";
    private static final String KEY_IDLE_LOCK_PASSWORD = "idle_lock_password";
    private static final String KEY_MAP_POINT_CONFIG = "room_map_point_config";
    private static final String ROOM_MAP_VERSION = "root_map_v1";
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
    private PointAdapter mAdapter;
    private MediaAdapter mediaAdapter;
    private List<RouteNode> pointList=new ArrayList<>();
    private List<MyPoint> list = new ArrayList<>();
    private List<MediaModel> mediaModelList = new ArrayList<>();
    private final List<DestModel.DataBean> selectedPointList = new ArrayList<>();
    private final DestModel.DataBean[] compartmentPoints = new DestModel.DataBean[COMPARTMENT_COUNT];
    private int activeCompartmentIndex = -1;
    private TtsUntil ttsUntil;
    private boolean isPermissionRequested;


    private WebSocketService webSocketService;
    private final OkHttpClient warehouseTaskWebSocketClient = new OkHttpClient.Builder().build();
    private final OkHttpClient arrivalHttpClient = new OkHttpClient.Builder()
            .readTimeout(6, TimeUnit.MINUTES)
            .build();
    private final Object arrivalWaitLock = new Object();
    private final Map<Integer, Integer> screenRouteBayByPointId = new HashMap<>();
    private final List<DeliveryProgressItem> deliveryProgressItems = new ArrayList<>();
    private final Gson mapConfigGson = new Gson();
    private final Set<Integer> loggedMissingMapPointIds = new HashSet<>();
    private final List<MapMarkerChoice> mapMarkerChoices = new ArrayList<>();
    private final List<RobotPointChoice> mapRobotPointChoices = new ArrayList<>();
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean idleLocked = false;
    private boolean unlockDialogShowing = false;
    private boolean idleConfigDialogShowing = false;
    private boolean coreInitialized = false;
    private volatile boolean activityDestroyed = false;
    private boolean pointUiBound = false;
    private boolean warehouseTaskPending = false;
    private volatile boolean upstreamChargeTaskActive = false;
    private boolean startupGoChargeSent = false;
    private String pendingWarehouseTaskName = "";
    private WebSocket pendingWarehouseTaskWebSocket;
    private int warehouseTaskLoadingStep = 0;
    private int idleUnlockTapCount = 0;
    private long idleUnlockFirstTapTime = 0L;
    private int pointRefreshRetryCount = 0;
    private boolean pointAutoRefreshActive = false;
    private volatile boolean screenCompartmentRouteActive = false;
    private boolean arrivalWaitActive = false;
    private int arrivalWaitGeneration = 0;
    private int arrivalWaitNavigationTaskGeneration = 0;
    private int lastHandledScreenArrivalPosition = -1;
    private volatile int navigationTaskGeneration = 0;
    private int activeNavigationTaskGeneration = 0;
    private int activeNavigationSessionGeneration = -1;
    private boolean navigationTaskActive = false;
    private boolean mapPointOverlayInitialized = false;
    private boolean mapEditMode = false;
    private boolean mapEditPasswordDialogShowing = false;
    private boolean mapEditorUpdatingControls = false;
    private boolean navigationLegArmed = false;
    private int navigationLegPosition = -1;
    private int expectedNavigationRoutePosition = -1;
    private int navigationRouteOffset = 0;
    private int arrivalWaitRoutePosition = -1;
    private Runnable navigationPrepareTimeoutRunnable;
    private Runnable arrivalWaitTimeoutRunnable;
    private Call pendingArrivalReportCall;
    private Integer focusedMapRobotPointId;
    private MapPointConfig savedMapPointConfig;
    private MapPointConfig editingMapPointConfig;
    private MapPointConfig.Marker currentEditingMapMarker;
    private ArrayAdapter<MapMarkerChoice> mapMarkerChoiceAdapter;
    private ArrayAdapter<RobotPointChoice> mapRobotPointChoiceAdapter;
    private long arrivalWaitDeadlineElapsedRealtime = 0L;
    private String deliveryProgressSummary = "暂无配送任务";
    private final Runnable deliveryCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (activityDestroyed || !isArrivalWaitActive()) {
                return;
            }
            renderDeliveryProgress();
            handler.postDelayed(this, DELIVERY_COUNTDOWN_UPDATE_INTERVAL_MS);
        }
    };
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
            if (activityDestroyed) {
                return;
            }
            handleWarehouseTaskFailure(pendingWarehouseTaskName, "接口响应超时");
        }
    };
    private final Runnable warehouseTaskLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (activityDestroyed
                    || !warehouseTaskPending
                    || TextUtils.isEmpty(pendingWarehouseTaskName)) {
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
    private final Runnable warehouseTaskStatusClearRunnable = new Runnable() {
        @Override
        public void run() {
            if (activityDestroyed) {
                return;
            }
            mBinding.tvWarehouseTaskStatus.setText("");
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
        DiagnosticLogRecorder.info("LIFECYCLE", "MainActivity onCreate");
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
        initializeMapPointOverlay();
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
        DiagnosticLogRecorder.info("LIFECYCLE", "MainActivity onResume");
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
        DiagnosticLogRecorder.info("LIFECYCLE", "MainActivity onPause");
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
    private volatile PickupStatusServer pickupStatusServer;
    private List<RouteNode> response=new ArrayList();

    private void initWebSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activityDestroyed) {
                    return;
                }
                server = new WebServer(9095,MainActivity.this);
                try {
                    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    if (activityDestroyed) {
                        server.stop();
                        return;
                    }
                    server.setWebCallback(new WebServer.WebCallback() {
                        @Override
                        public void onMessage(String text,String url) {
                            Log.d("MyServer","url=="+url+",text=="+text);
                            DiagnosticLogRecorder.info("HTTP", "收到上游请求 path=" + url);
                            switch (url){
                                case "/robot_task/go_to_charge":
                                    Integer upstreamPileId =
                                            UpstreamChargeTaskParser.parsePositivePileId(text);
                                    if (upstreamPileId == null) {
                                        Log.w(TAG, "ignore invalid upstream go_to_charge task");
                                        DiagnosticLogRecorder.warn(
                                                "CHARGER",
                                                "忽略无效上游回充任务 path=/robot_task/go_to_charge"
                                        );
                                        break;
                                    }
                                    runOnUiThread(() -> {
                                        if (activityDestroyed) {
                                            return;
                                        }
                                        PeanutCharger availableCharger = mPeanutCharger;
                                        if (availableCharger == null) {
                                            Log.w(TAG, "ignore upstream go_to_charge: charger unavailable");
                                            DiagnosticLogRecorder.warn(
                                                    "CHARGER",
                                                    "忽略上游回充任务：充电模块未就绪 pile=" + upstreamPileId
                                            );
                                            tip("充电模块未就绪，已忽略上游回充任务");
                                            return;
                                        }
                                        DiagnosticLogRecorder.info(
                                                "CHARGER",
                                                "执行上游回充任务 pile=" + upstreamPileId
                                        );
                                        upstreamChargeTaskActive = true;
                                        discardMapEditingForPreemptingTask("HTTP 回充任务");
                                        cancelScreenCompartmentRoute("HTTP 回充任务");
                                        mBinding.tvNavigate.setEnabled(true);
                                        flag="go_to_charge";
                                        DiagnosticLogRecorder.info(
                                                "NAV",
                                                "调用 NavManager.stop reason=HTTP 回充任务"
                                        );
                                        NavManager.getInstance().stop();
                                        //   NavManager.getInstance().release();
                                        availableCharger.setPile(upstreamPileId);
                                        availableCharger.execute();
                                        availableCharger.performAction(PeanutCharger.CHARGE_ACTION_AUTO);
                                    });
                                    break;
                                case "/robot_task/send_point":
                                    List<RouteNode> upstreamRouteNodes;
                                    try {
                                        upstreamRouteNodes = new Gson().fromJson(
                                                text,
                                                new TypeToken<List<RouteNode>>(){}.getType()
                                        );
                                    } catch (RuntimeException exception) {
                                        Log.w(TAG, "ignore malformed upstream send_point route", exception);
                                        DiagnosticLogRecorder.warn(
                                                "HTTP",
                                                "忽略格式错误的 send_point 路线"
                                        );
                                        break;
                                    }
                                    boolean routeContainsNullNode = false;
                                    if (upstreamRouteNodes != null) {
                                        for (RouteNode upstreamRouteNode : upstreamRouteNodes) {
                                            if (upstreamRouteNode == null) {
                                                routeContainsNullNode = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (upstreamRouteNodes == null
                                            || upstreamRouteNodes.isEmpty()
                                            || routeContainsNullNode) {
                                        Log.w(TAG, "ignore invalid upstream send_point route");
                                        DiagnosticLogRecorder.warn(
                                                "HTTP",
                                                "忽略空或包含空节点的 send_point 路线"
                                        );
                                        break;
                                    }
                                    runOnUiThread(() -> {
                                        if (activityDestroyed) {
                                            return;
                                        }
                                        upstreamChargeTaskActive = false;
                                        discardMapEditingForPreemptingTask("上游 send_point 任务");
                                        cancelScreenCompartmentRoute("上游 send_point 抢占");
                                        if (mPeanutCharger!=null){
                                            Log.d("navigatenext","CHARGE_ACTION_STOP");
                                            DiagnosticLogRecorder.info(
                                                    "CHARGER",
                                                    "上游 send_point 发送 CHARGE_ACTION_STOP"
                                            );
                                            mPeanutCharger.performAction(PeanutCharger.CHARGE_ACTION_STOP);
                                        }
                                        flag="send_point";
                                        routeNodes = new ArrayList<>(upstreamRouteNodes);
                                        DiagnosticLogRecorder.info(
                                                "HTTP",
                                                "接受上游 send_point " + describeRoute(routeNodes)
                                        );
                                        Log.d("navigatenext","send_poin=="+new Gson().toJson(routeNodes));
                                        prepareNav(routeNodes);
                                    });
                                    break;
                                case "/robot_task/send_stop":
                                    Log.d("MyServer","send_stop==");
                                    DiagnosticLogRecorder.info("HTTP", "接受上游 send_stop");
                                    runOnUiThread(() -> {
                                        if (activityDestroyed) {
                                            return;
                                        }
                                        cancelScreenCompartmentRoute("上游停止任务");
                                        DiagnosticLogRecorder.info(
                                                "NAV",
                                                "调用 NavManager.stop reason=上游停止任务"
                                        );
                                        NavManager.getInstance().stop();
                                        mBinding.tvNavigate.setEnabled(true);
                                    });
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
                    DiagnosticLogRecorder.error(
                            "HTTP",
                            "9095 服务启动失败：" + e.getMessage()
                    );
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

        startPickupStatusServer();
        bindService(new Intent(this, WebSocketService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    private void startPickupStatusServer() {
        PickupStatusServer newPickupStatusServer = new PickupStatusServer(
                PICKUP_STATUS_SERVER_PORT,
                this::handlePickupCompleted
        );
        pickupStatusServer = newPickupStatusServer;
        new Thread(() -> {
            if (activityDestroyed) {
                return;
            }
            try {
                newPickupStatusServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                if (activityDestroyed) {
                    newPickupStatusServer.stop();
                    return;
                }
                Log.i(TAG, "pickup status server started on port " + PICKUP_STATUS_SERVER_PORT);
                DiagnosticLogRecorder.info(
                        "PICKUP",
                        "pickup_status 服务已启动 port=" + PICKUP_STATUS_SERVER_PORT
                );
            } catch (IOException exception) {
                Log.e(TAG, "pickup status server failed to start", exception);
                DiagnosticLogRecorder.error(
                        "PICKUP",
                        "pickup_status 服务启动失败：" + exception.getMessage()
                );
            }
        }, "pickup-status-server").start();
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
        mBinding.tvDiagnosticLogs.setOnClickListener(this);
        mBinding.tvCompartment1.setOnClickListener(this);
        mBinding.tvCompartment2.setOnClickListener(this);
        mBinding.tvCompartment3.setOnClickListener(this);
        mBinding.tvPointSelectionTab.setOnClickListener(this);
        mBinding.tvDeliveryProgressTab.setOnClickListener(this);
        showPointSelectionView();
        renderDeliveryProgress();

        PeanutRuntime.getInstance().registerListener(mRuntimeListener);
        mAdapter.setOnClickItemListener(new OnItemClickListener() {
            @Override
            public void onClick(int position,boolean isSelect) {
                if (position < 0 || position >= mAdapter.getData().size()) {
                    return;
                }
                DestModel.DataBean point = mAdapter.getData().get(position);
                handlePointClick(point);
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
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            mBinding.rvPoint.setLayoutManager(gridLayoutManager);
            mBinding.rvPoint.setAdapter(mAdapter);
        } else {
            replacePointData(mAdapter, displayData);
        }
        mAdapter.setSelectedPoints(getHighlightedPoints());
        // mediaAdapter=new MediaAdapter(mediaModelList,MainActivity.this);
        //  mAdapter= new PointAdapter(testData);
        updateCompartmentUi();
        mAdapter.setTtsUtil(ttsUntil);
        if (mapPointOverlayInitialized) {
            if (mapEditMode) {
                refreshMapEditorControls(currentEditingMapMarker);
            }
            refreshMapPointOverlay();
        }
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
            reconcilePointBindings(remotePointData);
            mBinding.tvPointEmpty.setText("未获取到点位");
            pointAutoRefreshActive = false;
            handler.removeCallbacks(pointRefreshRetryRunnable);
            if (manualRefresh) {
                tip("点位已刷新：" + remotePointData.size() + "个");
            }
        } else {
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

    private void handlePointClick(DestModel.DataBean point) {
        if (point == null) {
            return;
        }
        if (mapEditMode) {
            tip("地图编辑中，请先保存或取消");
            refreshPointBindingUi();
            return;
        }
        focusMapPoint(point);
        if (screenCompartmentRouteActive) {
            tip("配送进行中，点位和仓位仅供查看");
            refreshPointBindingUi();
            return;
        }
        if (activeCompartmentIndex < 0) {
            refreshPointBindingUi();
            return;
        }

        int boundCompartmentIndex = findCompartmentIndexByPointId(point.getId());
        if (boundCompartmentIndex >= 0 && boundCompartmentIndex != activeCompartmentIndex) {
            tip("该点位已绑定仓位 " + (boundCompartmentIndex + 1));
            refreshPointBindingUi();
            return;
        }

        DestModel.DataBean activeBoundPoint = compartmentPoints[activeCompartmentIndex];
        if (activeBoundPoint != null && activeBoundPoint.getId() == point.getId()) {
            compartmentPoints[activeCompartmentIndex] = null;
            removeSelectedPointById(activeBoundPoint.getId());
            refreshPointBindingUi();
            return;
        }

        if (activeBoundPoint != null) {
            int selectedRouteIndex = findSelectedPointIndex(activeBoundPoint.getId());
            compartmentPoints[activeCompartmentIndex] = point;
            selectedPointList.set(selectedRouteIndex, point);
        } else {
            compartmentPoints[activeCompartmentIndex] = point;
            selectedPointList.add(point);
        }
        activeCompartmentIndex = -1;
        refreshPointBindingUi();
    }

    private void handleCompartmentClick(int compartmentIndex) {
        if (compartmentIndex < 0 || compartmentIndex >= compartmentPoints.length) {
            return;
        }
        if (mapEditMode) {
            tip("地图编辑中，请先保存或取消");
            return;
        }
        if (screenCompartmentRouteActive) {
            tip("配送进行中，点位和仓位仅供查看");
            return;
        }
        activeCompartmentIndex = compartmentIndex;
        refreshPointBindingUi();
    }

    private void removeSelectedPointById(int pointId) {
        int selectedIndex = findSelectedPointIndex(pointId);
        if (selectedIndex >= 0) {
            selectedPointList.remove(selectedIndex);
        }
    }

    private int findSelectedPointIndex(int pointId) {
        for (int i = 0; i < selectedPointList.size(); i++) {
            DestModel.DataBean selectedPoint = selectedPointList.get(i);
            if (selectedPoint != null && selectedPoint.getId() == pointId) {
                return i;
            }
        }
        return -1;
    }

    private DestModel.DataBean findPointById(List<DestModel.DataBean> pointData, int pointId) {
        if (pointData == null) {
            return null;
        }
        for (DestModel.DataBean point : pointData) {
            if (point != null && point.getId() == pointId) {
                return point;
            }
        }
        return null;
    }

    private void reconcilePointBindings(List<DestModel.DataBean> displayData) {
        for (int compartmentIndex = 0; compartmentIndex < compartmentPoints.length; compartmentIndex++) {
            DestModel.DataBean boundPoint = compartmentPoints[compartmentIndex];
            if (boundPoint == null) {
                continue;
            }
            DestModel.DataBean latestPoint = findPointById(displayData, boundPoint.getId());
            if (latestPoint == null) {
                compartmentPoints[compartmentIndex] = null;
                removeSelectedPointById(boundPoint.getId());
            } else {
                compartmentPoints[compartmentIndex] = latestPoint;
            }
        }

        for (int selectedIndex = selectedPointList.size() - 1; selectedIndex >= 0; selectedIndex--) {
            DestModel.DataBean selectedPoint = selectedPointList.get(selectedIndex);
            DestModel.DataBean latestPoint = selectedPoint == null
                    ? null
                    : findPointById(displayData, selectedPoint.getId());
            if (latestPoint == null || findCompartmentIndexByPointId(latestPoint.getId()) < 0) {
                selectedPointList.remove(selectedIndex);
            } else {
                selectedPointList.set(selectedIndex, latestPoint);
            }
        }

    }

    private int findCompartmentIndexByPointId(int pointId) {
        for (int compartmentIndex = 0; compartmentIndex < compartmentPoints.length; compartmentIndex++) {
            DestModel.DataBean boundPoint = compartmentPoints[compartmentIndex];
            if (boundPoint != null && boundPoint.getId() == pointId) {
                return compartmentIndex;
            }
        }
        return -1;
    }

    private boolean activateScreenCompartmentRoute() {
        Map<Integer, Integer> routeBaySnapshot = new HashMap<>();
        for (DestModel.DataBean selectedPoint : selectedPointList) {
            int compartmentIndex = findCompartmentIndexByPointId(selectedPoint.getId());
            if (compartmentIndex < 0) {
                tip("点位未绑定有效仓位，请重新选择");
                return false;
            }
            if (routeBaySnapshot.containsKey(selectedPoint.getId())) {
                tip("同一点位不能绑定多个仓位");
                return false;
            }
            routeBaySnapshot.put(selectedPoint.getId(), compartmentIndex + 1);
        }

        cancelScreenCompartmentRoute("开始新的屏幕仓位任务");
        synchronized (arrivalWaitLock) {
            screenRouteBayByPointId.clear();
            screenRouteBayByPointId.putAll(routeBaySnapshot);
            lastHandledScreenArrivalPosition = -1;
            screenCompartmentRouteActive = true;
        }
        DiagnosticLogRecorder.info(
                "DELIVERY",
                "创建屏幕仓位任务 bindings=" + routeBaySnapshot
                        + ", selectedPoints=" + selectedPointList.size()
        );
        activeCompartmentIndex = -1;
        createDeliveryProgressSnapshot(routeBaySnapshot);
        showDeliveryProgressView();
        refreshPointBindingUi();
        return true;
    }

    private void createDeliveryProgressSnapshot(Map<Integer, Integer> routeBaySnapshot) {
        deliveryProgressItems.clear();
        for (DestModel.DataBean selectedPoint : selectedPointList) {
            Integer bay = routeBaySnapshot.get(selectedPoint.getId());
            if (bay != null) {
                deliveryProgressItems.add(new DeliveryProgressItem(
                        getPointDisplayName(selectedPoint),
                        bay,
                        DELIVERY_STATUS_QUEUED
                ));
            }
        }
        if (!deliveryProgressItems.isEmpty()) {
            deliveryProgressItems.get(0).status = DELIVERY_STATUS_TRAVELING;
            deliveryProgressSummary = "配送中 · 前往第 1/" + deliveryProgressItems.size() + " 个点位";
        } else {
            deliveryProgressSummary = "暂无配送任务";
        }
        renderDeliveryProgress();
    }

    private void showPointSelectionView() {
        if (mBinding == null) {
            return;
        }
        mBinding.llPointSelectionContent.setVisibility(View.VISIBLE);
        mBinding.svDeliveryProgress.setVisibility(View.GONE);
        updateDeliveryTabStyles(false);
    }

    private void showDeliveryProgressView() {
        if (mBinding == null) {
            return;
        }
        mBinding.llPointSelectionContent.setVisibility(View.GONE);
        mBinding.svDeliveryProgress.setVisibility(View.VISIBLE);
        updateDeliveryTabStyles(true);
        renderDeliveryProgress();
    }

    private void updateDeliveryTabStyles(boolean deliveryProgressSelected) {
        int selectedTextColor = ContextCompat.getColor(this, R.color.ui_accent);
        int unselectedTextColor = ContextCompat.getColor(this, R.color.ui_text_secondary);
        mBinding.tvPointSelectionTab.setBackgroundResource(deliveryProgressSelected
                ? R.drawable.tab_unselected_background
                : R.drawable.tab_selected_background);
        mBinding.tvDeliveryProgressTab.setBackgroundResource(deliveryProgressSelected
                ? R.drawable.tab_selected_background
                : R.drawable.tab_unselected_background);
        mBinding.tvPointSelectionTab.setTextColor(deliveryProgressSelected
                ? unselectedTextColor
                : selectedTextColor);
        mBinding.tvDeliveryProgressTab.setTextColor(deliveryProgressSelected
                ? selectedTextColor
                : unselectedTextColor);
    }

    private void updateDeliveryProgressStatus(int routePosition, String status) {
        if (routePosition < 0 || routePosition >= deliveryProgressItems.size()) {
            return;
        }
        deliveryProgressItems.get(routePosition).status = status;
        if (DELIVERY_STATUS_TRAVELING.equals(status)) {
            deliveryProgressSummary = "配送中 · 前往第 " + (routePosition + 1)
                    + "/" + deliveryProgressItems.size() + " 个点位";
        } else if (DELIVERY_STATUS_WAITING.equals(status)) {
            deliveryProgressSummary = "配送中 · 第 " + (routePosition + 1)
                    + "/" + deliveryProgressItems.size() + " 个点位等待取餐";
        }
        renderDeliveryProgress();
    }

    private void renderDeliveryProgress() {
        if (mBinding == null || activityDestroyed) {
            return;
        }
        mBinding.tvDeliveryProgressSummary.setText(deliveryProgressSummary);
        mBinding.llDeliveryProgressItems.removeAllViews();
        for (int routePosition = 0; routePosition < deliveryProgressItems.size(); routePosition++) {
            DeliveryProgressItem progressItem = deliveryProgressItems.get(routePosition);
            TextView progressCard = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, dp(6));
            progressCard.setLayoutParams(layoutParams);
            progressCard.setMinHeight(dp(64));
            progressCard.setPadding(dp(10), dp(8), dp(10), dp(8));
            progressCard.setTextColor(ContextCompat.getColor(this, R.color.ui_text_primary));
            progressCard.setTextSize(13);
            progressCard.setGravity(Gravity.CENTER_VERTICAL);
            progressCard.setBackgroundResource(
                    getDeliveryProgressBackgroundResource(progressItem.status)
            );

            StringBuilder cardText = new StringBuilder()
                    .append(routePosition + 1)
                    .append(". ")
                    .append(progressItem.pointName)
                    .append("\n仓位 ")
                    .append(progressItem.bay)
                    .append(" · ")
                    .append(progressItem.status);
            if (DELIVERY_STATUS_WAITING.equals(progressItem.status)) {
                cardText.append(" · ").append(formatArrivalWaitCountdown());
            }
            progressCard.setText(cardText.toString());
            mBinding.llDeliveryProgressItems.addView(progressCard);
        }
    }

    private int getDeliveryProgressBackgroundResource(String status) {
        if (DELIVERY_STATUS_TRAVELING.equals(status)) {
            return R.drawable.delivery_progress_traveling;
        }
        if (DELIVERY_STATUS_WAITING.equals(status)) {
            return R.drawable.delivery_progress_waiting;
        }
        if (DELIVERY_STATUS_PICKED_UP.equals(status)) {
            return R.drawable.delivery_progress_completed;
        }
        if (DELIVERY_STATUS_TIMED_OUT.equals(status)
                || DELIVERY_STATUS_CANCELLED.equals(status)) {
            return R.drawable.delivery_progress_problem;
        }
        return R.drawable.delivery_progress_queued;
    }

    private String formatArrivalWaitCountdown() {
        long remainingMilliseconds = Math.max(
                0L,
                arrivalWaitDeadlineElapsedRealtime - SystemClock.elapsedRealtime()
        );
        long remainingSeconds = (remainingMilliseconds + 999L) / 1000L;
        long remainingMinutes = remainingSeconds / 60L;
        long secondsWithinMinute = remainingSeconds % 60L;
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                remainingMinutes,
                secondsWithinMinute
        );
    }

    private static final class DeliveryProgressItem {
        private final String pointName;
        private final int bay;
        private String status;

        private DeliveryProgressItem(String pointName, int bay, String status) {
            this.pointName = pointName;
            this.bay = bay;
            this.status = status;
        }
    }

    private List<DestModel.DataBean> getHighlightedPoints() {
        return new ArrayList<>(selectedPointList);
    }

    private void refreshPointBindingUi() {
        if (mAdapter != null) {
            mAdapter.setSelectedPoints(getHighlightedPoints());
        }
        updateCompartmentUi();
    }

    private void updateCompartmentUi() {
        if (mBinding == null) {
            return;
        }
        boolean deliveryRouteIsReadOnly = screenCompartmentRouteActive;
        mBinding.rvPoint.setAlpha(deliveryRouteIsReadOnly ? 0.65f : 1.0f);
        mBinding.tvCompartment1.setAlpha(deliveryRouteIsReadOnly ? 0.65f : 1.0f);
        mBinding.tvCompartment2.setAlpha(deliveryRouteIsReadOnly ? 0.65f : 1.0f);
        mBinding.tvCompartment3.setAlpha(deliveryRouteIsReadOnly ? 0.65f : 1.0f);
        mBinding.tvPendingPoint.setVisibility(View.GONE);
        updateCompartmentCard(mBinding.tvCompartment1, 0);
        updateCompartmentCard(mBinding.tvCompartment2, 1);
        updateCompartmentCard(mBinding.tvCompartment3, 2);
    }

    private void updateCompartmentCard(TextView compartmentView, int compartmentIndex) {
        String compartmentName = "仓位 " + (compartmentIndex + 1);
        DestModel.DataBean boundPoint = compartmentPoints[compartmentIndex];
        boolean compartmentIsActive = compartmentIndex == activeCompartmentIndex
                && !screenCompartmentRouteActive;
        compartmentView.setSelected(compartmentIsActive);
        if (boundPoint != null) {
            String pointName = getPointDisplayName(boundPoint);
            compartmentView.setText(compartmentName + "\n" + pointName);
            compartmentView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_compartment_check, 0
            );
            String boundContentDescription = compartmentName + "，已绑定点位" + pointName;
            if (screenCompartmentRouteActive) {
                boundContentDescription += "，配送进行中不可编辑";
            } else if (compartmentIsActive) {
                boundContentDescription += "，已选择，点击其他点位可替换绑定，点击当前点位可解绑";
            } else {
                boundContentDescription += "，点击选择后可替换绑定";
            }
            compartmentView.setContentDescription(boundContentDescription);
        } else {
            compartmentView.setText(compartmentName);
            compartmentView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_compartment_add, 0
            );
            if (screenCompartmentRouteActive) {
                compartmentView.setContentDescription(
                        compartmentName + "，未绑定，配送进行中不可编辑"
                );
            } else if (compartmentIsActive) {
                compartmentView.setContentDescription(
                        compartmentName + "，未绑定，已选择，点击点位即可绑定"
                );
            } else {
                compartmentView.setContentDescription(
                        compartmentName + "，未绑定，点击选择仓位"
                );
            }
        }
    }

    private String getPointDisplayName(DestModel.DataBean point) {
        return TextUtils.isEmpty(point.getName()) ? String.valueOf(point.getId()) : point.getName();
    }

    private void initializeMapPointOverlay() {
        savedMapPointConfig = loadMapPointConfig();
        Drawable mapDrawable = mBinding.ivRoomMap.getDrawable();
        if (mapDrawable != null
                && mapDrawable.getIntrinsicWidth() > 0
                && mapDrawable.getIntrinsicHeight() > 0) {
            mBinding.mapPointOverlay.setSourceImageSize(
                    mapDrawable.getIntrinsicWidth(),
                    mapDrawable.getIntrinsicHeight()
            );
        } else {
            Log.e(TAG, "地图资源尺寸无效，标注层将保持隐藏");
        }

        mBinding.mapPointOverlay.setOnMapEditEntryListener(this::requestMapEditEntry);
        mBinding.mapPointOverlay.setOnMarkerInteractionListener(
                new MapPointOverlayView.OnMarkerInteractionListener() {
                    @Override
                    public void onMarkerSelected(int robotPointId) {
                        selectMapMarkerFromOverlay(robotPointId);
                    }

                    @Override
                    public void onMarkerPositionChanged(
                            int robotPointId,
                            float normalizedX,
                            float normalizedY
                    ) {
                        updateEditingMapMarkerPosition(robotPointId, normalizedX, normalizedY);
                    }
                }
        );

        mapMarkerChoiceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                mapMarkerChoices
        );
        mapMarkerChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinnerMapMarker.setAdapter(mapMarkerChoiceAdapter);
        mBinding.spinnerMapMarker.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long rowId
                    ) {
                        if (!mapEditorUpdatingControls
                                && position >= 0
                                && position < mapMarkerChoices.size()) {
                            MapPointConfig.Marker selectedMarker =
                                    mapMarkerChoices.get(position).marker;
                            if (selectedMarker != currentEditingMapMarker) {
                                selectMapEditorMarker(selectedMarker);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        mapRobotPointChoiceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                mapRobotPointChoices
        );
        mapRobotPointChoiceAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        mBinding.spinnerMapRobotPoint.setAdapter(mapRobotPointChoiceAdapter);
        mBinding.spinnerMapRobotPoint.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View selectedView,
                            int position,
                            long rowId
                    ) {
                        if (!mapEditorUpdatingControls
                                && position >= 0
                                && position < mapRobotPointChoices.size()) {
                            selectMapEditorRobotPoint(mapRobotPointChoices.get(position).point);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        mBinding.etMapMarkerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence sequence,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence sequence,
                    int start,
                    int before,
                    int count
            ) {
                if (!mapEditorUpdatingControls && currentEditingMapMarker != null) {
                    currentEditingMapMarker.setDisplayName(sequence.toString().trim());
                    refreshCurrentMapMarkerChoiceLabel();
                    refreshMapPointOverlay();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mBinding.btMapMarkerDelete.setOnClickListener(view -> deleteCurrentMapMarker());
        mBinding.btMapEditSave.setOnClickListener(view -> saveMapEditing());
        mBinding.btMapEditCancel.setOnClickListener(view -> cancelMapEditing());
        mBinding.llMapEditToolbar.setVisibility(View.GONE);
        mBinding.mapPointOverlay.setEditMode(false);
        mapPointOverlayInitialized = true;
        refreshMapPointOverlay();
    }

    private MapPointConfig loadMapPointConfig() {
        String serializedConfig = MmkvUtils.decodeString(KEY_MAP_POINT_CONFIG);
        if (TextUtils.isEmpty(serializedConfig)) {
            return new MapPointConfig(ROOM_MAP_VERSION);
        }
        try {
            MapPointConfig parsedConfig = mapConfigGson.fromJson(
                    serializedConfig,
                    MapPointConfig.class
            );
            if (parsedConfig == null || !ROOM_MAP_VERSION.equals(parsedConfig.getMapVersion())) {
                String storedVersion = parsedConfig == null ? "null" : parsedConfig.getMapVersion();
                Log.w(TAG, "忽略地图版本不匹配的点位配置: " + storedVersion);
                return MapPointConfigSanitizer.sanitize(parsedConfig, ROOM_MAP_VERSION);
            }

            int storedMarkerCount = parsedConfig.getMarkers().size();
            MapPointConfig sanitizedConfig = MapPointConfigSanitizer.sanitize(
                    parsedConfig,
                    ROOM_MAP_VERSION
            );
            if (sanitizedConfig.getMarkers().size() != storedMarkerCount) {
                Log.w(TAG, "已忽略 disabled、坐标无效或重复的地图标注");
            }
            return sanitizedConfig;
        } catch (RuntimeException exception) {
            Log.e(TAG, "地图点位配置损坏，已忽略", exception);
            return new MapPointConfig(ROOM_MAP_VERSION);
        }
    }

    private boolean hasValidNormalizedCoordinates(MapPointConfig.Marker marker) {
        return MapPointConfigSanitizer.hasValidNormalizedCoordinates(marker);
    }

    private void refreshMapPointOverlay() {
        if (!mapPointOverlayInitialized) {
            return;
        }
        MapPointConfig activeConfig = mapEditMode ? editingMapPointConfig : savedMapPointConfig;
        List<MapPointConfig.Marker> displayMarkers = getDisplayableMapMarkers(activeConfig, true);
        mBinding.mapPointOverlay.setMarkers(displayMarkers);
        if (!mapEditMode) {
            if (focusedMapRobotPointId != null
                    && !containsMapMarker(displayMarkers, focusedMapRobotPointId)) {
                focusedMapRobotPointId = null;
            }
            mBinding.mapPointOverlay.setFocusedRobotPointId(focusedMapRobotPointId);
            mBinding.mapPointOverlay.setEditingRobotPointId(null);
        }
    }

    private List<MapPointConfig.Marker> getDisplayableMapMarkers(
            MapPointConfig config,
            boolean logMissingRobotPoints
    ) {
        List<MapPointConfig.Marker> displayMarkers = new ArrayList<>();
        List<DestModel.DataBean> robotPoints = getDisplayPointData();
        if (config == null || robotPoints.isEmpty()) {
            return displayMarkers;
        }

        for (MapPointConfig.Marker marker : config.getMarkers()) {
            if (!MapPointConfigSanitizer.isUsableMarker(marker)) {
                continue;
            }
            DestModel.DataBean robotPoint = findPointById(robotPoints, marker.getRobotPointId());
            if (robotPoint == null) {
                if (logMissingRobotPoints
                        && loggedMissingMapPointIds.add(marker.getRobotPointId())) {
                    Log.w(TAG, "忽略机器人列表中已不存在的地图标注: pointId="
                            + marker.getRobotPointId());
                }
                continue;
            }
            MapPointConfig.Marker displayMarker = marker.copy();
            displayMarker.setDisplayName(getMapMarkerDisplayName(marker, robotPoint));
            displayMarkers.add(displayMarker);
        }
        return displayMarkers;
    }

    private List<MapPointConfig.Marker> getEditableMapMarkers() {
        List<MapPointConfig.Marker> editableMarkers = new ArrayList<>();
        if (editingMapPointConfig == null) {
            return editableMarkers;
        }
        List<DestModel.DataBean> robotPoints = getDisplayPointData();
        for (MapPointConfig.Marker marker : editingMapPointConfig.getMarkers()) {
            if (MapPointConfigSanitizer.isUsableMarker(marker)
                    && findPointById(robotPoints, marker.getRobotPointId()) != null) {
                editableMarkers.add(marker);
            }
        }
        return editableMarkers;
    }

    private String getMapMarkerDisplayName(
            MapPointConfig.Marker marker,
            DestModel.DataBean robotPoint
    ) {
        if (!TextUtils.isEmpty(marker.getDisplayName())) {
            return marker.getDisplayName().trim();
        }
        return robotPoint == null
                ? String.valueOf(marker.getRobotPointId())
                : getPointDisplayName(robotPoint);
    }

    private boolean containsMapMarker(
            List<MapPointConfig.Marker> markers,
            int robotPointId
    ) {
        for (MapPointConfig.Marker marker : markers) {
            if (MapPointConfigSanitizer.isUsableMarker(marker)
                    && marker.getRobotPointId() == robotPointId) {
                return true;
            }
        }
        return false;
    }

    private void focusMapPoint(DestModel.DataBean point) {
        List<MapPointConfig.Marker> displayMarkers = getDisplayableMapMarkers(
                savedMapPointConfig,
                false
        );
        if (!containsMapMarker(displayMarkers, point.getId())) {
            focusedMapRobotPointId = null;
            mBinding.mapPointOverlay.setFocusedRobotPointId(null);
            tip("该点位尚未配置地图位置");
            return;
        }
        focusedMapRobotPointId = point.getId();
        mBinding.mapPointOverlay.setFocusedRobotPointId(focusedMapRobotPointId);
    }

    private void requestMapEditEntry() {
        if (mapEditMode || mapEditPasswordDialogShowing) {
            return;
        }
        if (isRobotTaskBusyForMapEditing()) {
            tip("机器人任务进行中，禁止编辑地图");
            return;
        }
        showMapEditPasswordDialog();
    }

    private void showMapEditPasswordDialog() {
        EditText passwordInput = new EditText(this);
        passwordInput.setSingleLine(true);
        passwordInput.setHint("请输入密码");
        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        int padding = dp(24);
        passwordInput.setPadding(padding, padding / 2, padding, padding / 2);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("地图编辑验证")
                .setView(passwordInput)
                .setPositiveButton("进入", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            mapEditPasswordDialogShowing = true;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (!getIdleLockPassword().equals(passwordInput.getText().toString())) {
                    passwordInput.setError("密码错误");
                    passwordInput.selectAll();
                    return;
                }
                if (isRobotTaskBusyForMapEditing()) {
                    dialog.dismiss();
                    tip("机器人任务进行中，禁止编辑地图");
                    return;
                }
                dialog.dismiss();
                enterMapEditMode();
            });
        });
        dialog.setOnDismissListener(dialogInterface -> mapEditPasswordDialogShowing = false);
        dialog.show();
    }

    private void enterMapEditMode() {
        if (isRobotTaskBusyForMapEditing()) {
            tip("机器人任务进行中，禁止编辑地图");
            return;
        }
        editingMapPointConfig = savedMapPointConfig.copy();
        mapEditMode = true;
        mBinding.llMapEditToolbar.setVisibility(View.VISIBLE);
        mBinding.llMapEditToolbar.bringToFront();
        mBinding.mapPointOverlay.setEditMode(true);
        refreshMapEditorControls(null);
        tip("已进入地图编辑模式");
    }

    private void refreshMapEditorControls(MapPointConfig.Marker preferredMarker) {
        if (!mapEditMode || editingMapPointConfig == null) {
            return;
        }

        List<MapPointConfig.Marker> editableMarkers = getEditableMapMarkers();
        MapPointConfig.Marker selectedMarker = preferredMarker;
        if (selectedMarker != null && !editableMarkers.contains(selectedMarker)) {
            selectedMarker = null;
        }
        if (selectedMarker == null && !editableMarkers.isEmpty()) {
            selectedMarker = editableMarkers.get(0);
        }

        mapEditorUpdatingControls = true;
        try {
            mapMarkerChoices.clear();
            mapMarkerChoices.add(new MapMarkerChoice(null, "标注：新建标注"));
            for (MapPointConfig.Marker marker : editableMarkers) {
                DestModel.DataBean robotPoint = findPointById(
                        getDisplayPointData(),
                        marker.getRobotPointId()
                );
                String markerName = robotPoint == null
                        ? String.valueOf(marker.getRobotPointId())
                        : getMapMarkerDisplayName(marker, robotPoint);
                mapMarkerChoices.add(new MapMarkerChoice(marker, "标注：" + markerName));
            }
            mapMarkerChoiceAdapter.notifyDataSetChanged();

            rebuildMapRobotPointChoices(selectedMarker != null);

            currentEditingMapMarker = selectedMarker;
            int markerChoicePosition = selectedMarker == null
                    ? 0
                    : findMapMarkerChoicePosition(selectedMarker);
            mBinding.spinnerMapMarker.setSelection(Math.max(0, markerChoicePosition), false);
            updateMapEditorFields(selectedMarker);
        } finally {
            mapEditorUpdatingControls = false;
        }
        refreshMapPointOverlay();
    }

    private int findMapMarkerChoicePosition(MapPointConfig.Marker marker) {
        for (int choiceIndex = 0; choiceIndex < mapMarkerChoices.size(); choiceIndex++) {
            if (mapMarkerChoices.get(choiceIndex).marker == marker) {
                return choiceIndex;
            }
        }
        return 0;
    }

    private void rebuildMapRobotPointChoices(boolean includeMappedRobotPoints) {
        mapRobotPointChoices.clear();
        Set<Integer> robotPointIds = new HashSet<>();
        for (DestModel.DataBean robotPoint : getDisplayPointData()) {
            boolean robotPointAvailableForSelection = robotPoint != null
                    && robotPointIds.add(robotPoint.getId())
                    && (includeMappedRobotPoints
                    || findMapMarkerByRobotPointId(robotPoint.getId()) == null);
            if (robotPointAvailableForSelection) {
                mapRobotPointChoices.add(new RobotPointChoice(
                        robotPoint,
                        "点位：" + getPointDisplayName(robotPoint) + " (" + robotPoint.getId() + ")"
                ));
            }
        }
        mapRobotPointChoiceAdapter.notifyDataSetChanged();
    }

    private void refreshCurrentMapMarkerChoiceLabel() {
        if (currentEditingMapMarker == null || mapMarkerChoiceAdapter == null) {
            return;
        }
        DestModel.DataBean robotPoint = findPointById(
                getDisplayPointData(),
                currentEditingMapMarker.getRobotPointId()
        );
        String markerLabel = "标注："
                + getMapMarkerDisplayName(currentEditingMapMarker, robotPoint);
        for (MapMarkerChoice markerChoice : mapMarkerChoices) {
            if (markerChoice.marker != currentEditingMapMarker) {
                continue;
            }
            markerChoice.setLabel(markerLabel);
            mapMarkerChoiceAdapter.notifyDataSetChanged();
            View selectedView = mBinding.spinnerMapMarker.getSelectedView();
            if (mBinding.spinnerMapMarker.getSelectedItem() == markerChoice
                    && selectedView instanceof TextView) {
                ((TextView) selectedView).setText(markerLabel);
            }
            return;
        }
    }

    private void updateMapEditorFields(MapPointConfig.Marker marker) {
        RobotPointChoice selectedRobotPointChoice = marker == null
                ? findFirstUnmappedRobotPointChoice()
                : findRobotPointChoice(marker.getRobotPointId());
        int robotPointChoicePosition = mapRobotPointChoices.indexOf(selectedRobotPointChoice);
        if (robotPointChoicePosition >= 0) {
            mBinding.spinnerMapRobotPoint.setSelection(robotPointChoicePosition, false);
        } else {
            mBinding.spinnerMapRobotPoint.setSelection(AdapterView.INVALID_POSITION, false);
        }

        DestModel.DataBean selectedRobotPoint = selectedRobotPointChoice == null
                ? null
                : selectedRobotPointChoice.point;
        String displayName = marker == null
                ? (selectedRobotPoint == null ? "" : getPointDisplayName(selectedRobotPoint))
                : getMapMarkerDisplayName(marker, selectedRobotPoint);
        mBinding.etMapMarkerName.setText(displayName);
        mBinding.etMapMarkerName.setSelection(displayName.length());
        mBinding.spinnerMapRobotPoint.setEnabled(selectedRobotPoint != null);
        mBinding.etMapMarkerName.setEnabled(selectedRobotPoint != null);
        mBinding.btMapMarkerDelete.setEnabled(marker != null);
        if (marker == null && selectedRobotPoint == null) {
            mBinding.tvMapEditStatus.setText(
                    getDisplayPointData().isEmpty() ? "暂无机器人点位" : "所有点位均已配置"
            );
        } else {
            mBinding.tvMapEditStatus.setText("地图编辑中");
        }
        mBinding.mapPointOverlay.setEditingRobotPointId(
                selectedRobotPoint == null ? null : selectedRobotPoint.getId()
        );
    }

    private RobotPointChoice findFirstUnmappedRobotPointChoice() {
        for (RobotPointChoice robotPointChoice : mapRobotPointChoices) {
            if (findMapMarkerByRobotPointId(robotPointChoice.point.getId()) == null) {
                return robotPointChoice;
            }
        }
        return null;
    }

    private RobotPointChoice findRobotPointChoice(int robotPointId) {
        for (RobotPointChoice robotPointChoice : mapRobotPointChoices) {
            if (robotPointChoice.point.getId() == robotPointId) {
                return robotPointChoice;
            }
        }
        return null;
    }

    private void selectMapEditorMarker(MapPointConfig.Marker marker) {
        if (!mapEditMode || editingMapPointConfig == null) {
            return;
        }
        mapEditorUpdatingControls = true;
        try {
            currentEditingMapMarker = marker;
            rebuildMapRobotPointChoices(marker != null);
            updateMapEditorFields(marker);
        } finally {
            mapEditorUpdatingControls = false;
        }
        refreshMapPointOverlay();
    }

    private void selectMapEditorRobotPoint(DestModel.DataBean robotPoint) {
        if (!mapEditMode || editingMapPointConfig == null || robotPoint == null) {
            return;
        }
        if (currentEditingMapMarker != null) {
            MapPointConfig.Marker occupiedMarker = findMapMarkerByRobotPointId(robotPoint.getId());
            if (occupiedMarker != null && occupiedMarker != currentEditingMapMarker) {
                tip("该机器人点位已有地图标注");
                refreshMapEditorControls(currentEditingMapMarker);
                return;
            }
            currentEditingMapMarker.setRobotPointId(robotPoint.getId());
            if (TextUtils.isEmpty(currentEditingMapMarker.getDisplayName())) {
                currentEditingMapMarker.setDisplayName(getPointDisplayName(robotPoint));
            }
            refreshMapEditorControls(currentEditingMapMarker);
            return;
        }

        MapPointConfig.Marker occupiedMarker = findMapMarkerByRobotPointId(robotPoint.getId());
        if (occupiedMarker != null) {
            tip("该机器人点位已有地图标注");
            refreshMapEditorControls(occupiedMarker);
            return;
        }

        mapEditorUpdatingControls = true;
        try {
            String defaultDisplayName = getPointDisplayName(robotPoint);
            mBinding.etMapMarkerName.setText(defaultDisplayName);
            mBinding.etMapMarkerName.setSelection(defaultDisplayName.length());
            mBinding.mapPointOverlay.setEditingRobotPointId(robotPoint.getId());
        } finally {
            mapEditorUpdatingControls = false;
        }
    }

    private void selectMapMarkerFromOverlay(int robotPointId) {
        if (!mapEditMode) {
            return;
        }
        MapPointConfig.Marker selectedMarker = findMapMarkerByRobotPointId(robotPointId);
        if (selectedMarker == null) {
            return;
        }
        int choicePosition = findMapMarkerChoicePosition(selectedMarker);
        if (choicePosition >= 0) {
            mBinding.spinnerMapMarker.setSelection(choicePosition);
        }
    }

    private void updateEditingMapMarkerPosition(
            int robotPointId,
            float normalizedX,
            float normalizedY
    ) {
        if (!mapEditMode || editingMapPointConfig == null) {
            return;
        }
        MapPointConfig.Marker marker = findMapMarkerByRobotPointId(robotPointId);
        if (marker != null) {
            marker.setNormalizedX(normalizedX);
            marker.setNormalizedY(normalizedY);
            currentEditingMapMarker = marker;
            return;
        }

        DestModel.DataBean robotPoint = findPointById(getDisplayPointData(), robotPointId);
        if (robotPoint == null) {
            tip("机器人点位已不存在，无法新增标注");
            return;
        }
        String displayName = mBinding.etMapMarkerName.getText().toString().trim();
        if (TextUtils.isEmpty(displayName)) {
            displayName = getPointDisplayName(robotPoint);
        }
        MapPointConfig.Marker newMarker = new MapPointConfig.Marker(
                robotPointId,
                displayName,
                normalizedX,
                normalizedY,
                true
        );
        editingMapPointConfig.getMarkers().add(newMarker);
        currentEditingMapMarker = newMarker;
        refreshMapEditorControls(newMarker);
    }

    private MapPointConfig.Marker findMapMarkerByRobotPointId(int robotPointId) {
        if (editingMapPointConfig == null) {
            return null;
        }
        for (MapPointConfig.Marker marker : editingMapPointConfig.getMarkers()) {
            if (MapPointConfigSanitizer.isUsableMarker(marker)
                    && marker.getRobotPointId() == robotPointId) {
                return marker;
            }
        }
        return null;
    }

    private void deleteCurrentMapMarker() {
        if (!mapEditMode || editingMapPointConfig == null || currentEditingMapMarker == null) {
            return;
        }
        editingMapPointConfig.getMarkers().remove(currentEditingMapMarker);
        currentEditingMapMarker = null;
        refreshMapEditorControls(null);
    }

    private void saveMapEditing() {
        if (!mapEditMode || editingMapPointConfig == null) {
            return;
        }
        if (getDisplayPointData().isEmpty()) {
            tip("暂无机器人点位，无法保存地图配置");
            return;
        }
        MapPointConfig validatedConfig = buildPersistableMapPointConfig();
        if (validatedConfig == null) {
            return;
        }
        try {
            boolean configSaved = MmkvUtils.saveString(
                    KEY_MAP_POINT_CONFIG,
                    mapConfigGson.toJson(validatedConfig)
            );
            if (!configSaved) {
                Log.e(TAG, "MMKV 返回 false，地图点位配置未保存");
                tip("地图点位配置保存失败");
                return;
            }
            savedMapPointConfig = validatedConfig.copy();
            exitMapEditMode();
            tip("地图点位配置已保存");
        } catch (RuntimeException exception) {
            Log.e(TAG, "保存地图点位配置失败", exception);
            tip("地图点位配置保存失败");
        }
    }

    private MapPointConfig buildPersistableMapPointConfig() {
        MapPointConfig persistedConfig = new MapPointConfig(ROOM_MAP_VERSION);
        Set<Integer> persistedRobotPointIds = new HashSet<>();
        for (MapPointConfig.Marker marker : editingMapPointConfig.getMarkers()) {
            if (marker == null || !marker.isEnabled()) {
                continue;
            }
            if (!hasValidNormalizedCoordinates(marker)) {
                Log.w(TAG, "保存时忽略坐标无效的地图标注: pointId=" + marker.getRobotPointId());
                continue;
            }
            DestModel.DataBean robotPoint = findPointById(
                    getDisplayPointData(),
                    marker.getRobotPointId()
            );
            if (robotPoint == null) {
                Log.w(TAG, "保存时忽略已不存在的机器人点位: pointId=" + marker.getRobotPointId());
                continue;
            }
            if (!persistedRobotPointIds.add(marker.getRobotPointId())) {
                tip("同一个机器人点位只能保存一个地图标注");
                return null;
            }
            MapPointConfig.Marker persistedMarker = marker.copy();
            persistedMarker.setDisplayName(getMapMarkerDisplayName(marker, robotPoint));
            persistedConfig.getMarkers().add(persistedMarker);
        }
        return persistedConfig;
    }

    private void cancelMapEditing() {
        if (!mapEditMode) {
            return;
        }
        exitMapEditMode();
        tip("已取消地图编辑");
    }

    private void exitMapEditMode() {
        mapEditMode = false;
        editingMapPointConfig = null;
        currentEditingMapMarker = null;
        mBinding.llMapEditToolbar.setVisibility(View.GONE);
        mBinding.mapPointOverlay.setEditMode(false);
        mBinding.mapPointOverlay.setEditingRobotPointId(null);
        refreshMapPointOverlay();
    }

    private boolean isRobotTaskBusyForMapEditing() {
        return screenCompartmentRouteActive
                || navigationTaskActive
                || warehouseTaskPending
                || upstreamChargeTaskActive;
    }

    private boolean blockRobotTaskActionWhileMapEditing() {
        if (!mapEditMode) {
            return false;
        }
        tip("地图编辑中，请先保存或取消");
        return true;
    }

    private void discardMapEditingForPreemptingTask(String taskName) {
        if (!mapEditMode) {
            return;
        }
        exitMapEditMode();
        Log.i(TAG, taskName + "抢占，已丢弃未保存的地图编辑工作副本");
        tip(taskName + "已接管，未保存的地图编辑已丢弃");
    }

    private static final class MapMarkerChoice {
        private final MapPointConfig.Marker marker;
        private String label;

        private MapMarkerChoice(MapPointConfig.Marker marker, String label) {
            this.marker = marker;
            this.label = label;
        }

        private void setLabel(String label) {
            this.label = label;
        }

        @NonNull
        @Override
        public String toString() {
            return label;
        }
    }

    private static final class RobotPointChoice {
        private final DestModel.DataBean point;
        private final String label;

        private RobotPointChoice(DestModel.DataBean point, String label) {
            this.point = point;
            this.label = label;
        }

        @NonNull
        @Override
        public String toString() {
            return label;
        }
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
        DiagnosticLogRecorder.info("LIFECYCLE", "MainActivity onDestroy start");
        cancelScreenCompartmentRoute("Activity 销毁");
        activityDestroyed = true;
        handler.removeCallbacks(idleLockRunnable);
        handler.removeCallbacks(pointRefreshRetryRunnable);
        handler.removeCallbacks(startupGoChargeRunnable);
        handler.removeCallbacks(warehouseTaskStatusClearRunnable);
        handler.removeCallbacks(warehouseTaskLoadingRunnable);
        handler.removeCallbacks(warehouseTaskTimeoutRunnable);
        if (server != null) {
            server.stop();
        }
        PickupStatusServer pickupServerToStop = pickupStatusServer;
        pickupStatusServer = null;
        if (pickupServerToStop != null) {
            pickupServerToStop.stop();
        }
        releaseWarehouseTaskResources();
        arrivalHttpClient.dispatcher().executorService().shutdown();
        arrivalHttpClient.connectionPool().evictAll();
        PeanutSDK.getInstance().release();
        DiagnosticLogRecorder.info(
                "NAV",
                "调用 NavManager.stop/release reason=Activity 销毁"
        );
        NavManager.getInstance().stop();
        NavManager.getInstance().release();
        PeanutRuntime.getInstance().removeListener(mRuntimeListener);
        if (mPeanutCharger != null) {
            mPeanutCharger.release();
        }
        DiagnosticLogRecorder.info("LIFECYCLE", "MainActivity onDestroy complete");
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
        DiagnosticLogRecorder.info("SDK", "PeanutSDK 初始化回调 code=" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (errorCode == SDK_INIT_SUCCESS) {
                    Log.d("routeNodes","SDK_INIT_SUCCESS:"+errorCode);
                    DiagnosticLogRecorder.info("SDK", "PeanutSDK 初始化成功");
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
                    DiagnosticLogRecorder.error(
                            "SDK",
                            "PeanutSDK 初始化失败 code=" + errorCode
                    );
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

        if (id == mBinding.tvPointSelectionTab.getId()) {
            showPointSelectionView();
        } else if (id == mBinding.tvDeliveryProgressTab.getId()) {
            showDeliveryProgressView();
        } else if (id == mBinding.tvDiagnosticLogs.getId()) {
            showDiagnosticLogDialog();
        } else if (id == mBinding.tvCompartment1.getId()) {
            handleCompartmentClick(0);
        } else if (id == mBinding.tvCompartment2.getId()) {
            handleCompartmentClick(1);
        } else if (id == mBinding.tvCompartment3.getId()) {
            handleCompartmentClick(2);
        } else if (id==mBinding.tvNavigate.getId()){
            if (blockRobotTaskActionWhileMapEditing()) {
                return;
            }
            if (selectedPointList.isEmpty()){
                tip("请先绑定点位到仓位");
                return;
            }
            routeNodes = new ArrayList<>();

            for (DestModel.DataBean dataBean : selectedPointList) {
                RouteNode routeNode = new RouteNode();
                routeNode.setId(dataBean.getId());
                routeNode.setName(dataBean.getName());
                routeNodes.add(routeNode);
            }
            if (!activateScreenCompartmentRoute()) {
                return;
            }
            flag="";
            mBinding.tvNavigate.setEnabled(false);
            DiagnosticLogRecorder.info(
                    "DELIVERY",
                    "用户点击立即出发 " + describeRoute(routeNodes)
            );
            Log.d("navigatenext","routeNodes===="+new Gson().toJson(routeNodes)+",size="+routeNodes.size());
//            RouteNode node = new RouteNode();
//            node.setId(Integer.parseInt(editText.getText().toString()));
//            node.setName("Point:" + editText.getText().toString());
//            point.setRouteNode(node);
            prepareNav(routeNodes);

        }else if (id==mBinding.tvSecondaryScreenDisplay.getId()){
            //   startHardwareTests(null);
        }else if (id==mBinding.tvRefreshPoints.getId()){
            if (blockRobotTaskActionWhileMapEditing()) {
                return;
            }
            if (screenCompartmentRouteActive) {
                tip("配送进行中，暂不支持刷新点位");
                return;
            }
            refreshPointData(true);
        }else if (id==mBinding.tvGoCharge.getId()){
            if (blockRobotTaskActionWhileMapEditing()) {
                return;
            }
            cancelScreenCompartmentRoute("手动回充");
            mBinding.tvNavigate.setEnabled(true);
            DiagnosticLogRecorder.info("WAREHOUSE", "用户点击回充");
            sendGoChargeTask();
        }else if (id==mBinding.tvPatrolWarehouse.getId()){
            if (blockRobotTaskActionWhileMapEditing()) {
                return;
            }
            cancelScreenCompartmentRoute("手动巡仓");
            mBinding.tvNavigate.setEnabled(true);
            DiagnosticLogRecorder.info("WAREHOUSE", "用户点击巡仓");
            sendPatrolWarehouseTask();
        }else if (id==mBinding.tvRecall.getId()){
            if (blockRobotTaskActionWhileMapEditing()) {
                return;
            }
            cancelScreenCompartmentRoute("手动召回");
            mBinding.tvNavigate.setEnabled(true);
            DiagnosticLogRecorder.info("WAREHOUSE", "用户点击召回");
            sendRecallTask();
        }
    }

    private void showDiagnosticLogDialog() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12), dp(8), dp(12), dp(8));

        TextView logTextView = new TextView(this);
        logTextView.setTextColor(ContextCompat.getColor(this, R.color.ui_text_primary));
        logTextView.setTextSize(12);
        logTextView.setTypeface(Typeface.MONOSPACE);
        logTextView.setTextIsSelectable(true);
        logTextView.setPadding(dp(8), dp(8), dp(8), dp(8));

        ScrollView logScrollView = new ScrollView(this);
        logScrollView.setFillViewport(true);
        logScrollView.addView(logTextView, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));
        content.addView(logScrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);
        String[] actionLabels = {"刷新", "复制全部", "清空", "关闭"};
        Button[] actionButtons = new Button[actionLabels.length];
        for (int index = 0; index < actionLabels.length; index++) {
            Button actionButton = new Button(this);
            actionButton.setText(actionLabels[index]);
            actionButton.setTextSize(13);
            actionButton.setAllCaps(false);
            actions.addView(actionButton, new LinearLayout.LayoutParams(0, dp(44), 1f));
            actionButtons[index] = actionButton;
        }
        content.addView(actions, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        AlertDialog logDialog = new AlertDialog.Builder(this)
                .setTitle("运行日志")
                .setView(content)
                .create();

        Runnable refreshLogs = () -> {
            String snapshot = DiagnosticLogRecorder.snapshot();
            logTextView.setText(TextUtils.isEmpty(snapshot) ? "暂无运行日志" : snapshot);
            logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
        };
        actionButtons[0].setOnClickListener(view -> refreshLogs.run());
        actionButtons[1].setOnClickListener(view -> {
            String snapshot = DiagnosticLogRecorder.snapshot();
            if (TextUtils.isEmpty(snapshot)) {
                tip("暂无可复制的运行日志");
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) getSystemService(
                    Context.CLIPBOARD_SERVICE
            );
            if (clipboard == null) {
                tip("系统剪贴板不可用");
                return;
            }
            try {
                clipboard.setPrimaryClip(ClipData.newPlainText("运行日志", snapshot));
                tip("运行日志已复制");
            } catch (RuntimeException exception) {
                Log.e(TAG, "copy diagnostic logs failed", exception);
                tip("运行日志复制失败，请重试");
            }
        });
        actionButtons[2].setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("清空运行日志")
                .setMessage("仅清除诊断记录，不会停止或改变当前机器人任务。")
                .setNegativeButton("取消", null)
                .setPositiveButton("清空", (dialog, which) -> {
                    DiagnosticLogRecorder.clear(success -> runOnUiThread(() -> {
                        refreshLogs.run();
                        tip(success
                                ? "运行日志已清空"
                                : "内存日志已清空，本地文件清理失败");
                    }));
                    refreshLogs.run();
                })
                .show());
        actionButtons[3].setOnClickListener(view -> logDialog.dismiss());

        logDialog.setOnShowListener(dialog -> {
            Window dialogWindow = logDialog.getWindow();
            if (dialogWindow != null) {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                dialogWindow.setLayout(
                        (int) (screenWidth * 0.92f),
                        (int) (screenHeight * 0.82f)
                );
            }
        });
        refreshLogs.run();
        logDialog.show();
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
        DiagnosticLogRecorder.info(
                "WAREHOUSE",
                "计划启动自动回充 delayMs=" + STARTUP_GO_CHARGE_DELAY_MS
        );
        handler.removeCallbacks(startupGoChargeRunnable);
        handler.postDelayed(startupGoChargeRunnable, STARTUP_GO_CHARGE_DELAY_MS);
    }

    private void sendStartupGoChargeTask() {
        if (startupGoChargeSent) {
            return;
        }
        if (mapEditMode) {
            Log.d(TAG, "delay startup go charge: map editing active");
            scheduleStartupGoChargeTask();
            return;
        }
        if (warehouseTaskPending) {
            Log.d(TAG, "skip startup go charge: warehouse task pending");
            return;
        }

        startupGoChargeSent = true;
        Log.d(TAG, "send startup go charge task");
        DiagnosticLogRecorder.info("WAREHOUSE", "发送启动自动回充任务");
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
        if (blockRobotTaskActionWhileMapEditing()) {
            return;
        }
        if (warehouseTaskPending) {
            tip(pendingWarehouseTaskName + "请求发送中，请稍候");
            return;
        }

        beginWarehouseTaskLoading(taskName);
        Log.d(TAG, taskName + "指令发送到 " + WAREHOUSE_TASK_WS + ": " + payload);
        DiagnosticLogRecorder.info(
                "WAREHOUSE",
                "发送任务 name=" + taskName + ", endpoint=" + WAREHOUSE_TASK_WS
        );
        Request request = new Request.Builder()
                .url(WAREHOUSE_TASK_WS)
                .build();

        warehouseTaskWebSocketClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                pendingWarehouseTaskWebSocket = webSocket;
                if (activityDestroyed) {
                    pendingWarehouseTaskWebSocket = null;
                    webSocket.close(1001, "activity destroyed");
                    webSocket.cancel();
                    return;
                }
                boolean sent = webSocket.send(payload);
                Log.d(TAG, taskName + "指令" + (sent ? "发送成功" : "发送失败") + ": " + payload);
                DiagnosticLogRecorder.info(
                        "WAREHOUSE",
                        "任务 WebSocket 写入 name=" + taskName + ", success=" + sent
                );
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
                DiagnosticLogRecorder.info(
                        "WAREHOUSE",
                        "收到任务响应 name=" + taskName + ", binary=false"
                );
                if (activityDestroyed) {
                    webSocket.close(1001, "activity destroyed");
                    return;
                }
                handleWarehouseTaskResponse(taskName, text, successMessage);
                webSocket.close(1000, taskName + " response received");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                String text = bytes.utf8();
                Log.d(TAG, taskName + "指令收到二进制响应: " + text);
                DiagnosticLogRecorder.info(
                        "WAREHOUSE",
                        "收到任务响应 name=" + taskName + ", binary=true"
                );
                if (activityDestroyed) {
                    webSocket.close(1001, "activity destroyed");
                    return;
                }
                handleWarehouseTaskResponse(taskName, text, successMessage);
                webSocket.close(1000, taskName + " response received");
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, taskName + "指令连接失败: " + t.getMessage(), t);
                DiagnosticLogRecorder.error(
                        "WAREHOUSE",
                        "任务连接失败 name=" + taskName + "：" + t.getMessage()
                );
                if (activityDestroyed) {
                    return;
                }
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
        DiagnosticLogRecorder.info(
                "WAREHOUSE",
                "任务已受理 name=" + taskName + ", message=" + message
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activityDestroyed) {
                    return;
                }
                finishWarehouseTaskUi(taskName);
                mBinding.tvWarehouseTaskStatus.setText(message);
                mBinding.tvWarehouseTaskStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blue));
                tip(message);
                scheduleWarehouseTaskStatusClear();
            }
        });
    }

    private void handleWarehouseTaskFailure(String taskName, String message) {
        if (!warehouseTaskPending) {
            return;
        }
        Log.e(TAG, taskName + "指令失败: " + message);
        DiagnosticLogRecorder.error(
                "WAREHOUSE",
                "任务失败 name=" + taskName + ", message=" + message
        );
        closePendingWarehouseTaskWebSocket();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activityDestroyed) {
                    return;
                }
                finishWarehouseTaskUi(taskName);
                String displayMessage = taskName + "失败：" + message;
                mBinding.tvWarehouseTaskStatus.setText(displayMessage);
                mBinding.tvWarehouseTaskStatus.setTextColor(Color.RED);
                tip(displayMessage);
                scheduleWarehouseTaskStatusClear();
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
                if (activityDestroyed) {
                    return;
                }
                handler.removeCallbacks(warehouseTaskStatusClearRunnable);
                mBinding.tvWarehouseTaskStatus.setText(message);
                mBinding.tvWarehouseTaskStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.grey_700));
            }
        });
    }

    private void scheduleWarehouseTaskStatusClear() {
        handler.removeCallbacks(warehouseTaskStatusClearRunnable);
        handler.postDelayed(warehouseTaskStatusClearRunnable, WAREHOUSE_TASK_STATUS_CLEAR_DELAY_MS);
    }

    private void closePendingWarehouseTaskWebSocket() {
        if (pendingWarehouseTaskWebSocket != null) {
            pendingWarehouseTaskWebSocket.close(1000, "warehouse task finished");
            pendingWarehouseTaskWebSocket = null;
        }
    }

    private void releaseWarehouseTaskResources() {
        WebSocket warehouseTaskWebSocket = pendingWarehouseTaskWebSocket;
        pendingWarehouseTaskWebSocket = null;
        warehouseTaskPending = false;
        pendingWarehouseTaskName = "";
        if (warehouseTaskWebSocket != null) {
            warehouseTaskWebSocket.close(1001, "activity destroyed");
            warehouseTaskWebSocket.cancel();
        }
        warehouseTaskWebSocketClient.dispatcher().cancelAll();
        warehouseTaskWebSocketClient.dispatcher().executorService().shutdown();
        warehouseTaskWebSocketClient.connectionPool().evictAll();
    }

    /**
     * 准备导航
     * @param routeNodes
     */
    private void prepareNav(List<RouteNode> newRouteNodes) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            List<RouteNode> routeSnapshot = newRouteNodes == null
                    ? null
                    : new ArrayList<>(newRouteNodes);
            handler.post(() -> prepareNav(routeSnapshot));
            return;
        }
        if (activityDestroyed || newRouteNodes == null || newRouteNodes.isEmpty()) {
            Log.w(TAG, "ignore invalid navigation route");
            DiagnosticLogRecorder.warn("NAV", "忽略无效导航路线");
            return;
        }

        invalidateNavigationTask("prepare replacement");
        DiagnosticLogRecorder.info(
                "NAV",
                "调用 NavManager.stop reason=prepare replacement"
        );
        NavManager.getInstance().stop();
//        NavManager.getInstance().release();
        routeNodes = new ArrayList<>(newRouteNodes);
        navigationRouteOffset = 0;

        navigationTaskGeneration++;
        activeNavigationTaskGeneration = navigationTaskGeneration;
        navigationTaskActive = true;
        navigationLegArmed = false;
        navigationLegPosition = -1;
        expectedNavigationRoutePosition = 0;

        DiagnosticLogRecorder.info(
                "NAV",
                "创建导航任务 task=" + activeNavigationTaskGeneration
                        + ", source=" + (screenCompartmentRouteActive ? "screen" : flag)
                        + ", " + describeRoute(routeNodes)
        );
        prepareNavigationRoute();
    }

    private void prepareNavigationRoute() {
        NavManager navManager = NavManager.getInstance();
        activeNavigationSessionGeneration = navManager.getNavigationSessionGeneration();
        peanutNavigation = navManager.getmPeanutNavigation();
        peanutNavigation.setTargets(new ArrayList<>(routeNodes));

        int navigationSpeed = routeNodes.size()==1
                ? MmkvUtils.decodeInt("single_point_speed", DEFAULT_NAVIGATION_SPEED)
                : MmkvUtils.decodeInt("multiple_point_speed", DEFAULT_NAVIGATION_SPEED);
        navManager.setSpeed(navigationSpeed);
        DiagnosticLogRecorder.info(
                "NAV",
                "准备路线 task=" + activeNavigationTaskGeneration
                        + ", session=" + activeNavigationSessionGeneration
                        + ", speed=" + navigationSpeed
        );
        scheduleNavigationPrepareTimeout(
                activeNavigationTaskGeneration,
                activeNavigationSessionGeneration
        );
        navManager.prepare();
    }

    private void scheduleNavigationPrepareTimeout(int taskGeneration, int sessionGeneration) {
        clearNavigationPrepareTimeout();
        navigationPrepareTimeoutRunnable = () -> handleNavigationPrepareTimeout(
                taskGeneration,
                sessionGeneration
        );
        handler.postDelayed(navigationPrepareTimeoutRunnable, NAVIGATION_PREPARE_TIMEOUT_MS);
    }

    private void clearNavigationPrepareTimeout() {
        Runnable timeoutRunnable = navigationPrepareTimeoutRunnable;
        navigationPrepareTimeoutRunnable = null;
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }
    }

    private void handleNavigationPrepareTimeout(int taskGeneration, int sessionGeneration) {
        navigationPrepareTimeoutRunnable = null;
        if (!isCurrentNavigationTask(taskGeneration)
                || sessionGeneration != activeNavigationSessionGeneration) {
            DiagnosticLogRecorder.debug(
                    "NAV",
                    "忽略旧路线准备超时 task=" + taskGeneration
                            + ", session=" + sessionGeneration
            );
            return;
        }
        DiagnosticLogRecorder.error(
                "NAV",
                "路线准备超时 task=" + taskGeneration + ", session=" + sessionGeneration
        );
        if (screenCompartmentRouteActive) {
            cancelScreenCompartmentRoute("导航路线准备超时");
        } else {
            invalidateNavigationTask("navigation prepare timeout");
            DiagnosticLogRecorder.info(
                    "NAV",
                    "调用 NavManager.stop reason=navigation prepare timeout"
            );
            NavManager.getInstance().stop();
        }
        mBinding.tvNavigate.setEnabled(true);
        flag = "";
        tip("导航路线准备失败，请重试");
    }

    private int getCurrentRoutePosition() {
        if (peanutNavigation == null) {
            return -1;
        }
        int sdkPosition = peanutNavigation.getCurrentPosition();
        if (sdkPosition < 0) {
            return -1;
        }
        return navigationRouteOffset + sdkPosition;
    }

    private void startNextScreenNavigationLeg(int taskGeneration, int nextPosition) {
        if (!isCurrentNavigationTask(taskGeneration) || !isValidRoutePosition(nextPosition)) {
            Log.w(TAG, "ignore invalid next screen leg: generation=" + taskGeneration
                    + ", position=" + nextPosition);
            return;
        }
        navigationLegArmed = false;
        navigationLegPosition = -1;
        expectedNavigationRoutePosition = nextPosition;
        DiagnosticLogRecorder.info(
                "NAV",
                "前往下一点 task=" + taskGeneration + ", position=" + nextPosition
        );
        NavManager.getInstance().nextDes();
        NavManager.getInstance().readyGo(true);
    }

    private String describeRoute(List<RouteNode> nodes) {
        if (nodes == null) {
            return "route=null";
        }
        StringBuilder description = new StringBuilder("routeSize=").append(nodes.size());
        int displayedNodes = Math.min(nodes.size(), 10);
        for (int index = 0; index < displayedNodes; index++) {
            RouteNode node = nodes.get(index);
            description.append(", [").append(index).append("]=");
            if (node == null) {
                description.append("null");
            } else {
                description.append(node.getId()).append(':').append(node.getName());
            }
        }
        if (nodes.size() > displayedNodes) {
            description.append(", ...");
        }
        return description.toString();
    }

    public  List<DestModel.DataBean> getRouteNodesList(){

        return  mAdapter.getData();
    }

        @Override
    public void onStateChanged(int state, int schedule) {
        Log.w(TAG, "ignore navigation state without session token: state=" + state);
    }

    @Override
    public void onSessionStateChanged(int sessionGeneration, int state, int schedule) {
        int observedTaskGeneration = navigationTaskGeneration;
        DiagnosticLogRecorder.info(
                "NAV",
                "SDK 状态 state=" + state
                        + ", schedule=" + schedule
                        + ", observedTask=" + observedTaskGeneration
                        + ", session=" + sessionGeneration
        );
        if (Looper.myLooper() == Looper.getMainLooper()) {
            handleNavigationStateChanged(state, observedTaskGeneration, sessionGeneration);
        } else {
            handler.post(() -> handleNavigationStateChanged(
                    state,
                    observedTaskGeneration,
                    sessionGeneration
            ));
        }
    }

    private void handleNavigationStateChanged(
            int state,
            int observedTaskGeneration,
            int observedSessionGeneration
    ) {
        Log.d("navigatenext", "state=" + state + ", generation=" + observedTaskGeneration);
        if (!isCurrentNavigationTask(observedTaskGeneration)
                || observedSessionGeneration != activeNavigationSessionGeneration
                || peanutNavigation == null) {
            Log.d(TAG, "ignore stale navigation state: state=" + state
                    + ", generation=" + observedTaskGeneration
                    + ", session=" + observedSessionGeneration);
            DiagnosticLogRecorder.warn(
                    "NAV",
                    "忽略旧导航状态 state=" + state
                            + ", task=" + observedTaskGeneration
                            + ", activeTask=" + activeNavigationTaskGeneration
                            + ", session=" + observedSessionGeneration
                            + ", activeSession=" + activeNavigationSessionGeneration
            );
            return;
        }

        if (state == Navigation.STATE_RUNNING) {
            int currentPosition = getCurrentRoutePosition();
            if (!isValidRoutePosition(currentPosition)
                    || currentPosition != expectedNavigationRoutePosition
                    || isArrivalWaitActive()) {
                DiagnosticLogRecorder.warn(
                        "NAV",
                        "拒绝 STATE_RUNNING position=" + currentPosition
                                + ", expected=" + expectedNavigationRoutePosition
                                + ", waiting=" + isArrivalWaitActive()
                );
                return;
            }
            navigationLegPosition = currentPosition;
            navigationLegArmed = true;
            Log.d(TAG, "navigation leg armed: generation=" + observedTaskGeneration
                    + ", position=" + currentPosition);
            DiagnosticLogRecorder.info(
                    "NAV",
                    "接受 STATE_RUNNING task=" + observedTaskGeneration
                            + ", position=" + currentPosition
            );
            return;
        }

        if (state != Navigation.STATE_DESTINATION) {
            return;
        }

        int currentPosition = getCurrentRoutePosition();
        if (!navigationLegArmed
                || currentPosition != navigationLegPosition
                || !isValidRoutePosition(currentPosition)) {
            Log.d(TAG, "ignore unarmed destination: generation=" + observedTaskGeneration
                    + ", position=" + currentPosition
                    + ", armedPosition=" + navigationLegPosition);
            DiagnosticLogRecorder.warn(
                    "NAV",
                    "拒绝 STATE_DESTINATION task=" + observedTaskGeneration
                            + ", position=" + currentPosition
                            + ", armed=" + navigationLegArmed
                            + ", armedPosition=" + navigationLegPosition
            );
            return;
        }

        navigationLegArmed = false;
        DiagnosticLogRecorder.info(
                "NAV",
                "接受 STATE_DESTINATION task=" + observedTaskGeneration
                        + ", position=" + currentPosition
        );
        arrived(observedTaskGeneration, currentPosition);
    }

    private void arrived(int taskGeneration, int currentPosition) {
        if (!isCurrentNavigationTask(taskGeneration)
                || peanutNavigation == null
                || !isValidRoutePosition(currentPosition)) {
            Log.w(TAG, "ignore invalid arrival: generation=" + taskGeneration
                    + ", position=" + currentPosition);
            DiagnosticLogRecorder.warn(
                    "NAV",
                    "忽略无效到位 task=" + taskGeneration + ", position=" + currentPosition
            );
            return;
        }
        DiagnosticLogRecorder.info(
                "NAV",
                "到位后发送 readyGo(false) task=" + taskGeneration
                        + ", position=" + currentPosition
                        + ", screenRoute=" + screenCompartmentRouteActive
        );
        NavManager.getInstance().readyGo(false);
//        Log.d("navigatenext","getQueueName="+(response.get(0).getQueueName()));
        Log.d("navigatenext","getCurrentPosition="+currentPosition+",size=="+routeNodes.size());
        if (screenCompartmentRouteActive) {
            handleScreenCompartmentArrival(taskGeneration, currentPosition);
            return;
        }
        if ("send_point".equals(flag)
                && !TextUtils.isEmpty(routeNodes.get(currentPosition).getQueueName())){
            mediaPlayerShow(routeNodes.get(currentPosition).getQueueName());
        }
        //   ttsUntil.speech(peanutNavigation.getCurrentNode().getQueueName(),false);
        navigatenext();
        //  Toast.makeText(this,"已经到达目的地", Toast.LENGTH_SHORT).show();
    }

    private boolean isCurrentNavigationTask(int taskGeneration) {
        return !activityDestroyed
                && navigationTaskActive
                && taskGeneration == navigationTaskGeneration
                && taskGeneration == activeNavigationTaskGeneration;
    }

    private boolean isValidRoutePosition(int position) {
        return routeNodes != null && position >= 0 && position < routeNodes.size();
    }

    private boolean isArrivalWaitActive() {
        synchronized (arrivalWaitLock) {
            return arrivalWaitActive;
        }
    }

    private void invalidateNavigationTask(String reason) {
        int invalidatedGeneration = activeNavigationTaskGeneration;
        boolean taskWasActive = navigationTaskActive;
        navigationTaskGeneration++;
        activeNavigationTaskGeneration = navigationTaskGeneration;
        navigationTaskActive = false;
        activeNavigationSessionGeneration = -1;
        navigationLegArmed = false;
        navigationLegPosition = -1;
        expectedNavigationRoutePosition = -1;
        navigationRouteOffset = 0;
        clearNavigationPrepareTimeout();
        Log.d(TAG, "navigation task invalidated: " + reason
                + ", generation=" + navigationTaskGeneration);
        DiagnosticLogRecorder.info(
                "NAV",
                "导航任务失效 reason=" + reason
                        + ", wasActive=" + taskWasActive
                        + ", invalidatedTask=" + invalidatedGeneration
                        + ", nextGeneration=" + navigationTaskGeneration
        );
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

    private void handleScreenCompartmentArrival(int taskGeneration, int currentPosition) {
        if (!isCurrentNavigationTask(taskGeneration) || !isValidRoutePosition(currentPosition)) {
            Log.e(TAG, "screen arrival position is invalid: " + currentPosition);
            return;
        }

        RouteNode currentNode = routeNodes.get(currentPosition);
        Integer bay;
        int waitGeneration;
        synchronized (arrivalWaitLock) {
            if (!screenCompartmentRouteActive
                    || arrivalWaitActive
                    || lastHandledScreenArrivalPosition == currentPosition) {
                Log.d(TAG, "ignore duplicate screen arrival at position " + currentPosition);
                return;
            }
            bay = screenRouteBayByPointId.get(currentNode.getId());
            if (bay == null) {
                Log.e(TAG, "no compartment mapping for point " + currentNode.getId());
                return;
            }
            arrivalWaitGeneration++;
            waitGeneration = arrivalWaitGeneration;
            arrivalWaitActive = true;
            arrivalWaitNavigationTaskGeneration = taskGeneration;
            arrivalWaitRoutePosition = currentPosition;
            lastHandledScreenArrivalPosition = currentPosition;
        }
        arrivalWaitDeadlineElapsedRealtime = SystemClock.elapsedRealtime() + ARRIVAL_WAIT_TIMEOUT_MS;
        updateDeliveryProgressStatus(currentPosition, DELIVERY_STATUS_WAITING);
        handler.removeCallbacks(deliveryCountdownRunnable);
        handler.post(deliveryCountdownRunnable);

        if (routeNodes.size() == 1) {
            Log.d("navigatenext","delivery_voice_address="+DEFAULT_DELIVERY_VOICE_URL);
            mediaPlayerShow(DEFAULT_DELIVERY_VOICE_URL);
        }

        sendArrivalReport(currentNode, bay, waitGeneration);
        Runnable timeoutRunnable = () -> {
            if (!claimArrivalWait(waitGeneration)) {
                return;
            }
            completeArrivalWait(waitGeneration, "等待五分钟超时");
        };
        synchronized (arrivalWaitLock) {
            if (waitGeneration != arrivalWaitGeneration || !arrivalWaitActive) {
                return;
            }
            arrivalWaitTimeoutRunnable = timeoutRunnable;
        }
        handler.postDelayed(timeoutRunnable, ARRIVAL_WAIT_TIMEOUT_MS);
        Log.i(TAG, "arrival wait started: point=" + currentNode.getName()
                + ", bay=" + bay + ", generation=" + waitGeneration);
        DiagnosticLogRecorder.info(
                "ARRIVAL",
                "开始等待取餐 point=" + currentNode.getName()
                        + ", pointId=" + currentNode.getId()
                        + ", bay=" + bay
                        + ", wait=" + waitGeneration
                        + ", task=" + taskGeneration
                        + ", timeoutMs=" + ARRIVAL_WAIT_TIMEOUT_MS
        );
    }

    private boolean handlePickupCompleted() {
        int waitGeneration;
        synchronized (arrivalWaitLock) {
            waitGeneration = arrivalWaitGeneration;
        }
        if (!claimArrivalWait(waitGeneration)) {
            DiagnosticLogRecorder.warn(
                    "PICKUP",
                    "拒绝 pickup_status：当前无可消费等待 wait=" + waitGeneration
            );
            return false;
        }
        DiagnosticLogRecorder.info(
                "PICKUP",
                "接受 pickup_status wait=" + waitGeneration
        );
        handler.post(() -> completeArrivalWait(waitGeneration, "收到取餐完成通知"));
        return true;
    }

    private boolean claimArrivalWait(int waitGeneration) {
        synchronized (arrivalWaitLock) {
            if (!screenCompartmentRouteActive
                    || !arrivalWaitActive
                    || waitGeneration != arrivalWaitGeneration) {
                return false;
            }
            arrivalWaitActive = false;
            return true;
        }
    }

    private void completeArrivalWait(int waitGeneration, String reason) {
        Runnable timeoutRunnable;
        int taskGeneration;
        int routePosition;
        synchronized (arrivalWaitLock) {
            if (!screenCompartmentRouteActive || waitGeneration != arrivalWaitGeneration) {
                return;
            }
            timeoutRunnable = arrivalWaitTimeoutRunnable;
            arrivalWaitTimeoutRunnable = null;
            taskGeneration = arrivalWaitNavigationTaskGeneration;
            routePosition = arrivalWaitRoutePosition;
        }
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }
        handler.removeCallbacks(deliveryCountdownRunnable);
        arrivalWaitDeadlineElapsedRealtime = 0L;
        cancelPendingArrivalReport();
        if (!isCurrentNavigationTask(taskGeneration)) {
            Log.d(TAG, "ignore stale arrival wait completion: waitGeneration=" + waitGeneration
                    + ", taskGeneration=" + taskGeneration);
            DiagnosticLogRecorder.warn(
                    "ARRIVAL",
                    "忽略旧等待完成 wait=" + waitGeneration + ", task=" + taskGeneration
            );
            return;
        }
        Log.i(TAG, reason + ", generation=" + waitGeneration);
        DiagnosticLogRecorder.info(
                "ARRIVAL",
                "完成等待 reason=" + reason
                        + ", wait=" + waitGeneration
                        + ", task=" + taskGeneration
                        + ", position=" + routePosition
        );
        String completedStatus = reason.contains("超时")
                ? DELIVERY_STATUS_TIMED_OUT
                : DELIVERY_STATUS_PICKED_UP;
        updateDeliveryProgressStatus(routePosition, completedStatus);

        if (routePosition == routeNodes.size() - 1) {
            synchronized (arrivalWaitLock) {
                screenCompartmentRouteActive = false;
                screenRouteBayByPointId.clear();
                lastHandledScreenArrivalPosition = -1;
            }
            invalidateNavigationTask("screen route completed");
            deliveryProgressSummary = "配送完成 · 已开始召回";
            renderDeliveryProgress();
            refreshPointBindingUi();
            mBinding.tvNavigate.setEnabled(true);
            sendRecallTask();
        } else {
            updateDeliveryProgressStatus(routePosition + 1, DELIVERY_STATUS_TRAVELING);
            startNextScreenNavigationLeg(taskGeneration, routePosition + 1);
        }
    }

    private void sendArrivalReport(RouteNode currentNode, int bay, int waitGeneration) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("point_name", currentNode.getName());
            payload.put("bay", bay);
            RequestBody requestBody = RequestBody.create(payload.toString(), JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(NAV_ARRIVE_URL)
                    .post(requestBody)
                    .build();

            Call arrivalReportCall = arrivalHttpClient.newCall(request);
            Call previousArrivalReportCall;
            synchronized (arrivalWaitLock) {
                if (!screenCompartmentRouteActive
                        || !arrivalWaitActive
                        || waitGeneration != arrivalWaitGeneration) {
                    Log.d(TAG, "skip stale nav_arrive request: generation=" + waitGeneration);
                    return;
                }
                previousArrivalReportCall = pendingArrivalReportCall;
                pendingArrivalReportCall = arrivalReportCall;
            }
            if (previousArrivalReportCall != null) {
                previousArrivalReportCall.cancel();
            }
            Log.i(TAG, "send nav_arrive: " + payload);
            DiagnosticLogRecorder.info(
                    "HTTP",
                    "发送 nav_arrive point=" + currentNode.getName()
                            + ", bay=" + bay
                            + ", wait=" + waitGeneration
            );
            arrivalReportCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                    clearPendingArrivalReport(call);
                    if (!call.isCanceled()) {
                        Log.e(TAG, "nav_arrive request failed", exception);
                        DiagnosticLogRecorder.error(
                                "HTTP",
                                "nav_arrive 失败 wait=" + waitGeneration
                                        + "：" + exception.getMessage()
                        );
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try (Response arrivalResponse = response) {
                        String responseBody = arrivalResponse.body() == null
                                ? ""
                                : arrivalResponse.body().string();
                        Log.i(TAG, "nav_arrive response: status=" + arrivalResponse.code()
                                + ", body=" + responseBody);
                        DiagnosticLogRecorder.info(
                                "HTTP",
                                "nav_arrive 响应 wait=" + waitGeneration
                                        + ", status=" + arrivalResponse.code()
                        );
                    } finally {
                        clearPendingArrivalReport(call);
                    }
                }
            });
        } catch (JSONException exception) {
            Log.e(TAG, "failed to create nav_arrive payload", exception);
            DiagnosticLogRecorder.error(
                    "HTTP",
                    "创建 nav_arrive 请求失败：" + exception.getMessage()
            );
        }
    }

    private void cancelPendingArrivalReport() {
        Call arrivalReportCall;
        synchronized (arrivalWaitLock) {
            arrivalReportCall = pendingArrivalReportCall;
            pendingArrivalReportCall = null;
        }
        if (arrivalReportCall != null) {
            arrivalReportCall.cancel();
            DiagnosticLogRecorder.debug("HTTP", "取消未完成 nav_arrive 请求");
        }
    }

    private void clearPendingArrivalReport(Call arrivalReportCall) {
        synchronized (arrivalWaitLock) {
            if (pendingArrivalReportCall == arrivalReportCall) {
                pendingArrivalReportCall = null;
            }
        }
    }

    private void cancelScreenCompartmentRoute(String reason) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(() -> cancelScreenCompartmentRoute(reason));
            return;
        }
        boolean deliveryRouteWasActive = screenCompartmentRouteActive;
        Runnable timeoutRunnable;
        synchronized (arrivalWaitLock) {
            arrivalWaitGeneration++;
            arrivalWaitActive = false;
            arrivalWaitNavigationTaskGeneration = 0;
            arrivalWaitRoutePosition = -1;
            screenCompartmentRouteActive = false;
            lastHandledScreenArrivalPosition = -1;
            screenRouteBayByPointId.clear();
            timeoutRunnable = arrivalWaitTimeoutRunnable;
            arrivalWaitTimeoutRunnable = null;
        }
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
        }
        handler.removeCallbacks(deliveryCountdownRunnable);
        arrivalWaitDeadlineElapsedRealtime = 0L;
        cancelPendingArrivalReport();
        invalidateNavigationTask(reason);
        if (deliveryRouteWasActive) {
            for (DeliveryProgressItem progressItem : deliveryProgressItems) {
                if (DELIVERY_STATUS_QUEUED.equals(progressItem.status)
                        || DELIVERY_STATUS_TRAVELING.equals(progressItem.status)
                        || DELIVERY_STATUS_WAITING.equals(progressItem.status)) {
                    progressItem.status = DELIVERY_STATUS_CANCELLED;
                }
            }
            deliveryProgressSummary = "配送结束 · " + reason;
            renderDeliveryProgress();
            refreshPointBindingUi();
        }
        Log.d(TAG, "screen compartment route cancelled: " + reason);
        DiagnosticLogRecorder.info(
                "DELIVERY",
                "屏幕仓位任务取消 reason=" + reason
                        + ", wasActive=" + deliveryRouteWasActive
        );
    }

    private void navigatenext() {
        Log.d("navigatenext","lastNode="+NavManager.getInstance().isLastNode());
        Log.d("navigatenext","NextNode="+new Gson().toJson(NavManager.getInstance().getNextNode()));
        Log.d("navigatenext","Targets="+new Gson().toJson(NavManager.getInstance().getTargets()));
        if ( NavManager.getInstance().isLastNode()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (activityDestroyed) {
                        return;
                    }
                    String completedTaskFlag = flag;
                    if (!TextUtils.isEmpty(completedTaskFlag)){
                        String completionMessage = completedTaskFlag + "已完成";
                        if (webSocketService == null) {
                            Log.w(TAG, "skip navigation completion report: websocket unavailable");
                        } else {
                            try {
                                webSocketService.send(completionMessage);
                            } catch (RuntimeException exception) {
                                Log.e(TAG, "navigation completion report failed", exception);
                            }
                        }

                        Log.d("websocket=======", "flag =============" + completedTaskFlag);
                        flag="";
                    }
                    invalidateNavigationTask("upstream route completed");
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
        Log.w(TAG, "ignore route prepared without session token");
    }

    @Override
    public void onSessionRoutePrepared(int sessionGeneration, RouteNode... routeNodes) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(() -> onSessionRoutePrepared(sessionGeneration, routeNodes));
            return;
        }
        if (!navigationTaskActive
                || sessionGeneration != activeNavigationSessionGeneration) {
            Log.d(TAG, "ignore stale route prepared: session=" + sessionGeneration);
            DiagnosticLogRecorder.warn(
                    "NAV",
                    "忽略旧路线准备完成 session=" + sessionGeneration
                            + ", activeSession=" + activeNavigationSessionGeneration
            );
            return;
        }
        clearNavigationPrepareTimeout();
        Log.d("navigatenext","readyGo=====");
        DiagnosticLogRecorder.info(
                "NAV",
                "路线准备完成并发送 readyGo(true) task=" + activeNavigationTaskGeneration
                        + ", session=" + sessionGeneration
                        + ", preparedNodes=" + (routeNodes == null ? 0 : routeNodes.length)
        );
        NavManager.getInstance().readyGo(true);
    }

    @Override
    public void onDistanceChanged(float v) {
        Log.d("navigatenext","onDistanceChanged="+v);
    }

    @Override
    public void onError(int i) {
        Log.w(TAG, "ignore navigation error without session token: code=" + i);
    }

    @Override
    public void onSessionError(int sessionGeneration, int code) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(() -> onSessionError(sessionGeneration, code));
            return;
        }
        if (sessionGeneration != activeNavigationSessionGeneration) {
            Log.d(TAG, "ignore stale navigation error: session=" + sessionGeneration);
            DiagnosticLogRecorder.warn(
                    "NAV",
                    "忽略旧导航错误 code=" + code + ", session=" + sessionGeneration
            );
            return;
        }
        DiagnosticLogRecorder.error(
                "NAV",
                "导航错误 code=" + code
                        + ", task=" + activeNavigationTaskGeneration
                        + ", session=" + sessionGeneration
        );
        clearNavigationPrepareTimeout();
        if (screenCompartmentRouteActive) {
            cancelScreenCompartmentRoute("导航错误：" + code);
        } else {
            invalidateNavigationTask("navigation error: " + code);
        }
        mBinding.tvNavigate.setEnabled(true);
        tip("导航启动失败，错误码：" + code);
        Log.d("navigatenext","onerror="+code);
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
            DiagnosticLogRecorder.info(
                    "CHARGER",
                    "充电信息 event=" + event
                            + ", power=" + chargerInfo.getPower()
                            + ", chargeEvent=" + chargerInfo.getEvent()
            );
        }

        @Override
        public void onChargerStatusChanged(int status) {
            if (status==4&&webSocketService!=null){
                isCharging=true;
                webSocketService.send("开始充电");
            }else if(status==1||status==6){
                isCharging=false;
                upstreamChargeTaskActive = false;
            }
            Log.d("Charger===", "status = " + status);
            DiagnosticLogRecorder.info(
                    "CHARGER",
                    "充电状态 status=" + status
                            + ", isCharging=" + isCharging
                            + ", upstreamChargeActive=" + upstreamChargeTaskActive
            );
        }

        @Override
        public void onError(int errorCode) {
            upstreamChargeTaskActive = false;
            Log.d("Charger===", "errorCode = " + errorCode);
            DiagnosticLogRecorder.error(
                    "CHARGER",
                    "充电模块错误 code=" + errorCode
            );
        }
    };
}
