package myApp.trainingdiary.result;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.customview.NumericRightOrderWheelAdapter;
import myApp.trainingdiary.customview.StringRightOrderWheelAdapter;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;
import myApp.trainingdiary.utils.TrainingDurationManger;

/*
 * ��������� ��� ������ ���������� ������� ���������� 
 */

public class ResultActivity extends ActionBarActivity implements OnClickListener {


    private DBHelper dbHelper;
    final int MENU_DEL_LAST_SET = 1;
    final int MENU_SHOW_LAST_RESULT = 2;

    private final int ID_STOP = 1;
    private final int ID_START = 2;
    private final int ID_RESET = 3;

    // forms
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    private SoundPool soundPool;
    private int soundClick;
    private AudioManager audioManager;
    private long ex_id;
    private long tr_id;
    private List<MeasureWheels> measureWheelsList = new ArrayList<MeasureWheels>();
    private AlertDialog undoDialog;
    private Resources resources;
    private TextView training_stat_text;

    private Chronometer mChrono;
    private QuickAction exerciseActionTools;

    private long elapsedTime = 0;
    private String currentTime = "";
    private long startTime = SystemClock.elapsedRealtime();
    private boolean resume = false;
    private boolean reset = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_result);
        resources = getResources();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        training_stat_text = (TextView) findViewById(R.id.cur_training_stats);

        mChrono = (Chronometer) findViewById(R.id.mChrono);
        setChronoTickListener();

        createExcerciseTools();

        dbHelper = dbHelper.getInstance(this);
        ex_id = getIntent().getExtras().getLong(Const.EXERCISE_ID);
        tr_id = getIntent().getExtras().getLong(Const.TRAINING_ID);
        Log.d(Const.LOG_TAG, "ex_id:" + ex_id + " tr_id:" + tr_id);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.measure_layout);

        List<Measure> measureList = dbHelper.READ.getMeasuresInExercise(ex_id);
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
        ImageButton undoButton = (ImageButton) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(this);
        Button historyButton = (Button) findViewById(R.id.history_result_button);
        historyButton.setOnClickListener(this);

        TrainingSet tr_stat = dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id);
        if (tr_stat != null)
            setLastTrainingStatOnWheels(tr_stat);
        createUndoDialog();
        setTitle(dbHelper.READ.getExerciseNameById(ex_id));
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
        TrainingSet last_set = dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp_id);
        if (last_set != null) {
            chronometerReset();
            chronometerStart();
        }
        printCurrentTrainingProgress();
    }

    private void setLastTrainingStatOnWheels(TrainingSet tr_stat) {
        List<TrainingSetValue> values = tr_stat.getValues();
        Log.d(Const.LOG_TAG, "setLastTrainingStatOnWheels:" + values);
        for (int i = 0; i < measureWheelsList.size(); i++) {
            MeasureWheels measureWheels = measureWheelsList.get(i);
            TrainingSetValue measureValue = values.get(i);
            measureWheels.setValue(measureValue.getValue());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actsettings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentStat = new Intent(this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return true;
    }

    private void createUndoDialog() {
        String title = getResources().getString(R.string.dialog_del_approach_title);
        String cancelButton = getResources().getString(R.string.cancel_button);
        String deleteButton = getResources().getString(R.string.delete_button);

        undoDialog = DialogProvider.createSimpleDialog(this, title, null, deleteButton, cancelButton, new DialogProvider.SimpleDialogClickListener() {
            @Override
            public void onPositiveClick() {
                //TODO: иногда не пашет
                Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
                int deleted = dbHelper.WRITE.deleteLastTrainingSetInCurrentTrainingStamp(ex_id);
                if (deleted > 0) {
                    printCurrentTrainingProgress();
                    Toast.makeText(ResultActivity.this, R.string.deleted,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultActivity.this, R.string.nothing_to_deleted,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeClick() {
                undoDialog.cancel();
            }
        });
    }

    private void printCurrentTrainingProgress() {
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
        List<TrainingSet> tr_stats = dbHelper.READ.getTrainingSetListInTrainingStampByExercise(ex_id, tr_stamp_id);
        Log.d(Const.LOG_TAG, "tr_stats:" + tr_stats);
        String stats = formTrainingStats(tr_stats);
        training_stat_text.setText(stats);
    }

    private String formTrainingStats(List<TrainingSet> sets) {
        String result = "";
        for (TrainingSet set : sets) {
            result = MeasureFormatter.valueFormat(set) + "; " + result;
        }
        result += "\n" + getString(R.string.sum_approach) + " " + sets.size();
        return result;
    }

    private void playClick() {
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("disable_sound", false)) {
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            soundPool.play(soundClick, volume, volume, 1, 0, 1f);
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.write_button:
                writeToDB();
                printCurrentTrainingProgress();
                chronometerReset();
                chronometerStart();
                break;
            case R.id.history_result_button:
                openHistoryDetailActivity(ex_id);
                break;
            case R.id.undo_button:
                String message = getResources().getString(R.string.dialog_del_approach_msg);
                TrainingSet set = dbHelper.READ.getLastTrainingSetByExerciseInLastOpenTrainingStamp(ex_id, tr_id);
                Log.d(Const.LOG_TAG, "undo TrainingStamp:" + dbHelper.READ.getLastTrainingStamp());
                Log.d(Const.LOG_TAG, "undo ex_id:" + ex_id + " tr_id:" + tr_id);
                Log.d(Const.LOG_TAG, "undo set:" + set);
                if (set != null) {
                    String value = MeasureFormatter.valueFormat(set);
                    Log.d(Const.LOG_TAG, "undo value:" + value);
                    message = String.format(message, value);
                    undoDialog.setMessage(message);
                    undoDialog.show();
                } else {
                    Toast.makeText(ResultActivity.this, R.string.nothing_to_deleted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    protected void openHistoryDetailActivity(long ex_id) {
        Intent intentOpenAct = new Intent(this, HistoryDetailActivity.class);
        intentOpenAct.putExtra(Const.EXERCISE_ID, ex_id);
        intentOpenAct.putExtra(Const.HISTORY_TYPE, Const.EXERCISE_TYPE);
        startActivity(intentOpenAct);
    }

    private void writeToDB() {
        Long training_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
        List<TrainingSetValue> values = getTrainingSetValues();
        TrainingSet trainingSet = new TrainingSet(null, training_stamp_id, new Date(), ex_id, tr_id, values);
        dbHelper.EM.persist(trainingSet);
    }

    private List<TrainingSetValue> getTrainingSetValues() {
        List<TrainingSetValue> trainingSetValues = new ArrayList<TrainingSetValue>();
        long i = 0;
        for (MeasureWheels measureWheels : measureWheelsList) {
            TrainingSetValue trainingSetValue = new TrainingSetValue(null, null,
                    MeasureFormatter.getDoubleValue(measureWheels.getStringValue(), measureWheels.getMeasure()), i);
            trainingSetValues.add(trainingSetValue);
            i++;
        }
        return trainingSetValues;
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
                case Numeric:
                    MeasureWheel measureWheel = createNumWheel(measure.getMax(), measure.getStep(), false);
                    wheelLayout.addView(measureWheel.wheelView);
                    measureWheelList.add(measureWheel);
                    if (measure.getStep() < 1) {
                        MeasureWheel measureTailWheel = createTailWheel(measure.getStep());
                        measureWheelList.add(measureTailWheel);
                        wheelLayout.addView(measureTailWheel.wheelView);
                    }
                    break;
                case Temporal:
                    for (int i = measure.getMax(); i >= measure.getStep().intValue(); i--) {
                        MeasureWheel measureTimeWheel = null;
                        switch (i) {
                            case 0:
                                measureTimeWheel = createNumWheel(999, 1d, true);
                                break;
                            case 1:
                                measureTimeWheel = createNumWheel(59, 1d, true);
                                break;
                            case 2:
                                measureTimeWheel = createNumWheel(59, 1d, true);
                                break;
                            case 3:
                                measureTimeWheel = createNumWheel(23, 1d, true);
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
            BigDecimal _step = BigDecimal.valueOf(0d);
            Log.d(Const.LOG_TAG, "step: " + step);
            while (_step.doubleValue() < 1) {
                tails.add(String.valueOf(_step).substring(1));
                Log.d(Const.LOG_TAG, "_step: " + _step);

                _step = _step.add(BigDecimal.valueOf(step));

            }
            ArrayWheelAdapter<String> wheelAdapter = new StringRightOrderWheelAdapter<String>(ResultActivity.this,
                    tails.toArray(new String[tails.size()]));

            WheelView wheelView = (WheelView) getLayoutInflater().inflate(R.layout.wheel, null);
            wheelView.setViewAdapter(wheelAdapter);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2F);
            wheelView.setLayoutParams(params);
            measureWheel.wheelAdapter = wheelAdapter;
            measureWheel.wheelView = wheelView;
            wheelView.setCurrentItem(tails.size() - 1);
            wheelView.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    playClick();
                }
            });
            return measureWheel;
        }

        private MeasureWheel createNumWheel(Integer max, Double step, boolean withZeros) {
            MeasureWheel measureWheel = new MeasureWheel();
            int length = String.valueOf(max).length();
            String zero = (withZeros) ? "0" : "";
            NumericRightOrderWheelAdapter wheelAdapter = new NumericRightOrderWheelAdapter(ResultActivity.this, 0, max,
                    "%" + zero + length + "d");

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
                case Numeric:
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
                case Temporal:
                    for (MeasureWheel measureWheel : measureWheelList) {
                        if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                            result += ":" + String.valueOf(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getItem(measureWheel.wheelView
                                    .getCurrentItem()));
                        }
                    }
                    result = result.substring(1);
                    break;
            }
            return result;
        }


        public void setValue(Double measureValue) {
            switch (measure.getType()) {
                case Numeric:
                    for (MeasureWheel measureWheel : measureWheelList) {
                        if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                            int intValue = measureValue.intValue();
                            Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                            measureWheel.wheelView.setCurrentItem(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                        } else if (measureWheel.wheelAdapter instanceof StringRightOrderWheelAdapter) {
                            String intValue = measureValue.toString().substring(measureValue.toString().indexOf("."));
                            Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                            measureWheel.wheelView.setCurrentItem(((StringRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                        }
                    }
                    break;
                case Temporal:
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(measureValue.longValue()));
                    int i = 0;
                    for (MeasureWheel measureWheel : measureWheelList) {
                        if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                            int intValue = (i == 0) ? c.get(Calendar.MINUTE) : c.get(Calendar.SECOND);
                            Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                            measureWheel.wheelView.setCurrentItem(
                                    ((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                            i++;
                        }
                    }
                    break;
            }
        }
    }


    private void createExcerciseTools() {

        ActionItem startItem = new ActionItem(ID_START,
                getResources().getString(R.string.timerStart),
                getResources().getDrawable(R.drawable.icon_play));
        ActionItem stopItem = new ActionItem(ID_STOP,
                getResources().getString(R.string.timerStop),
                getResources().getDrawable(R.drawable.icon_stop));
        ActionItem resetItem = new ActionItem(ID_RESET,
                getResources().getString(R.string.timerReset),
                getResources().getDrawable(R.drawable.icon_replay));

        exerciseActionTools = new QuickAction(this);
        exerciseActionTools.addActionItem(startItem);
        exerciseActionTools.addActionItem(stopItem);
        exerciseActionTools.addActionItem(resetItem);

        // setup the action item click listener
        exerciseActionTools
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction quickAction, int pos,
                                            int actionId) {
                        //ActionItem actionItem = quickAction.getActionItem(pos);
                        switch (actionId) {
                            case ID_START: {
                                chronometerStart();
                                break;
                            }
                            case ID_STOP:
                                chronometerStop();
                                break;
                            case ID_RESET:
                                chronometerReset();
                                break;
                        }
                    }
                });
    }

    private void chronometerStart() {
        reset = false;
        if (!resume) {
            mChrono.setBase(SystemClock.elapsedRealtime());
            mChrono.start();
        } else {
            mChrono.start();
        }

    }

    private void chronometerStop() {
        mChrono.stop();
        if (reset) {
            mChrono.setText("00:00");
            resume = false;
        } else {
            mChrono.setText(currentTime);
            resume = true;
        }
    }

    private void chronometerReset() {
        mChrono.stop();
        mChrono.setText("00:00");
        resume = false;
        reset = true;
    }

    private void setChronoTickListener() {
        mChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                if (!resume) {
                    long minutes = ((SystemClock.elapsedRealtime() - mChrono.getBase()) / 1000) / 60;
                    long seconds = ((SystemClock.elapsedRealtime() - mChrono.getBase()) / 1000) % 60;
                    String min = "";
                    String sec = "";
                    if (minutes < 10)
                        min = "0" + minutes;
                    else
                        min = "" + minutes;
                    if (seconds < 10)
                        sec = "0" + seconds;
                    else
                        sec = "" + seconds;
                    currentTime = min + ":" + sec;
                    arg0.setText(currentTime);
                    elapsedTime = SystemClock.elapsedRealtime();
                } else {
                    long minutes = ((elapsedTime - mChrono.getBase()) / 1000) / 60;
                    long seconds = ((elapsedTime - mChrono.getBase()) / 1000) % 60;
                    String min = "";
                    String sec = "";
                    if (minutes < 10)
                        min = "0" + minutes;
                    else
                        min = "" + minutes;
                    if (seconds < 10)
                        sec = "0" + seconds;
                    else
                        sec = "" + seconds;
                    currentTime = min + ":" + sec;
                    arg0.setText(currentTime);
                    elapsedTime = elapsedTime + 1000;
                }
            }
        });
    }

    public void onClickTimer(View view) {
        exerciseActionTools.show(view);
    }
}
