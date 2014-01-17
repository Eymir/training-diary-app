package myApp.trainingdiary.history;

import android.content.Intent;
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
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.utils.Const;

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
        intentOpenHistoryDetails.putExtra(Const.HISTORY_TYPE, Const.TRAINING_TYPE);
        intentOpenHistoryDetails.putExtra(Const.DATE_FIELD, date);
        startActivity(intentOpenHistoryDetails);
    }

    private void loadTrainingList() {
        List<TrainingStamp> tr_stamps = dbHelper.READ.getTrainingMainHistory();
        Log.d(Const.LOG_TAG, "trainings.count: " + tr_stamps.size());
        ArrayList<DateItem> itemArrayList = trainingCursorToItemArray(tr_stamps);
        trainingHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        trainingHistoryList.setAdapter(trainingHistoryAdapter);
    }

    private ArrayList<DateItem> trainingCursorToItemArray(List<TrainingStamp> tr_stamps) {
        ArrayList<DateItem> items = new ArrayList<DateItem>();
        for (TrainingStamp stamp : tr_stamps) {
            Date date = stamp.getStartDate();
            boolean isSameDayExist = isSameDay(stamp, tr_stamps);
            String title = (isSameDayExist) ? SDF_DATETIME.format(date) : SDF_DATE.format(date);
            items.add(new DateItem(title, date));
        }
        return items;
    }

    private boolean isSameDay(TrainingStamp stamp, List<TrainingStamp> tr_stamps) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stamp.getStartDate());
        int tr_day = calendar.get(Calendar.DAY_OF_YEAR);
        int tr_year = calendar.get(Calendar.YEAR);
        for (TrainingStamp s : tr_stamps) {
            if (!s.getId().equals(stamp.getId())) {
                calendar.setTime(s.getStartDate());
                int cur_day = calendar.get(Calendar.DAY_OF_YEAR);
                int cur_year = calendar.get(Calendar.YEAR);
                if ((cur_day == tr_day) && (cur_year == tr_year)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSameDay(Date tr_date, List<Long> checkList) {
        Calendar calendarCurrent = Calendar.getInstance();
        Log.d(Const.LOG_TAG, "tr_date: " + SDF_DATETIME.format(tr_date));
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
        List<Exercise> exerciseList = dbHelper.READ.getExercisesForHistory();
        Log.d(Const.LOG_TAG, "Exercises.count: " + exerciseList.size());
        ArrayList<?> itemArrayList = exerciseCursorToItemArray(exerciseList);
        exerciseHistoryAdapter = new CustomItemAdapter(HistoryMainActivity.this, itemArrayList);
        exerciseHistoryList.setAdapter(exerciseHistoryAdapter);
    }

    private ArrayList<Item> exerciseCursorToItemArray(List<Exercise> exerciseList) {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Exercise ex : exerciseList) {
            Log.d(Const.LOG_TAG, "ex: " + ex);
            items.add(new ExerciseItem(ex.getName(), ex.getType().getIcon().name(), ex.getId()));
        }
        return items;
    }

    private void openExerciseHistoryDetails(long ex_id) {
        Intent intentOpenHistoryDetails = new Intent(this, HistoryDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Const.EXERCISE_ID, ex_id);
        intentOpenHistoryDetails.putExtra(Const.HISTORY_TYPE, Const.EXERCISE_TYPE);
        startActivity(intentOpenHistoryDetails);
    }

}
