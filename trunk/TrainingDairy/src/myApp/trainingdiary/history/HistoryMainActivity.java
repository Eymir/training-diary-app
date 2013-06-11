package myApp.trainingdiary.history;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;
import android.widget.*;

import myApp.trainingdiary.R;
import myApp.trainingdiary.utils.Consts;
import myApp.trainingdiary.customview.itemadapter.CustomItemAdapter;
import myApp.trainingdiary.customview.itemadapter.DateItem;
import myApp.trainingdiary.customview.itemadapter.ExerciseItem;
import myApp.trainingdiary.customview.itemadapter.Item;
import myApp.trainingdiary.customview.itemadapter.SmallSectionItem;
import myApp.trainingdiary.db.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

/*
 * �������� �������� � �������� ����������, ���������� ������������� �� ����
 */

public class HistoryMainActivity extends Activity {

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
        Intent intentOpenHistoryDetails = new Intent(this, HistoryDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Consts.HISTORY_TYPE, Consts.TRAINING_TYPE);
        intentOpenHistoryDetails.putExtra(Consts.DATE_FIELD, date);
        startActivity(intentOpenHistoryDetails);
    }

    private void loadTrainingList() {
        Cursor tr_cursor = dbHelper.READ.getTrainingMainHistory();
        Log.d(Consts.LOG_TAG, "trainings.count: " + tr_cursor.getCount());
        ArrayList<DateItem> itemArrayList = trainingCursorToItemArray(tr_cursor);
        trainingHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        trainingHistoryList.setAdapter(trainingHistoryAdapter);
    }

    private ArrayList<DateItem> trainingCursorToItemArray(Cursor cursor) {
        ArrayList<DateItem> items = new ArrayList<DateItem>();
        try {
            List<Long> checkList = new ArrayList<Long>();
            while (cursor.moveToNext()) {
                Long tr_date_long = cursor.getLong(cursor.getColumnIndex("training_date"));
                checkList.add(tr_date_long);
            }
            for (Long date : checkList) {
                Date tr_date = new Date(date);
                boolean isSameDayExist = isSameDay(tr_date, new ArrayList<Long>(checkList));
                String title = (isSameDayExist) ? SDF_DATETIME.format(tr_date) : SDF_DATE.format(tr_date);
                items.add(new DateItem(title, tr_date));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    private boolean isSameDay(Date tr_date, List<Long> checkList) {
        Calendar calendarCurrent = Calendar.getInstance();
        Log.d(Consts.LOG_TAG, "tr_date: " + SDF_DATETIME.format(tr_date));
        calendarCurrent.setTime(tr_date);
        int tr_day = calendarCurrent.get(Calendar.DAY_OF_YEAR);
        int tr_year = calendarCurrent.get(Calendar.YEAR);
        checkList.remove(tr_date.getTime());
        for (Long date : checkList) {
            Date curDate = new Date(date);
            calendarCurrent.setTime(curDate);
            int cur_day = calendarCurrent.get(Calendar.DAY_OF_YEAR);
            int cur_year = calendarCurrent.get(Calendar.YEAR);
            if ((cur_day == tr_day) && (cur_year == tr_year)) {
                return true;
            }
        }
        return false;
    }

    private void loadExerciseList() {
        Cursor ex_cursor = dbHelper.READ.getExercisesForHistory();
        Log.d(Consts.LOG_TAG, "Exercises.count: " + ex_cursor.getCount());
        ArrayList<?> itemArrayList = exerciseCursorToItemArray(ex_cursor);
        exerciseHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        exerciseHistoryList.setAdapter(exerciseHistoryAdapter);
    }

    private ArrayList<Item> exerciseCursorToItemArray(Cursor cursor) {
        ArrayList<Item> items = new ArrayList<Item>();
        try {
            String lastTrName = "\n";
            while (cursor.moveToNext()) {
                String tr_name = cursor.getString(cursor.getColumnIndex("tr_name"));
                String ex_name = cursor.getString(cursor.getColumnIndex("ex_name"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                Long ex_id = cursor.getLong(cursor.getColumnIndex("ex_id"));
                Log.d(Consts.LOG_TAG, "ex_name: " + ex_name + " tr_name: " + tr_name);
                if ((tr_name == null && lastTrName == null) || (tr_name != null && !tr_name.equals(lastTrName))) {
                    if (tr_name == null || tr_name.isEmpty())
                        items.add(new SmallSectionItem(getResources().getString(R.string.empty_training_section)));
                    else {
                        items.add(new SmallSectionItem(tr_name));
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
        Intent intentOpenHistoryDetails = new Intent(this, HistoryDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Consts.EXERCISE_ID, ex_id);
        intentOpenHistoryDetails.putExtra(Consts.HISTORY_TYPE, Consts.EXERCISE_TYPE);
        startActivity(intentOpenHistoryDetails);
    }

}
