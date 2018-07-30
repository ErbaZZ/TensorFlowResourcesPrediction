package com.example.student.tensorflowmobiledemo;

import android.util.Log;

import java.util.ArrayList;

public class RecordManager {
    private static ArrayList<float[]> statuses;
    private static ArrayList<Float> predicted;
    private static ArrayList<Float> actual;

    public RecordManager() {
        statuses = new ArrayList<float[]>();
        predicted = new ArrayList<Float>();
        actual = new ArrayList<Float>();
    }

    public RecordManager(ArrayList<float[]> r, ArrayList<Float> p, ArrayList<Float> a) {
        statuses = r;
        predicted = p;
        actual = a;
    }

    public static void addRecord(float[] record) {
        statuses.add(record);
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
        if (numCompare == 0) return 1.0f;
        return numCorrect / (float)numCompare;
    }

    /**
     * Compare the actual and predicted array to get confusion matrix
     * @return Each value in the confusion matrix in the order: TP, TN, FP, FM
     */
    public int[] calculateConfusionMatrix() {
        ArrayList<Float> shifted = getShiftedPredicted();
        int n = actual.size();
        if (n == 0) return new int[] {0, 0, 0, 0};
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < n; i++) {
            if (shifted.get(i) == -1f) continue;
            int temp = Math.round(shifted.get(i));
            if (temp == 1) {
                if (actual.get(i) == 1) tp++;
                else fp++;
            }
            else if (temp == 0) {
                if (actual.get(i) == 1) fn++;
                else tn++;
            }
        }

        int[] result = {tp, tn, fp, fn};
        return result;
    }

    public float getPredictedElement(int index) {
        ArrayList<Float> sp = getShiftedPredicted();
        if (index >= sp.size()) index = sp.size() - 1;
        return sp.get(index);
    }

    public float getActualElement(int index) {
        ArrayList<Float> ac = getActual();
        if (index >= ac.size()) index = ac.size() - 1;
        return ac.get(index);
    }

    public static ArrayList<float[]> getStatuses() {
        return statuses;
    }
}
