package com.example.student.tensorflowmobiledemo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Background service to record the system status every minute
 */
public class RecordService extends IntentService {
    public RecordService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Add repeated tasks
        Log.i("Service Status","Running");
        StatusRecorder statusRecorder = new StatusRecorder(this.getApplicationContext());
        statusRecorder.updateStatuses();
    }
}
