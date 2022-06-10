package com.community.easeim.section.ground.bean;

public class GroundGroupBean {
    private String id;
    /**
     * 社区Id
     */
    private String groundId;
    /**
     * 群ID
     */
    private String groupId;
    /**
     * 群名称
     */
    private String groupName;
    /**
     * 群分类
     */
    private String groupType;
    /**
     * index (1 社区大厅，2 随便聊聊 ，0 社区创建的其它渠道群)
     */
    private int groupIndex;
    /**
     * ext (其它扩展)
     */
    private String groupExt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroundId() {
        return groundId;
    }

    public void setGroundId(String groundId) {
        this.groundId = groundId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupExt() {
        return groupExt;
    }

    public void setGroupExt(String groupExt) {
        this.groupExt = groupExt;
    }
}
