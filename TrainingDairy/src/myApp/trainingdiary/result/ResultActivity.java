package myApp.trainingdiary.result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.util.Log;
import android.widget.*;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import myApp.trainingdiary.R;
import myApp.trainingdiary.HistoryAct.History_detailsv2;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.customview.NumericRightOrderWheelAdapter;
import myApp.trainingdiary.forBD.DBHelper;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import myApp.trainingdiary.forBD.DbFormatter;
import myApp.trainingdiary.forBD.Measure;
import myApp.trainingdiary.forBD.TrainingStat;

/*
 * ��������� ��� ������ ���������� ������� ���������� 
 */

public class ResultActivity extends Activity implements OnClickListener {

    TextView tvnameEx, training_stat_text;
    String strNameEx;
    String strNameTr;
    DBHelper dbHelper;
    final int MENU_DEL_LAST_SET = 1;
    final int MENU_SHOW_LAST_RESULT = 2;

    // forms
    Button btnSet;
    //btnW1p, btnW2p, btnW3p, btnW1m, btnW2m, btnW3m, btnW4p, btnW4m,
    //		btnRepp, btnRepm,
    //EditText editTextW1, editTextW2, editTextW3, editTextW4, editTextRep;
    //
    private WheelView bigNumWheel;
    private WheelView smallNumWheel;
    private WheelView repeatWheel;

    private ArrayWheelAdapter<String> smallNumWheelAdapter;
    private NumericRightOrderWheelAdapter repeatWheelAdapter;
    private NumericRightOrderWheelAdapter bigNumWheelAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm.ss");

    //��� ������������ ������....
    private SoundPool soundPool;
    private int soundClick;
    AudioManager audioManager;
    boolean Soundloaded = false;
    private long ex_id;
    private long tr_id;
    private List<MeasureWheels> measureWheelsList = new ArrayList<MeasureWheels>();
    private AlertDialog undoDialog;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resources = getResources();

