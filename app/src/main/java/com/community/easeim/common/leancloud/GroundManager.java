package com.community.easeim.common.leancloud;

import androidx.annotation.NonNull;

import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMGroup;

import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;
import retrofit2.Call;

public class GroundManager {
    /**
     * 用户加入的社区关联表
     *  关系id（自动生成）   环信id  社区id   社区名称
     */

    private GroundManager() {
    }

    private static GroundManager instance;

    public static GroundManager getInstance() {
        if (instance == null) {
            instance = new GroundManager();
        }
        return instance;
    }


    //社区信息 开始
    /**
     * 创建社区
     * @param groundBean
     */
    public void createGround(GroundBean groundBean) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("ground_id", groundBean.getGroundId())
                .put("ground_name", groundBean.getGroundName())
                .put("ground_describe", groundBean.getGroundDes())
                .put("ground_cover", groundBean.getGroundCover())
                .put("ground_owner", groundBean.getGroundOwner())
                .put("can_invite", groundBean.isMemberCanInvite())
                .put("type", groundBean.isGround_type())
                .build();
        ApiManager.getAppServerApi().createGround(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }


    //根据关键字查找
    public void getGroundListByKey(String key, EMValueCallBack<List<GroundBean>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("key", key)
                .build();
        ApiManager.getAppServerApi().getGroundListKey(build).enqueue(new AppServerApi.ResultCallback<List<GroundBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<GroundBean>>> call, @NonNull List<GroundBean> data) {
                    callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<List<GroundBean>>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 社区列表
     * @param callBack
     */
    public void getGroundList(Set<String> listId, EMValueCallBack<List<GroundBean>> callBack) {
        ApiManager.getAppServerApi().getGroundList().enqueue(new AppServerApi.ResultCallback<List<GroundBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<GroundBean>>> call, @NonNull List<GroundBean> data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<List<GroundBean>>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 保存用户和社区关系
     * @param userId
     * @param groundId
     */
    public void saveUserAndGroundInfo(String userId, String groundId) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("user_id", userId)
                .put("ground_id", groundId)
                .build();
        ApiManager.getAppServerApi().saveUserGroundInfo(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {

            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
            }
        });
    }


    /**
     * 修改社区名称
     * @param groundId
     * @param name
     */
    public void updateGroundName(String groundId, String name, EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .put("name", name)
                .build();
        ApiManager.getAppServerApi().updateGroundName(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                callBack.onSuccess(true);
            }

            @Override
            public void onResultOk() {
                callBack.onSuccess(true);
                super.onResultOk();
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 更新社区封面
     * @param groundId
     * @param cover
     */
    public void updateGroundCover(String groundId, String cover, EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .put("cover", cover)
                .build();
        ApiManager.getAppServerApi().updateGroundCover(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {

            }
            @Override
            public void onResultOk() {
                callBack.onSuccess(true);
                super.onResultOk();
            }
            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
            }
        });
    }


    /**
     * 修改社区简介
     * @param groundId
     * @param describe
     */
    public void updateGroundDescribe(String groundId, String describe, EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .put("describe", describe)
                .build();
        ApiManager.getAppServerApi().updateGroundDescribe(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {

            }
            @Override
            public void onResultOk() {
                callBack.onSuccess(true);
                super.onResultOk();
            }
            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     * 根据用户ID查询社区
     */
    public void getUserGroundList(String userId, EMValueCallBack<List<GroundBean>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", userId)
                .build();
        ApiManager.getAppServerApi().getGroundListByUserId(build).enqueue(new AppServerApi.ResultCallback<List<GroundBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<GroundBean>>> call, @NonNull List<GroundBean> data) {
                if (data != null) {
                    callBack.onSuccess(data);
                }
            }

            @Override
            protected void onFail(Call<Result<List<GroundBean>>> call, @NonNull Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    /**
     * 根据用户ID和社区ID 查询唯一关系社区
     */
    public void getUserGroundBeanById(String userId, String groundId, EMValueCallBack<GroundBean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .build();
        ApiManager.getAppServerApi().getGroundByGroundId(build).enqueue(new AppServerApi.ResultCallback<GroundBean>() {
            @Override
            protected void onSuccess(Call<Result<GroundBean>> call, @NonNull GroundBean data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<GroundBean>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 踢出社区或离开社区
     */
    public void leaveGround(String groundId, String userId,EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .put("userId", userId)
                .build();
        ApiManager.getAppServerApi().leaveGround(build).enqueue(new AppServerApi.ResultCallback<Object>() {

            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                callBack.onSuccess(true);
            }
            @Override
            public void onResultOk() {
                callBack.onSuccess(true);
                super.onResultOk();
            }
            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) { }
        });
    }

    /**
     * 解散社区
     * @param groundId
     * @param userId
     */
    public void destroyGround(String groundId, String userId, EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .put("userId", userId)
                .build();
        ApiManager.getAppServerApi().destroyGround(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                callBack.onSuccess(true);
            }
            @Override
            public void onResultOk() {
                callBack.onSuccess(true);
                super.onResultOk();
            }
            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) { t.printStackTrace();}
        });
    }

    /**
     * 社区和群的关系表：
     * 关系id（自动生成）  社区id  群id   群名称（渠道名称）  分类名称（默认为空）  index (1 社区大厅，2 随便聊聊 ，0 社区创建的其它渠道群)   ext (其它扩展)
     */

    public void saveGroundGroupInfo(String ground_id, EMGroup group, String groupType, int index, String group_ext) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", ground_id)
                .put("groupId", group.getGroupId())
                .put("groupName", group.getGroupName())
                .put("groupType", groupType)
                .put("groupIndex", index)
                .put("groupExt", group_ext)
                .build();
        ApiManager.getAppServerApi().saveGroundAndGroup(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }
            @Override
            public void onResultOk() {
                super.onResultOk();
            }
            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 根据社区ID查询群
     */
    public void getGroundGroupBeanList(String ground_id, EMValueCallBack<List<GroundGroupBean>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", ground_id)
                .build();
        ApiManager.getAppServerApi().getGroundGroups(build).enqueue(new AppServerApi.ResultCallback<List<GroundGroupBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<GroundGroupBean>>> call, @NonNull List<GroundGroupBean> data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<List<GroundGroupBean>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }

        });
    }

    /**
     * 根据群ID查询社区
     */
    public void getGroundGroupByGroupId(String groupId, EMValueCallBack<GroundGroupBean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groupId", groupId)
                .build();
        ApiManager.getAppServerApi().getGroundGroupByGroupId(build).enqueue(new AppServerApi.ResultCallback<GroundGroupBean>() {
            @Override
            protected void onSuccess(Call<Result<GroundGroupBean>> call, @NonNull GroundGroupBean data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<GroundGroupBean>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 修改频道（群名称）名称
     * @param groupId
     * @param name
     */
    public void updateGroupName(String groupId, String name) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groupId", groupId)
                .put("name", name)
                .build();
        ApiManager.getAppServerApi().updateGroupName(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                LiveDataBus.get().with(EaseConstant.GROUND_CHANGE).postValue(EaseEvent.create(EaseConstant.GROUND_CHANGE));
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) { t.printStackTrace();}
        });
    }

    /**
     * 通过群ID删除一条社区和群的关联记录
     * @param groupId
     */
    public void deleteGroundGroupInfo(String groupId) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groupId", groupId)
                .build();
        ApiManager.getAppServerApi().deleteGroundGroupByGroupId(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) { t.printStackTrace();}
        });
    }

}
