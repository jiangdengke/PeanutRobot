package com.yuandaima.peanutrobot.bean;

import java.util.List;

public class InfoModel {

    private int workMode;
    private int power;
    private int motorStatus;
    private String destList;
    private String charging;
    private String working;

    public InfoModel(int workMode, int power, int motorStatus, String destList) {
        this.workMode = workMode;
        this.power = power;
        this.motorStatus = motorStatus;
        this.destList = destList;
    }

    public InfoModel(int workMode, int power, int motorStatus, String destList, String charging, String working) {
        this.workMode = workMode;
        this.power = power;
        this.motorStatus = motorStatus;
        this.destList = destList;
        this.charging = charging;
        this.working = working;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMotorStatus() {
        return motorStatus;
    }

    public void setMotorStatus(int motorStatus) {
        this.motorStatus = motorStatus;
    }

    public String getDestList() {
        return destList;
    }

    public void setDestList(String destList) {
        this.destList = destList;
    }
}