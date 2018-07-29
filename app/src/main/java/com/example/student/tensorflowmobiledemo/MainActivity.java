package com.example.student.tensorflowmobiledemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.tensorflow.Operation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private AppDatabase db;
    private StatusDao statusDao;
    private RecordDao recordDao;

    private int pointCounter = 0;
    private int importStatusSize;
    private int importRecordSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the trigger receiver to get the trigger from background service
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(triggerReceiver,
                        new IntentFilter("trigger"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the trigger as the Activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(triggerReceiver);
        super.onPause();
    }

    @Override
    protected  void onStop() {
        super.onStop();
        //saveDatabase();
    }


    /**
     * Receives trigger from the background service to update the information on the screen and reschedule the AlarmReceiver
     */
    private BroadcastReceiver triggerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scheduleAlarm();
            setAccuracyText(recordManager.calculateAccuracy());
            updateText();
            updateGraph(recordManager.getShiftedPredicted(), recordManager.getActual());
            Log.d("DB size", statusDao.getAll().size() + "");
        }
    };

    /**
     * Update the predicted WIFI status and current WIFI status TextViews
     */
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
    private void initialize() {
        tvPredicted = findViewById(R.id.tvPredicted);
        tvActual = findViewById(R.id.tvCurrent);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        graph = findViewById(R.id.graph);
        tfPredictor = new TFPredictor(MODEL_FILE, INPUT_NODE, OUTPUT_NODE, getAssets());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);               // Prevent the screen from turning off
        cancelAlarm();
        scheduleAlarm();
        //loadDatabase();

        // Get the values for the first time
        StatusRecorder statusRecorder = new StatusRecorder(this.getApplicationContext());
        statusRecorder.updateStatuses();
        float[] statuses = statusRecorder.getCurrentStatuses();
        float firstPredicted = TFPredictor.predict(statuses)[0];
        float firstActual = statusRecorder.getWIFIStatus();
        Log.d("Result", "Predicted: " + firstPredicted + ", Actual: " + firstActual);
        recordManager.addResult(firstPredicted, firstActual);
        recordManager.addRecord(statuses);
        updateText();
        setAccuracyText(recordManager.calculateAccuracy());
        visualizeGraph(recordManager.getShiftedPredicted(), recordManager.getActual());
    }

    private void loadDatabase() {
        // Fetch past data from Room database
        db = Room.databaseBuilder(this, AppDatabase.class, "status-record").allowMainThreadQueries().build();

        statusDao = db.statusDao();
        recordDao = db.recordDao();

        List<Record> pastRecordList = recordDao.getAll();
        List<Status> pastStatusList = statusDao.getAll();
        importStatusSize = pastStatusList.size();
        importRecordSize = pastRecordList.size();

        ArrayList<Float> pastPredicted = new ArrayList<Float>();
        ArrayList<Float> pastActual = new ArrayList<Float>();
        for (Record r : pastRecordList) {
            pastPredicted.add(r.getPredicted());
            pastActual.add(r.getActual());
        }

        ArrayList<float[]> pastStatuses = new ArrayList<float[]>();
        for (Status s : pastStatusList) {
            pastStatuses.add(s.toFloatArray());
        }

        recordManager = new RecordManager(pastStatuses, pastPredicted, pastActual);

    }

    private void saveDatabase() {
        ArrayList<Float> predicted = recordManager.getPredicted();
        ArrayList<Float> actual = recordManager.getActual();
        ArrayList<float[]> statuses = recordManager.getStatuses();

        ArrayList<Record> savedRecords = new ArrayList<Record>();
        ArrayList<Status> savedStatuses = new ArrayList<Status>();

        for (int i = importRecordSize; i < actual.size(); i++) {
            savedRecords.add(new Record(predicted.get(i), actual.get(i)));
        }

        for (int i = importStatusSize; i < statuses.size(); i++) {
            savedStatuses.add(new Status(statuses.get(i)));
        }

        Record[] recordArray = (Record[]) savedRecords.toArray();
        Status[] statusArray = (Status[]) savedStatuses.toArray();

        recordDao.insertAll(recordArray);
        statusDao.insertAll(statusArray);
    }

    /**
     * Start the schedule to run repeated tasks every interval period
     */
    private void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60;   // 1 minute interval
        //alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, interval, pIntent);
        alarm.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pIntent);
    }

    /**
     * Cancel the repeated tasks schedule
     */
    private void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    /**
     * Visualize the data points of the two ArrayLists as a graph
     *
     * @param shiftedPredicted ArrayList of the WIFI status predicted with the inference model shifted to match the actual time
     * @param actual           ArrayList of the actual WIFI status
     */
    private void visualizeGraph(ArrayList<Float> shiftedPredicted, ArrayList<Float> actual) {
        seriesPredicted = new LineGraphSeries<>();
        seriesActual = new LineGraphSeries<>();

        seriesPredicted.setTitle("Predicted");
        seriesPredicted.setColor(Color.YELLOW);

        seriesActual.setTitle("Actual");
        seriesActual.setColor(Color.BLUE);
        // set manual bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-0.25);
        graph.getViewport().setMaxY(1.25);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(50);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        updateGraph(shiftedPredicted, actual);

        graph.addSeries(seriesActual);
        graph.addSeries(seriesPredicted);
        graph.getLegendRenderer().setVisible(true);
    }

    /**
     * Add new data points from the ArrayLists to the series in the graph
     * @param predicted ArrayList of the predicted WIFI status values (Shifted to the same time with actual)
     * @param actual ArrayList of the actual WIFI status values
     */
    private void updateGraph(ArrayList<Float> predicted, ArrayList<Float> actual) {
        for (int i = pointCounter; i < actual.size(); i++) {
            Log.d("Graph", "Point Counter: " + pointCounter + " Predicted: " + predicted.get(i));
            if (predicted.get(i) != -1f) {
                Log.d("Predicted added", predicted.get(i) + "");
                DataPoint dpp = new DataPoint(pointCounter, Math.round(predicted.get(i)));
                seriesPredicted.appendData(dpp, true, pointCounter + 5);
            }
            DataPoint dpa = new DataPoint(pointCounter, actual.get(i));
            seriesActual.appendData(dpa, true, pointCounter + 5);
            pointCounter++;
        }

    }

    /**
     * Set the text of tvAccuracy text view using accuracy value
     * @param percentage Percentage of the prediction accuracy
     */
    private void setAccuracyText(float percentage) {
        tvAccuracy.setText(percentage * 100 + "%");
    }

    /**
     * Print all the TensorFlow operations of the loaded model into Logcat
     */
    private void printTFOperations() {
        Iterator<Operation> operations = tfPredictor.getTFInterface().graph().operations();

        while (operations.hasNext()) {
            Operation op = operations.next();
            Log.i("Operation", op.toString());
        }
    }

}


