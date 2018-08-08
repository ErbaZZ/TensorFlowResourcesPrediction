package com.example.student.ResourcesUsagePrediction;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "Predicted")
    private float predicted;

    @ColumnInfo(name = "Actual")
    private float actual;

    public Record(float predicted, float actual) {
        this.predicted = predicted;
        this.actual = actual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPredicted() {
        return predicted;
    }

    public void setPredicted(float predicted) {
        this.predicted = predicted;
    }

    public float getActual() {
        return actual;
    }

    public void setActual(float actual) {
        this.actual = actual;
    }
}
