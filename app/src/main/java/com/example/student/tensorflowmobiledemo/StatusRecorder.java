package com.example.student.tensorflowmobiledemo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class StatusRecorder{

    private Context context;                        // Application Context
    private boolean screenIsEnabled;                // Screen ON/OFF status
    private float batteryLevel;                     // Battery level ranges from 0.0 for empty battery to 1.0 for full battery
    private boolean batteryIsCharging;              // Battery charging status
    private boolean batteryIsUSBCharging;           // Battery charging status using USB
    private boolean batteryIsACCharging;            // Battery charging status using AC power
    private boolean batteryIsWirelessCharging;      // Battery charging status using wireless charging
    private boolean bluetoothIsEnabled;             // Bluetooth ON/OFF status
    private boolean wifiIsEnabled;                  // WIFI ON/OFF status
    private float trafficMobileRx;                  // Mobile data received since boot
    private float trafficMobileTx;                  // Mobile data sent since boot
    private float trafficTotalRx;                   // Total data received since boot
    private float trafficTotalTx;                   // Total data sent since boot
    private boolean cellularIsEnabled;              // Cellular ON/OFF status
    private String cellularType;                    // Type of cellular connection
    private boolean airplaneIsEnabled;              // Airplane mode ON/OFF status
    private float timeNormMinute;                   // Normalized minute of day ( minute / 1440 )
    private float timeNormDate;                     // Normalized date of month ( date / 31 )
    private float timeNormDayOfWeek;                // Normalized day of week ( dayOfWeekNum / 7 ) * Sunday = 1, ... Saturday = 7

    public StatusRecorder(Context c) {
        context = c;
    }

    public void updateStatuses() {
        updateBatteryStatus();
        updateScreenStatus();
        updateBluetoothStatus();
        updateWIFIStatus();
        updateTrafficStatus();
        updateCellularStatus();
        updateAirplaneStatus();
        updateTime();
    }

    private void updateTime() {
        Calendar currentTime = Calendar.getInstance();
        timeNormMinute = (currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)) / 1440f;
        timeNormDate = currentTime.get(Calendar.DAY_OF_MONTH) / 31f;
        timeNormDayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK) / 7f;

        Log.d("Time", "Minute: " + timeNormMinute + " Date: " + timeNormDate + " DayOfWeek: " + timeNormDayOfWeek);
    }

    private void updateAirplaneStatus() {
        airplaneIsEnabled = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

        Log.d("Airplane Status", airplaneIsEnabled + "");
    }

    private void updateCellularStatus() {
        ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo == null){
            // No cellular connectivity
            cellularType = "NONE";
            cellularIsEnabled = false;
        } else {
            cellularType = networkInfo.getSubtypeName();
            cellularIsEnabled = cellularStateToBoolean(networkInfo.getState());
        }

        Log.d("Cellular Status", cellularIsEnabled + " " + cellularType);
    }

    private void updateTrafficStatus() {
        trafficMobileRx = TrafficStats.getMobileRxBytes();
        trafficMobileTx =  TrafficStats.getMobileTxBytes();
        trafficTotalRx = TrafficStats.getTotalRxBytes();
        trafficTotalTx =  TrafficStats.getTotalTxBytes();

        Log.d("Traffic status", "Mobile rx: " + trafficMobileRx + " Mobile tx: " + trafficMobileTx + " Total rx: " + trafficTotalRx + " Total tx: " + trafficTotalTx);
    }

    private void updateWIFIStatus() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiIsEnabled = wifiManager.isWifiEnabled();

        Log.d("WIFI Status", wifiIsEnabled + "");
    }

    private void updateBluetoothStatus() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) bluetoothIsEnabled = false;
        else if (mBluetoothAdapter.isEnabled()) bluetoothIsEnabled = true;
        else bluetoothIsEnabled = false;

        Log.d("Bluetooth Status", bluetoothIsEnabled + "");
    }

    private void updateBatteryStatus() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / 100f;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        batteryIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        batteryIsUSBCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        batteryIsACCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        batteryIsWirelessCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        Log.d("Battery Status","Level: " + batteryLevel + " isCharging: " + batteryIsCharging + " usbCharging: " + batteryIsUSBCharging + " acCharging: " + batteryIsACCharging + " wirelessCharging: " + batteryIsWirelessCharging);
    }

    private void updateScreenStatus() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        screenIsEnabled = powerManager.isInteractive();
        Log.d("Screen Status", screenIsEnabled + "");
    }

    private boolean cellularStateToBoolean(NetworkInfo.State state){
        switch (state){
            case CONNECTED:
            case CONNECTING:
                return true;
            case DISCONNECTED:
            case SUSPENDED:
                return false;
            default:
                return false;
        }
    }

    public float[] getCurrentStatuses() {
        updateStatuses();
        float[] statuses = {timeNormMinute, timeNormDate, timeNormDayOfWeek, batteryIsCharging ? 1f : 0f, batteryIsUSBCharging ? 1f : 0f, batteryIsWirelessCharging ? 1f : 0f, batteryIsACCharging ? 1f : 0f, wifiIsEnabled ? 1f : 0f, batteryLevel, bluetoothIsEnabled ? 1f : 0f, cellularIsEnabled ? 1f : 0f, screenIsEnabled ? 1f : 0f};
        return  statuses;
    }

    public float[] getCurrentStatuses(boolean update) {
        if (update == true) updateStatuses();
        float[] statuses = {timeNormMinute, timeNormDate, timeNormDayOfWeek, batteryIsCharging ? 1f : 0f, batteryIsUSBCharging ? 1f : 0f, batteryIsWirelessCharging ? 1f : 0f, batteryIsACCharging ? 1f : 0f, wifiIsEnabled ? 1f : 0f, batteryLevel, bluetoothIsEnabled ? 1f : 0f, cellularIsEnabled ? 1f : 0f, screenIsEnabled ? 1f : 0f};
        return  statuses;
    }

    public float getWIFIStatus() {
        updateWIFIStatus();
        return wifiIsEnabled ? 1f : 0f;
    }
}
