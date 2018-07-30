package com.example.student.tensorflowmobiledemo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Status.class, Record.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract StatusDao statusDao();
    public abstract RecordDao recordDao();
}
