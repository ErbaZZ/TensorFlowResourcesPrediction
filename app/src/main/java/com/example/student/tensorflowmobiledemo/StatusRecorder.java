package com.example.student.tensorflowmobiledemo;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;

public class StatusRecorder {

    private boolean screenStatus;
    private PowerManager powerManager;

    public StatusRecorder(Context context) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }
}
