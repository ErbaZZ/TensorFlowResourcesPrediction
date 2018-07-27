package com.example.student.tensorflowmobiledemo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Status {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "Minute")
    private float minute;

    @ColumnInfo(name = "Date")
    private float date;

    @ColumnInfo(name = "DayOfWeek")
    private float dayOfWeek;

    @ColumnInfo(name = "Is_Charging")
    private float isCharging;

    @ColumnInfo(name = "Chg_USB")
    private float chgUSB;

    @ColumnInfo(name = "Chg_Wireless")
    private float chgWireless;

    @ColumnInfo(name = "Chg_AC")
    private float chgAC;

    @ColumnInfo(name = "WIFI_Status")
    private float wifiStatus;

    @ColumnInfo(name = "Percentage")
    private float percentage;

    @ColumnInfo(name = "Bluetooth_Status")
    private float bluetoothStatus;

    @ColumnInfo(name = "Cellular_Status")
    private float cellularStatus;

    @ColumnInfo(name = "Screen_Status")
    private float screenStatus;

    public float getMinute() {
        return minute;
    }

    public void setMinute(float minute) {
        this.minute = minute;
    }

    public float getDate() {
        return date;
    }

    public void setDate(float date) {
        this.date = date;
    }

    public float getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(float dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public float getIsCharging() {
        return isCharging;
    }

    public void setIsCharging(float isCharging) {
        this.isCharging = isCharging;
    }

    public float getChgUSB() {
        return chgUSB;
    }

    public void setChgUSB(float chgUSB) {
        this.chgUSB = chgUSB;
    }

    public float getChgWireless() {
        return chgWireless;
    }

    public void setChgWireless(float chgWireless) {
        this.chgWireless = chgWireless;
    }

    public float getChgAC() {
        return chgAC;
    }

    public void setChgAC(float chgAC) {
        this.chgAC = chgAC;
    }

    public float getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(float wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getBluetoothStatus() {
        return bluetoothStatus;
    }

    public void setBluetoothStatus(float bluetoothStatus) {
        this.bluetoothStatus = bluetoothStatus;
    }

    public float getCellularStatus() {
        return cellularStatus;
    }

    public void setCellularStatus(float cellularStatus) {
        this.cellularStatus = cellularStatus;
    }

    public float getScreenStatus() {
        return screenStatus;
    }

    public void setScreenStatus(float screenStatus) {
        this.screenStatus = screenStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
