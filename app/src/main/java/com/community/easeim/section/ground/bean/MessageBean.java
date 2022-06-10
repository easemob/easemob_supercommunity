package com.community.easeim.section.ground.bean;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MessageBean implements Serializable {
    private String callId;
    private String eventType;
    private String timestamp;
    private String chat_type;
    private String group_id;
    private String msg_from;
    private String msg_to;
    private String msg_id;
    private String payload;
    private String securityVersion;
    private String security;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getChat_type() {
        return chat_type;
    }

    public void setChat_type(String chat_type) {
        this.chat_type = chat_type;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMsg_from() {
        return msg_from;
    }

    public void setMsg_from(String msg_from) {
        this.msg_from = msg_from;
    }

    public String getMsg_to() {
        return msg_to;
    }

    public void setMsg_to(String msg_to) {
        this.msg_to = msg_to;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getPayload() {

        try {
            JSONObject object = new JSONObject(payload);
            JSONArray array = object.optJSONArray("bodies");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.has("msg") || !TextUtils.isEmpty(jsonObject.optString("msg"))){
                    return jsonObject.optString("msg");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "哈喽大家好!";
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSecurityVersion() {
        return securityVersion;
    }

    public void setSecurityVersion(String securityVersion) {
        this.securityVersion = securityVersion;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "callId='" + callId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", chat_type='" + chat_type + '\'' +
                ", group_id='" + group_id + '\'' +
                ", msg_from='" + msg_from + '\'' +
                ", msg_to='" + msg_to + '\'' +
                ", msg_id='" + msg_id + '\'' +
                ", payload='" + payload + '\'' +
                ", securityVersion='" + securityVersion + '\'' +
                ", security='" + security + '\'' +
                '}';
    }
}
