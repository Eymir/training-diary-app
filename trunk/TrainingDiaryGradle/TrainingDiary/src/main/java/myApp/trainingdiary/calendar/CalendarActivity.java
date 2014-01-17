package myApp.trainingdiary.calendar;

import android.annotation.SuppressLint;
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

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.utils.Const;

@SuppressLint("SimpleDateFormat")
public class CalendarActivity extends ActionBarActivity {

    private CaldroidFragment caldroidFragment;
    //final SimpleDateFormat formatterMy = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    //final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
    private DBHelper dbHelper;

    List<TrainingStamp> stamps = new ArrayList<TrainingStamp>();


    private void setCustomResourceForDates(int month, int year){

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        Long timeStart = cal.getTimeInMillis();

        cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        Long timeEnd = cal.getTimeInMillis();

        //List< TrainingStamp >
                stamps  = dbHelper.
                   READ.getTrainingStampInInterval(timeStart, timeEnd);

        if (caldroidFragment != null) {
            for (TrainingStamp tr : stamps){
                Date trainingDate = tr.getStartDate();

                Calendar c = Calendar.getInstance();
                c.setTime(trainingDate);

                if(currentYear == c.get(Calendar.YEAR) && currentMonth == c.get(Calendar.MONTH)
                        && currentDay == c.get(Calendar.DAY_OF_MONTH)){
                    caldroidFragment.setBackgroundResourceForDate(R.drawable.calendar_cell_red_border,
                            trainingDate);
                    caldroidFragment.setTextColorForDate(R.color.white, trainingDate);
                }
                else {
                caldroidFragment.setBackgroundResourceForDate(R.color.green,trainingDate);
                caldroidFragment.setTextColorForDate(R.color.white, trainingDate);
                }
            }
        }
    }

    private void showTrainingDayHistory(Date date){
        Intent intentOpenHistoryDetails = new Intent(this, HistoryDetailActivity.class);
        intentOpenHistoryDetails.putExtra(Const.HISTORY_TYPE, Const.TRAINING_TYPE);
        intentOpenHistoryDetails.putExtra(Const.DATE_FIELD, date);
        startActivity(intentOpenHistoryDetails);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = DBHelper.getInstance(this);

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
        // caldroidFragment = new CaldroidSampleCustomFragment();

        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }

        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            // CaldroidFragment.TUESDAY); // Tuesday
            caldroidFragment.setArguments(args);
        }

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                for (TrainingStamp tr : stamps){
                    Date trainingDate = tr.getStartDate();
                    Calendar c = Calendar.getInstance();
                    c.setTime(trainingDate);
                    int trDay = c.get(Calendar.DAY_OF_MONTH);
                    c.setTime(date);
                    int selDay = c.get(Calendar.DAY_OF_MONTH);
                    if(trDay == selDay){
                       showTrainingDayHistory(date);
                       break;
                    }
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                setCustomResourceForDates(month, year);
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {

                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
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
                Intent intentStat = new Intent(this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

}
