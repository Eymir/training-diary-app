package myApp.trainingdiary.history;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;
import android.widget.*;

import myApp.trainingdiary.R;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.customview.CustomItemAdapter;
import myApp.trainingdiary.customview.DateItem;
import myApp.trainingdiary.customview.ExerciseItem;
import myApp.trainingdiary.customview.Item;
import myApp.trainingdiary.customview.SectionItem;
import myApp.trainingdiary.db.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

    private CustomItemAdapter trainingHistoryAdapter;
    private CustomItemAdapter exerciseHistoryAdapter;

    private RadioButton trainingRadioButton;
    private RadioButton exerciseRadioButton;

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yyyy");

    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_main);
        dbHelper = DBHelper.getInstance(this);
        trainingHistoryList = (ListView) findViewById(R.id.training_history_list);
        exerciseHistoryList = (ListView) findViewById(R.id.exercise_history_list);


        trainingRadioButton = (RadioButton) findViewById(R.id.training_rb);
        exerciseRadioButton = (RadioButton) findViewById(R.id.exercise_rb);

        trainingRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseHistoryList.setVisibility(View.GONE);
                if (!trainingHistoryAdapter.isEmpty())
                    trainingHistoryList.setVisibility(View.VISIBLE);
            }
        });


        exerciseRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainingHistoryList.setVisibility(View.GONE);
                if (!exerciseHistoryAdapter.isEmpty())
                    exerciseHistoryList.setVisibility(View.VISIBLE);
            }
        });

        loadExerciseList();
        loadTrainingList();

        trainingHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                DateItem item = (DateItem) trainingHistoryAdapter.getItem(pos);
                openTrainingDetails(item.getDate());
            }
        });

        exerciseHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ExerciseItem item = (ExerciseItem) exerciseHistoryAdapter.getItem(pos);
                openExerciseHistoryDetails(item.getExId());
            }
        });

        View emptyView = findViewById(R.id.empty_view);
        trainingHistoryList.setEmptyView(emptyView);
        exerciseHistoryList.setEmptyView(emptyView);
    }

    private void openTrainingDetails(Date date) {
        Intent intentOpenHistoryDetails = new Intent(this, HistoryTrainingDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Consts.DATE_FIELD, date);
        startActivity(intentOpenHistoryDetails);
    }

    private void loadTrainingList() {
        Cursor tr_cursor = dbHelper.getTrainingMainHistory();
        Log.d(Consts.LOG_TAG, "Exercise.count: " + tr_cursor.getCount());
        ArrayList<DateItem> itemArrayList = trainingCursorToItemArray(tr_cursor);
        trainingHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        trainingHistoryList.setAdapter(trainingHistoryAdapter);
    }

    private ArrayList<DateItem> trainingCursorToItemArray(Cursor cursor) {
        ArrayList<DateItem> items = new ArrayList<DateItem>();
        try {
            Date previousDate = new Date(0L);
            Calendar calendarPrevious = Calendar.getInstance();
            Calendar calendarCurrent = Calendar.getInstance();
            while (cursor.moveToNext()) {
                Long tr_date_long = cursor.getLong(cursor.getColumnIndex("training_date"));
                Date tr_date = new Date(tr_date_long);
                calendarCurrent.setTime(tr_date);
                int cur_day = calendarCurrent.get(Calendar.DAY_OF_YEAR);
                calendarPrevious.setTime(previousDate);
                int prev_day = calendarPrevious.get(Calendar.DAY_OF_YEAR);
                String title = (cur_day == prev_day) ? SDF_DATETIME.format(tr_date) : SDF_DATE.format(tr_date);
                previousDate = tr_date;
                items.add(new DateItem(title, tr_date));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    private void loadExerciseList() {
        Cursor ex_cursor = dbHelper.getExercisesForHistory();
        Log.d(Consts.LOG_TAG, "Exercise.count: " + ex_cursor.getCount());
        ArrayList<?> itemArrayList = exerciseCursorToItemArray(ex_cursor);
        exerciseHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        exerciseHistoryList.setAdapter(exerciseHistoryAdapter);
    }

    private ArrayList<Item> exerciseCursorToItemArray(Cursor cursor) {
        ArrayList<Item> items = new ArrayList<Item>();
        try {
            String lastTrName = "\n";
            while (cursor.moveToNext()) {
                //tr.name tr_name, ex.name ex_name, ex.id ex_id, ex_type.icon_res icon
                String tr_name = cursor.getString(cursor.getColumnIndex("tr_name"));
                String ex_name = cursor.getString(cursor.getColumnIndex("ex_name"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                Long ex_id = cursor.getLong(cursor.getColumnIndex("ex_id"));

                if (tr_name != lastTrName) {
                    if (tr_name == null || tr_name.isEmpty())
                        items.add(new SectionItem(getResources().getString(R.string.empty_training_section)));
                    else {
                        items.add(new SectionItem(tr_name));
                    }
                    lastTrName = tr_name;
                }
                items.add(new ExerciseItem(ex_name, icon, ex_id));

            }
        } finally {
            cursor.close();
        }
        return items;
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
