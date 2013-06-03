package myApp.trainingdiary.history;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import myApp.trainingdiary.R;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.customview.itemadapter.BigSectionItem;
import myApp.trainingdiary.customview.itemadapter.CustomItemAdapter;
import myApp.trainingdiary.customview.itemadapter.Item;
import myApp.trainingdiary.customview.itemadapter.StatisticItem;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;

public class HistoryDetailActivity extends Activity {

    private DBHelper dbHelper;
    private Date training_date;
    private long ex_id;
    private ListView listView;
    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        dbHelper = DBHelper.getInstance(this);
        listView = (ListView) findViewById(R.id.listView);

        int type = getIntent().getExtras().getInt(Consts.HISTORY_TYPE);

        switch (type) {
            case Consts.TRAINING_TYPE:
                training_date = (Date) getIntent().getExtras().get(Consts.DATE_FIELD);
                setTitle(getTitle() + ": " + SDF_DATETIME.format(training_date));
                Cursor trainingCursor = dbHelper.getTrainingStatByTrainingDate(training_date);
                ArrayList<?> trItemArrayList = trainingCursorToItemArray(trainingCursor);
                CustomItemAdapter trainingHistoryAdapter = new CustomItemAdapter(HistoryDetailActivity.this, trItemArrayList);
                listView.setAdapter(trainingHistoryAdapter);
                break;
            case Consts.EXERCISE_TYPE:
                ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
                Exercise exercise = dbHelper.getExerciseById(ex_id);
                setTitle(getTitle() + ": " + ((exercise == null) ? null : exercise.getName()));
                Cursor exerciseCursor = dbHelper.getTrainingStatByExercise(ex_id);
                ArrayList<?> exItemArrayList = exerciseCursorToItemArray(exerciseCursor);
                CustomItemAdapter exerciseHistoryAdapter = new CustomItemAdapter(HistoryDetailActivity.this, exItemArrayList);
                listView.setAdapter(exerciseHistoryAdapter);
                break;

        }

    }

    private ArrayList<?> exerciseCursorToItemArray(Cursor exerciseCursor) {
        ArrayList<Item> items = new ArrayList<Item>();
        try {
            // stat.value, stat.training_date from TrainingStat stat
            Long prevTrDate = null;
            Integer i = 1;
            while (exerciseCursor.moveToNext()) {
                String value = exerciseCursor.getString(exerciseCursor.getColumnIndex("value"));
                Long training_date = exerciseCursor.getLong(exerciseCursor.getColumnIndex("training_date"));
                if (!training_date.equals(prevTrDate)) {
                    prevTrDate = training_date;
                    items.add(new BigSectionItem(SDF_DATETIME.format(new Date(training_date)), "ico_train"));
                    i = 1;
                }
                items.add(new StatisticItem(i, value));
                i++;
            }
        } finally {
            exerciseCursor.close();
        }
        return items;
    }

    private ArrayList<?> trainingCursorToItemArray(Cursor trainingCursor) {
        ArrayList<Item> items = new ArrayList<Item>();
        try {
            String prevExName = null;
            Integer i = 1;
            while (trainingCursor.moveToNext()) {
                String name = trainingCursor.getString(trainingCursor.getColumnIndex("name"));
                String value = trainingCursor.getString(trainingCursor.getColumnIndex("value"));
                String icon = trainingCursor.getString(trainingCursor.getColumnIndex("icon"));
                Long date = trainingCursor.getLong(trainingCursor.getColumnIndex("date"));
                if (!name.equals(prevExName)) {
                    prevExName = name;
                    items.add(new BigSectionItem(name, icon));
                    i = 1;
                }
                items.add(new StatisticItem(i, value));
                i++;
            }
        } finally {
            trainingCursor.close();
        }
        return items;
    }

    private void getHistory() {

        TableLayout table = null;

        dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sqlQuery;
        String param = "";


        String[] args = {param};

        Cursor cEx = db.rawQuery(null, args);
        String result = "";

        if (cEx.moveToFirst()) {
            int exNameIndex = cEx.getColumnIndex("exercise");
            int exTrDateIndex = cEx.getColumnIndex("trainingdate");

            do {
                String Exercise = cEx.getString(exNameIndex);
                String TrDate = cEx.getString(exTrDateIndex);

                //����� �������� ����������
                TableRow row0 = new TableRow(this);
                LayoutParams lprow0 = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                row0.setLayoutParams(lprow0);

                TextView textName = new TextView(this);
                textName.setText(Exercise + " " + TrDate);
                textName.setTextSize(30);
                textName.setTextColor(Color.WHITE);
                LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                textName.setLayoutParams(lp1);

                row0.addView(textName);
                table.addView(row0, new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                View vv1 = new View(this);
                vv1.setBackgroundColor(Color.GRAY);
                LayoutParams lpvv1 = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
                vv1.setLayoutParams(lpvv1);
                table.addView(vv1);
                //


                String sqlQuery2 = "select power, count from TrainingStat where trainingdate = ? and exercise = ?";
                String[] args2 = {TrDate, Exercise};
                Cursor cExHis = db.rawQuery(sqlQuery2, args2);

                if (cExHis.moveToFirst()) {
                    int indPow = cExHis.getColumnIndex("power");
                    int indCou = cExHis.getColumnIndex("count");
                    int countRep = 0;
                    Float maxPow = 0.0F;
                    Float maxInt = 0.0F;
                    //String maxIntRes = "";
                    Float fullmass = 0.0F;

                    do {
                        Float pow = cExHis.getFloat(indPow);
                        int cou = cExHis.getInt(indCou);
                        countRep++;
                        result = countRep + ".  " + pow + "x" + cou;
                        fullmass = pow * cou;
                        if (pow > maxPow) maxPow = pow;
                        if (fullmass > maxInt) {
                            maxInt = fullmass;
                        }

                        //����� ���������� �������
                        TableRow row1 = new TableRow(this);
                        LayoutParams lprow1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.MATCH_PARENT);
                        row1.setLayoutParams(lprow1);

                        TextView textRep = new TextView(this);
                        textRep.setText(result);
                        textRep.setTextColor(Color.WHITE);
                        LayoutParams lp0 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);
                        textRep.setLayoutParams(lp0);

                        row1.addView(textRep);
                        table.addView(row1, new TableLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                        //����� ����������� ����� ���������
                        View vv2 = new View(this);
                        vv2.setBackgroundColor(Color.GRAY);
                        LayoutParams lpvv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
                        vv2.setLayoutParams(lpvv2);
                        table.addView(vv2);

                    } while (cExHis.moveToNext());

                    //����� ������������� ���� ����������
                    TableRow row2 = new TableRow(this);
                    LayoutParams lprow2 = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
                    row0.setLayoutParams(lprow2);

                    TextView textMax = new TextView(this);
                    textMax.setText("�������� = " + maxPow);
                    textMax.setTextSize(15);
                    textMax.setTextColor(Color.YELLOW);
                    LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
                    textMax.setLayoutParams(lp2);

                    row2.addView(textMax);
                    table.addView(row2, new TableLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                    //����� ����������� ����������
                    View vv = new View(this);
                    vv.setBackgroundColor(Color.BLACK);
                    LayoutParams lpvv = new LayoutParams(LayoutParams.MATCH_PARENT, 3);
                    vv.setLayoutParams(lpvv);
                    table.addView(vv);
                }

                cExHis.close();

            } while (cEx.moveToNext());
        }
        cEx.close();
        dbHelper.close();
    }

}
