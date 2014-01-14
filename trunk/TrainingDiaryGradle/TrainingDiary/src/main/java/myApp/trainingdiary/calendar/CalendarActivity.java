package myApp.trainingdiary.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
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


    private void setCustomResourceForDates(int month, int year){

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        Long timeStart = cal.getTimeInMillis();

        cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        Long timeEnd = cal.getTimeInMillis();

        List< TrainingStamp > stamps  = dbHelper.
                   READ.getTrainingStampInInterval(timeStart, timeEnd);

        if (caldroidFragment != null) {
            for (TrainingStamp tr : stamps){
                Date trainingDate = tr.getStartDate();
                caldroidFragment.setBackgroundResourceForDate(R.color.green,
                        trainingDate);
                caldroidFragment.setTextColorForDate(R.color.white, trainingDate);

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
                showTrainingDayHistory(date);

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

}
