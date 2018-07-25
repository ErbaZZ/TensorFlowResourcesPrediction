package com.example.student.tensorflowmobiledemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Log;

public class StatusRecorder{

    private Context context;
    private boolean screenStatus;

    private float batteryLevel;
    private boolean batteryIsCharging;
    private boolean batteryIsUSBCharging;
    private boolean batteryIsACCharging;
    private boolean batteryIsWirelessCharging;
    private boolean bluetoothIsEnabled;

    public StatusRecorder(Context c) {
        context = c;
    }

    public void updateStatuses() {
        updateBatteryStatus();
        updateScreenStatus();
        updateBluetoothStatus();
//        updateWIFIStatus();
/*        //int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
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
       // boolean inAirplaneMode = intent.getBooleanExtra("state", false);
        boolean inAirplaneMode = false;
        String allstatus = (String.valueOf(batteryLevel)+" IsCharging = "+String.valueOf(isCharging)+"% WirelessCharging = "+wirelessCharging+" USBCharging = "+String.valueOf(usbCharging)+" ACCharging = "+String.valueOf(acCharging)+" Bluetooth: "+state+" Network Type connected: "+connType+" Network State connected: "+connState+" Wifi Status: "+wifi+" AirplaneMode: "+inAirplaneMode
                +" RX: "+rx+" TX: "+tx+" TotalRX: "+totalrx+" TotalTX: "+totaltx);
        Log.i("Statuses", allstatus);*/
    }

/*    private void updateWIFIStatus() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !StringUtil.isBlank(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
    }*/

    private void updateBluetoothStatus() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) bluetoothIsEnabled = false;
        else if (mBluetoothAdapter.isEnabled()) bluetoothIsEnabled = true;
        else bluetoothIsEnabled = false;
        Log.i("Bluetooth Status", bluetoothIsEnabled + "");
    }

    private void updateBatteryStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / 100f;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        batteryIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        batteryIsUSBCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        batteryIsACCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        batteryIsWirelessCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        Log.i("Battery Status","Level: " + batteryLevel + " isCharging: " + batteryIsCharging + " usbCharging: " + batteryIsUSBCharging + " acCharging: " + batteryIsACCharging + " wirelessCharging: " + batteryIsWirelessCharging);
    }

    private void updateScreenStatus() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        screenStatus = powerManager.isInteractive();
        Log.i("Screen Status", screenStatus + "");
    }

    private int StateToNum(NetworkInfo.State  state){
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
    private int DetailedStateToNum(NetworkInfo.DetailedState state){
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
}
