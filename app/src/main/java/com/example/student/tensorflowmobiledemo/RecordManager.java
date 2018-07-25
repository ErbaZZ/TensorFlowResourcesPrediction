package com.example.student.tensorflowmobiledemo;

import java.util.ArrayList;

public class RecordManager {
    private static ArrayList<Float> predicted;
    private static ArrayList<Float> actual;

    public RecordManager() {
        predicted = new ArrayList<Float>();
        actual = new ArrayList<Float>();
    }

    public RecordManager(ArrayList<Float> p, ArrayList<Float> a) {
        predicted = p;
        actual = a;
    }

    public static void record(float p, float a) {
        predicted.add(p);
        actual.add(a);
    }

    public ArrayList<Float> getPredicted() {
        return predicted;
    }

    public ArrayList<Float> getShiftedPredicted(int shift) {
        ArrayList<Float> shiftedPredicted = (ArrayList<Float>)predicted.clone();
        for (int i = 0; i < shift; i++) {
            shiftedPredicted.add(0, -1.0f);
        }
        return shiftedPredicted;
    }

    public ArrayList<Float> getShiftedPredicted() {
        int shift = 30;
        ArrayList<Float> shiftedPredicted = (ArrayList<Float>)predicted.clone();
        for (int i = 0; i < shift; i++) {
            shiftedPredicted.add(0, -1.0f);
        }
        return shiftedPredicted;
    }

    public ArrayList<Float> getActual() {
        return actual;
    }

    public float calculateAccuracy() {
        int n = actual.size();
        int numCorrect = 0;
        for (int i = 0; i < n; i++) {
            if (predicted.get(i + 30) == actual.get(i)) numCorrect++;
        }
        return numCorrect / (float)n;
    }
}
