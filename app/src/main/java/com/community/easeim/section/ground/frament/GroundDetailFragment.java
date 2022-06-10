package com.community.easeim.section.ground.frament;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.DemoApplication;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.CommonUtil;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.ImManager;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.chat.viewmodel.MessageViewModel;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;
import com.community.easeim.section.conversation.viewmodel.ConversationListViewModel;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.adapter.ContactAdapter;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.community.easeim.section.ground.activity.GroundModifiyActivity;
import com.community.easeim.section.ground.activity.JustHangOutActivity;
import com.community.easeim.section.ground.activity.MembersListActivity;
import com.community.easeim.section.ground.adapter.ChannelAdapter;
import com.community.easeim.section.ground.adapter.ChannelManageAdapter;
import com.community.easeim.section.ground.adapter.SelectAdapter;
import com.community.easeim.section.ground.adapter.VoiceAdapter;
import com.community.easeim.section.ground.adapter.VoiceHeadAdapter;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.ground.bean.SelectBean;
import com.community.easeim.section.group.activity.GroupMsgDisturbActivity;
import com.community.easeim.section.group.viewmodels.GroupDetailViewModel;
import com.community.easeim.section.group.viewmodels.GroupPickContactsViewModel;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.me.activity.ChooseHeadImageActivity;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.voice.VoiceTalkActivity;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceChannelManager;
import com.community.easeim.voice.VoiceMember;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class GroundDetailFragment extends BaseInitFragment implements View.OnClickListener {

    private RecyclerView mRvVoiceMembers;
    private VoiceHeadAdapter mAdapter;
    private LinearLayout mLlChannels;
    private ImageView mIvArrow, mIvAdd;
    private MyBottomSheetDialog mGroundMoreDialog;
    private ClipboardManager mClipboard;
    private TextView mTvId,mTvJoinGround;
    private MyBottomSheetDialog mGroundInviteDialog;
    private RecyclerView mRvMembers;
    private List<MyBottomSheetDialog> mSheetList = new ArrayList<>();
    private ContactAdapter mContactAdapter;
    private TextView mTvName;
    private GroundBean mGroundBean;
    private List<GroundGroupBean> mList,mFullList;
    private List<VoiceChannel> mVoiceList;
    private RecyclerView mRvChannel;
    private ChannelAdapter mChannelAdapter;
    private TextView mTvDes;
    private MyBottomSheetDialog mAddChannelDialog;
    private ChannelManageAdapter mChannelManageAdapter;
    private RecyclerView mRvChannelManage;
    private MyBottomSheetDialog mChannelManageDialog;
    private ImageView mIvCover;
    private ContactsViewModel mContactModel;
    private GroupPickContactsViewModel mGroupPickContactsViewModel;
    private ConversationListViewModel mConversationListViewModel;
    private TextView mTvTitle, mTvLogoutGround, mTvGroundNickName;
    private EMGroup mCourtGroup;
    private TextView mTvUnreadCourt;
    private TextView mTvNum;
    private VoiceAdapter mVoiceAdapter;

    private CircleImageView mGroundCover;
    private TextView mGroundName, mGroundDes;
    private ImageView mIvDesExpand;

    private TextView mTvInviteContent;
    private ImageView mIvPrivate;
    private ImageView mIvCoverGoto;
    private ImageView mIvGroundNameGoto;
    private ImageView mIvDesGoto;
    private ImageView mIvMemberGoto;
    private View mVMemberDivider;
    private LinearLayout mLlMembers;

    private GroupDetailViewModel mGroupDetailViewModel;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ground_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mIvCover = findViewById(R.id.iv_cover);
        mRvVoiceMembers = findViewById(R.id.rv_voice_members);
        mLlChannels = findViewById(R.id.ll_channels);
        mTvUnreadCourt = findViewById(R.id.tv_unread_court);
        mIvArrow = findViewById(R.id.iv_arrow);
        mIvAdd = findViewById(R.id.iv_add);
        mTvId = findViewById(R.id.tv_id);
        mTvJoinGround = findViewById(R.id.tv_join_ground);
        mTvName = findViewById(R.id.tv_name);
        mTvDes = findViewById(R.id.tv_des);
        mRvChannel = findViewById(R.id.rv_channel);
        mTvNum = findViewById(R.id.tv_num);
        mIvDesExpand = findViewById(R.id.iv_des_expand);
        mIvPrivate = findViewById(R.id.iv_private);

        initSheet();

    }

    public void setFragmentData(GroundBean bean, EMGroup group, List<GroundGroupBean> list) {
        mCourtGroup = group;
        mGroundBean = bean;
        mFullList = new ArrayList<>();
        mFullList.addAll(list);
        list.remove(0);
        mList = list;

        if (mIvDesExpand != null) {
            mIvDesExpand.setImageResource(R.drawable.icon_des_expand);
            mIvDesExpand.setVisibility(View.INVISIBLE);
        }

        if (mTvDes != null) {
            mTvDes.setMaxLines(2);
        }


        VoiceChannelManager.getInstance().getVoiceChannelListByGroundId(mGroundBean.getGroundId(), new EMValueCallBack<List<VoiceChannel>>() {
            @Override
            public void onSuccess(List<VoiceChannel> value) {
                mVoiceList = value;
                getMembers(mVoiceList.get(0).getChannelId());
                setViewData();
            }

            @Override
            public void onError(int error, String errorMsg) {
                setViewData();
            }
        });
    }


    private void setViewData() {
        if (mTvUnreadCourt != null && mCourtGroup != null) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mCourtGroup.getGroupId());
            if (conversation != null) {
                int unreadCount = conversation.getUnreadMsgCount();
                CommonUtil.getInstance().setShowNum(mTvUnreadCourt, unreadCount);
            } else {
                mTvUnreadCourt.setVisibility(View.GONE);
            }
        }

        if (mTvId != null) {
            mTvId.setText(mGroundBean.getGroundId());
        }

        if (DemoConstant.groundStrollIds.contains(mGroundBean.getGroundId())) {
            mTvJoinGround.setVisibility(View.VISIBLE);
            mTvJoinGround.setOnClickListener(this);
        } else {
            mTvJoinGround.setVisibility(View.GONE);
        }


        if (mTvName != null) {
            mTvName.setText(mGroundBean.getGroundName());
        }

        if (mIvPrivate != null) {
            mIvPrivate.setVisibility(!mGroundBean.isGround_type() ? View.VISIBLE : View.GONE);
        }

        if (mTvDes != null) {
            String des = mGroundBean.getGroundDes();
            if (TextUtils.isEmpty(des)) {
                des = "暂无简介";
            }
            mTvDes.setText(des);

            mTvDes.post(new Runnable() {
                @Override
                public void run() {
                    if (mTvDes.getLineCount() > 2) {
                        mIvDesExpand.setVisibility(View.VISIBLE);
                        mTvDes.setMaxLines(2);
                        mIvDesExpand.setOnClickListener(onExpandListener);
                    } else {
                        mIvDesExpand.setVisibility(View.INVISIBLE);
                        mTvDes.setMaxLines(Integer.MAX_VALUE);
                        mIvDesExpand.setOnClickListener(null);
                    }
                }
            });
        }

        if (mIvCover != null) {
            CustomImageUtil.getInstance().setCoverView(mIvCover, mGroundBean.getGroundCover());
        }

        if (mRvChannel != null && mChannelAdapter != null) {
           // EaseCommonUtils.sortByName(mList);
            mChannelAdapter.setData(mList);
        }

        if (mRvChannel != null && mVoiceAdapter != null) {
            mVoiceAdapter.setData(mVoiceList);
        }

        if (mTvNum != null && mCourtGroup != null) {
            mTvNum.setText(mCourtGroup.getMemberCount() + "");
        }

        if (mRvChannelManage != null && mChannelManageAdapter != null) {
            mChannelManageAdapter.setData(mList);
        }

        if (mTvTitle != null) {
            mTvTitle.setText(mGroundBean.getGroundName());
        }

        if (mTvLogoutGround != null) {
            mTvLogoutGround.setText(TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser()) ? "解散社区" : "退出社区");
        }

        if (mGroundBean.isMemberCanInvite()) {
            mGroundMoreDialog.findViewById(R.id.ll_invite).setVisibility(View.VISIBLE);
        } else {
            mGroundMoreDialog.findViewById(R.id.ll_invite).setVisibility(View.GONE);
        }
