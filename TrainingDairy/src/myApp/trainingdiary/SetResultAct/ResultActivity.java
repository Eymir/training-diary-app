package myApp.trainingdiary.SetResultAct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import myApp.trainingdiary.forBD.Measure;
import myApp.trainingdiary.forBD.TrainingStat;

/*
 * ��������� ��� ������ ���������� ������� ���������� 
 */

public class ResultActivity extends Activity implements OnClickListener {

    TextView tvnameEx, tvEndedRep;
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
    private List<MeasureWheels> measureWheels;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Resources res = getResources();
        String last_result_text = res.getString(R.string.last_result);
        TextView lastResultView = (TextView) findViewById(R.id.last_result_text);
        dbHelper = DBHelper.getInstance(this);
        ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
        tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);

        TrainingStat tr_stat = dbHelper.getLastTrainingStatByExerciseInTraining(ex_id, tr_id);
        String last_result_info;
        if (tr_stat != null) {
            last_result_info = tr_stat.getValue()
                    + "(" + sdf.format(tr_stat.getTrainingDate()) + ")";
        } else {
            last_result_info = getString(R.string.last_training_empty);
        }
        lastResultView.setText(String.format(last_result_text, last_result_info));
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.measure_layout);

        List<Measure> measureList = dbHelper.getMeasuresInExercise(ex_id);
        for (Measure measure : measureList) {
            MeasureWheels measureWheels = new MeasureWheels(measure);
            linearLayout.addView(measureWheels.getView());
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundClick = soundPool.load(this, R.raw.click3, 1);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
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
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            String zeros = "";
            for (int i = 0; i < length; i++) {
                zeros += "0";
            }
            NumericRightOrderWheelAdapter wheelAdapter = new NumericRightOrderWheelAdapter(ResultActivity.this, 0, max,
                    "%0" + length + "d");

            WheelView wheelView = (WheelView) getLayoutInflater().inflate(R.layout.wheel, null);
            wheelView.setViewAdapter(wheelAdapter);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, MENU_DEL_LAST_SET, 1, "������� ��������� ������");
//        menu.add(0, MENU_SHOW_LAST_RESULT, 1, "�������� ������� ����������");
//        return true;
//    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.write_button:
                setRepOnDB();
                RefreshTvEndedRep();
                break;
            default:
                break;
        }

    }

    private void changeCountET(EditText Et, String type) {
        if (type.equalsIgnoreCase("+")) {
            String i = Et.getText().toString();
            int j = Integer.parseInt(i);
            j = j + 1;
            if (j > 9) {
                j = 0;
            }
            String res = Integer.toString(j);
            Et.setText(res);
        } else if (type.equalsIgnoreCase("-")) {
            String i = Et.getText().toString();
            int j = Integer.parseInt(i);
            j = j - 1;
            if (j < 0) {
                j = 0;
            }
            String res = Integer.toString(j);
            Et.setText(res);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setRepOnDB() {
        dbHelper = DBHelper.getInstance(this);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // ������� ����
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String Date = sdf.format(Calendar.getInstance().getTime());

        // ������� ���
        String strPower = String.valueOf(bigNumWheelAdapter.getItem(bigNumWheel
                .getCurrentItem()))
                + smallNumWheelAdapter.getItemText(smallNumWheel
                .getCurrentItem());

        Float floPower = (float) Float.parseFloat(strPower);

        // ������� ����������
        // String strRep = repeatWheel.getCurrentItem();
        int intRep = repeatWheelAdapter.getItem(repeatWheel.getCurrentItem());
        // Integer.parseInt(strRep);

        cv.put("trainingdate", Date);
        cv.put("exercise", strNameEx);
        cv.put("power", floPower);
        cv.put("count", intRep);
        cv.put("trainingday", strNameTr);
        cv.put("exercisetype", "1");
        db.insert("TrainingStat", null, cv);

        // db.close();
        dbHelper.close();
    }

    @SuppressLint("SimpleDateFormat")
    private void RefreshTvEndedRep() {
        dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String Date = sdf.format(Calendar.getInstance().getTime());

        String sqlQuery = "select power, count from TrainingStat where trainingdate = ? and exercise = ?";
        String[] args = {Date, strNameEx};
        Cursor c = db.rawQuery(sqlQuery, args);

        String result = "";

        if (c.moveToFirst()) {
            int exNameIndex = c.getColumnIndex("power");
            int exNameIndex2 = c.getColumnIndex("count");
            int i = 0;
            do {
                Float pow = c.getFloat(exNameIndex);
                int cou = c.getInt(exNameIndex2);
                result = result + pow + "x" + cou + "; ";
                i++;
            } while (c.moveToNext());

            result = result + "\n����� ��������: " + i;
        }

        c.close();
        dbHelper.close();
        tvEndedRep.setText(result);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_DEL_LAST_SET:
                DelDialog();
                break;
            case MENU_SHOW_LAST_RESULT:
                showlastEx();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DelDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("�������� �������!!!");
        adb.setMessage("������� ��������� ������?");
        adb.setPositiveButton(getResources().getString(R.string.YES),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DelLastEx();
                    }
                });
        adb.setNegativeButton(getResources().getString(R.string.NO),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        adb.create().show();
    }

    @SuppressLint("SimpleDateFormat")
    private void DelLastEx() {

        dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String Date = sdf.format(Calendar.getInstance().getTime());
        // �������� ������ ���������� ������� � ����������
        String sqlQuery = "SELECT " + "id " + "FROM TrainingStat "
                + "WHERE trainingdate = ? AND exercise = ? "
                + "ORDER BY id DESC LIMIT 1";

        String[] args = {Date, strNameEx};
        Cursor c = db.rawQuery(sqlQuery, args);
        c.moveToFirst();
        int index = c.getColumnIndex("id");
        int idEx = c.getInt(index);
        // ������� ������
        db.delete("TrainingStat", "id = " + idEx, null);
        // ��������� ������� ��������
        RefreshTvEndedRep();

    }

    private void showlastEx() {

        Intent History_detailsv2 = new Intent(this, History_detailsv2.class);
        History_detailsv2.putExtra("AllEx", false);
        History_detailsv2.putExtra("nameEx", strNameEx);
        startActivity(History_detailsv2);

    }

}
