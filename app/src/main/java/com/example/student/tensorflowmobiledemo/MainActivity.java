package com.example.student.tensorflowmobiledemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.BatteryManager;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "KerasModelMin.pb";
    private static final String INPUT_NODE = "lstm_40_input_1";
    private static final String OUTPUT_NODE = "output_node0";
    private float[] collectInfo ;
    private TextView battery;
    private BroadcastReceiver InfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            boolean wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String blue = "";
            if (mBluetoothAdapter == null) {
                blue.concat("bluetooth isn't supported");
                Log.i("Bluetooth", "Not supported");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                     blue.concat("bluetooth isn't enable");
                    Log.i("Bluetooth", "Not enabled");
                }
                else{
                     blue.concat("bluetooth is enable");
                    Log.i("Bluetooth", "Enabled");
                }
            }
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            //int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
            NetworkInfo networkInfo2 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.DetailedState detailedState;
            int wifi = -1;
            if(networkInfo2!=null)
            {
                detailedState = networkInfo2.getDetailedState();
                wifi = DetailedStateToNum(detailedState);
            }
            ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            String connType;
            int connState;
            if (networkInfo == null){
                // No cellular connectivity
                connType = "NONE";
                connState = -1;
            } else {
                connType = networkInfo.getSubtypeName();
                connState = StateToNum(networkInfo.getState());
            }
            float rx = TrafficStats.getMobileRxBytes();
            float tx =  TrafficStats.getMobileTxBytes();
            float totalrx = TrafficStats.getTotalRxBytes();
            float totaltx =  TrafficStats.getTotalTxBytes();
            boolean inAirplaneMode = intent.getBooleanExtra("state", false);
            battery.setText(String.valueOf(level)+"% WifiCharging = "+wirelessCharge+" IsCharging = "+String.valueOf(isCharging)+" USBCharging = "+String.valueOf(usbCharge)+" ACCharging = "+String.valueOf(acCharge)+" Bluetooth: "+state+" Network Type connected: "+connType+" Network State connected: "+connState+" Wifi Status: "+wifi+" AirplaneMode: "+inAirplaneMode
            +" RX: "+rx+" TX: "+tx+" TotalRX: "+totalrx+" TotalTX: "+totaltx);
        }
    };
    public static int StateToNum(NetworkInfo.State  state){
        switch (state){
            case CONNECTED:
                return 4;
            case CONNECTING:
                return 5;
            case DISCONNECTED:
                return 6;
            case SUSPENDED:
                return 11;
            default:
                return -1;

        }
    }
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


    private Float[] predicted;
    private Float[] actual;
    private TFPredictor tfPredictor;
    private RecordManager recordManager;
    private TextView tvPredicted;
    private TextView tvCurrent;
    private TextView tvAccuracy;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPredicted = findViewById(R.id.tvPredicted);
        tvCurrent = findViewById(R.id.tvCurrent);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        graph = findViewById(R.id.graph);

        predicted = new Float[100];
        actual = new Float[100];
        for (int i = 0; i < 100; i++) {
            predicted[i] = Float.valueOf(Math.round(Math.random()));
            actual[i] = Float.valueOf(Math.round(Math.random()));
        }

        recordManager = new RecordManager(new ArrayList<Float>(Arrays.asList(predicted)), new ArrayList<Float>(Arrays.asList(actual)));

        this.visualize(recordManager.getPredicted(), recordManager.getActual());
        battery = (TextView)this.findViewById(R.id.text1);
        this.registerReceiver(this.InfoReceiver,new IntentFilter(Intent.ACTION_DEFAULT));

        this.initialize();
//        Iterator<Operation> operations = tfPredictor.getTFInterface().graph().operations();
//
//        while (operations.hasNext()) {
//            Operation op = operations.next();
//            Log.i("Operation", op.toString());
//        }
        float[] input = {
                1400f / 1440f, 20f / 31f, 1f, 0f, 0f, 0f, 0f, 1f, 0.69f, 1f, 1f, 0f,
                1401f / 1440f, 20f / 31f, 1f, 0f, 0f, 0f, 0f, 1f, 0.68f, 1f, 1f, 0f,
                1402f / 1440f, 20f / 31f, 1f, 0f, 0f, 0f, 0f, 1f, 0.675f, 1f, 1f, 0f};
        long[] inputDimension = {3, 12, 1};
        float[] result = tfPredictor.predict(input, inputDimension);
        for (float r : result) {
            Log.i("Result", "" + r);
        }


    }

    /**
     * Initialize the necessary variables
     */
    public void initialize() {
        tfPredictor = new TFPredictor(MODEL_FILE, INPUT_NODE, OUTPUT_NODE, getAssets());
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
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, pIntent);
    }

    /**
     * Cancel the repeated tasks schedule
     */
    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    /**
     * Visualize the data points of the two ArrayLists as a graph
     *
     * @param predicted ArrayList of the WIFI status predicted with the inference model
     * @param actual    ArrayList of the actual WIFI status
     */
    public void visualize(ArrayList<Float> predicted, ArrayList<Float> actual) {
        LineGraphSeries<DataPoint> seriesp = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> seriesa = new LineGraphSeries<>();
        for (int i = 0; i < 100; i++) {
            DataPoint dpp = new DataPoint(i, predicted.get(i));
            DataPoint dpa = new DataPoint(i, actual.get(i));
            seriesp.appendData(dpp, true, 100);
            seriesa.appendData(dpa, true, 100);
        }

        seriesp.setTitle("Predicted");
        seriesp.setColor(Color.YELLOW);

        seriesa.setTitle("Actual");
        seriesa.setColor(Color.BLUE);

//        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScalable(true);
        graph.addSeries(seriesa);
        graph.addSeries(seriesp);
    }
}


