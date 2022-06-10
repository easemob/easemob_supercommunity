package com.community.easeim.voice;

import java.io.Serializable;

public class VoiceMember implements Serializable {
    private String objectId;
    private String channelId;
    private int streamId;
    private String memberId;
    private String avatar;
    private String name;
    private boolean speakerOff;
    private boolean mutedByAdmin;
    private boolean mutedBySelf;

    private boolean kickOff;

    public boolean isKickOff() {
        return kickOff;
    }

    public void setKickOff(boolean kickOff) {
        this.kickOff = kickOff;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channel) {
        this.channelId = channel;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean getSpeakerOff() {
        return speakerOff;
    }

    public void setSpeakerOff(boolean isSpeakerOff) {
        this.speakerOff = isSpeakerOff;
    }

    public boolean getMutedByAdmin() {
        return mutedByAdmin;
    }

    public void setMutedByAdmin(boolean isMutedByAdmin) {
        this.mutedByAdmin = isMutedByAdmin;
    }

    public void setMutedBySelf(boolean isMuteBySelf) {
        this.mutedBySelf = isMuteBySelf;
    }

    public boolean getMutedBySelf() {
        return mutedBySelf;
    }

    @Override
    public String toString() {
        return "VoiceMember{" +
                "channelId='" + channelId + '\'' +
                ", streamId=" + streamId +
                ", memberId='" + memberId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", isSpeakerOff=" + speakerOff +
                ", isMutedByAdmin=" + mutedByAdmin +
                ", isMuteBySelf=" + mutedBySelf +
                ", objectId='" + objectId + '\'' +
                ", isKickOff=" + kickOff +
                '}';
    }

}
