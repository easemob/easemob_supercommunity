package com.community.easeim.voice;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.List;

public class VoiceChannel implements Serializable {
    private String groundId;
    private String channelId;
    private String channelName;
    private String objectId;
    private String groundName;
    /**
     * index (1 随便交流，0 社区创建的其它语音渠道)
     */
    private int roomIndex;
    private String owner;


    public String getGroundName() {
        return groundName;
    }

    public void setGroundName(String groundName) {
        this.groundName = groundName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    private String description;

    private List<Member> speakers;
    private int members = 0;

    public String getGroundId() {
        return groundId;
    }

    public void setGroundId(String groundId) {
        this.groundId = groundId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getRoomIndex() {
        return roomIndex;
    }

    public void setRoomIndex(int roomIndex) {
        this.roomIndex = roomIndex;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Member> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Member> speakers) {
        this.speakers = speakers;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

}
