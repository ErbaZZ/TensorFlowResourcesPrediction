package com.example.student.ResourcesUsagePrediction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver which will launch the RecordService every time the intent is received
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 111;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RecordService.class);
        // TODO: Add actual required extras
        // i.putExtra("foo", "bar");
        context.startService(i);
    }
}
