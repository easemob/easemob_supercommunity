package com.community.easeim.section.friend.fragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;

import java.util.List;

public class ContactStartFragment extends BaseInitFragment {

    private AddContactFragment mAddContactFragment;
    private AllContactFragment mAllContactFragment;
    private ContactsViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_start;
    }

    @Override
    protected void initData() {
        mAddContactFragment = new AddContactFragment();
        mAllContactFragment = new AllContactFragment();

        observer();
    }

    private void observer(){
        mViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);

        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    switchFragmentByList(list);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                }
            });
        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(true);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if(event == null) {
                return;
            }
            if(event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.loadContactList(true);
    }

    private void switchFragmentByList(List<EaseUser> list){
        if (list == null || list.isEmpty()) {
            showAddContact();
        } else {
            showAllContact();
        }
    }

    private void showAllContact() {
        replace(mAllContactFragment, R.id.fcv_fragment_contact,"all");
    }

    private void showAddContact() {
        replace(mAddContactFragment, R.id.fcv_fragment_contact,"add");
    }

}
