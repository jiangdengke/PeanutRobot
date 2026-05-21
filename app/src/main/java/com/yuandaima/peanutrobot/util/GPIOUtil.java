package com.yuandaima.peanutrobot.util;

import android.os.Build;

import java.io.FileOutputStream;
import java.io.IOException;

public  class GPIOUtil {
  private final static String TAG = "GPIOUtil";
  //控制副屏是否显示 t5pro 设备
  //  public static int GPIO224 = 224;
  //控制副屏是否显示 T10 设备

  //取餐按键灯
  public static int GPIO_TAKE_FOOD_BUTTON = 153;
  //这里传值，必须传ASCII码的值，所以得带单引号

  public static int GPIO = 235;
  public static int OPEN_SCEEN = '1';
  public static int CLOSE_SCEEN = '0';

  public static void IOCtrl(int pin, int level) {
    ctrlSecondScreenLight(GPIOUtil.OPEN_SCEEN == level ? 40000 : 0);
    String path;
    path = "/sys/class/gpio/gpio" + pin + "/value";
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(path);
      out.write(level);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  /**
   * 非T11机型：亮度调节范围22000-40000
   * T11机型：亮度调节范围10000-22000
   */
  public static void ctrlSecondScreenLight(int light) {
    Process process = null;
    try {
      String[] cmdLine = {"sh", "-c", "echo " + light + " > /sys/class/pwm/duty\n"};
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        cmdLine = new String[]{"sh", "-c", "echo " + light * 10 + " > /sys/class/pwm/pwmchip1/pwm0/duty_cycle\n"};
      }
      process = Runtime.getRuntime().exec(cmdLine);
      process.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        process.destroy();
      } catch (Exception e) {}
    }
  }

}
