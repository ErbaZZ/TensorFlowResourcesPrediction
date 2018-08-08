package com.example.student.ResourcesUsagePrediction;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StatusDao {
    @Query("SELECT * FROM Status")
    List<Status> getAll();

    @Insert
    void insertAll(Status... statuses);

    @Delete
    void delete(Status status);
}
