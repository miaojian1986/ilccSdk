package com.illcc.libbase.model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;

@Keep
@Entity
public class SaveData extends BaseModel {
    @Id
    String key;
    @Property
    String type;
    @Property
    String jsonStrng;
    @Property
    long time;


    public SaveData(){}

    public SaveData(String key, String type, String jsonStrng, long time) {
        this.key = key;
        this.type = type;
        this.jsonStrng = jsonStrng;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public SaveData setKey(String key) {
        this.key = key;
        return this;
    }

    public String getType() {
        return type;
    }

    public SaveData setType(String type) {
        this.type = type;
        return this;
    }

    public String getJsonStrng() {
        return jsonStrng;
    }

    public SaveData setJsonStrng(String jsonStrng) {
        this.jsonStrng = jsonStrng;
        return this;
    }

    public long getTime() {
        return time;
    }

    public SaveData setTime(long time) {
        this.time = time;
        return this;
    }
}
