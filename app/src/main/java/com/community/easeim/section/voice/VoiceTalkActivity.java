package com.community.easeim.section.voice;

import android.bluetooth.BluetoothHeadset;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.adapter.ContactAdapter;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.voice.adapter.VoiceMemberAdapter;
import com.community.easeim.section.voice.headphone.BluetoothAndHeadPhoneListener;
import com.community.easeim.section.voice.headphone.BluetoothAndHeadPhoneReceiver;
import com.community.easeim.voice.Callback;
import com.community.easeim.voice.CmdMediaCtrl;
import com.community.easeim.voice.RtcManager;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceChannelManager;
import com.community.easeim.voice.VoiceMember;
import com.community.easeim.voice.VoiceMemberManager;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;

import java.util.List;


public class VoiceTalkActivity extends BaseInitActivity implements VoiceMemberAdapter.OnMoreClickListener, BluetoothAndHeadPhoneListener, View.OnClickListener {

    private String mChannelId;
    private LinearLayout mLlJoin;
    private RecyclerView mRvMembers;
    private VoiceMemberAdapter mAdapter;
    private ImageView mIvBg;
    private LinearLayout mLlCtrl;
    private CheckBox mCbSpeaker;
    private CheckBox mCbMic;
    private TextView mTvName;
    private MyBottomSheetDialog mMemberCtrlSheet;
    private CircleImageView mCivMemberHead;
    private TextView mTvMemberName;
    private TextView mTvSwitchMemberMic;
    private AudioManager mAm;
    private BluetoothAndHeadPhoneReceiver mBluetoothReceiver;
    private MyBottomSheetDialog mInviteSheet;
    private RecyclerView mRvContactMembers;
    private ContactAdapter mContactAdapter;
    private ContactsViewModel mContactModel;
    private ImageView mIvInvite;
    private TextView mTvInvite;
    private String mGroundName;
    private ClipboardManager mClipboard;

    public static void actionStart(Context context, String channelId, String groundName) {
        Intent intent = new Intent(context, VoiceTalkActivity.class);
        intent.putExtra(EaseConstant.CHANNEL_ID, channelId);
        intent.putExtra(EaseConstant.GROUND_NAME,groundName);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice_talk;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mLlJoin = findViewById(R.id.ll_join);
        mIvBg = findViewById(R.id.iv_bg);
        mLlCtrl = findViewById(R.id.ll_ctrl);
        mCbSpeaker = findViewById(R.id.cb_speaker);
        mCbMic = findViewById(R.id.cb_mic);
        mTvName = findViewById(R.id.tv_name);
        mIvInvite = findViewById(R.id.iv_invite);

        initRv();

        switchState(false);

        initBottomSheet();
    }

    private void initBottomSheet() {
        mMemberCtrlSheet = new MyBottomSheetDialog(mContext);
        mMemberCtrlSheet.setContentView(R.layout.sheet_member_voice_ctrl);
        mCivMemberHead = mMemberCtrlSheet.findViewById(R.id.civ_head);
        mTvMemberName = mMemberCtrlSheet.findViewById(R.id.tv_name);
        mTvSwitchMemberMic = mMemberCtrlSheet.findViewById(R.id.tv_switch_member_mic);

        mInviteSheet = new MyBottomSheetDialog(mContext);
        mInviteSheet.setContentView(R.layout.sheet_voice_invite);
        mRvContactMembers = mInviteSheet.findViewById(R.id.rv_members);
        mInviteSheet.findViewById(R.id.ll_copy_invitation_code).setOnClickListener(this);
        mTvInvite = mInviteSheet.findViewById(R.id.tv_invite);
    }

    private void switchState(boolean isJoined) {
        mLlCtrl.setVisibility(isJoined ? View.VISIBLE : View.GONE);
        mRvMembers.setVisibility(isJoined ? View.VISIBLE : View.GONE);
        mIvInvite.setVisibility(isJoined ? View.VISIBLE : View.GONE);

        mIvBg.setVisibility(isJoined ? View.GONE : View.VISIBLE);
        mLlJoin.setVisibility(isJoined ? View.GONE : View.VISIBLE);

    }

