package com.example.student.tensorflowmobiledemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "KerasModelMin.pb";
    private static final String INPUT_NODE = "lstm_40_input_1";
    private static final String OUTPUT_NODE = "output_node0";

    private TFPredictor tfPredictor;
    private RecordManager recordManager;
    private TextView tvPredicted;
    private TextView tvActual;
    private TextView tvAccuracy;
    private GraphView graph;
    private LineGraphSeries<DataPoint> seriesPredicted;
    private LineGraphSeries<DataPoint> seriesActual;

    private int pointCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initialize();

//        Iterator<Operation> operations = tfPredictor.getTFInterface().graph().operations();
//
//        while (operations.hasNext()) {
//            Operation op = operations.next();
//            Log.i("Operation", op.toString());
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setAccuracyText(recordManager.calculateAccuracy());
        updateText();
        updateGraph(recordManager.getShiftedPredicted(), recordManager.getActual());
    }

    private void updateText() {
        float currentPredicted = recordManager.getPredictedElement(pointCounter);
        float currentActual = recordManager.getActualElement(pointCounter);

        if (Math.round(currentPredicted) == 1) tvPredicted.setText("ON");
        else if (Math.round(currentPredicted) == 0) tvPredicted.setText("OFF");
        if (currentActual == 1f) tvActual.setText("ON");
        else if (currentActual == 0f) tvActual.setText("OFF");
    }

    /**
     * Initialize the necessary variables
     */
    public void initialize() {
        tvPredicted = findViewById(R.id.tvPredicted);
        tvActual = findViewById(R.id.tvCurrent);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        graph = findViewById(R.id.graph);

        tfPredictor = new TFPredictor(MODEL_FILE, INPUT_NODE, OUTPUT_NODE, getAssets());
        ArrayList<Float> predicted = new ArrayList<Float>();
        ArrayList<Float> actual = new ArrayList<Float>();
        for (int i = 0; i < 30; i++) {
            predicted.add(Float.valueOf(Math.round(Math.random())));
            actual.add(Float.valueOf(Math.round(Math.random())));
        }
        recordManager = new RecordManager(new ArrayList<float[]>(), predicted, actual);
        cancelAlarm();
        scheduleAlarm();
        visualizeGraph(recordManager.getShiftedPredicted(), recordManager.getActual());
    }

    /**
     * Start the schedule to run repeated tasks every interval period
     */
    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60;   // 1 minute interval
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pIntent);
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
     * @param shiftedPredicted ArrayList of the WIFI status predicted with the inference model shifted to match the actual time
     * @param actual    ArrayList of the actual WIFI status
     */
    private void visualizeGraph(ArrayList<Float> shiftedPredicted, ArrayList<Float> actual) {
        seriesPredicted = new LineGraphSeries<>();
        seriesActual = new LineGraphSeries<>();
        updateGraph(shiftedPredicted, actual);

        seriesPredicted.setTitle("Predicted");
        seriesPredicted.setColor(Color.YELLOW);

        seriesActual.setTitle("Actual");
        seriesActual.setColor(Color.BLUE);
        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.addSeries(seriesActual);
        graph.addSeries(seriesPredicted);
    }

    private void updateGraph(ArrayList<Float> predicted, ArrayList<Float> actual) {
        for (int i = pointCounter; i < actual.size(); i++) {
            Log.d("Graph", "Point Counter: " + pointCounter + " Predicted: " + predicted.get(i));
            if (predicted.get(i) != -1f) {
                Log.d("Predicted added", predicted.get(i) + "");
                DataPoint dpp = new DataPoint(pointCounter, Math.round(predicted.get(i)));
                seriesPredicted.appendData(dpp, false, 50);
            }
            DataPoint dpa = new DataPoint(pointCounter, actual.get(i));
            seriesActual.appendData(dpa, false, 50);
            pointCounter++;
        }

    }

    public void setAccuracyText(float percentage) {
        tvAccuracy.setText(percentage * 100 + "%");
    }
}