//        if (mTvGroundNickName != null && !TextUtils.isEmpty(mGroundBean.getUserGName())) {
//            mTvGroundNickName.setText(mGroundBean.getUserGName());
//        }

        if (mIvAdd != null) {
            if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
                mIvAdd.setVisibility(View.VISIBLE);
            } else {
                mIvAdd.setVisibility(View.GONE);
            }
        }
    }

    private boolean isExpand;
    private View.OnClickListener onExpandListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isExpand) {
                mTvDes.setMaxLines(2);//如果当前是展开的，则设置为闭合
                mIvDesExpand.setImageResource(R.drawable.icon_des_expand);//箭头重置为向下的状态
                isExpand = false;
            } else {
                mTvDes.setMaxLines(Integer.MAX_VALUE);//如果是闭合的，则展开
                mIvDesExpand.setImageResource(R.drawable.icon_des_close);//箭头设置为向上
                isExpand = true;
            }
        }
    };


    private void initRvAddChannel() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext);
        verticalManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView rvSelect = mAddChannelDialog.findViewById(R.id.rv_select);
        EditText etChannelName = mAddChannelDialog.findViewById(R.id.et_channel_name);
        TextView tvCreateChannel = mAddChannelDialog.findViewById(R.id.tv_create);

        rvSelect.setLayoutManager(verticalManager);
        List<SelectBean> list = new ArrayList<>();
        list.add(new SelectBean(R.drawable.icon_jing, "文字频道"));
        list.add(new SelectBean(R.drawable.icon_voice, "语音频道"));
        SelectAdapter selectAdapter = new SelectAdapter(list);
        rvSelect.setAdapter(selectAdapter);


            etChannelName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String channelName = etChannelName.getText().toString();
                if (TextUtils.isEmpty(channelName)) {
                    tvCreateChannel.setTextColor(Color.parseColor("#929497"));
                    tvCreateChannel.setOnClickListener(null);
                } else {
                    tvCreateChannel.setTextColor(Color.parseColor("#27AE60"));
                    tvCreateChannel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etChannelName.setText("");
                            if (selectAdapter.getCheckedPos() == 0) {
                                if (TextUtils.equals(getString(R.string.court_name),channelName)) {
                                    com.hjq.toast.ToastUtils.show("\"社区大厅\"为保留渠道名称不能使用!");
                                    return;
                                }

                                selectAdapter.setChecked(0);

                                ToastUtils.showToast("开始创建新频道");
                                ImManager.getInstance().createChannel(channelName, mGroundBean, new EMValueCallBack<EMGroup>() {
                                    @Override
                                    public void onSuccess(EMGroup group) {
                                        GroundManager.getInstance().saveGroundGroupInfo(mGroundBean.getGroundId(), group, "", 0, "");
                                        ToastUtils.showToast("频道创建成功");
                                        mAddChannelDialog.dismiss();
                                        LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.CHANNEL_CREATE));
                                    }

                                    @Override
                                    public void onError(int error, String errorMsg) {
                                        ToastUtils.showToast("频道创建失败:" + errorMsg);
                                    }
                                });
                            } else {
                                if (TextUtils.equals("语音交流",channelName)) {
                                    com.hjq.toast.ToastUtils.show("\"语音交流\"为保留频道名称不能使用!");
                                    return;
                                }
                                ImManager.getInstance().createVoiceChannel(mGroundBean,channelName);
                                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.CHANNEL_CREATE));
                                mAddChannelDialog.dismiss();
                                selectAdapter.setChecked(1);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initSheet() {
        //more
        mGroundMoreDialog = new MyBottomSheetDialog(mContext);
        mGroundMoreDialog.setContentView(R.layout.sheet_ground_detail);
        mTvTitle = mGroundMoreDialog.findViewById(R.id.tv_title);
        mTvLogoutGround = mGroundMoreDialog.findViewById(R.id.tv_logout_ground);
        mGroundCover = mGroundMoreDialog.findViewById(R.id.civ_ground_cover);
        mGroundName = mGroundMoreDialog.findViewById(R.id.tv_ground_name);
        mGroundDes = mGroundMoreDialog.findViewById(R.id.tv_ground_des);
        mTvGroundNickName = mGroundMoreDialog.findViewById(R.id.tv_user_ground_name);
        mGroundMoreDialog.findViewById(R.id.ll_copy_invitation_code).setOnClickListener(this);
        mGroundCover.setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.tv_logout_ground).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.tv_ground_name).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.tv_ground_des).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.ll_members).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.ll_invite).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.ll_user_ground_name).setOnClickListener(this);
        mGroundMoreDialog.findViewById(R.id.ll_notification_setting).setOnClickListener(this);
        mIvCoverGoto = mGroundMoreDialog.findViewById(R.id.iv_cover_goto);
        mIvGroundNameGoto = mGroundMoreDialog.findViewById(R.id.iv_ground_name_goto);
        mIvDesGoto = mGroundMoreDialog.findViewById(R.id.iv_des_goto);
        mIvMemberGoto = mGroundMoreDialog.findViewById(R.id.iv_member_goto);

        mLlMembers = mGroundMoreDialog.findViewById(R.id.ll_members);
        mVMemberDivider = mGroundMoreDialog.findViewById(R.id.v_member_divider);

        mSheetList.add(mGroundMoreDialog);
        //Hello！欢迎来「社区名」和我一起嗨！打开⬇️链接：https://www.easemob.com/download/app/discord_demo

        //invite
        mGroundInviteDialog = new MyBottomSheetDialog(mContext);
        mGroundInviteDialog.setContentView(R.layout.sheet_ground_invite);
        mTvInviteContent = mGroundInviteDialog.findViewById(R.id.tv_invite);
        mGroundInviteDialog.findViewById(R.id.ll_copy_invitation_code).setOnClickListener(this);
        mRvMembers = mGroundInviteDialog.findViewById(R.id.rv_members);
        mSheetList.add(mGroundInviteDialog);


        //add channel
        mAddChannelDialog = new MyBottomSheetDialog(mContext);
        mAddChannelDialog.setContentView(R.layout.sheet_add_channel);
        mSheetList.add(mAddChannelDialog);

        //channel manage
        mChannelManageDialog = new MyBottomSheetDialog(mContext);
        mChannelManageDialog.setContentView(R.layout.sheet_channel_manage);
        mSheetList.add(mChannelManageDialog);
    }

    private void initRvChannelManage() {
        LinearLayoutManager verticalManager = new LinearLayoutManager(mContext);
        verticalManager.setOrientation(RecyclerView.VERTICAL);
        mRvChannelManage = mChannelManageDialog.findViewById(R.id.rv_channel);
        mRvChannelManage.setLayoutManager(verticalManager);
        mChannelManageAdapter = new ChannelManageAdapter();
        mChannelManageAdapter.setOnDeleteListener(new ChannelManageAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String groupId, String name) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("确认删频道“" + name + "”？")
                        .setTitleKey(name)
                        .setMsg("删除后不可恢复，三思啊！")
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                ToastUtils.showToast("开始删除");
                                ImManager.getInstance().deleteChannel(groupId, new ImManager.CallBack<Object>() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        ToastUtils.showToast("删除完毕");
                                        mChannelManageDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailed(String err) {

                                    }
                                });
                            }
                        })
                        .show();


            }
        });
        mRvChannelManage.setAdapter(mChannelManageAdapter);
    }

    @Override
    protected void initListener() {
        mIvArrow.setOnClickListener(this);
        findViewById(R.id.tv_ground_title).setOnClickListener(this);
        findViewById(R.id.tv_ground_title).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
                    onManageChannelClick();
                }
                return true;
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(this);
        findViewById(R.id.iv_copy).setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);
        findViewById(R.id.ll_court).setOnClickListener(this);
        findViewById(R.id.ll_voice_channel).setOnClickListener(this);
        findViewById(R.id.iv_invite).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        mGroupDetailViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        mGroupDetailViewModel.getLeaveGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_LEAVE, EaseEvent.TYPE.GROUP));
                }
            });
        });
        initVoiceMembers();

        initInviteMembers();

        initRvChannel();

        initRvAddChannel();

        initRvChannelManage();

        setViewData();

        observer();
    }

    private void initRvChannel() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvChannel.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mChannelAdapter = new ChannelAdapter();
        mVoiceAdapter = new VoiceAdapter();

        ConcatAdapter allChannelAdapter = new ConcatAdapter(mChannelAdapter, mVoiceAdapter);
        mChannelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroundGroupBean groundGroupBean) {

                if (DemoConstant.groundStrollIds.contains(groundGroupBean.getGroundId())) {
                    JustHangOutActivity.actionStart(mContext,new Gson().toJson(groundGroupBean),new Gson().toJson(mGroundBean));
                    return;
                }

                EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                if (group != null) {
                    ChatActivity.actionStart(mContext, group.getGroupId(), EaseConstant.CHATTYPE_GROUP);
                } else {
                    EMClient.getInstance().groupManager().asyncJoinGroup(groundGroupBean.getGroupId(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            ChatActivity.actionStart(mContext, groundGroupBean.getGroupId(), EaseConstant.CHATTYPE_GROUP);
                        }

                        @Override
                        public void onError(int code, String error) {
                            Log.e("tagqi", "onError: error " + error);
                        }

                        @Override
                        public void onProgress(int progress, String status) {
                        }
                    });
                }

            }
        });
        mVoiceAdapter.setOnItemClickListener(new VoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VoiceChannel channel) {
                VoiceTalkActivity.actionStart(mContext, channel.getChannelId(), channel.getGroundName());
            }
        });
        mRvChannel.setAdapter(allChannelAdapter);

        mChannelAdapter.setData(mList);
        mVoiceAdapter.setData(mVoiceList);
    }

    private void observer() {
        //contact
        mContactModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        mContactModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    setAdapterData(list);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    setAdapterData(data);
                }
            });
        });

        mContactModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mContactModel.loadContactList(false);
                }
            });
        });

        mContactModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mContactModel.loadContactList(false);
                }
            });
        });

        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(true);
            }
        });


        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });


        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.loadContactList(true);


        //message
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);

        //voice channel member
        LiveDataBus.get().with(EaseConstant.VOICE_MEMBER_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::getVoiceChannelMembers);

    }

    private void getVoiceChannelMembers(EaseEvent event) {
        if (TextUtils.equals(event.message, mVoiceList.get(0).getChannelId())) {
            getMembers(event.message);
        }
    }

    private void getMembers(String channelId) {
        VoiceChannelManager.getInstance().getChannelMembers(channelId, new EMValueCallBack<List<VoiceMember>>() {
            @Override
            public void onSuccess(List<VoiceMember> list) {
                mAdapter.setData(list);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void loadList(EaseEvent change) {
        if (change == null) {
            return;
        }
        if (change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave() || change.isChatRoomLeave()
                || change.isContactChange()
                || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange()) {
            mChannelAdapter.notifyDataSetChanged();

            if (mTvUnreadCourt != null && mCourtGroup != null) {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mCourtGroup.getGroupId(), EMConversation.EMConversationType.GroupChat, true);
                CommonUtil.getInstance().setShowNum(mTvUnreadCourt, conversation.getUnreadMsgCount());
            }
        }
    }

    private void setAdapterData(List<EaseUser> list) {
        mContactAdapter.setData(list);
    }

    private void initInviteMembers() {
        mGroupPickContactsViewModel = new ViewModelProvider(this).get(GroupPickContactsViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvMembers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mContactAdapter = new ContactAdapter();
        mContactAdapter.setOnItemClickListener(new OnItemClickListener<EaseUser>() {
            @Override
            public void onItemClick(EaseUser easeUser) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("提示")
                        .setTitleKey(easeUser.getNickname())
                        .setMsg("确认邀请“" + easeUser.getNickname() + "”加入社区？")
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                if (mCourtGroup != null && TextUtils.equals(mCourtGroup.getGroupName(), DemoApplication.getInstance().getString(R.string.court_name))) {
                                    mGroupPickContactsViewModel.addGroupMembers(true, mCourtGroup.getGroupId(), new String[]{easeUser.getUsername()});
                                }
                            }
                        })
                        .show();
            }
        });
        mRvMembers.setAdapter(mContactAdapter);
    }

    private void initVoiceMembers() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 6);
        mRvVoiceMembers.setLayoutManager(layoutManager);
        mAdapter = new VoiceHeadAdapter();
        mRvVoiceMembers.setAdapter(mAdapter);
    }

    private void onChannelExpandClick() {
        int visibility = mLlChannels.getVisibility();
        mLlChannels.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        mIvArrow.setImageResource(visibility == View.VISIBLE ? R.drawable.arrow_up : R.drawable.arrow_down);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_ground_title || id == R.id.iv_arrow) {
            onChannelExpandClick();
        } else if (id == R.id.iv_more) {
            onMoreClick();
        } else if (id == R.id.iv_copy) {
            onIdCopyClick();
        }else if(id == R.id.tv_join_ground){
            onJoinGround();
        } else if (id == R.id.ll_members) {
            onMembersClick();
        } else if (id == R.id.ll_invite || id == R.id.iv_invite) {
            onInvite();
        } else if (id == R.id.iv_add) {
            onAddChannel();
        } else if (id == R.id.ll_voice_channel) {
            VoiceTalkActivity.actionStart(mContext, mGroundBean.getGroundId(), mGroundBean.getGroundName());
        } else if (id == R.id.ll_court) {
            onCourtClick();
        } else if (id == R.id.ll_copy_invitation_code) {
            mClipboard.setPrimaryClip(ClipData.newPlainText(null, "https://www.easemob.com/download/app/discord_demo"));
            ToastUtils.showToast("已复制社区邀请链接");
        } else if (id == R.id.civ_ground_cover) {
            if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
                Intent intent = new Intent(mContext, ChooseHeadImageActivity.class);
                intent.putExtra(EaseConstant.TYPE_IMAGE, EaseConstant.TYPE_IMAGE_COVER);
                startActivityForResult(intent, 3);
            }
        } else if (id == R.id.tv_ground_name) {
            if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
                Intent intent = new Intent(mContext, GroundModifiyActivity.class);
                intent.putExtra("modifyType", 1);
                intent.putExtra("objId", mGroundBean.getGroundId());
                intent.putExtra("nickName", mGroundBean.getGroundName());
                startActivityForResult(intent, 1);
            }
        } else if (id == R.id.tv_ground_des) {
            if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
                Intent intent = new Intent(mContext, GroundModifiyActivity.class);
                intent.putExtra("modifyType", 2);
                intent.putExtra("objId", mGroundBean.getGroundId());
                intent.putExtra("nickName", mGroundBean.getGroundDes());
                startActivityForResult(intent, 2);
            }
        } else if (id == R.id.ll_user_ground_name) {
//            Intent intent = new Intent(mContext, GroundModifiyActivity.class);
//            intent.putExtra("modifyType", 3);
//            intent.putExtra("objId", mGroundBean.getObjId());
//            intent.putExtra("nickName", mGroundBean.getUserGName());
//            startActivityForResult(intent, 4);
        } else if (id == R.id.tv_logout_ground) {
            logoutGround();
        } else if (id == R.id.ll_notification_setting){
            GroupMsgDisturbActivity.actionStart(mContext,mCourtGroup.getGroupId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //1 社区名称 2社区简介 3 社区头像  4 我在社区的昵称
        if ((requestCode == 1 && resultCode == getActivity().RESULT_OK)) {
            if (data != null) {
                String groundName = data.getStringExtra("groundName");
                mGroundName.setText(groundName);
            }
        } else if ((requestCode == 2 && resultCode == getActivity().RESULT_OK)) {
            if (data != null) {
                String groundDescribe = data.getStringExtra("groundDescribe");
                mGroundDes.setText(groundDescribe);
            }
        } else if ((requestCode == 3 && resultCode == getActivity().RESULT_OK)) {
            if (data != null) {
                String headImageUrl = data.getStringExtra("headImage");
                GroundManager.getInstance().updateGroundCover(mGroundBean.getGroundId(), headImageUrl, new EMValueCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        CustomImageUtil.getInstance().setCoverView(mGroundCover, headImageUrl);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                    }
                });
            }
        } else if ((requestCode == 4 && resultCode == getActivity().RESULT_OK)) {
            if (data != null) {
                String groundNickName = data.getStringExtra("groundNickName");
                mTvGroundNickName.setText(groundNickName);
            }
        }

        GroundManager.getInstance().getUserGroundBeanById(EMClient.getInstance().getCurrentUser(), mGroundBean.getGroundId(), new EMValueCallBack<GroundBean>() {
            @Override
            public void onSuccess(GroundBean value) {
                mGroundBean = value;
                EaseEvent event = EaseEvent.create(EaseConstant.GROUND_CHANGE);
                event.message = new Gson().toJson(mGroundBean);
                LiveDataBus.get().with(EaseConstant.GROUND_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }

    /**
     * 解散或者退出社区
     */
    private void logoutGround() {
        if (TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser())) {
            //解散社区 解散群
            GroundManager.getInstance().getGroundGroupBeanList(mGroundBean.getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
                @Override
                public void onSuccess(List<GroundGroupBean> value) {
                    for (GroundGroupBean groundGroupBean : value) {
                        EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                        if (group != null) {
                            mGroupDetailViewModel.destroyGroup(group.getGroupId());
                        }
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                }
            });
            //解散社区 后端统一处理 删除社区表记录、 用户和社区的关联、文字频道（群）、语音频道
            GroundManager.getInstance().destroyGround(mGroundBean.getGroundId(), EMClient.getInstance().getCurrentUser(), new EMValueCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    DemoConstant.groundIds.remove(mGroundBean.getGroundId());
                    mGroundMoreDialog.dismiss();
                    EaseEvent event = EaseEvent.create(EaseConstant.GROUND_CHANGE);
                    LiveDataBus.get().with(EaseConstant.GROUND_CHANGE).postValue(event);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        } else {
            //退出社区 1、退出群 2、删除用户和社区关系的记录
            GroundManager.getInstance().getGroundGroupBeanList(mGroundBean.getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
                @Override
                public void onSuccess(List<GroundGroupBean> value) {
                    for (GroundGroupBean groundGroupBean : value) {
                        EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                        if (group != null) {
                            mGroupDetailViewModel.leaveGroup(group.getGroupId());
                        }
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                }
            });
            GroundManager.getInstance().leaveGround(mGroundBean.getGroundId(), EMClient.getInstance().getCurrentUser(), new EMValueCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    DemoConstant.groundIds.remove(mGroundBean.getGroundId());
                    mGroundMoreDialog.dismiss();
                    EaseEvent event = EaseEvent.create(EaseConstant.GROUND_CHANGE);
                    LiveDataBus.get().with(EaseConstant.GROUND_CHANGE).postValue(event);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }
    }

    private void onCourtClick() {
        if (DemoConstant.groundStrollIds.contains(mFullList.get(0).getGroundId())) {
            JustHangOutActivity.actionStart(mContext,new Gson().toJson(mFullList.get(0)),new Gson().toJson(mGroundBean));
            return;
        }
        if (mCourtGroup == null) {
            return;
        }
        ChatActivity.actionStart(mContext, mCourtGroup.getGroupId(), EaseConstant.CHATTYPE_GROUP);
    }

    private void onManageChannelClick() {
        closeAllSheet();
        mChannelManageDialog.show();
    }

    private void onAddChannel() {
        closeAllSheet();
        mAddChannelDialog.show();
    }

    private void onInvite() {
        closeAllSheet();
        String str = "Hello！欢迎来「" + mGroundBean.getGroundName() + "」和我一起嗨！打开⬇️链接：https://www.easemob.com/download/app/discord_demo";
        mTvInviteContent.setText(str);
        mGroundInviteDialog.show();
    }

    private void closeAllSheet() {
        for (MyBottomSheetDialog sheetDialog : mSheetList) {
            if (sheetDialog.isShowing()) sheetDialog.dismiss();
        }
    }

    private void onMembersClick() {
        MembersListActivity.actionStart(mContext, mCourtGroup.getGroupId());
    }

    private void onIdCopyClick() {
        mClipboard.setPrimaryClip(ClipData.newPlainText(null, mTvId.getText()));
        ToastUtils.showToast("已复制社区ID到剪切板");
    }

    protected void onMoreClick() {
        closeAllSheet();
        boolean isOwner = TextUtils.equals(mGroundBean.getGroundOwner(), EMClient.getInstance().getCurrentUser());
        mGroundName.setText(TextUtils.isEmpty(mGroundBean.getGroundName()) ? "暂无名称" : mGroundBean.getGroundName());
        mGroundDes.setText(TextUtils.isEmpty(mGroundBean.getGroundDes()) ? "暂无简介" : mGroundBean.getGroundDes());
        mIvCoverGoto.setVisibility(isOwner?View.VISIBLE:View.INVISIBLE);
        mIvDesGoto.setVisibility(isOwner?View.VISIBLE:View.GONE);
        mIvGroundNameGoto.setVisibility(isOwner?View.VISIBLE:View.GONE);
        mIvMemberGoto.setVisibility(isOwner?View.VISIBLE:View.GONE);

        mLlMembers.setVisibility(isOwner?View.VISIBLE:View.GONE);
        mVMemberDivider.setVisibility(isOwner?View.VISIBLE:View.GONE);

        CustomImageUtil.getInstance().setCoverView(mGroundCover, mGroundBean.getGroundCover());
        mGroundMoreDialog.show();
    }

    private void onJoinGround(){
        new CustomDialog
                .Builder(mContext)
                .setTitle("提示")
                .setMsg("确认加入该社区?")
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        GroundManager.getInstance().saveUserAndGroundInfo(EMClient.getInstance().getCurrentUser(),mGroundBean.getGroundId());
                        joinGroundGroup(mFullList.get(0));
                    }
                }).show();
    }

    private void joinGroundGroup(GroundGroupBean groundGroupBean) {
            EMClient.getInstance().groupManager().asyncJoinGroup(groundGroupBean.getGroupId(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    DemoConstant.groundStrollIds.remove(groundGroupBean.getGroundId());
                    com.hjq.toast.ToastUtils.show("加入成功!");
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                }

                @Override
                public void onError(int code, String error) {
                    Log.e("tagqi", "onError: error " + error);
                }

                @Override
                public void onProgress(int progress, String status) {
                }
            });
    }
}
