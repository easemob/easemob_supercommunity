package com.community.easeim.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.community.easeim.common.db.converter.DateConverter;
import com.community.easeim.common.db.dao.AppKeyDao;
import com.community.easeim.common.db.dao.EmUserDao;
import com.community.easeim.common.db.dao.InviteMessageDao;
import com.community.easeim.common.db.dao.MsgTypeManageDao;
import com.community.easeim.common.db.entity.AppKeyEntity;
import com.community.easeim.common.db.entity.EmUserEntity;
import com.community.easeim.common.db.entity.InviteMessage;
import com.community.easeim.common.db.entity.MsgTypeManageEntity;

@Database(entities = {EmUserEntity.class,
        InviteMessage.class,
        MsgTypeManageEntity.class,
        AppKeyEntity.class},
        version = 17)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

    public abstract InviteMessageDao inviteMessageDao();

    public abstract MsgTypeManageDao msgTypeManageDao();

    public abstract AppKeyDao appKeyDao();
}
