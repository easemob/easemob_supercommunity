package com.community.easeim.section.group.adapter;

import com.community.easeim.R;
import com.community.easeim.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.item_empty;
    }
}
