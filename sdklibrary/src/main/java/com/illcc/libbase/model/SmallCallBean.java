package com.illcc.libbase.model;


public class SmallCallBean extends BaseModel {
    private String id;
    private int company_id;
    private String app_user;
    private String a_number;
    private String b_number;
    private String x_number;
    private int current_state;
    private String bind_id;
    private long create_time;
    private long answer_time;
    private int forbid;
    private String blackjson;
    private int user_id;
    private int forwarding;
    private String da_type;
    private int sim;
    private int small;




    //0-直呼
    private int call_type_id;
    private String call_id;
    private int a_position;
    private int x_position;


    private String iccid;

    private String caller;
    private String callee;


    public String getCall_id() {
        return call_id;
    }

    public SmallCallBean setCall_id(String call_id) {
        this.call_id = call_id;
        return this;
    }

    public int getA_position() {
        return a_position;
    }

    public SmallCallBean setA_position(int a_position) {
        this.a_position = a_position;
        return this;
    }

    public int getX_position() {
        return x_position;
    }

    public SmallCallBean setX_position(int x_position) {
        this.x_position = x_position;
        return this;
    }

    private int call_types;

    public String getIccid() {
        return iccid;
    }

    public SmallCallBean setIccid(String iccid) {
        this.iccid = iccid;
        return this;
    }

    public String getCaller() {
        return caller;
    }

    public SmallCallBean setCaller(String caller) {
        this.caller = caller;
        return this;
    }

    public String getCallee() {
        return callee;
    }

    public SmallCallBean setCallee(String callee) {
        this.callee = callee;
        return this;
    }

    public int getCall_type_id() {
        return call_type_id;
    }

    public SmallCallBean setCall_type_id(int call_type_id) {
        this.call_type_id = call_type_id;
        return this;
    }


    public int getCall_types() {
        return call_types;
    }

    public SmallCallBean setCall_types(int call_types) {
        this.call_types = call_types;
        return this;
    }

    public int getSim() {
        return sim;
    }

    public SmallCallBean setSim(int sim) {
        this.sim = sim;
        return this;
    }

    public int getSmall() {
        return small;
    }

    public SmallCallBean setSmall(int small) {
        this.small = small;
        return this;
    }

    public String getDa_type() {
        return da_type;
    }

    public SmallCallBean setDa_type(String da_type) {
        this.da_type = da_type;
        return this;
    }

    public String getId() {
        return id;
    }

    public SmallCallBean setId(String id) {
        this.id = id;
        return this;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setApp_user(String app_user) {
        this.app_user = app_user;
    }

    public String getApp_user() {
        return app_user;
    }

    public void setA_number(String a_number) {
        this.a_number = a_number;
    }

    public String getA_number() {
        return a_number;
    }

    public void setB_number(String b_number) {
        this.b_number = b_number;
    }

    public String getB_number() {
        return b_number;
    }

    public void setX_number(String x_number) {
        this.x_number = x_number;
    }

    public String getX_number() {
        return x_number;
    }

    public void setCurrent_state(int current_state) {
        this.current_state = current_state;
    }

    public int getCurrent_state() {
        return current_state;
    }

    public void setBind_id(String bind_id) {
        this.bind_id = bind_id;
    }

    public String getBind_id() {
        return bind_id;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setAnswer_time(long answer_time) {
        this.answer_time = answer_time;
    }

    public long getAnswer_time() {
        return answer_time;
    }

    public void setForbid(int forbid) {
        this.forbid = forbid;
    }

    public int getForbid() {
        return forbid;
    }

    public void setBlackjson(String blackjson) {
        this.blackjson = blackjson;
    }

    public String getBlackjson() {
        return blackjson;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setForwarding(int forwarding) {
        this.forwarding = forwarding;
    }

    public int getForwarding() {
        return forwarding;
    }


}
