package myApp.trainingdiary.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.db.entity.TrainingStamp;

@SuppressLint("SimpleDateFormat")
public class CalendarActivity extends ActionBarActivity {

    private CaldroidFragment caldroidFragment;
    final SimpleDateFormat formatterMy = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
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

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        Long timeStart = cal.getTimeInMillis();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, day + 1);
        Long timeEnd = cal.getTimeInMillis();

        List< TrainingStamp > trainingStamps  = dbHelper.READ.
                getTrainingStampInIntervalWithTrainingSet(timeStart, timeEnd);

        int n = 0;
        String msg  = "";
        String trainingDate = "";
        List<TrainingSetValue> trSetVal;

        for (TrainingStamp trStamp : trainingStamps){
            List<TrainingSet> trSet = trStamp.getTrainingSetList();
            trainingDate = formatter.format(trStamp.getStartDate());

            for (TrainingSet set : trSet){
                n=n+1;
                trSetVal = set.getValues();
                long exId = set.getExerciseId();
                String exercise =  dbHelper.READ.getExerciseById(exId).getName();
                msg = msg +""+n+". "+exercise+" "+trSetVal+"\n";
            }
        }
        showHistoryDialog(msg, trainingDate);
    }

    private void showHistoryDialog(String msg, String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(date);
        final TextView message = new TextView(this);
        final SpannableString s = new SpannableString(msg);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

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

        //setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(), formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
                showTrainingDayHistory(date);

            }

            @Override
            public void onChangeMonth(int month, int year) {
              //  String text = "month: " + month + " year: " + year + "its true";
//                Toast.makeText(getApplicationContext(), text,
//                        Toast.LENGTH_SHORT).show();
                setCustomResourceForDates(month, year);
            }

            @Override
            public void onLongClickDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(),
//                        "Long click " + formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
//                    Toast.makeText(getApplicationContext(),
//                            "Caldroid view is created", Toast.LENGTH_SHORT)
//                            .show();
                }
            }

        };

        // Setup Caldroid
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
