//package com.yuandaima.peanutrobot;
//
//import static com.keenon.sdk.external.PeanutSDK.SDK_INIT_SUCCESS;
//
//import android.Manifest;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.pm.PackageManager;
//import android.hardware.display.DisplayManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.storage.StorageManager;
//import android.os.storage.StorageVolume;
//import android.provider.Settings;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.keenon.common.constant.PeanutConstants;
//import com.keenon.common.external.PeanutConfig;
//import com.keenon.sdk.component.charger.PeanutCharger;
//import com.keenon.sdk.component.charger.common.Charger;
//import com.keenon.sdk.component.charger.common.ChargerInfo;
//import com.keenon.sdk.component.navigation.PeanutNavigation;
//import com.keenon.sdk.component.navigation.common.Navigation;
//import com.keenon.sdk.component.navigation.route.RouteNode;
//import com.keenon.sdk.component.runtime.PeanutRuntime;
//import com.keenon.sdk.constant.TopicName;
//import com.keenon.sdk.external.IDataCallback;
//import com.keenon.sdk.external.PeanutSDK;
//import com.keenon.sdk.hedera.model.ApiError;
//import com.yuandaima.peanutrobot.adapter.MediaAdapter;
//import com.yuandaima.peanutrobot.adapter.PointAdapter;
//import com.yuandaima.peanutrobot.bean.BannerModel;
//import com.yuandaima.peanutrobot.bean.ChargeModel;
//import com.yuandaima.peanutrobot.bean.DestModel;
//import com.yuandaima.peanutrobot.bean.InfoModel;
//import com.yuandaima.peanutrobot.bean.MediaModel;
//import com.yuandaima.peanutrobot.bean.MyPoint;
//import com.yuandaima.peanutrobot.bean.ScreenModel;
//import com.yuandaima.peanutrobot.databinding.ActivityMainBinding;
//import com.yuandaima.peanutrobot.fragment.MyMultiCameraFragment;
//import com.yuandaima.peanutrobot.interFace.OnItemClickListener;
//import com.yuandaima.peanutrobot.manager.NavManager;
//import com.yuandaima.peanutrobot.manager.USBCameraManager;
//import com.yuandaima.peanutrobot.manager.WebSocketManager;
//import com.yuandaima.peanutrobot.presentation.PresentationCoucou;
//import com.yuandaima.peanutrobot.server.WebServer;
//import com.yuandaima.peanutrobot.server.WebSocketService;
//import com.yuandaima.peanutrobot.util.GPIOUtil;
//import com.yuandaima.peanutrobot.util.ScrollAndSelectHelper;
//import com.yuandaima.peanutrobot.util.TtsUntil;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import fi.iki.elonen.NanoHTTPD;
//import okio.ByteString;
//
//
//public class MainActivity2 extends AppCompatActivity implements  View.OnClickListener, Navigation.Listener{
//    private String TAG="MainActivity===";
//    private static final int ARRIVE_STAY_DURATION = 3000;
//    private ActivityMainBinding mBinding;
//    private PointAdapter mAdapter,verticalAdapter;
//    private MediaAdapter mediaAdapter;
//    private List<RouteNode> pointList=new ArrayList<>();
//    private List<MyPoint> list = new ArrayList<>();
//    private List<MediaModel> mediaModelList = new ArrayList<>();
//    private TtsUntil ttsUntil;
//    private boolean isPermissionRequested;
//    private ScrollAndSelectHelper scrollHelper;
//
//
//
//    private WebSocketService webSocketService;
//    Handler handler = new Handler(Looper.getMainLooper());
//    private DestModel destModel=new DestModel();
//    private List<DestModel.DataBean> testData;
//    private List<RouteNode> routeNodes;
//    private PeanutNavigation peanutNavigation;
//    private DisplayManager displayManager;
//    private USBCameraManager cameraManager ;
//    private WebSocketManager webSocketManager;
//    private String flag;
//    private Runnable uploadRunnable = new Runnable() {
//        @Override
//        public void run() {
//            uploadInfo();
//            handler.postDelayed(this, 5000);
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.R)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.addFlags(-2147483648);
//        window.setStatusBarColor(getResources().getColor(R.color.white));
//        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
//        window.getDecorView().setSystemUiVisibility(8192);
//        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());
//        initSubscribe();
//        requestPermission();
//        initSDK(PeanutConstants.REMOTE_LINK_PROXY);
//        initTts();
//        initUSBCameraManager();
//        initWebSocket();
//        initMediaPlayer();
////        if (checkPermissions()) {
////            initFragmentLayout();
////        } else {
////            requestPermissions();
////        }
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        handler.postDelayed(uploadRunnable, 10000);
//    }
//    boolean mFlag=false;
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        handler.removeCallbacks(uploadRunnable);
//    }
//
//    private void initUSBCameraManager() {
//      //  cameraManager  = USBCameraManager.getInstance();
//    }
//    MediaPlayer mediaPlayer;
//    private  void initMediaPlayer(){
//        mediaPlayer = new MediaPlayer();
//      //  mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//    }
//    private void initFragmentLayout() {
//        Log.d(TAG,"initFragmentLayout");
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, new MyMultiCameraFragment())
//                .commit();
//    }
//    private void initWebSocketManager() {
//        webSocketManager=new WebSocketManager("ws://192.168.78.19:9096");
//        webSocketManager.connect();
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.R)
//    public static String getUsbPathPrefix(Context context) {
//        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//
//        if (storageManager != null) {
//            for (StorageVolume volume : storageManager.getStorageVolumes()) {
//                if (volume.isRemovable() && volume.getState().equals(Environment.MEDIA_MOUNTED)) {
//                    if (volume.getDirectory() != null) {
//                        return volume.getDirectory().getAbsolutePath();  // 如：/storage/XXXX-XXXX
//                    }
//
//                    try {
//                        Method getPathMethod = StorageVolume.class.getMethod("getPath");
//                        String path = (String) getPathMethod.invoke(volume);
//                        return path;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return null;
//    }
//    public List<StorageVolume> getStorageVolumesCompat() {
//        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//        List<StorageVolume> volumes = new ArrayList<>();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            // 新版本直接调用
//            return storageManager.getStorageVolumes();
//        } else {
//            // 低版本使用反射
//            try {
//                Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
//                Object[] volumeList = (Object[]) getVolumeList.invoke(storageManager);
//
//                // 将 Object[] 转换为 List<StorageVolume>
//                for (Object obj : volumeList) {
//                    if (obj instanceof StorageVolume) {
//                        volumes.add((StorageVolume) obj);
//                    }
//                }
//            } catch (Exception e) {
//                Log.d("usbPath","e="+e.getMessage());
//
//                e.printStackTrace();
//                // 反射失败时的备用方案
//            }
//        }
//        return volumes;
//    }
//
//
//    private void initSubscribe() {
//        PeanutSDK.getInstance().subscribe(TopicName.BUTTON_STATUS,buttonStatusCallback);
//        PeanutSDK.getInstance().subscribe(TopicName.CHARGE_MATCH_TIMES,chargeCallback );
//
//    }
//
//
//
//    private List<String> requiredPermissions = Arrays.asList(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    );
//    private static final int REQUEST_PERMISSIONS = 1001;
//    private boolean checkPermissions() {
//        for (String permission : requiredPermissions) {
//            if (ContextCompat.checkSelfPermission(this, permission)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Log.d("MultiCamera","permission==="+permission);
//                return false;
//            }
//        }
//        return true;
//    }
//    private void requestPermissions() {
//        Log.d("MultiCamera","requestPermissions");
//        String[] permissionsArray = requiredPermissions.toArray(new String[0]);
//        ActivityCompat.requestPermissions(this, permissionsArray, REQUEST_PERMISSIONS);
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d("MultiCamera","onRequestPermissionsResult");
//        if (requestCode == REQUEST_PERMISSIONS) {
//            Log.d("MultiCamera","REQUEST_PERMISSIONS");
//            boolean allGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    break;
//                }
//            }
//            if (allGranted) {
//                Log.d("MultiCamera","allGranted");
//                initFragmentLayout();
//            } else {
//                Log.d("MultiCamera","showPermissionDeniedDialog");
//                showPermissionDeniedDialog();
//            }
//        }
//    }
//
//    private void showPermissionDeniedDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("权限被拒绝")
//                .setMessage("需要摄像头、录音和存储权限才能使用本应用")
//                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.setData(Uri.parse("package:" + getPackageName()));
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }
//
//
//
//    public static enum MediaType {
//        IMAGE,
//        VIDEO
//    }
//    private WebServer server;
//    private List<RouteNode> response;
//
//    private void initWebSocket() {
//        new Thread(new Runnable() {
//
//
//            @Override
//            public void run() {
//                server = new WebServer(9095, MainActivity2.this);
//                try {
//                    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
//                    server.setWebCallback(new WebServer.WebCallback() {
//                        @Override
//                        public void onMessage(String text,String url) {
//                            Log.d("MyServer","url=="+url+",text=="+text);
//                            switch (url){
//                                case "/robot_task/go_to_charge":
//                                    flag="go_to_charge";
//                                    ChargeModel chargeModel = new Gson().fromJson(text, new TypeToken<ChargeModel>(){}.getType());
//                                    Log.d("MyServer","ChargeModel=="+new Gson().toJson(chargeModel));
//                                    NavManager.getInstance().stop();
//                                 //   NavManager.getInstance().release();
//                                    initCharger(chargeModel);
//                                break;
//                                case "/robot_task/send_point":
//                                    flag="send_point";
//                                    response = new Gson().fromJson(text, new TypeToken<List<RouteNode>>(){}.getType());
//                                    Log.d("navigatenext","send_poin=="+new Gson().toJson(response));
//                                    prepareNav(response);
//                                    //  peanutNavigation.skipTo(1);
//                                case "/robot_task/send_stop":
//                                    Log.d("MyServer","send_stop==");
//                                    NavManager.getInstance().stop();
//                                break;
//                                case "/robot_task/screen_control":
//                                    ScreenModel screenModel = new Gson().fromJson(text, new TypeToken<ScreenModel>(){}.getType());
//                                    Log.d("presentation","screenModel="+new Gson().toJson(screenModel));
//                                    startHardwareTests(screenModel,null, MediaType.VIDEO);
//                                    break;
//                                case "/robot_task/screen_control_img_display":
//                                    BannerModel bannerModelList = new Gson().fromJson(text, new TypeToken<BannerModel>(){}.getType());
//                                    Log.d("presentation","  bannerModelList="+new Gson().toJson( bannerModelList));
//                                    startHardwareTests(null,bannerModelList, MediaType.IMAGE);
//                                    break;
//
//                            }
//
//                        }
//                    });
//                } catch (IOException e) {
//                    Log.e("WebServer", "启动失败", e);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 显示错误信息
//                            // statusTextView.setText("服务器启动失败");
//                        }
//                    });
//                }
//            }
//        }).start();
//
//
//
//        bindService(new Intent(this, WebSocketService.class), serviceConnection, BIND_AUTO_CREATE);
//    }
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            webSocketService = ((WebSocketService.LocalBinder) service).getService();
//            webSocketService.setWebSocketCallback(webSocketCallback);
//          //  uploadInfo();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            webSocketService = null;
//        }
//    };
//
//    private WebSocketService.WebSocketCallback webSocketCallback = new WebSocketService.WebSocketCallback() {
//        @Override
//        public void onMessage(final String responseData) {
//
//        }
//
//        @Override
//        public void onMessage(ByteString bytes) {
//            Log.d("websocket=======","onMessage=="+bytes.toString());
//          //  startHardwareTests(null,null,null);
//        }
//
//        @Override
//        public void onOpen() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG,"onOpen");
//                }
//            });
//        }
//
//        @Override
//        public void onClosed() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG,"onClosed");
//                }
//            });
//        }
//    };
//    private void initNavManager() {
//        Log.d("routeNodes","initNavManager");
//        NavManager.getInstance().init(MainActivity2.this, 3000, 2, true);
//        // routeNodes = NavManager.getInstance().getRouteNodes();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String destList = PeanutRuntime.getInstance().getRuntimeInfo().getDestList();
//                destModel = new Gson().fromJson(destList, DestModel.class);
//                mInitView();
//                initListener();
//
//                Log.d(TAG, "destList: " + destList);
//            }
//        }, 1000);
////
//    }
//
//    private void initTts() {
//        ttsUntil = TtsUntil.getInstance();
//        ttsUntil.initTts(MainActivity2.this, () -> {
//
//        });
//        getLifecycle().addObserver(ttsUntil);
//    }
//
//    private void initListener() {
//        mBinding.tvNavigate.setOnClickListener(this);
//        mBinding.tvSecondaryScreenDisplay.setOnClickListener(this);
//
//        PeanutRuntime.getInstance().registerListener(mRuntimeListener);
//        mAdapter.setOnClickItemListener(new OnItemClickListener() {
//            @Override
//            public void onClick(int position,boolean isSelect) {
//                // 1. 平滑滚动到指定位置
//                //        mBinding.rvVerticalPoint.smoothScrollToPosition(position);
//                scrollHelper.smoothScrollToPositionAndSelect(position, isSelect,()->{
//                    View viewByPosition = verticalAdapter.getViewByPosition(position, R.id.tv_point);
//                    viewByPosition.setSelected(isSelect);
//                    verticalAdapter.notifyDataSetChanged();
//                });
//
//            }
//        });
//
//
//        verticalAdapter.setOnClickItemListener(new OnItemClickListener() {
//            @Override
//            public void onClick(int position,boolean isSelect) {
//                View viewByPosition = mAdapter.getViewByPosition(position, R.id.tv_point);
//                viewByPosition.setSelected(isSelect);
//                verticalAdapter.notifyDataSetChanged();
//            }
//        });
//
//
//
//
//
//    }
//
//
//    private void mInitView() {
//        scrollHelper = new ScrollAndSelectHelper(
//                mBinding.rvVerticalPoint,
//                verticalAdapter
//        );
//
//        // List<RouteNode> nodeList = Arrays.asList(routeNodes);
//        //pointList
//         mAdapter= new PointAdapter(destModel.getData());
//        // mediaAdapter=new MediaAdapter(mediaModelList,MainActivity.this);
//      //  mAdapter= new PointAdapter(testData);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,5);
//        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(this);
//
//        mBinding.rvPoint.setLayoutManager(gridLayoutManager);
//        mBinding.rvPoint.setAdapter(mAdapter);
//
//
//
//        verticalAdapter= new PointAdapter(destModel.getData());
//    //    verticalAdapter= new PointAdapter(testData);
//        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mBinding.rvVerticalPoint.setLayoutManager(linearLayoutManager);
//        mBinding.rvVerticalPoint.setAdapter(verticalAdapter);
//
//
//        mAdapter.setTtsUtil(ttsUntil);
//    }
//
//
//    private void initSDK(String ip) {
//        PeanutConfig.getConfig()
//                .setLinkType(PeanutConstants.REMOTE_LINK_PROXY.equals(ip) ? PeanutConstants.LinkType.COAP : PeanutConstants.LinkType.COM_COAP)
//                .setLinkIP(ip)
//                .enableLog(true)
//                .setLogLevel(Log.DEBUG)
//                .setAppId("353110c9f77947da8fd26a908a80d866")
//                .setSecret("nPlQERTP4qKBBpfoMxMFtgNJG1eC1nU6U7nA2g0eUNVS/iKjcTxL3ZtGLebwINKLAx/kACtCq7UBvt1QCODovm2gq7dsXAK4pgjBRK2OqQF0SMvBNMxjqVFh73zzI8tCP12D+eCfG2WMCUu4EVyaBYg6sD7FzGWL")
//                .enableUMLog(false);
//        PeanutSDK.getInstance().init(this.getApplicationContext(), mErrorListener);
//
//
//
//    }
//
//    /**
//     * 初始化充电模组
//     */
//    private void initCharger(ChargeModel chargeModel) {
//
//        //初始化
//        mPeanutCharger = new PeanutCharger.Builder()
//                .setListener(listener)
//                .setPile(Integer.parseInt(chargeModel.getData().get(0).getId()))
//                .build();
//
//        mPeanutCharger.execute();
//
//
// //       mPeanutCharger.performAction(PeanutCharger.CHARGE_ACTION_AUTO);
//    }
//
//
//
//    /**
//     * Android6.0之后需要动态申请权限
//     */
//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
//            isPermissionRequested = true;
//            ArrayList<String> permissionsList = new ArrayList<>();
//            String[] permissions = {
//                    Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.INTERNET,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_WIFI_STATE,
//                    Manifest.permission.READ_PHONE_STATE,
//            };
//
//            for (String perm : permissions) {
//                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
//                    permissionsList.add(perm);
//                    // 进入到这里代表没有权限.
//                }
//            }
//
//            if (!permissionsList.isEmpty()) {
//                String[] strings = new String[permissionsList.size()];
//                requestPermissions(permissionsList.toArray(strings), 0);
//            }
//        }
//    }
//    @Override
//    protected void onDestroy() {
//        PeanutSDK.getInstance().release();
//        NavManager.getInstance().stop();
//        NavManager.getInstance().release();
//        PeanutRuntime.getInstance().removeListener(mRuntimeListener);
//        if (mPeanutCharger != null) {
//            mPeanutCharger.release();
//        }
//        super.onDestroy();
//    }
//
//    private void addPoint(TextView editText, boolean manualNext) {
//        if (!TextUtils.isEmpty(editText.getText().toString())) {
//            MyPoint point = new MyPoint();
//            point.setManualControl(manualNext);
//            point.setDuration(ARRIVE_STAY_DURATION);
//            RouteNode node = new RouteNode();
//            node.setId(Integer.parseInt(editText.getText().toString()));
//            node.setName("Point:" + editText.getText().toString());
//            point.setRouteNode(node);
//            list.add(point);
//        }
//    }
//    private PeanutSDK.ErrorListener mErrorListener = errorCode -> {
//        Log.d(TAG, "onInit:" + errorCode);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (errorCode == SDK_INIT_SUCCESS) {
//                    Log.d("routeNodes","SDK_INIT_SUCCESS:"+errorCode);
//                    initNavManager();
//
//                    tip("SDK_INIT_SUCCESS");
//
//                    PeanutRuntime.getInstance().start(new PeanutRuntime.Listener() {
//                        @Override
//                        public void onEvent(int event, Object obj) {
//
//                        }
//
//                        @Override
//                        public void onHealth(Object content) {
//
//                        }
//
//                        @Override
//                        public void onHeartbeat(Object content) {
//
//                        }
//                    });
//                } else {
//                    Log.d("routeNodes","SDK_INIT_FAIL:"+errorCode);
//                    tip("SDK_INIT_FAIL:"+errorCode);
//                }
//            }
//        });
//    };
//    private PeanutRuntime.Listener mRuntimeListener = new PeanutRuntime.Listener() {
//        @Override
//        public void onEvent(int event, Object obj) {
//            Log.d(TAG, "onEvent:" + event + ", content: " + obj);
//
//
//        }
//
//        @Override
//        public void onHealth(Object content) {
//            Log.d(TAG, "onHealth:" + content);
//
//        }
//
//        @Override
//        public void onHeartbeat(Object content) {
//            Log.d(TAG, "onHeartbeat:" + content);
//
//        }
//    };
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//
//        if (id==mBinding.tvNavigate.getId()){
//            flag="";
//            mBinding.tvNavigate.setEnabled(false);
//            List<DestModel.DataBean> routeNodeList = mAdapter.getData();
//
////            List<RouteNode> routeNodes = IntStream.range(0, routeNodeList.size())
////                    .filter(i -> {
////                        View view = mAdapter.getViewByPosition(i, R.id.tv_point);
////                        return view != null && view.isSelected();
////                    })
////                    .mapToObj(i -> {
////                        DestModel.DataBean dataBean = routeNodeList.get(i);
////                        RouteNode routeNode = new RouteNode();
////                        routeNode.setId(dataBean.getId());
////                        routeNode.setName(dataBean.getName());
////                        // routeNode.setOtherProperty(dataBean.getOtherProperty());
////                        return routeNode;
////                    })
////                    .collect(Collectors.toList());
//
//
//            routeNodes = new ArrayList<>();
//
//            for (DestModel.DataBean dataBean : routeNodeList) {
//                int position = routeNodeList.indexOf(dataBean);
//                View view = mAdapter.getViewByPosition(position, R.id.tv_point);
//
//                if (view != null && view.isSelected()) {
//                    RouteNode routeNode = new RouteNode();
//                    routeNode.setId(dataBean.getId());
//                    routeNode.setName(dataBean.getName());
//                    routeNodes.add(routeNode);
//                }
//            }
//
//            if (routeNodes.isEmpty()){
//                tip("请选择点位");
//                return;
//            }
//            Log.d("routeNodes===","routeNodes===="+new Gson().toJson(routeNodes));
////            RouteNode node = new RouteNode();
////            node.setId(Integer.parseInt(editText.getText().toString()));
////            node.setName("Point:" + editText.getText().toString());
////            point.setRouteNode(node);
//
//
//
//
//            prepareNav(routeNodes);
//
//        }else if (id==mBinding.tvSecondaryScreenDisplay.getId()){
//         //   startHardwareTests(null);
//
//        }
//    }
//
//    /**
//     * 准备导航
//     * @param routeNodes
//     */
//    private void prepareNav(List<RouteNode> routeNodes) {
//        NavManager.getInstance().stop();
////        NavManager.getInstance().release();
//        peanutNavigation = NavManager.getInstance().getmPeanutNavigation();
//
//        peanutNavigation.setTargets(routeNodes);
//        NavManager.getInstance().setSpeed(100);
//        NavManager.getInstance().prepare();
//
//    }
//
//    public  List<DestModel.DataBean> getRouteNodesList(){
//
//        return  mAdapter.getData();
//    }
//
//    @Override
//    public void onStateChanged(int state, int schedule) {
//        switch (state) {
//            case Navigation.STATE_DESTINATION:
//                arrived();
//                break;
//            case Navigation.STATE_COLLISION:
//            case Navigation.STATE_BLOCKED:
//                Toast.makeText(this, R.string.avoid_tip, Toast.LENGTH_SHORT).show();
//
//                break;
//            case Navigation.STATE_BLOCKING:
//                ttsUntil.speech("遇到阻碍",false);
//
//                Toast.makeText(this, R.string.block_timeout_tip, Toast.LENGTH_SHORT).show();
//
//                break;
//        }
//    }
//
//    private void arrived() {
//        NavManager.getInstance().readyGo(false);
////        Log.d("navigatenext","getQueueName="+(response.get(0).getQueueName()));
//        Log.d("navigatenext","getCurrentPosition="+peanutNavigation.getCurrentPosition());
//
//        mediaPlayer = new MediaPlayer();
//
//        try {
//        //    mediaPlayer.setDataSource("http://10.171.255.19:8000/AccessStayPrompt.mp3");
//            mediaPlayer.setDataSource(response.get(0).getQueueName());
//            mediaPlayer.prepareAsync();
//
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    Log.d("navigatenext","onPrepared");
//                    mp.start();
//                }
//            });
//            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    Log.d("navigatenext","onError");
//                    return true;
//                }
//            });
//        } catch (IOException e) {
//            Log.d("navigatenext","IOException="+e.getMessage());
//            throw new RuntimeException(e);
//        }
//     //   ttsUntil.speech(peanutNavigation.getCurrentNode().getQueueName(),false);
//        navigatenext();
//
//      //  Toast.makeText(this,"已经到达目的地", Toast.LENGTH_SHORT).show();
//    }
//    private void navigatenext() {
//        Log.d("navigatenext","lastNode="+NavManager.getInstance().isLastNode());
//        Log.d("navigatenext","NextNode="+new Gson().toJson(NavManager.getInstance().getNextNode()));
//        Log.d("navigatenext","Targets="+new Gson().toJson(NavManager.getInstance().getTargets()));
//
//        if ( NavManager.getInstance().isLastNode()) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (!TextUtils.isEmpty(flag)){
//                        webSocketService.send(flag+"已完成");
//                        Log.d("websocket=======", "flag =============" + flag);
//                        flag="";
//                    }
//                 //   ttsUntil.speech("已经没有下一个目的地了",false);
//                    mBinding.tvNavigate.setEnabled(true);
//                }
//            });
//        } else {
//            NavManager.getInstance().nextDes();
//            NavManager.getInstance().readyGo(true);
//        }
//    }
//    @Override
//    public void onRouteNode(int i, RouteNode routeNode) {
//
//    }
//    PresentationCoucou pres;
//    private void startHardwareTests(ScreenModel screenModel, BannerModel bannerModelList,MediaType mediaType) {
//        Log.d("presentation","=== DÉMARRAGE DES TESTS ===");
//        GPIOUtil.IOCtrl(GPIOUtil.GPIO, GPIOUtil.OPEN_SCEEN);//打开
////        GPIOUtil.IOCtrl(GPIOUtil.GPIO, GPIOUtil.CLOSE_SCEEN);//关闭
//        try {
//            // Test 1 : Liste des écrans disponibles
//            Log.d("presentation","\n[Test 1] Recherche des écrans disponibles...");
//            Display[] displays = displayManager.getDisplays();
//            Log.d("presentation","Nombre d’écrans détectés : " + displays.length);
//            for (int i = 0; i < displays.length; i++) {
//                Log.d("presentation"," - Display[" + i + "] : " + displays[i].getName());
//            }
//
//            // Si plusieurs écrans, afficher “Coucou” sur le secondaire
//            if (displays.length > 1) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                       if (pres != null) pres.cancel();
//                        pres = new PresentationCoucou(MainActivity2.this, displays[1],screenModel,  bannerModelList, mediaType);
//                        pres.show();
//                    }
//                });
//                Log.d("presentation","\n[Test 2] Tentative d’affichage sur le second écran...");
//
//                Log.d("presentation","✅ Présentation affichée sur : " + displays[1].getName());
//            } else {
//                Log.d("presentation","⚠️ Aucun écran secondaire détecté.");
//            }
//
//        } catch (Exception e) {
//            Log.d("presentation","❌ Erreur : " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        Log.d("presentation","\n=== FIN DES TESTS ===");
//    }
//    /**
//     * 回传数据
//     */
//    private void uploadInfo(){
//        //工作模式
//        int workMode = PeanutRuntime.getInstance().getRuntimeInfo().getWorkMode();
//        //电量
//        int power = PeanutRuntime.getInstance().getRuntimeInfo().getPower();
//        //电机状态
//        int motorStatus = PeanutRuntime.getInstance().getRuntimeInfo().getMotorStatus();
//
//        String destList = PeanutRuntime.getInstance().getRuntimeInfo().getDestList();
//
//        InfoModel infoModel=new InfoModel(workMode,power,motorStatus,destList);
//        Log.d("websocket=======","workMode=="+workMode+",power==="+power+"motorStatus==="+motorStatus+",destList==="+destList+",routeNodes==="+new Gson().toJson(routeNodes));
//        if (webSocketService != null) {
//           Log.d("websocket=======","uploadInfo=="+new Gson().toJson(infoModel));
//           webSocketService.send(new Gson().toJson(infoModel));
//        }
//
//    }
//
//    PeanutCharger mPeanutCharger;
//
//
//
//
//    public void tip(final String str) {
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast toast = Toast.makeText(MainActivity2.this, str, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0); //居中显示
//                LinearLayout linearLayout = (LinearLayout) toast.getView();
//                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
//                messageTextView.setTextSize(50);//设置toast字体大小
//                messageTextView.setGravity(Gravity.CENTER);
//                messageTextView.setWidth(1200);
//                toast.setGravity(Gravity.CENTER,0,0);
//                toast.show();
//            }
//        });
//    }
//
//    IDataCallback buttonStatusCallback = new IDataCallback() {
//        @Override
//        public void success(String result) {
//           // NavManager.getInstance().readyGo(false);
//            Log.d(TAG,"buttonStatusCallback success=="+result);
//        }
//
//        @Override
//        public void error(ApiError error) {
//            Log.d(TAG,"buttonStatusCallback error=="+error.toString());
//        }
//    };
//    IDataCallback chargeCallback= new IDataCallback() {
//        @Override
//        public void success(String response) {
//            Log.d("Charger===", "success = " + response);
//        }
//
//        @Override
//        public void error(ApiError error) {
//            Log.d("Charger===", "error = " + error);
//        }
//    };
//    @Override
//    public void onRoutePrepared(RouteNode... routeNodes) {
//        Log.d(TAG,"readyGo=====");
//        NavManager.getInstance().readyGo(true);
//    }
//
//    @Override
//    public void onDistanceChanged(float v) {
//
//    }
//
//    @Override
//    public void onError(int i) {
//        flag="";
//    }
//
//    @Override
//    public void onEvent(int i) {
//
//    }
//
//
//    //充电回调
//    Charger.Listener listener=new Charger.Listener() {
//        @Override
//        public void onChargerInfoChanged(int event, ChargerInfo chargerInfo) {
//            Log.d("Charger===", "event = " + event +
//                    " Power = " + chargerInfo.getPower() + " ChargeEvent = " + chargerInfo.getEvent());
//        }
//
//        @Override
//        public void onChargerStatusChanged(int status) {
//            Log.d("Charger===", "status = " + status);
//        }
//
//        @Override
//        public void onError(int errorCode) {
//            Log.d("Charger===", "errorCode = " + errorCode);
//        }
//    };
//}