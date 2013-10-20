package myApp.trainingdiary.history;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.customview.itemadapter.item.CustomItemAdapter;
import myApp.trainingdiary.customview.itemadapter.item.DateItem;
import myApp.trainingdiary.customview.itemadapter.item.ExerciseItem;
import myApp.trainingdiary.customview.itemadapter.item.Item;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.utils.Consts;

/*
 * �������� �������� � �������� ����������, ���������� ������������� �� ����
 */

public class HistoryMainActivity extends ActionBarActivity {

    private DBHelper dbHelper;
    private ListView trainingHistoryList;
    private ListView exerciseHistoryList;

    private CustomItemAdapter trainingHistoryAdapter;
    private CustomItemAdapter exerciseHistoryAdapter;

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yyyy");

    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_main);

        dbHelper = DBHelper.getInstance(this);

        trainingHistoryList = (ListView) findViewById(R.id.training_history_list);
        exerciseHistoryList = (ListView) findViewById(R.id.exercise_history_list);

        loadExerciseList();
        loadTrainingList();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.training_radio_button)
                .setTabListener(new ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                        exerciseHistoryList.setVisibility(View.GONE);
                        if (!trainingHistoryAdapter.isEmpty())
                            trainingHistoryList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                    }
                });
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.exercises_radio_button)
                .setTabListener(new ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                        trainingHistoryList.setVisibility(View.GONE);
                        if (!exerciseHistoryAdapter.isEmpty())
                            exerciseHistoryList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                    }
                });
        actionBar.addTab(tab);

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
            while (cursor.moveToNext()) {
                String ex_name = cursor.getString(cursor.getColumnIndex("ex_name"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                Long ex_id = cursor.getLong(cursor.getColumnIndex("ex_id"));
                Log.d(Consts.LOG_TAG, "ex_name: " + ex_name);
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
