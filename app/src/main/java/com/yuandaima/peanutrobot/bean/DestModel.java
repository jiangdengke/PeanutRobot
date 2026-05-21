package com.yuandaima.peanutrobot.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class DestModel {


    private int code;
    private String msg;
    private int status;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bind_map_md5 : c1337640e6559df3fb139af282a79ddf
         * buildingInfo : A
         * elevator_id : 0
         * floor : 1
         * floorInfo :
         * id : 1
         * name : 1_充电桩
         * phone : 0
         * phoneStr :
         * pose : {"orientation":{"w":-0.01434703301422562,"x":0,"y":0,"z":0.9998970760251721},"position":{"x":0.2766910565857492,"y":-0.007390610809257459,"z":0}}
         * type : charge
         */
        private int itemType=0;
        private String bind_map_md5;
        private String buildingInfo;
        private int elevator_id;
        private int floor;
        private String floorInfo;
        private int id;
        private String name;
        private int phone;
        private String phoneStr;
        private PoseBean pose;
        private String type;

        public String getBind_map_md5() {
            return bind_map_md5;
        }

        public void setBind_map_md5(String bind_map_md5) {
            this.bind_map_md5 = bind_map_md5;
        }

        public String getBuildingInfo() {
            return buildingInfo;
        }

        public void setBuildingInfo(String buildingInfo) {
            this.buildingInfo = buildingInfo;
        }

        public int getElevator_id() {
            return elevator_id;
        }

        public void setElevator_id(int elevator_id) {
            this.elevator_id = elevator_id;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public String getFloorInfo() {
            return floorInfo;
        }

        public void setFloorInfo(String floorInfo) {
            this.floorInfo = floorInfo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPhone() {
            return phone;
        }

        public void setPhone(int phone) {
            this.phone = phone;
        }

        public String getPhoneStr() {
            return phoneStr;
        }

        public void setPhoneStr(String phoneStr) {
            this.phoneStr = phoneStr;
        }

        public PoseBean getPose() {
            return pose;
        }

        public void setPose(PoseBean pose) {
            this.pose = pose;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        public int getItemType() {
            return itemType;
        }

        public static class PoseBean {
            /**
             * orientation : {"w":-0.01434703301422562,"x":0,"y":0,"z":0.9998970760251721}
             * position : {"x":0.2766910565857492,"y":-0.007390610809257459,"z":0}
             */

            private OrientationBean orientation;
            private PositionBean position;

            public OrientationBean getOrientation() {
                return orientation;
            }

            public void setOrientation(OrientationBean orientation) {
                this.orientation = orientation;
            }

            public PositionBean getPosition() {
                return position;
            }

            public void setPosition(PositionBean position) {
                this.position = position;
            }

            public static class OrientationBean {
                /**
                 * w : -0.01434703301422562
                 * x : 0
                 * y : 0
                 * z : 0.9998970760251721
                 */

                private double w;
                private int x;
                private int y;
                private double z;

                public double getW() {
                    return w;
                }

                public void setW(double w) {
                    this.w = w;
                }

                public int getX() {
                    return x;
                }

                public void setX(int x) {
                    this.x = x;
                }

                public int getY() {
                    return y;
                }

                public void setY(int y) {
                    this.y = y;
                }

                public double getZ() {
                    return z;
                }

                public void setZ(double z) {
                    this.z = z;
                }
            }

            public static class PositionBean {
                /**
                 * x : 0.2766910565857492
                 * y : -0.007390610809257459
                 * z : 0
                 */

                private double x;
                private double y;
                private int z;

                public double getX() {
                    return x;
                }

                public void setX(double x) {
                    this.x = x;
                }

                public double getY() {
                    return y;
                }

                public void setY(double y) {
                    this.y = y;
                }

                public int getZ() {
                    return z;
                }

                public void setZ(int z) {
                    this.z = z;
                }
            }
        }
    }
}