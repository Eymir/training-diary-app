package myApp.trainingdiary.history;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.itemadapter.item.BigSectionItem;
import myApp.trainingdiary.customview.itemadapter.item.CustomItemAdapter;
import myApp.trainingdiary.customview.itemadapter.item.Item;
import myApp.trainingdiary.customview.itemadapter.item.StatisticItem;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;

public class HistoryDetailActivity extends ActionBarActivity {

    private DBHelper dbHelper;
    private Date training_date;
    private long ex_id;
    private ListView listView;
    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        dbHelper = DBHelper.getInstance(this);
        listView = (ListView) findViewById(R.id.listView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        int type = getIntent().getExtras().getInt(Const.HISTORY_TYPE);

        switch (type) {
            case Const.TRAINING_TYPE:
                training_date = (Date) getIntent().getExtras().get(Const.DATE_FIELD);
                setTitle(getTitle() + ": " + SDF_DATE.format(training_date));
                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.setTime(training_date);
                Long timeStart = cal.getTimeInMillis();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, day + 1);
                Long timeEnd = cal.getTimeInMillis();
                List< TrainingStamp > trainingStamps  = dbHelper.READ.
                        getTrainingStampInIntervalWithTrainingSet(timeStart, timeEnd);
                for (TrainingStamp trainingStamp : trainingStamps){
                    ArrayList<?> trItemArrayList = trainingCursorToItemArray(trainingStamp);
                    CustomItemAdapter trainingHistoryAdapter = new CustomItemAdapter(HistoryDetailActivity.this, trItemArrayList);
                    listView.setAdapter(trainingHistoryAdapter);
                }
                break;
            case Const.EXERCISE_TYPE:
                ex_id = getIntent().getExtras().getLong(Const.EXERCISE_ID);
                Exercise exercise = dbHelper.READ.getExerciseById(ex_id);
                setTitle(getTitle() + ": " + ((exercise == null) ? null : exercise.getName()));

                List<TrainingStamp> trainingStampList = dbHelper.READ.getTrainingStampWithExactExerciseDesc(ex_id);
                Log.d(Const.LOG_TAG, "trainingStampList.count(): " + trainingStampList.size());
                ArrayList<?> exItemArrayList = exerciseCursorToItemArray(trainingStampList);
                CustomItemAdapter exerciseHistoryAdapter = new CustomItemAdapter(HistoryDetailActivity.this, exItemArrayList);
                listView.setAdapter(exerciseHistoryAdapter);
                break;
        }

    }

    private ArrayList<?> exerciseCursorToItemArray(List<TrainingStamp> trainingStampList) {
        ArrayList<Item> items = new ArrayList<Item>();
        for (TrainingStamp stamp : trainingStampList) {
            String icon = getResources().getResourceName(R.drawable.icon_train);
            BigSectionItem item = new BigSectionItem(SDF_DATETIME.format(stamp.getStartDate()), icon);
            items.add(item);
            Log.d(Const.LOG_TAG, "BigSectionItem: " + item);
            for (int i = 0; i < stamp.getTrainingSetList().size(); i++) {
                TrainingSet set = stamp.getTrainingSetList().get(i);
                String value = MeasureFormatter.valueFormat(set);
                items.add(new StatisticItem(i + 1, value));
            }
        }
        return items;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private ArrayList<?> trainingCursorToItemArray(TrainingStamp stamp) {
        ArrayList<Item> items = new ArrayList<Item>();
        Long prevExId = null;
        Integer i = 1;
        List<TrainingSet> setList = stamp.getTrainingSetList();
        for (TrainingSet set : setList) {
            String value = MeasureFormatter.valueFormat(set);
            if (!set.getExerciseId().equals(prevExId)) {
                prevExId = set.getExerciseId();
                Exercise ex = dbHelper.READ.getExerciseById(set.getExerciseId());
                items.add(new BigSectionItem(ex.getName(), ex.getType().getIcon().name()));
                i = 1;
            }
            items.add(new StatisticItem(i, value));
            i++;
        }
        return items;
    }
}
