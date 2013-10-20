package myApp.trainingdiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.excercise.AddExerciseActivity;
import myApp.trainingdiary.history.HistoryMainActivity;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.training.TrainingActivity;

public class SuperMainActivity extends ActionBarActivity implements View.OnClickListener {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);


        Button start = (Button) findViewById(R.id.start_training_button);
        start.setOnClickListener(this);

        Button exercise = (Button) findViewById(R.id.exercise_main_button);
        exercise.setOnClickListener(this);

        Button history = (Button) findViewById(R.id.history_main_button);
        history.setOnClickListener(this);

        Button stat = (Button) findViewById(R.id.stat_main_button);
        stat.setOnClickListener(this);

        dbHelper = DBHelper.getInstance(this);

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
