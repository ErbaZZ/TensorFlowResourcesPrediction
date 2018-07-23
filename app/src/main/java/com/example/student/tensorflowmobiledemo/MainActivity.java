package com.example.student.tensorflowmobiledemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "KerasModelSec2.pb";
    private static final String INPUT_NODE = "lstm_1_input";
    private static final String OUTPUT_NODE = "output_node0";
    private TFPredictor TFPredictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TFPredictor = new TFPredictor(MODEL_FILE, INPUT_NODE, OUTPUT_NODE, getAssets());
        cancelAlarm();
        scheduleAlarm();
        float[] input = {
                0f,0f,0f,0f,0f,0.69f,1f,1f,0f,
                0f,0f,0f,0f,0f,0.68f,1f,1f,0f,
                0f,0f,0f,0f,0f,0.675f,1f,1f,0f};
        long[] inputDimension = {3, 9, 1};
        float[] result = TFPredictor.predict(input, inputDimension);
        for (float r : result) {
            Log.i("Result", "" + r);
        }
    }

    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // Run every minute
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 1000*60, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}


