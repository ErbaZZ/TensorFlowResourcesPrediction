package com.example.student.tensorflowmobiledemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "KerasModelMin.pb";
    private static final String INPUT_NODE = "lstm_40_input_1";
    private static final String OUTPUT_NODE = "output_node0";
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
        this.initialize();
//        Iterator<Operation> operations = tfPredictor.getTFInterface().graph().operations();
//
//        while (operations.hasNext()) {
//            Operation op = operations.next();
//            Log.i("Operation", op.toString());
//        }
        float[] input = {
                1400f/1440f,20f/31f,1f,0f,0f,0f,0f,1f,0.69f,1f,1f,0f,
                1401f/1440f,20f/31f,1f,0f,0f,0f,0f,1f,0.68f,1f,1f,0f,
                1402f/1440f,20f/31f,1f,0f,0f,0f,0f,1f,0.675f,1f,1f,0f};
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
    }

    /**
     * Visualize the data points of the two ArrayLists as a graph
     * @param predicted ArrayList of the WIFI status predicted with the inference model
     * @param actual ArrayList of the actual WIFI status
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