        TextView lastResultView = (TextView) findViewById(R.id.last_result_text);
        training_stat_text = (TextView) findViewById(R.id.cur_training_stats);
        dbHelper = DBHelper.getInstance(this);
        ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
        tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.measure_layout);

        List<Measure> measureList = dbHelper.getMeasuresInExercise(ex_id);
        for (Measure measure : measureList) {
            MeasureWheels measureWheels = new MeasureWheels(measure);
            linearLayout.addView(measureWheels.getView());
            measureWheelsList.add(measureWheels);
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundClick = soundPool.load(this, R.raw.click3, 1);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        Button writeButton = (Button) findViewById(R.id.write_button);
        writeButton.setOnClickListener(this);
        Button undoButton = (Button) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(this);

        String last_result_text = resources.getString(R.string.last_result);
        TrainingStat tr_stat = dbHelper.getLastTrainingStatByExerciseInTraining(ex_id, tr_id);
        String last_result_info;
        if (tr_stat != null) {
            last_result_info = tr_stat.getValue()
                    + "(" + sdf.format(tr_stat.getTrainingDate()) + ")";
        } else {
            last_result_info = getString(R.string.last_training_empty);
        }
        lastResultView.setText(String.format(last_result_text, last_result_info));
        if (tr_stat != null)
            setLastTrainingStatOnWheels(tr_stat);

        createUndoDialog();
        printCurrentTrainingProgress();
    }

    private void setLastTrainingStatOnWheels(TrainingStat tr_stat) {
        String value = tr_stat.getValue();

        List<String> measureValues = DbFormatter.toMeasureValues(value);
        for (int i = 0; i < measureWheelsList.size(); i++) {
            MeasureWheels measureWheels = measureWheelsList.get(i);
            String measureValue = measureValues.get(i);
            measureWheels.setValue(measureValue);
        }
    }

    private void createUndoDialog() {

        String title = getResources().getString(R.string.dialog_del_approach_title);
        String btnRename = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.delete_button);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);

        adb.setPositiveButton(btnDel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbHelper.deleteLastTrainingStat(ex_id, tr_id);
                printCurrentTrainingProgress();
                Toast.makeText(ResultActivity.this, R.string.deleted,
                        Toast.LENGTH_SHORT).show();
            }
        });
        adb.setNegativeButton(btnRename, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                undoDialog.cancel();
            }
        });

        undoDialog = adb.create();
    }

    private void printCurrentTrainingProgress() {
        List<TrainingStat> tr_stats = dbHelper.getTrainingStatForLastPeriod(ex_id, Consts.TWO_HOURS);
        String stats = formTrainingStats(tr_stats);
        training_stat_text.setText(stats);
    }

    private String formTrainingStats(List<TrainingStat> stats) {
        String result = "";
        for (TrainingStat stat : stats) {
            result += stat.getValue() + "; ";
        }
        result += "\n" + getString(R.string.summ_approach) + stats.size();
        return result;
    }

    private class MeasureWheel {
        public WheelView wheelView;
        public WheelViewAdapter wheelAdapter;
    }

    private class MeasureWheels {
        private Measure measure;
        private List<MeasureWheel> measureWheelList;
        private LinearLayout mainLayout;

        private MeasureWheels(Measure measure) {
            this.measure = measure;
            measureWheelList = new ArrayList<MeasureWheel>();
            mainLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.wheel_column, null);
            LinearLayout wheelLayout = (LinearLayout) mainLayout.findViewById(R.id.wheel_layout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1F);
            wheelLayout.setLayoutParams(params);
            TextView textView = (TextView) mainLayout.findViewById(R.id.label);
            textView.setText(measure.getName() + ":");
            switch (measure.getType()) {
                case 0:
                    MeasureWheel measureWheel = createNumWheel(measure.getMax(), measure.getStep());
                    wheelLayout.addView(measureWheel.wheelView);
                    measureWheelList.add(measureWheel);
                    if (measure.getStep() < 1) {
                        MeasureWheel measureTailWheel = createTailWheel(measure.getStep());
                        measureWheelList.add(measureTailWheel);
                        wheelLayout.addView(measureTailWheel.wheelView);
                    }
                    break;
                case 1:
                    for (int i = measure.getMax(); i >= measure.getStep().intValue(); i--) {
                        MeasureWheel measureTimeWheel = null;
                        switch (i) {
                            case 0:
                                measureTimeWheel = createNumWheel(1000, 1d);
                                break;
                            case 1:
                                measureTimeWheel = createNumWheel(60, 1d);
                                break;
                            case 2:
                                measureTimeWheel = createNumWheel(60, 1d);
                                break;
                            case 3:
                                measureTimeWheel = createNumWheel(24, 1d);
                                break;

                        }
                        measureWheelList.add(measureTimeWheel);
                        wheelLayout.addView(measureTimeWheel.wheelView);
                    }
                    break;
            }
            float weight = 1F / measureWheelList.size();
            LinearLayout.LayoutParams main_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, weight);
            main_params.setMargins(10, 0, 10, 0);
            mainLayout.setLayoutParams(main_params);
        }

        private MeasureWheel createTailWheel(Double step) {
            MeasureWheel measureWheel = new MeasureWheel();
            List<String> tails = new ArrayList<String>();
            Double _step = 0d;
            while (_step < 1) {
                tails.add(String.valueOf(_step).substring(1));
                _step += step;
            }
            ArrayWheelAdapter<String> wheelAdapter = new ArrayWheelAdapter<String>(ResultActivity.this,
                    tails.toArray(new String[tails.size()]));

            WheelView wheelView = (WheelView) getLayoutInflater().inflate(R.layout.wheel, null);
            wheelView.setViewAdapter(wheelAdapter);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2F);
            wheelView.setLayoutParams(params);
            measureWheel.wheelAdapter = wheelAdapter;
            measureWheel.wheelView = wheelView;
            wheelView.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    playClick();
                }
            });
            return measureWheel;
        }

        private MeasureWheel createNumWheel(Integer max, Double step) {
            MeasureWheel measureWheel = new MeasureWheel();
            int length = String.valueOf(max).length();
            NumericRightOrderWheelAdapter wheelAdapter = new NumericRightOrderWheelAdapter(ResultActivity.this, 0, max,
                    "%" + length + "d");

            WheelView wheelView = (WheelView) getLayoutInflater().inflate(R.layout.wheel, null);
            wheelView.setViewAdapter(wheelAdapter);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1F);
            wheelView.setLayoutParams(params);
            wheelView.setCyclic(true);
            wheelView.setCurrentItem(max);
            measureWheel.wheelAdapter = wheelAdapter;
            measureWheel.wheelView = wheelView;
            wheelView.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    playClick();
                }
            });
            return measureWheel;
        }

        private Measure getMeasure() {
            return measure;
        }

        private void setMeasure(Measure measure) {
            this.measure = measure;
        }

        public View getView() {
            return mainLayout;
        }

        private List<MeasureWheel> getMeasureWheelList() {
            return measureWheelList;
        }


        public String getStringValue() {
            String result = "";

            switch (measure.getType()) {
                case 0:
                    for (MeasureWheel measureWheel : measureWheelList) {
                        if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                            result += String.valueOf(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getItem(measureWheel.wheelView
                                    .getCurrentItem()));
                        } else if (measureWheel.wheelAdapter instanceof ArrayWheelAdapter) {
                            result += String.valueOf(((ArrayWheelAdapter) measureWheel.wheelAdapter).getItemText(measureWheel.wheelView
                                    .getCurrentItem()));
                        }
                    }
                    break;
                case 1:

                    break;
            }
            return result;
        }

        private int getIndexByValue(NumericRightOrderWheelAdapter adapter, int value) {
            for (int i = 0; i < adapter.getItemsCount(); i++) {
                if (adapter.getItem(i) == value) {
                    return i;
                }
            }
            return 0;
        }

        private int getIndexByValue(WheelViewAdapter wheelAdapter, String intValue) {
//            for (int i = 0; i < wheelAdapter.getItemsCount(); i++) {
//                if (wheelAdapter.getItem(i) == value) {
//                    return i;
//                }
//            }
            return 0;
        }

        public void setValue(String measureValue) {
            switch (measure.getType()) {
                case 0:
                    for (MeasureWheel measureWheel : measureWheelList) {
                        if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
//                            int intValue = new Double(measureValue).intValue();
//                            measureWheel.wheelView.setCurrentItem(
//                                    getIndexByValue((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter, intValue));

                        } else if (measureWheel.wheelAdapter instanceof ArrayWheelAdapter) {
//                            String intValue = measureValue.substring(measureValue.indexOf("."));
//                            measureWheel.wheelView.setCurrentItem(getIndexByValue(measureWheel.wheelAdapter, intValue));
                        }
                    }
                    break;
                case 1:

                    break;
            }
        }


    }

    private void playClick() {

        //�������� ��������� ���������
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        soundPool.play(soundClick, volume, volume, 1, 0, 1f);

    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.write_button:
                writeToDB();
                printCurrentTrainingProgress();
                break;
            case R.id.undo_button:
                String message = getResources().getString(R.string.dialog_del_approach_msg);
                TrainingStat stat = dbHelper.getLastTrainingStatByExerciseInTraining(ex_id, tr_id);
                if (stat != null) {
                    String value = stat.getValue();
                    message = String.format(message, value);
                    undoDialog.setMessage(message);
                    undoDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private void writeToDB() {
        String result = "";
        for (MeasureWheels measureWheels : measureWheelsList) {
            result += measureWheels.getStringValue() + "x";
        }
        result = result.substring(0, result.length() - 1);
        if (result != null && !result.isEmpty())
            dbHelper.insertTrainingStat(ex_id, tr_id, new Date().getTime(), result);
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//    }


    private void showlastEx() {
        Intent History_detailsv2 = new Intent(this, History_detailsv2.class);
        History_detailsv2.putExtra("AllEx", false);
        History_detailsv2.putExtra("nameEx", strNameEx);
        startActivity(History_detailsv2);
    }

}
