package com.example.student.tensorflowmobiledemo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecordDao {
    @Query("SELECT * FROM Record")
    List<Record> getAll();

    @Insert
    void insertAll(Record... records);

    @Delete
    void delete(Record record);
}
