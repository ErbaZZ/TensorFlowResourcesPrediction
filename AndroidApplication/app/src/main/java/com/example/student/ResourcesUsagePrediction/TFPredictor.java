package com.example.student.ResourcesUsagePrediction;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

/**
 * TensorFlow Mobile predictor using pre-trained model to make inference on the result
 */
public class TFPredictor {
    private static String modelFile;
    private static String inputNode;
    private static String outputNode;
    private static TensorFlowInferenceInterface tfInterface;
    private static long[] defaultDimension = {1, 12, 1};
    private AssetManager assets;

    public TFPredictor(String model, String input, String output, AssetManager assets) {
        modelFile = model;
        inputNode = input;
        outputNode = output;
        this.assets = assets;
        tfInterface = new TensorFlowInferenceInterface(assets, modelFile);
    }

    public static float[] predict(float[] input, long[] inputDim) {
        long[] inputDimension = inputDim;
        tfInterface.feed(inputNode, input, inputDimension);
        tfInterface.run(new String[] {outputNode});
        float[] result = new float[(int)inputDimension[0]];
        tfInterface.fetch(outputNode, result);
        return result;
    }
    public static float[] predict(float[] input) {
        long[] inputDimension = defaultDimension;
        tfInterface.feed(inputNode, input, inputDimension);
        tfInterface.run(new String[] {outputNode});
        float[] result = new float[(int)inputDimension[0]];
        tfInterface.fetch(outputNode, result);
        return result;
    }

    public static TensorFlowInferenceInterface getTFInterface() {
        return tfInterface;
    }
}
