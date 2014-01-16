package myApp.trainingdiary.result;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.viewpagerindicator.TitlePageIndicator;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.db.DBHelper;
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

    private AlertDialog undoDialog;
    private Resources resources;

    private Chronometer mChrono;
    private QuickAction exerciseActionTools;

    private long elapsedTime = 0;
    private String currentTime = "";
    private long startTime = SystemClock.elapsedRealtime();
    private boolean resume = false;
    private boolean reset = false;
    private ResultFragmentAdapter mAdapter;
    private ResultFragment curResultFragment;
    private Map<Long, WheelFragment> wheelMap = new HashMap<Long, WheelFragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("keep_screen_on", false))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_result);
        resources = getResources();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mChrono = (Chronometer) findViewById(R.id.mChrono);
        setChronoTickListener();

        createExcerciseTools();
        dbHelper = dbHelper.getInstance(this);
        ex_id = getIntent().getExtras().getLong(Const.EXERCISE_ID);
        tr_id = getIntent().getExtras().getLong(Const.TRAINING_ID);
        Log.d(Const.LOG_TAG, "ex_id:" + ex_id + " tr_id:" + tr_id);

        mAdapter = new ResultFragmentAdapter(getSupportFragmentManager(), tr_id);
        ViewPager mPager = (ViewPager) findViewById(R.id.result_pager);
        mPager.setAdapter(mAdapter);
        TitlePageIndicator mIndicator = (TitlePageIndicator) findViewById(R.id.result_indicator);
        mIndicator.setViewPager(mPager);
        Long ex_pos = dbHelper.READ.getExercisePositionInTraining(ex_id);
        mPager.setCurrentItem(ex_pos.intValue());
        curResultFragment = (ResultFragment) mAdapter.getItem(ex_pos.intValue());
        WheelFragment wheelFragment = getWheelFragmentByExId(ex_id);
        attachWheelFragment(wheelFragment);
        mIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                curResultFragment = (ResultFragment) mAdapter.getItem(position);
                ex_id = curResultFragment.getExercise().getId();
                Log.d(Const.LOG_TAG, "onPageSelected.ex: " + dbHelper.READ.getExerciseById(ex_id).getName());
                WheelFragment wheelFragment = getWheelFragmentByExId(ex_id);
                attachWheelFragment(wheelFragment);
            }
        });


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

        createUndoDialog();
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
        TrainingSet last_set = dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp_id);
        if (last_set != null) {
            chronometerReset();
            chronometerStart();
        }
    }

    private void attachWheelFragment(WheelFragment wheelFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.measure_layout, wheelFragment)
                .commit();
    }

    private WheelFragment getWheelFragmentByExId(long ex_id) {
        WheelFragment wheelFragment = null;
        if (!wheelMap.containsKey(ex_id)) {
            wheelFragment = WheelFragment.newInstance(dbHelper.READ.getExerciseById(ex_id), dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id));
            wheelMap.put(ex_id, wheelFragment);
        } else {
            wheelFragment = wheelMap.get(ex_id);
        }
        return wheelFragment;
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
                Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
                int deleted = dbHelper.WRITE.deleteLastTrainingSetInCurrentTrainingStamp(ex_id);
                if (deleted > 0) {
                    curResultFragment.refreshView(ResultActivity.this);
                    mAdapter.notifyDataSetChanged();
                    wheelMap.get(ex_id).setTrainingSet(dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id));
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

//    private void printCurrentTrainingProgress() {
//
//        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
//        List<TrainingSet> tr_stats = dbHelper.READ.getTrainingSetListInTrainingStampByExercise(ex_id, tr_stamp_id);
//        Log.d(Const.LOG_TAG, "tr_stats:" + tr_stats);
//        String stats = formTrainingStats(tr_stats);
//        TextView training_stat_text = (TextView) findViewById(R.id.cur_training_stats);
//        training_stat_text.setText(stats);
//    }

//    private String formTrainingStats(List<TrainingSet> sets) {
//        String result = "";
//        for (TrainingSet set : sets) {
//            result = MeasureFormatter.valueFormat(set) + "; " + result;
//        }
//        result += "\n" + getString(R.string.sum_approach) + " " + sets.size();
//        return result;
//    }

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
                curResultFragment.refreshView(ResultActivity.this);
                mAdapter.notifyDataSetChanged();
                wheelMap.get(ex_id).setTrainingSet(dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id));
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
        for (MeasureWheels measureWheels : wheelMap.get(ex_id).getMeasureWheelsList()) {
            TrainingSetValue trainingSetValue = new TrainingSetValue(null, null,
                    MeasureFormatter.getDoubleValue(measureWheels.getStringValue(), measureWheels.getMeasure()), i);
            trainingSetValues.add(trainingSetValue);
            i++;
        }
        return trainingSetValues;
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
