package myApp.trainingdiary.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.support.v4.widget.*;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.*;
import android.widget.CursorAdapter;
import myApp.trainingdiary.R;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;

/*
 * �������� �������� � �������� ����������, ���������� ������������� �� ����
 */

public class HistoryMainActivity extends Activity {

    ListView lvMainHistory;

    private DBHelper dbHelper;
    private ListView trainingHistoryList;
    private ListView exerciseHistoryList;

    private SimpleCursorAdapter trainingHistoryAdapter;
    private SimpleCursorAdapter exerciseHistoryAdapter;

    private RadioButton trainingRadioButton;
    private RadioButton exerciseRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_main);
        trainingHistoryList = (ListView) findViewById(R.id.training_history_list);
        exerciseHistoryList = (ListView) findViewById(R.id.exercise_history_list);


        trainingRadioButton = (RadioButton) findViewById(R.id.training_rb);
        exerciseRadioButton = (RadioButton) findViewById(R.id.exercise_rb);

        trainingRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                exerciseHistoryList.setVisibility(View.GONE);
                trainingHistoryList.setVisibility(View.VISIBLE);
            }
        });

        exerciseRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                trainingHistoryList.setVisibility(View.GONE);
                exerciseHistoryList.setVisibility(View.VISIBLE);
            }
        });

        loadExerciseList();
        loadTrainingList();

        trainingHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                long ex_id = trainingHistoryAdapter.getItemId(pos);
            }
        });

        exerciseHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                long ex_id = exerciseHistoryAdapter.getItemId(pos);
                openExerciseHistoryDetails(ex_id);
            }
        });

    }

    private void loadTrainingList() {
        Cursor tr_cursor = dbHelper.getTrainingsForHistory();
        Log.d(Consts.LOG_TAG, "Exercise.count: " + tr_cursor.getCount());
        String[] from = {"date", "_id"};
        int[] to = {R.id.label, R.id.icon};
        trainingHistoryAdapter = new SimpleCursorAdapter(
                HistoryMainActivity.this, R.layout.exercise_plain_row, tr_cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        trainingHistoryAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    ((ImageView) view).setImageResource(R.drawable.ico_train);
                    return true;
                }
                return false;
            }
        });

        trainingHistoryList.setAdapter(trainingHistoryAdapter);
    }

    private void loadExerciseList() {
        Cursor ex_cursor = dbHelper.getExercisesForHistory();
        Log.d(Consts.LOG_TAG, "Exercise.count: " + ex_cursor.getCount());
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
        exerciseHistoryAdapter = new SimpleCursorAdapter(
                HistoryMainActivity.this, R.layout.exercise_plain_row, ex_cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        exerciseHistoryAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    ((ImageView) view).setImageResource(getResources()
                            .getIdentifier(cursor.getString(columnIndex),
                                    "drawable", getPackageName()));
                    return true;
                }
                return false;
            }
        });

        exerciseHistoryList.setAdapter(exerciseHistoryAdapter);
    }

    private void openExerciseHistoryDetails(long ex_id) {
        Intent intentOpenHistoryDetails = new Intent(this, HistoryExerciseDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Consts.EXERCISE_ID, ex_id);
        startActivity(intentOpenHistoryDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history_main_acrivity, menu);
        return true;
    }

    private void GetTrainingsDay() {
        dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] Column = {"trainingdate"};
        Cursor c = db.query("TrainingStat", Column, null, null, "trainingdate", null, null);
        int size = c.getCount();
        SortedMap<String, String> list = new TreeMap<String, String>();

        if (size > 0) {
            if (c.moveToFirst()) {
                int nameColIndex = c.getColumnIndex("trainingdate");

                do {
                    list.put(c.getString(nameColIndex), "0");
                } while (c.moveToNext());
            }

            c.close();
            dbHelper.close();

            int imgTr = R.drawable.ico_train;
            final String ATTRIBUTE_NAME_TEXT = "text";
            final String ATTRIBUTE_NAME_IMAGE = "image";

            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(list.size());
            Map<String, Object> m;

            for (Map.Entry<String, String> entry : list.entrySet()) {
                String name = entry.getKey();
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_TEXT, name);
                m.put(ATTRIBUTE_NAME_IMAGE, imgTr);
                data.add(m);
            }

            String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE};
            int[] to = {R.id.label, R.id.icon};
//	        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.exerciseslv, from, to);        
//	        lvMainHistory.setAdapter(sAdapter);  	        
        } else {
            Toast.makeText(this, "� ������� ��� �� ����� ����������", Toast.LENGTH_LONG).show();
            String[] arrTrainings = new String[]{"<������� ���>"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrTrainings);
            lvMainHistory.setAdapter(adapter);
            return;
        }
    }

    private void openHistoryDetails(String DateTraining) {
        Intent intentOpenHistoryDetails = new Intent(this, HistoryExerciseDetailActivity.class);
        intentOpenHistoryDetails.putExtra("AllEx", true);
        intentOpenHistoryDetails.putExtra("strDateTr", DateTraining);
        startActivity(intentOpenHistoryDetails);
    }

    private String ParserOnItemClick(String nonParsed) {
        int index = nonParsed.indexOf("text=");
        String HalfParsed = nonParsed.substring(index + 5);
        int index2 = HalfParsed.length();
        String Parsed = HalfParsed.substring(0, index2 - 1);
        return Parsed;
    }

}
