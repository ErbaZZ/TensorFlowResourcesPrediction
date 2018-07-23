package com.example.student.tensorflowmobiledemo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BackgroundService extends IntentService {
    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Add repeated tasks
        Log.i("Service Status","Running");
    }
}
