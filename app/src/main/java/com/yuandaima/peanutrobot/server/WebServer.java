package com.yuandaima.peanutrobot.server;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.yuandaima.peanutrobot.MainActivity;
import com.yuandaima.peanutrobot.bean.testModel;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
    private static final String TAG = "MyServer";
    private static final String API_PREFIX = "robot_task/send_point";

    private final File targetDir;
    private MainActivity activity;
    public WebServer(int port,MainActivity activity) {
        super(port);
        this.activity=activity;
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.targetDir = new File(basePath + "/peanut/media/ad/");
        // 如果文件夹不存在，则创建它
        if (!targetDir.exists()) {
            if (targetDir.mkdirs()) {
                Log.i(TAG, "目标目录创建成功: " + targetDir.getAbsolutePath());
            } else {
                Log.e(TAG, "目标目录创建失败，请检查权限或路径");
            }
        }
    }



    @Override
    public Response serve(IHTTPSession session) {
        Response response;
        String uri = session.getUri();

        if (Method.POST.equals(session.getMethod())) {
            // 处理 POST 请求
            Log.d("MyServer","处理 POST 请求"+uri);
            response = handlePostRequest(session,uri);

        }  else {
            Log.d("MyServer","getData");
            response = newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED,
                    "application/json",
                    "{\"error\":\"Method not allowed\"}");
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.addHeader("Content-Type", "application/json; charset=utf-8");
        InputStream data = response.getData();
        String s = new Gson().toJson(data);
        return response;
    }

    private Response handlePostRequest(IHTTPSession session,String uri) {
        try {
            Log.d("MyServer","uri=="+uri);
            if (uri.equals("/robot_task/get_point_list")||uri.equals("/robot_task/send_video")) {
                return handleApiRequest(session, uri);
            }


            // 1. 解析请求体
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);

            // 2. 获取 POST 数据
            String postData = files.get("postData");

            // 如果 postData 为空，尝试从 session 获取
            if ((postData == null || postData.isEmpty()) && session.getHeaders().containsKey("content-type")) {
                Map<String, String> params = new HashMap<>();
                session.parseBody(params);
                postData = params.get("postData");
            }

            Log.d("MyServer", "接收到的原始数据: " + postData);




            if (webCallback!=null){
                webCallback.onMessage(postData,uri);
            }

         //   JSONObject json = new JSONObject(postData);

            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "success");
            responseJson.put("code", 200);
            responseJson.put("message", "数据接收成功");
          //  responseJson.put("data",new Gson().toJson(json));

            return newFixedLengthResponse(Response.Status.OK,
                    "application/json",
                    responseJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MyServer", "处理 POST 请求出错: " + e.getMessage());

            JSONObject errorJson = new JSONObject();
            try {
                errorJson.put("status", "error");
                errorJson.put("code", 500);
                errorJson.put("message", e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                    "application/json",
                    errorJson.toString());
        }
    }


    private Response handleApiRequest(IHTTPSession session, String path) throws Exception {
        Method method = session.getMethod();
        Log.d("ApiLog","path=="+path);
        // API 路由表
        switch (path) {
            case "/robot_task/get_point_list":
                if (method == Method.POST) return   createJsonResponse(200, "成功", new Gson().toJson(activity.getRouteNodesList()));;
                break;
            case "/robot_task/send_video":
                if (method == Method.POST)return  handleVideoUpload(session);
                break;

        }

        return createJsonResponse(405, "方法不支持或路径不存在", null);
    }
    @Nullable
    private Response handleVideoUpload(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);

            for (String fieldName : files.keySet()) {
                String tempFilePath = files.get(fieldName);
                File tempFile = new File(tempFilePath);

                if (tempFile.exists()) {

                    String finalFileName = "video_" + System.currentTimeMillis() + ".mp4";

                    File finalFile = new File(targetDir, finalFileName);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.copy(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }else {
  //                      FileUtils.copyFile(tempFile, finalFile);
//                                copyFileWithIo(tempFile, finalFile);
                    }

                    Log.i(TAG, "文件保存成功: " + finalFile.getAbsolutePath());

                    // 删除临时文件
                    // tempFile.delete();

                    return newFixedLengthResponse(Response.Status.OK, "text/plain",
                            "文件上传成功，已保存至: " + finalFile.getAbsolutePath());
                }
            }
        } catch (IOException | ResponseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private Response createJsonResponse(int code, String message, Object data) {
        try {
            JSONObject json = new JSONObject();
            json.put("code", code);
            json.put("message", message);
            if (data != null) {
                json.put("data", data);
            }

            Response response = newFixedLengthResponse(
                    Response.Status.OK,
                    "application/json",
                    json.toString()
            );

            // CORS
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        } catch (Exception e) {
            return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR,
                    "application/json",
                    "{\"error\":\"内部错误\"}"
            );
        }
    }

    private String processRequest(String page, String rows, String pointType) {
        // 这里实现您的业务逻辑
        int pageNum = Integer.parseInt(page);
        int rowsNum = Integer.parseInt(rows);
        int type = Integer.parseInt(pointType);

        // 示例处理：返回查询结果信息
        return String.format("查询第 %d 页，每页 %d 条，类型为 %d 的数据",
                pageNum, rowsNum, type);
    }


    private Response handleInspectionAlarm(IHTTPSession session) {
        try {
            // 解析 POST 数据
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);

            // 获取 POST 数据
            String postData = files.get("postData");
            if (postData == null || postData.isEmpty()) {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST,
                        "application/json",
                        "{\"error\": \"请求体为空\"}");
            }

            // 解析 JSON
            JSONObject json = new JSONObject(postData);

            // 提取数据
            String robotId = json.optString("robot_id");
            String alarmType = json.optString("alarm_type");
            String level = json.optString("level");
            String timestamp = json.optString("timestamp");

            // 处理数据（这里只是示例）
            Log.d("RobotServer", "收到告警: " +
                    "机器人ID=" + robotId +
                    ", 类型=" + alarmType +
                    ", 级别=" + level);

            // 返回成功响应
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "success");
            responseJson.put("message", "告警接收成功");
            responseJson.put("received_time", System.currentTimeMillis());

            // 添加 CORS 支持（如果从网页访问）
            Response response = newFixedLengthResponse(
                    Response.Status.OK,
                    "application/json",
                    responseJson.toString()
            );
            response.addHeader("Access-Control-Allow-Origin", "*");

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR,
                    "application/json",
                    "{\"error\": \"" + e.getMessage() + "\"}"
            );
        }
    }
    private WebCallback webCallback;

    public interface WebCallback {
        void onMessage(String text,String url);

    }

    public void setWebCallback(WebCallback webCallback) {
        this.webCallback = webCallback;
    }

    @Override
    public void start() throws IOException {
        super.start();
        Log.i("WebServer", "服务器启动在端口: " + getListeningPort());
    }

    @Override
    public void stop() {
        super.stop();
        Log.i("WebServer", "服务器已停止");
    }

}