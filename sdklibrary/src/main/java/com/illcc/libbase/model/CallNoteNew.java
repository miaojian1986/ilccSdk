package com.illcc.libbase.model;

import java.io.Serializable;

public class CallNoteNew implements Serializable {

    private int user_id;
    private String caller;
    private String callee;
    private String created_at;
    private int num;
    private String callee_address;
    private String ai_number_id;
    private String recordpath;

    //是否挂机
    private int isuplog;
    //是否上传录音
    private int isuprecord;

    private String callid;







    public String getCallid() {
        return callid;
    }

    public CallNoteNew setCallid(String callid) {
        this.callid = callid;
        return this;
    }

    public int getIsuplog() {
        return isuplog;
    }

    public CallNoteNew setIsuplog(int isuplog) {
        this.isuplog = isuplog;
        return this;
    }

    public int getIsuprecord() {
        return isuprecord;
    }

    public CallNoteNew setIsuprecord(int isuprecord) {
        this.isuprecord = isuprecord;
        return this;
    }

    public String getAi_number_id() {
        return ai_number_id;
    }

    public CallNoteNew setAi_number_id(String ai_number_id) {
        this.ai_number_id = ai_number_id;
        return this;
    }

    public String getRecordpath() {
        return recordpath;
    }

    public CallNoteNew setRecordpath(String recordpath) {
        this.recordpath = recordpath;
        return this;
    }




    private String hightLightWords;

    public String getHightLightWords() {
        return hightLightWords;
    }

    public CallNoteNew setHightLightWords(String hightLightWords) {
        this.hightLightWords = hightLightWords;
        return this;
    }




    public CallNoteNew setCaller(String caller) {
        this.caller = caller;
        return this;
    }

    public String getCallee() {
        return callee;
    }

    public CallNoteNew setCallee(String callee) {
        this.callee = callee;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }



    public int getNum() {
        return num;
    }

    public CallNoteNew setNum(int num) {
        this.num = num;
        return this;
    }

    public String getCallee_address() {
        return callee_address;
    }


}
