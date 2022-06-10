package com.community.easeim.section.ground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.ground.adapter.MemberAdapter;
import com.community.easeim.section.ground.adapter.MemberFilterAdapter;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.ground.bean.MemberBean;
import com.community.easeim.section.group.viewmodels.GroupMemberAuthorityViewModel;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class MembersListActivity extends BaseInitActivity implements MemberAdapter.OnItemClickListener, MemberFilterAdapter.OnFilterListener {

    private RecyclerView mRvMembers;
    private MemberAdapter mAdapter;
    private RecyclerView mRvFilter;
    private MemberFilterAdapter mFilterAdapter;
    private List<String> mFilterList,mBlackList;
    private List<MemberBean> mMemberBeanList;
    private TextView mTvNum;
    private String groupId;
    protected GroupMemberAuthorityViewModel viewModel;
    private MyBottomSheetDialog mMemberCtrlSheet;
    private ImageView mCivMemberHead;
    private TextView mTvMemberName;
    private TextView mTv2Black;
    private TextView mTvCadre;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_members_list;
    }

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, MembersListActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvMembers = findViewById(R.id.rv_members);
        mRvFilter = findViewById(R.id.rv_filter);
        mTvNum = findViewById(R.id.tv_num);
    }

    @Override
    protected void initData() {
        mMemberBeanList = new ArrayList<>();
        mBlackList = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(GroupMemberAuthorityViewModel.class);
        groupId = getIntent().getStringExtra("groupId");
        initFilterRv();
        initMemberRv();
        initBottomSheet();
//        viewModel.getRefreshObservable().observe(this, response -> {
//            parseResource(response, new OnResourceParseCallback<String>() {
//                @Override
//                public void onSuccess(String message) {
//                    viewModel.getGroup(groupId);
//                    viewModel.getMembers(groupId);
//                    viewModel.getBlackMembers(groupId);
//                }
//            });
//        });
    }

    private void initBottomSheet() {
        mMemberCtrlSheet = new MyBottomSheetDialog(mContext);
        mMemberCtrlSheet.setContentView(R.layout.sheet_member_ctrl);
        mCivMemberHead = mMemberCtrlSheet.findViewById(R.id.civ_head);
        mTvMemberName = mMemberCtrlSheet.findViewById(R.id.tv_name);
        mTv2Black = mMemberCtrlSheet.findViewById(R.id.tv_2_black);
        mTvCadre = mMemberCtrlSheet.findViewById(R.id.tv_cadre);
    }

    private void initFilterRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvFilter.setLayoutManager(layoutManager);

        viewModel.getGroup(groupId);
        viewModel.getMembers(groupId);
        viewModel.getBlackMembers(groupId);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup group) {
                    List<String> adminList = group.getAdminList();
                    for (String user : adminList) {
                        if (!listContain(user))
                            mMemberBeanList.add(new MemberBean(MemberBean.ONLINE_ONLINE, user, MemberBean.ROLE_ADMIN));
                    }
                    if (!listContain(group.getOwner()))
                        mMemberBeanList.add(new MemberBean(MemberBean.ONLINE_ALL, group.getOwner(), MemberBean.ROLE_OWNER));

                    mAdapter.setData(mMemberBeanList, TextUtils.equals(group.getOwner(), EMClient.getInstance().getCurrentUser()), adminList.contains(EMClient.getInstance().getCurrentUser()));
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                }
            });
        });

        viewModel.getMemberObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    if (data.size() == 0)
                        return;
                    for (EaseUser easeUser : data) {
                        if (!listContain(easeUser.getUsername()))
                            mMemberBeanList.add(new MemberBean(MemberBean.ONLINE_ALL, easeUser.getUsername(), MemberBean.ROLE_MEMBER));
                    }
                    mAdapter.setData(mMemberBeanList);
                    mTvNum.setText(mMemberBeanList.size() + "人");
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                }
            });
        });
        viewModel.getBlackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    mBlackList.clear();
                    if (data.size() == 0)
                        return;
                    mBlackList.addAll(data);
                    for (String user : data) {
                        mMemberBeanList.add(new MemberBean(MemberBean.ONLINE_BLACK, user, MemberBean.ROLE_MEMBER));
                    }
                    mAdapter.setData(mMemberBeanList);
                    mTvNum.setText(mMemberBeanList.size() + "人");
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();

                }
            });
        });


        mFilterAdapter = new MemberFilterAdapter();
        mRvFilter.setAdapter(mFilterAdapter);

        mFilterList = new ArrayList<>();
        mFilterList.add("全部成员");
        mFilterList.add("干部");
        mFilterList.add("小黑屋");

        mFilterAdapter.setData(mFilterList);
        mFilterAdapter.setOnFilterListener(this);
    }

    private boolean listContain(String id) {
        if (mMemberBeanList.isEmpty()) {
            return false;
        }

        for (MemberBean memberBean : mMemberBeanList) {
            if (TextUtils.equals(memberBean.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    private void initMemberRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvMembers.setLayoutManager(layoutManager);

        mAdapter = new MemberAdapter();
        mRvMembers.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(MemberBean bean) {
        ContactDetailActivity.actionStart(mContext, bean.getId());
    }

    @Override
    public void onMoreClick(MemberBean member) {
        mMemberCtrlSheet.show();
        CustomImageUtil.getInstance().setHeadView(mCivMemberHead, member.getAvatar());
        mTvMemberName.setText(member.getName());



        if (member.getRole() == MemberBean.ROLE_ADMIN) {
            mTvCadre.setText("移除干部");
            mTvCadre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CustomDialog
                            .Builder(mContext)
                            .setTitle("确认把“" + member.getName() + "”移除干部权限？")
                            .setTitleKey(member.getName())
                            .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                @Override
                                public void onConfirmClick(View view) {
                                    viewModel.removeGroupAdmin(groupId, member.getId());
                                }
                            })
                            .show();

                    mMemberCtrlSheet.dismiss();
                }
            });
        } else {
            mTvCadre.setText("提干");
            mTvCadre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CustomDialog
                            .Builder(mContext)
                            .setTitle("确认把“" + member.getName() + "”提拔为干部？")
                            .setTitleKey(member.getName())
                            .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                @Override
                                public void onConfirmClick(View view) {
                                    viewModel.addGroupAdmin(groupId, member.getId());
                                }
                            })
                            .show();

                    mMemberCtrlSheet.dismiss();
                }
            });
        }

        if (mBlackList.contains(member.getId())){
            mTv2Black.setText("移出小黑屋");
            mTv2Black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomDialog
                            .Builder(mContext)
                            .setTitle("确认把“" + member.getName() + "”移出小黑屋？")
                            .setTitleKey(member.getName())
                            .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                @Override
                                public void onConfirmClick(View view) {
                                    //移除黑名单等于 移除群 移除社区
                                    viewModel.unblockUser(groupId, member.getId());
                                    GroundManager.getInstance().getGroundGroupByGroupId(groupId, new EMValueCallBack<GroundGroupBean>() {
                                        @Override
                                        public void onSuccess(GroundGroupBean value) {
                                            GroundManager.getInstance().leaveGround(value.getGroundId(), member.getId(), new EMValueCallBack<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean value) { }

                                                @Override
                                                public void onError(int error, String errorMsg) { }
                                            });
                                        }

                                        @Override
                                        public void onError(int error, String errorMsg) { }
                                    });
                                }
                            })
                            .show();

                    mMemberCtrlSheet.dismiss();
                }
            });
        } else {
            mTv2Black.setText("关进小黑屋");
            mTv2Black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomDialog
                            .Builder(mContext)
                            .setTitle("确认把“" + member.getName() + "”关到小黑屋？")
                            .setTitleKey(member.getName())
                            .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                @Override
                                public void onConfirmClick(View view) {
                                    viewModel.blockUser(groupId, member.getId());
                                }
                            })
                            .show();

                    mMemberCtrlSheet.dismiss();
                }
            });
        }


        mMemberCtrlSheet.findViewById(R.id.tv_kick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("确认把“" + member.getName() + "”踢出本社区？")
                        .setTitleKey(member.getName())
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                viewModel.removeUserFromGroup(groupId,member.getId());
                                viewModel.getMembers(groupId);
                            }
                        })
                        .show();
                mMemberCtrlSheet.dismiss();
            }
        });
    }

    @Override
    public void onFilter(int pos) {
        String filter = mFilterList.get(pos);
        if (TextUtils.equals(filter, "全部成员")) {
            mAdapter.filter(MemberBean.ONLINE_ALL);
        } else if (TextUtils.equals(filter, "干部")) {
            mAdapter.filter(MemberBean.ONLINE_ONLINE);
        } else if (TextUtils.equals(filter, "小黑屋")) {
            mAdapter.filter(MemberBean.ONLINE_BLACK);
        }
    }
}
