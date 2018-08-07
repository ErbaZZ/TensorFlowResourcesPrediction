package com.example.student.ResourcesUsagePrediction;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
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
        Log.i("Service Status","Running");
        StatusRecorder statusRecorder = new StatusRecorder(this.getApplicationContext());
        statusRecorder.updateStatuses();
        float[] statuses = statusRecorder.getCurrentStatuses();
        float predicted = TFPredictor.predict(statuses)[0];
        float actual = statusRecorder.getWIFIStatus();
        Log.d("Result", "Predicted: " + predicted + ", Actual: " + actual);
        RecordManager.addResult(predicted, actual);
        RecordManager.addRecord(statuses);
        sendTrigger();
    }

    private void sendTrigger() {
        Intent intent = new Intent("trigger");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
