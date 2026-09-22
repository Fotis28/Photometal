package com.example.photometal1.Models;

public class Log_audit {

    private int Log_audit_id;
    private String log_time;
    private String full_name;
    private int user_id;
    private String table_name;
    private String action_type;
    private int record_id;
    private String old_data;
    private String new_data;

//id          SERIAL PRIMARY KEY,
    //    user_id     INT REFERENCES users(id),
    //    action_type TEXT NOT NULL CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE')),
    //    table_name  TEXT NOT NULL,
    //    record_id   INT,
    //    new_data    JSONB,
    //    old_data    JSONB,
    //    log_time    TIMESTAMP DEFAULT now()





    public Log_audit() {}

    // Αυτο χρησιμοποιείται για την συνάρτηση get_logs
    public Log_audit(int user_id, String table_name, String action_type, int record_id, String old_data, String new_data) {
        this.user_id = user_id;
        this.table_name = table_name;
        this.action_type = action_type;
        this.record_id = record_id;
        this.old_data = old_data;
        this.new_data = new_data;
    }


    public Log_audit(int log_audit_id, String log_time, int user_id, String table_name, String action_type, int record_id, String old_data, String new_data , String full_name) {
        this.Log_audit_id = log_audit_id;
        this.log_time = log_time;
        this.user_id = user_id;
        this.table_name = table_name;
        this.action_type = action_type;
        this.record_id = record_id;
        this.old_data = old_data;
        this.new_data = new_data;
        this.full_name = full_name;
    }



    public int getLog_audit_id() {
        return Log_audit_id;
    }
    public void setLog_audit_id(int log_audit_id) {
        this.Log_audit_id = log_audit_id;
    }
    public String getLog_time() {
        return log_time;
    }
    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }
    public int getUser_id() {
        return user_id;
    }
    public void set_user_id(int user_id) {
        this.user_id = user_id;
    }
    public String getTable_name() {
        return table_name;
    }
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
    public String getAction_type() {
        return action_type;
    }
    public void set_action_type(String action_type) {
        this.action_type = action_type;
    }
    public int getRecord_id() {
        return record_id;
    }
    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }
    public String getOld_data() {
        return old_data;
    }
    public void setOld_data(String old_data) {
        this.old_data = old_data;
    }
    public String getNew_data() {
        return new_data;
    }
    public void setNew_data(String new_data) {
        this.new_data = new_data;
    }
    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }
}
