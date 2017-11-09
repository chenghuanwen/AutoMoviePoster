package com.hisilicon.videocenter.util;

public class Mount {

    public static final int TYPE_NFS = 1;

    public static final int TYPE_SAMBA = 2;

    private int type;

    private String address;

    private String workpath;

    private String user;

    private String password;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkpath() {
        return workpath;
    }

    public void setWorkpath(String workpath) {
        this.workpath = workpath;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}