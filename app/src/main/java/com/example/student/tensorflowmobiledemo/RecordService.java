package com.example.student.tensorflowmobiledemo;

import android.app.IntentService;
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
        // TODO: Add repeated tasks
        Log.i("Service Status","Running");
        StatusRecorder statusRecorder = new StatusRecorder(this.getApplicationContext());
        statusRecorder.updateStatuses();
        float[] statuses = statusRecorder.getCurrentStatuses();
        float predicted = TFPredictor.predict(statuses)[0];
        float actual = statusRecorder.getWIFIStatus();
        Log.d("Result", "Predicted: " + predicted + ", Actual: " + actual);
        RecordManager.addResult(predicted, actual);
        RecordManager.addRecord(statuses);

        /*Intent i = new Intent(this, MainActivity.class);
        i.setAction(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);*/
        sendTrigger();
    }

    private void sendTrigger() {
        Intent intent = new Intent("trigger");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