    private void initRv() {
        mRvMembers = findViewById(R.id.rv_members);
        mRvMembers.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new VoiceMemberAdapter();
        mRvMembers.setAdapter(mAdapter);
        mAdapter.setOnMoreClickListener(this);

    }

    public void onInviteClick(View v) {
        String str = "Hello！欢迎来「" + mGroundName + "」和我一起嗨！打开⬇️链接：https://www.easemob.com/download/app/discord_demo";
        mTvInvite.setText(str);
        mInviteSheet.show();
    }

    @Override
    protected void initListener() {
        observer();

        registerHeadphoneReceiver();
    }

    private void observer() {
        LiveDataBus.get().with(EaseConstant.VOICE_MEMBER_CHANGE, EaseEvent.class).observe(this, this::getMembers);
        LiveDataBus.get().with(EaseConstant.VOICE_MEMBER_TALKING, EaseEvent.class).observe(this, this::updateTalking);

        //contact
        loadAllContacts();
    }

    private void updateTalking(EaseEvent easeEvent) {
        mAdapter.setSpeakNow(easeEvent.arg0,easeEvent.arg1);
    }

    private void loadAllContacts() {
        mContactModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        mContactModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    mContactAdapter.setData(list);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    mContactAdapter.setData(data);
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
    }

    private void getMembers(EaseEvent easeEvent) {
        if (TextUtils.equals(easeEvent.message, mChannelId)) {
            getMembers(false);
        }
    }

    private VoiceChannel mVoiceChannel;

    @Override
    protected void initData() {
        mChannelId = getIntent().getStringExtra(EaseConstant.CHANNEL_ID);
        mGroundName = getIntent().getStringExtra(EaseConstant.GROUND_NAME);
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        VoiceChannelManager.getInstance().getChannelById(mChannelId, new Callback<VoiceChannel>() {
            @Override
            public void onSuccess(VoiceChannel voiceChannel) {
                mVoiceChannel = voiceChannel;
                String owner = voiceChannel.getOwner();
                mTvName.setText(voiceChannel.getChannelName());
                if (TextUtils.equals(EMClient.getInstance().getCurrentUser(), owner)) {
                    mAdapter.showMore(owner);
                }
            }

            @Override
            public void onError(String err) {

            }
        });

        initInviteMembers();
    }

