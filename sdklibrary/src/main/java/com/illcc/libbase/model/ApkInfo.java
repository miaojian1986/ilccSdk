package com.illcc.libbase.model;


public class ApkInfo extends BaseModel {

    public String vname = "";//版本名

    public String path = "";//地址

    public String createTime = ""; //时间

    public String version = ""; //版本号

    public String log = ""; // 更新内容

    public int isForce; // 是否强制更新

    public String name;


    public String[] permissions;

}