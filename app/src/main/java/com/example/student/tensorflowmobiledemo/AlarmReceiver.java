package com.example.student.tensorflowmobiledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 111;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BackgroundService.class);
        // TODO: Add actual required extras
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
