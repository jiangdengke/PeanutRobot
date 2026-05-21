package com.yuandaima.peanutrobot.bean;

import java.util.List;

public class ChargeModel {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int charge;
        private String id;

        // getters and setters
        public int getCharge() { return charge; }
        public void setCharge(int charge) { this.charge = charge; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}