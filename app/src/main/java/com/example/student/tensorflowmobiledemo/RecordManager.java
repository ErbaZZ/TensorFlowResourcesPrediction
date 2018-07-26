package com.example.student.tensorflowmobiledemo;

import android.util.Log;

import java.util.ArrayList;

public class RecordManager {
    private static ArrayList<float[]> records;
    private static ArrayList<Float> predicted;
    private static ArrayList<Float> actual;

    public RecordManager() {
        records = new ArrayList<float[]>();
        predicted = new ArrayList<Float>();
        actual = new ArrayList<Float>();
    }

    public RecordManager(ArrayList<float[]> r, ArrayList<Float> p, ArrayList<Float> a) {
        records = r;
        predicted = p;
        actual = a;
    }

    public static void addRecord(float[] record) {
        records.add(record);
    }

    public static void addResult(float p, float a) {
        predicted.add(p);
        actual.add(a);
    }

    public ArrayList<Float> getPredicted() {
        return predicted;
    }

    public ArrayList<Float> getShiftedPredicted(int shift) {
        ArrayList<Float> shiftedPredicted = (ArrayList<Float>)predicted.clone();
        for (int i = 0; i < shift; i++) {
            shiftedPredicted.add(0, -1f);
        }
        Log.d("ShiftedPredicted",shiftedPredicted.size() + "");
        return shiftedPredicted;
    }

    public ArrayList<Float> getShiftedPredicted() {
        int shift = 30;
        ArrayList<Float> shiftedPredicted = (ArrayList<Float>)predicted.clone();
        for (int i = 0; i < shift; i++) {
            shiftedPredicted.add(0, -1f);
        }
        return shiftedPredicted;
    }

    public ArrayList<Float> getActual() {
        return actual;
    }

    public float calculateAccuracy() {
        ArrayList<Float> shifted = getShiftedPredicted();
        int n = actual.size();
        if (n == 0) return 1;
        int numCorrect = 0;
        int numCompare = 0;
        for (int i = 0; i < n; i++) {
            if (shifted.get(i) == -1f) continue;
            if (Math.round(shifted.get(i)) == actual.get(i)) numCorrect++;
            numCompare++;
        }
        return numCorrect / (float)numCompare;
    }

    public float getPredictedElement(int index) {
        return getShiftedPredicted().get(index);
    }

    public float getActualElement(int index) {
        return getActual().get(index);
    }
}
