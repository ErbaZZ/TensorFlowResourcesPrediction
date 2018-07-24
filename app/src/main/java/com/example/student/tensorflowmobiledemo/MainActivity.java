package com.example.student.tensorflowmobiledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView battery;
    private BroadcastReceiver aBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            battery.setText(String.valueOf(level)+"%");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        battery = (TextView)this.findViewById(R.id.text1);
        this.registerReceiver(this.aBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
}
