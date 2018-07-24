package com.example.student.tensorflowmobiledemo;

//HEAD
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {


    private TextView battery;
    private BroadcastReceiver aBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            boolean isCharging = status ==BatteryManager.BATTERY_STATUS_CHARGING|| status==BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String blue = "";
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                     blue.concat("bluetooth isn't enable");
                }
                else{
                     blue.concat("bluetooth is enable");
                }
            }
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
            battery.setText(String.valueOf(level)+"%"+" IsCharging = "+String.valueOf(isCharging)+" USBCharging = "+String.valueOf(usbCharge)+" ACCharging = "+String.valueOf(acCharge)+""+blue);
        }
    };
    public static int DetailedStateToNum(NetworkInfo.DetailedState state){
        switch (state){
            case AUTHENTICATING:
                return 1;
            case BLOCKED:
                return 2;
            case CAPTIVE_PORTAL_CHECK:
                return 3;
            case CONNECTED:
                return 4;
            case CONNECTING:
                return 5;
            case DISCONNECTED:
                return 6;
            case FAILED:
                return 7;
            case IDLE:
                return 8;
            case OBTAINING_IPADDR:
                return 9;
            case SCANNING:
                return 10;
            case SUSPENDED:
                return 11;
            case VERIFYING_POOR_LINK:
                return 12;
            default:
                return -1;

        }
    }

    private static final String MODEL_FILE = "KerasModelSec2.pb";
    private static final String INPUT_NODE = "lstm_1_input";
    private static final String OUTPUT_NODE = "output_node0";
    private TFPredictor TFPredictor;

//2c37dfa97f1168e610a2751b9455fa7527c583b4
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// HEAD
        battery = (TextView)this.findViewById(R.id.text1);
        this.registerReceiver(this.aBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.initialize();
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

    /**
     * Initialize the necessary variables
     */
    public void initialize() {
        TFPredictor = new TFPredictor(MODEL_FILE, INPUT_NODE, OUTPUT_NODE, getAssets());
        cancelAlarm();
        scheduleAlarm();
    }

    /**
     * Start the schedule to run repeated tasks every interval period
     */
    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60;   // 1 minute interval
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, interval, pIntent);
    }

    /**
     * Cancel the repeated tasks schedule
     */
    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
//2c37dfa97f1168e610a2751b9455fa7527c583b4
    }
}