    private void initInviteMembers() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvContactMembers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mContactAdapter = new ContactAdapter();
        mContactAdapter.setOnItemClickListener(new OnItemClickListener<EaseUser>() {
            @Override
            public void onItemClick(EaseUser easeUser) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("提示")
                        .setTitleKey(easeUser.getNickname())
                        .setMsg("确认邀请“" + easeUser.getNickname() + "”加入该语音频道？")
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                mInviteSheet.dismiss();
                                CmdMediaCtrl.getInstance().cmdInviteJoinVoice(easeUser.getUsername(), mVoiceChannel);
                            }
                        })
                        .show();
            }
        });
        mRvContactMembers.setAdapter(mContactAdapter);
    }

    private VoiceMember mine;

    private void getMembers(boolean first) {
        VoiceChannelManager.getInstance().getChannelMembers(mChannelId, new EMValueCallBack<List<VoiceMember>>() {
            @Override
            public void onSuccess(List<VoiceMember> list) {
                mAdapter.setData(list);
                for (VoiceMember member : list) {
                    if (TextUtils.equals(member.getMemberId(), EMClient.getInstance().getCurrentUser())) {
                        if (member.isKickOff()) {
                            ToastUtils.showToast("您已被踢出该频道");
                            onBackPressed();
                            break;
                        }

                        if (mine != null && !mine.getMutedByAdmin() && member.getMutedByAdmin()) {
                            ToastUtils.showToast("您已被强制关麦");
                        }

                        mine = member;
                        if (first && !TextUtils.equals(mVoiceChannel.getOwner(), mine.getMemberId())) {
                            closeMic();
                            return;
                        }
                        if (member.getMutedByAdmin()) {
                            mCbMic.setChecked(false);
                        } else {
                            mCbMic.setChecked(!member.getMutedBySelf());
                        }
                        break;
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    public void onFinishClick(View v) {
        new CustomDialog
                .Builder(mContext)
                .setTitle("提示")
                .setMsg("确认退出频道？")
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        AppManager.getInstance().finishActivity(VoiceTalkActivity.this);
                    }
                })
                .show();
    }

    public void onJoinClick(View v) {
        RtcManager.getInstance().joinChannel(mChannelId, new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                switchSpeaker(true);
                switchState(true);
                getMembers(true);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    public void switchSpeaker(View v) {
        boolean checked = !mCbSpeaker.isChecked();
        RtcManager.getInstance().muteAllRemoteAudioStream(checked);
        VoiceMemberManager.getInstance().muteAllRemoteAudio(mine, checked);
    }

    private void closeMic() {
        mCbMic.setChecked(false);
        VoiceMemberManager.getInstance().stopAudio(mine);
    }

    public void switchMic(View v) {
        if (mine.getMutedByAdmin()) {
            ToastUtils.showToast("您已被禁言");
            mCbMic.setChecked(false);
            return;
        }
        boolean checked = mCbMic.isChecked();
        if (checked) {
            RtcManager.getInstance().startAudio();
            VoiceMemberManager.getInstance().startAudio(mine);
        } else {
            RtcManager.getInstance().stopAudio();
            VoiceMemberManager.getInstance().stopAudio(mine);
        }
    }

    @Override
    public void onMoreClick(VoiceMember member) {
        mMemberCtrlSheet.show();
        CustomImageUtil.getInstance().setHeadView(mCivMemberHead, member.getAvatar());
        mTvMemberName.setText(member.getName());
        boolean mutedByAdmin = member.getMutedByAdmin();
        mTvSwitchMemberMic.setText(mutedByAdmin ? "取消关麦" : "强制关麦");
        mTvSwitchMemberMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("确认把“" + member.getName() + "”" + (mutedByAdmin ? "取消关麦" : "强制关麦") + "？")
                        .setTitleKey(member.getName())
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                VoiceMemberManager.getInstance().switchMic(!mutedByAdmin, member);
                            }
                        })
                        .show();

                mMemberCtrlSheet.dismiss();
            }
        });

        mMemberCtrlSheet.findViewById(R.id.tv_kick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("确认把“" + member.getName() + "”踢出频道？")
                        .setTitleKey(member.getName())
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                VoiceMemberManager.getInstance().kickOffMember(member);
                            }
                        })
                        .show();
                mMemberCtrlSheet.dismiss();
            }
        });
    }

    private void registerHeadphoneReceiver() {
        mBluetoothReceiver = new BluetoothAndHeadPhoneReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        mBluetoothReceiver.setListener(this);
        mAm = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAm.setMode(AudioManager.MODE_IN_COMMUNICATION);
        registerReceiver(mBluetoothReceiver, filter);
    }

    @Override
    public void bluetoothHeadset(boolean connected) {
        mAm.setBluetoothScoOn(connected);
    }

    @Override
    public void headPhoneState(boolean connected) {
        Log.e("MainActivity", connected + "");
        if (connected) {
            //有耳机连接 只用耳机
            mAm.stopBluetoothSco();
            mAm.setBluetoothScoOn(false);
            mAm.setSpeakerphoneOn(false);
        } else {
            //无耳机 自动用外放或听筒
            mAm.setSpeakerphoneOn(mSpeakerphoneOn);
        }
    }

    @Override
    protected void onDestroy() {
        RtcManager.getInstance().leaveChannel(mine);
        unregisterReceiver(mBluetoothReceiver);
        super.onDestroy();
    }

    private boolean mSpeakerphoneOn;

    public void switchSpeaker(boolean on) {
        mSpeakerphoneOn = on;
        if (!mAm.isWiredHeadsetOn() && !mAm.isBluetoothA2dpOn()) {
            Log.e("MainActivity", "no headphone , switch speaker：" + on);
            mAm.stopBluetoothSco();
            mAm.setBluetoothScoOn(false);
            mAm.setSpeakerphoneOn(mSpeakerphoneOn);
        } else if (mAm.isBluetoothA2dpOn()) {
            mAm.startBluetoothSco();
            mAm.setBluetoothScoOn(true);
            mAm.setSpeakerphoneOn(false);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_copy_invitation_code){
            mClipboard.setPrimaryClip(ClipData.newPlainText(null, "https://www.easemob.com/download/app/discord_demo"));
            ToastUtils.showToast("已复制社区邀请链接");
        }
    }
}