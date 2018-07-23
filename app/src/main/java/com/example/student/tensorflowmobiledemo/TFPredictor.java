package com.example.student.tensorflowmobiledemo;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class TFPredictor {
    private static String modelFile;
    private static String inputNode;
    private static String outputNode;
    private static long[] inputDimension;
    private static TensorFlowInferenceInterface tfInterface;
    private AssetManager assets;

    public TFPredictor(String model, String input, String output, long[] inputDim, AssetManager assets) {
        this.modelFile = model;
        this.inputNode = input;
        this.outputNode = output;
        this.inputDimension = inputDim;
        this.assets = assets;
        tfInterface = new TensorFlowInferenceInterface(assets, modelFile);
    }

    public float[] predict(float[] input) {
        tfInterface.feed(inputNode, input, inputDimension);
        tfInterface.run(new String[] {outputNode});
        float[] result = new float[(int)inputDimension[0]];
        tfInterface.fetch(outputNode, result);
        return result;
    }
}
