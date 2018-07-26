package com.example.student.tensorflowmobiledemo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        float predicted = TFPredictor.predict(statusRecorder.getCurrentStatuses())[0];
        float actual = statusRecorder.getWIFIStatus();
        Log.d("Result", "Predicted: " + predicted + ", Actual: " + actual);
        RecordManager.addResult(predicted, actual);

        Intent i = new Intent(this, MainActivity.class);
        i.setAction(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }
}
