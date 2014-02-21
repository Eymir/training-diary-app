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
import android.support.v4.view.MenuItemCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.viewpagerindicator.TitlePageIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.calendar.CalendarActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;
import myApp.trainingdiary.utils.SoundPlayer;
import myApp.trainingdiary.utils.TimerAlarmBroadcastReceiver;
import myApp.trainingdiary.utils.TrainingDurationManger;

import android.os.AsyncTask;

/*
 * ��������� ��� ������ ���������� ������� ���������� 
 */

public class ResultActivity extends ActionBarActivity implements OnClickListener {

    private DBHelper dbHelper;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
    private SoundPool soundPool;
    private int soundClick;
    private AudioManager audioManager;
    private long ex_id;
    private long tr_id;
    private AlertDialog undoDialog;
    private Resources resources;
    private Chronometer mChrono;
    private long elapsedTime = 0L;
    private long base = 0L;
    private String currentTime = "";
    private boolean resume = false;
    private boolean reset = false;
    private boolean timerOn = false;
    private ResultFragmentAdapter mAdapter;
    private ResultFragment curResultFragment;
    private Map<Long, WheelFragment> wheelMap = new HashMap<Long, WheelFragment>();
    private TextView timerText;
    private boolean rotation = false;
    private String textTime;
    private TextView numRep;
    private boolean useTimer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("keep_screen_on", false))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_result);
        resources = getResources();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //google analytics
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().setContext(this);

        mChrono = (Chronometer) findViewById(R.id.mChrono);
        setChronoTickListener();

        //createExcerciseTools();
        dbHelper = dbHelper.getInstance(this);
        ex_id = getIntent().getExtras().getLong(Const.EXERCISE_ID);
        tr_id = getIntent().getExtras().getLong(Const.TRAINING_ID);
        Log.d(Const.LOG_TAG, "ex_id:" + ex_id + " tr_id:" + tr_id);

        mAdapter = new ResultFragmentAdapter(getSupportFragmentManager(), tr_id);
        ViewPager mPager = (ViewPager) findViewById(R.id.result_pager);
        mPager.setAdapter(mAdapter);
        TitlePageIndicator mIndicator = (TitlePageIndicator) findViewById(R.id.result_indicator);
        mIndicator.setViewPager(mPager);
        Long ex_pos = dbHelper.READ.getExercisePositionInTraining(ex_id, tr_id);
        mPager.setCurrentItem(ex_pos.intValue());
        Log.d(Const.LOG_TAG, "exercise.ex: " + dbHelper.READ.getExerciseById(ex_id).getName() + " ex_pos: " + ex_pos + " tr_id: " + tr_id);
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
                if(numRep != null)
                    numRep.setText("["+getNumSets()+"]");
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

        createUndoDialog();
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(ResultActivity.this).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Integer.valueOf(workoutExpiringTimeout));
        TrainingSet last_set = dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp_id);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        useTimer = sp.getBoolean("use_timer", false);

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            if(useTimer){
                if(TimerAlarmBroadcastReceiver.RUN){
                    if(timerTask != null)
                        timerTask.cancel(false);
                    timerTask = new TimerTask();
                    timerTask.execute(getTimerRemainInSec());
                }
            }
            else {
                rotation = true;
                elapsedTime = savedInstanceState.getLong("elapsedTime");
                currentTime = savedInstanceState.getString("currentTime");
                textTime = savedInstanceState.getString("textTime");
                timerOn = savedInstanceState.getBoolean("timerOn");
                base = savedInstanceState.getLong("base");
                resume = savedInstanceState.getBoolean("resume");
                reset = savedInstanceState.getBoolean("reset");
                chronometerReturn();
            }
        }
        else {
            if (last_set != null) {
                if(useTimer){
                    if(TimerAlarmBroadcastReceiver.RUN){//если ресивер заведён
                        if(timerTask != null)
                            timerTask.cancel(false);
                        timerTask = new TimerTask();
                        timerTask.execute(getTimerRemainInSec());
                    }
                }
                else {
                    chronometerReset();
                    chronometerStart();
                }
            }
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
            wheelFragment = WheelFragment.newInstance(dbHelper.READ.getExerciseById(ex_id),
                    dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id), new WheelTickListener() {
                @Override
                public void tick(String value) {
                    playClick();
                }
            });
            wheelMap.put(ex_id, wheelFragment);
        } else {
            wheelFragment = wheelMap.get(ex_id);
        }
        return wheelFragment;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actresult_stopwatch_menu, menu);
        MenuItem timerItem = menu.findItem(R.id.actresult_timer);
        MenuItem numRepItem = menu.findItem(R.id.actresult_num_rep);
        timerText = (TextView) MenuItemCompat.getActionView(timerItem);
        numRep = (TextView) MenuItemCompat.getActionView(numRepItem);

        if (rotation && !reset)
            timerText.setText(currentTime);
        else
            timerText.setText("00:00");

        timerText.setTextSize(25);
        timerText.setPadding(10, 0, 0, 0);

        numRep.setText("["+getNumSets()+"]");
        numRep.setTextSize(25);
        numRep.setPadding(10,0,20,0);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resultmenu_settings:
                Intent intentStat = new Intent(this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.resultmenu_history:
                openHistoryDetailActivity(ex_id);
                return true;
            case R.id.resultmenu_play:
                chronometerStart();
                SoundPlayer.getInstance(this).stopPlaySound();
                return true;
            case R.id.resultmenu_stop:
                chronometerStop();
                SoundPlayer.getInstance(this).stopPlaySound();
                return true;
            case R.id.resultmenu_replay:
                chronometerReset();
                SoundPlayer.getInstance(this).stopPlaySound();
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
                String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(ResultActivity.this).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
                Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Integer.valueOf(workoutExpiringTimeout));
                int deleted = dbHelper.WRITE.deleteLastTrainingSetInCurrentTrainingStamp(ex_id);
                if (deleted > 0) {
                    curResultFragment.refreshView(ResultActivity.this);
                    mAdapter.notifyDataSetChanged();
                    wheelMap.get(ex_id).setTrainingSet(dbHelper.READ.getLastTrainingSetByExerciseInLastTrainingStamp(ex_id, tr_id));
                    Toast.makeText(ResultActivity.this, R.string.deleted,
                            Toast.LENGTH_SHORT).show();
                    numRep.setText("[" + getNumSets() + "]");
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
                numRep.setText("["+getNumSets()+"]");
                SoundPlayer.getInstance(this).stopPlaySound();
                break;
            case R.id.undo_button:
                String message = getResources().getString(R.string.dialog_del_approach_msg);
                TrainingSet set = dbHelper.READ.getLastTrainingSetByExerciseInLastOpenTrainingStamp(ex_id, tr_id);
//                Log.d(Const.LOG_TAG, "undo TrainingStamp:" + dbHelper.READ.getLastTrainingStamp());
//                Log.d(Const.LOG_TAG, "undo ex_id:" + ex_id + " tr_id:" + tr_id);
//                Log.d(Const.LOG_TAG, "undo set:" + set);
                if (set != null) {
                    String value = MeasureFormatter.valueFormat(set);
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
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(ResultActivity.this).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Long training_stamp_id = TrainingDurationManger.getTrainingStamp(Integer.valueOf(workoutExpiringTimeout));
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

    private void chronometerReturn() {
        if(useTimer){
            if(timerTask != null)
                cancelTimerTask();
            timerTask = new TimerTask();
            timerTask.execute(getTimerRemainInSec());
        }
        else {
            if (timerOn) {
                mChrono.setBase(base);
                mChrono.start();
            } else {
                mChrono.setBase(base);
            }
        }
    }

    private void chronometerStart() {
        if(useTimer){
            TimerAlarmBroadcastReceiver.getInstance(this).SetAlarm(getTimerTime(),999);
            timerText.setText("00:00");

            if(timerTask != null){
                cancelTimerTask();
            }
            timerTask = new TimerTask();
            timerTask.execute(getTimerRemainInSec());
        }
        else {
            reset = false;
            timerOn = true;
            rotation = false;
            if (!resume) {
                mChrono.setBase(SystemClock.elapsedRealtime());
                mChrono.start();
            } else {
                mChrono.start();
            }
        }
    }

    private void chronometerStop() {
        if(useTimer){
            TimerAlarmBroadcastReceiver.getInstance(this).CancelAlarm(999);
            if(timerTask != null)
                cancelTimerTask();
        }
        else {
            mChrono.stop();
            timerOn = false;
            rotation = false;
            if (reset) {
                mChrono.setText("00:00");
                if (timerText != null)
                    timerText.setText("00:00");
                textTime = "00:00";
                resume = false;
            } else {
                mChrono.setText(currentTime);
                if (timerText != null) {
                    timerText.setText(currentTime);
                    textTime = currentTime;
                }
                resume = true;
            }
        }
    }

    private long getTimerTime(){
        long time = getSharedPreferences("preferences", MODE_PRIVATE).getLong("set_timer_time",0L);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        long timerInt = sec*1000+60*1000*min;
        c = Calendar.getInstance();
        long timerTime = c.getTimeInMillis()+timerInt;
        c.setTimeInMillis(timerTime);
        return timerTime;
    }

    private int getTimerRemainInSec(){
        Calendar c = Calendar.getInstance();
        long cTime = c.getTimeInMillis();
        c.setTimeInMillis(TimerAlarmBroadcastReceiver.TIME);
        long tTime = c.getTimeInMillis();
        c.setTimeInMillis(tTime-cTime);
        int sec = c.get(Calendar.MINUTE)*60+ c.get(Calendar.SECOND);
        return sec;
    }

    private void chronometerReset() {
        if(useTimer){
            timerText.setText("00:00");
            if(timerTask != null)
                cancelTimerTask();
        }
        else {
            mChrono.stop();
            timerOn = false;
            mChrono.setText("00:00");
            if (timerText != null)
                timerText.setText("00:00");
            resume = false;
            reset = true;
        }
    }

    private void setChronoTickListener() {
        mChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {

                if(useTimer){
                    return;
                }

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
                    if (timerText != null)
                        timerText.setText(currentTime);
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
                    if (timerText != null)
                        timerText.setText(currentTime);
                    if (timerOn)
                        elapsedTime = elapsedTime + 1000;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentTime", currentTime);
        outState.putLong("elapsedTime", elapsedTime);
        outState.putBoolean("timerOn", timerOn);
        outState.putLong("base", mChrono.getBase());
        outState.putString("textTime", textTime);
        outState.putBoolean("resume", resume);
        outState.putBoolean("reset", reset);
    }

    private int getNumSets(){
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(ResultActivity.this).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Integer.valueOf(workoutExpiringTimeout));
        List<TrainingSet> tr_stats = DBHelper.getInstance(null).READ.getTrainingSetListInTrainingStampByExercise(ex_id, tr_stamp_id);
        return tr_stats.size();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        useTimer = sp.getBoolean("use_timer", false);
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStop(this);
        if(timerTask !=  null)
            timerTask.cancel(false);
    }

    private void cancelTimerTask(){
        if(timerTask != null)
            timerTask.cancel(false);
    }

    class TimerTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... sec) {
            try {
                int cnt = 0;
                int progress = sec[0]+1;
                while(cnt <= sec[0]) {
                    if(isCancelled())
                        return null;
                    publishProgress(--progress);
                    cnt++;
                    TimeUnit.SECONDS.sleep(1);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int min = values[0]/60;
            int sec = values[0] - min*60;
            String minutes;
            String seconds;
            if(min < 10)
                minutes = "0"+min;
            else
                minutes = ""+min;
            if(sec <10)
                seconds = "0"+sec;
            else
                seconds = ""+sec;

            String res = minutes + ":"+seconds;
            if(timerText != null)
                timerText.setText(res);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
