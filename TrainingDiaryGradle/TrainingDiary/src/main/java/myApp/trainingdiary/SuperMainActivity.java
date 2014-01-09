package myApp.trainingdiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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
import myApp.trainingdiary.utils.Const;

public class SuperMainActivity extends ActionBarActivity implements View.OnClickListener {

    private DBHelper dbHelper;
    private AlertDialog editStatListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        dbHelper = DBHelper.getInstance(this);
        try {
            Log.i(Const.LOG_TAG, "DB version: " + String.valueOf(dbHelper.getVersion()));
        } catch (Throwable e) {
            if (e.getMessage() != null && !e.getMessage().equals(""))
                Log.e(Const.LOG_TAG, e.getMessage(), e);
        }
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstStart = sp.getBoolean("about", true);

        if (firstStart) {
            showWhatNewsDialog();
            changeFirsStartPreference();
        }

        createEditStatListDialog();
        showCommonStatisticList();
    }

    private void initStatPref() {
        SharedPreferences sp = getSharedPreferences(Const.CHOSEN_STATISTIC, MODE_PRIVATE);

        if (sp.getAll().size() < StatisticEnum.values().length) {
            SharedPreferences.Editor editor = sp.edit();
            int i = 0;
            for (StatisticEnum stat : StatisticEnum.values()) {
                editor.putBoolean(stat.name(), (i < 5) ? true : false);
                i++;
            }
            editor.commit();
        }
        Log.d(Const.LOG_TAG, "SharedPreferences." + Const.CHOSEN_STATISTIC + ".count: " + sp.getAll().size());
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
        SharedPreferences sp = getSharedPreferences(Const.CHOSEN_STATISTIC, MODE_PRIVATE);
        ArrayList<StatItem> list = new ArrayList<StatItem>();
        for (StatisticEnum stat : StatisticEnum.values()) {
            if (sp.getBoolean(stat.name(), false)) {
                list.add(StatisticValueFactory.create(stat));
                Log.d(Const.LOG_TAG, "stat.name() is true:  " + stat.name());
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

    private void showWhatNewsDialog() {
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.newInVersionTitle) + " " + version);
        final TextView message = new TextView(this);
        final SpannableString s = new SpannableString(getResources()
                .getString(R.string.newInVersionMessage));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setTextColor(Color.BLACK);
        builder.setView(message);
        builder.setPositiveButton(R.string.btn_txt_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog AD = builder.create();
        AD.show();
    }

    private void changeFirsStartPreference() {
        //SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("about", false);
        editor.commit();
    }

}
