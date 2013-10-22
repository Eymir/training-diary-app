package myApp.trainingdiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.customview.stat.StatItem;
import myApp.trainingdiary.customview.stat.StatItemArrayAdapter;
import myApp.trainingdiary.customview.stat.StatisticEnum;
import myApp.trainingdiary.customview.stat.StatisticValueFactory;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.excercise.AddExerciseActivity;
import myApp.trainingdiary.history.HistoryMainActivity;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.training.TrainingActivity;
import myApp.trainingdiary.utils.Consts;

public class SuperMainActivity extends ActionBarActivity implements View.OnClickListener {

    private DBHelper dbHelper;
    private AlertDialog editStatListDialog;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        dbHelper = DBHelper.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        initStatPref();

        Button start = (Button) findViewById(R.id.start_training_button);
        start.setOnClickListener(this);

        Button exercise = (Button) findViewById(R.id.exercise_main_button);
        exercise.setOnClickListener(this);

        Button history = (Button) findViewById(R.id.history_main_button);
        history.setOnClickListener(this);

        Button stat = (Button) findViewById(R.id.stat_main_button);
        stat.setOnClickListener(this);

        ImageButton editStat = (ImageButton) findViewById(R.id.edit_user_activity);
        editStat.setOnClickListener(this);

        createEditStatListDialog();
        showCommonStatisticList();
    }

    private void initStatPref() {
        SharedPreferences sp = getSharedPreferences(Consts.CHOSEN_STATISTIC, MODE_PRIVATE);
        if (sp.getAll().size() < StatisticEnum.values().length) {
            int i = 0;
            for (StatisticEnum stat : StatisticEnum.values()) {
                sp.edit().putBoolean(stat.name(), (i < 5) ? true : false);
                i++;
            }
        }
        sp.edit().commit();
    }

    private void createEditStatListDialog() {
        editStatListDialog = DialogProvider.createStatListDialog(this, new DialogProvider.OkClickListener() {
            @Override
            public void onPositiveClick() {
                showCommonStatisticList();
            }
        });

    }

    private void showCommonStatisticList() {
        SharedPreferences sp = getSharedPreferences(Consts.CHOSEN_STATISTIC, MODE_PRIVATE);
        ArrayList<StatItem> list = new ArrayList<StatItem>();
        for (StatisticEnum stat : StatisticEnum.values()) {
            if (sp.getBoolean(stat.name(), false)) {
                list.add(StatisticValueFactory.create(stat));
            }
        }
        ListView listView = (ListView) findViewById(R.id.statListView);
        StatItemArrayAdapter statListadapter = new StatItemArrayAdapter(this, R.layout.stat_plain_row, R.layout.stat_plain_row, list);
        listView.setAdapter(statListadapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_training_button:
                Intent intent = new Intent(SuperMainActivity.this, TrainingActivity.class);
                startActivity(intent);
                break;
            case R.id.exercise_main_button:
                Intent intentAddEx = new Intent(SuperMainActivity.this, AddExerciseActivity.class);
                startActivity(intentAddEx);
                break;
            case R.id.history_main_button:
                Intent intentHist = new Intent(SuperMainActivity.this, HistoryMainActivity.class);
                startActivity(intentHist);
                break;
            case R.id.stat_main_button:
                Intent intentStat = new Intent(SuperMainActivity.this, StatisticActivity.class);
                startActivity(intentStat);
                break;
            case R.id.edit_user_activity:
                editStatListDialog.show();
                break;
        }
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
                Intent intentStat = new Intent(SuperMainActivity.this, SettingsActivity.class);
                startActivity(intentStat);
                return true;

        }
        return true;
    }

}
