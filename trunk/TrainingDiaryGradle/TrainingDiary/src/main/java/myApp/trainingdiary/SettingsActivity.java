package myApp.trainingdiary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import myApp.trainingdiary.calculators.MaxWeightCalculatorActivity;
import myApp.trainingdiary.calendar.CalendarActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.utils.BackupManager;
import myApp.trainingdiary.utils.Const;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends PreferenceActivity  implements
        Preference.OnPreferenceChangeListener {

    private DBHelper dbHelper;
    private Context context;

    private CheckBoxPreference checkBoxUseStopWatch;
    private CheckBoxPreference checkBoxUseTimer;

    private Preference set_timer_time;

    private static final String KEY_STOPWATCH = "use_stopwatch";
    private static final String KEY_TIMER = "use_timer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = this.getApplicationContext();

        checkBoxUseStopWatch = (CheckBoxPreference)findPreference(KEY_STOPWATCH);
        checkBoxUseTimer = (CheckBoxPreference)findPreference(KEY_TIMER);
        checkBoxUseStopWatch.setOnPreferenceChangeListener(this);
        checkBoxUseTimer.setOnPreferenceChangeListener(this);

        Preference about = findPreference("about");
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
        }
        assert about != null;
        if (version != null) {
            about.setSummary(getString(R.string.about_summary, version));
        } else {
            about.setSummary(getString(R.string.about_summary));

        }

        final String finalVersion = version;
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                DialogProvider.createAboutDialog(SettingsActivity.this, finalVersion).show();
                return false;
            }
        });
        Preference review = findPreference("rate_app");

        assert review != null;
        review.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=myApp.trainingdiary"));
                    startActivity(intent);
                } catch (Throwable e) {
                    Log.e(Const.LOG_TAG, e.getMessage());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=myApp.trainingdiary"));
                    startActivity(intent);
                }
                return false;
            }
        });
        Preference backup = findPreference("backup");

        assert backup != null;
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                try {
                    BackupManager.backupToSD(SettingsActivity.this);
                    Toast.makeText(SettingsActivity.this,
                            getString(R.string.backup_success) +
                                    BackupManager.BACKUP_FOLDER + "/" +
                                    DBHelper.DATABASE_NAME, Toast.LENGTH_LONG).show();
                } catch (Throwable e) {
                    Log.e(Const.LOG_TAG, e.getMessage(), e);
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
                return false;
            }
        });

        final AlertDialog restoreDialog = DialogProvider.createSimpleDialog(SettingsActivity.this,
                getString(R.string.restoration_data_base),getString(R.string.restoration_ask_for_continue),
                getResources().getString(R.string.YES),getResources().getString(R.string.NO),  new DialogProvider.SimpleDialogClickListener(){

            @Override
            public void onPositiveClick() {
                try {
                    BackupManager.restoreFromSD(SettingsActivity.this);
                    Toast.makeText(SettingsActivity.this,
                            getString(R.string.restore_success),
                            Toast.LENGTH_LONG).show();
                } catch (Throwable e) {
                    Log.e(Const.LOG_TAG, e.getMessage(), e);
                    Toast.makeText(SettingsActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNegativeClick() {
                Toast.makeText(SettingsActivity.this,
                        getString(R.string.Operation_cancelation),
                        Toast.LENGTH_LONG).show();
            }
        });
        Preference restore = findPreference("restore");

        assert restore != null;
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                restoreDialog.show();
                return false;
            }
        });

        set_timer_time = findPreference("set_timer_time");
        SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
        Long storedTime = pref.getLong("set_timer_time", 0L);
        int min = 5;
        int sec = 0;
        if(storedTime != 0L){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(storedTime);
            min = cal.get(Calendar.MINUTE);
            sec = cal.get(Calendar.SECOND);
        }
        set_timer_time.setSummary(""+min+" мин. "+sec+" сек.");
        set_timer_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                showTimePickerDialog();
                return false;
            }
        });

        Preference set_timer_sound = findPreference("set_timer_sound");
        set_timer_sound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                return false;
            }
        });

//        Раздел PRO
//        Preference max_Weight = findPreference("max_Weight");
//        assert max_Weight !=null;
//        max_Weight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference arg0) {
//                Intent MaxWeightCalculatorActivity = new Intent(context, MaxWeightCalculatorActivity.class);
//                startActivity(MaxWeightCalculatorActivity);
//                return false;
//            }
//        });
//
//        Preference max_Repeat = findPreference("max_Repeat");
//        assert max_Repeat !=null;
//        max_Repeat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference arg0) {
//
//                return false;
//            }
//        });
//
//        Preference work_Weight = findPreference("work_Weight");
//        assert work_Weight !=null;
//        work_Weight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference arg0) {
//
//                return false;
//            }
//        });
//
//        Preference percent = findPreference("percent");
//        assert percent !=null;
//        percent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference arg0) {
//
//                return false;
//            }
//        });
//
//        Preference cal = findPreference("cal");
//        assert cal !=null;
//        cal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference arg0) {
//                Intent calActivity = new Intent(context, CalendarActivity.class);
//                startActivity(calActivity);
//                return false;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        String key =  preference.getKey();

        if(key.equalsIgnoreCase(KEY_TIMER)){
            if(!checkBoxUseTimer.isChecked()){
                checkBoxUseStopWatch.setChecked(false);
            }
            else {
                if(!checkBoxUseStopWatch.isChecked())
                    return false;
            }
        }
        else if(key.equalsIgnoreCase(KEY_STOPWATCH)){
            if(!checkBoxUseStopWatch.isChecked()){
                checkBoxUseTimer.setChecked(false);
            }
            else {
                if(!checkBoxUseTimer.isChecked())
                    return false;
            }
        }
        return true;
    }

    private void showTimePickerDialog(){

        final Dialog d = new Dialog(this);
        SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
        Long storedTime = pref.getLong("set_timer_time", 0L);
        int min = 5;
        int sec = 0;
        if(storedTime != 0L){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(storedTime);
            min = cal.get(Calendar.MINUTE);
            sec = cal.get(Calendar.SECOND);
        }
        d.setTitle(""+min+" мин. "+sec+" сек.");
        d.setContentView(R.layout.number_picker_dialog);
        Button btnOk = (Button) d.findViewById(R.id.set_timer_btn_yes);
        Button btnNo = (Button) d.findViewById(R.id.set_timer_btn_no);
        final WheelView wheelMin = (WheelView) d.findViewById(R.id.set_timer_wheel_min);
        final WheelView wheelSec = (WheelView) d.findViewById(R.id.set_timer_wheel_sec);

        NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.text);
        wheelMin.setViewAdapter(minAdapter);
        wheelMin.setCyclic(true);
        wheelMin.setCurrentItem(min);
        wheelMin.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                d.setTitle(""+newValue+" мин. "+wheelSec.getCurrentItem()+" сек.");
            }
        });

        NumericWheelAdapter secAdapter = new NumericWheelAdapter(this, 0, 59, "%02d");
        secAdapter.setItemResource(R.layout.wheel_text_item);
        secAdapter.setItemTextResource(R.id.text);
        wheelSec.setViewAdapter(minAdapter);
        wheelSec.setCyclic(true);
        wheelSec.setCurrentItem(sec);
        wheelSec.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                d.setTitle(""+wheelMin.getCurrentItem()+" мин. "+newValue+" сек.");
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int min = wheelMin.getCurrentItem();
                int sec = wheelSec.getCurrentItem();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE,min);
                c.set(Calendar.SECOND, sec);
                SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("set_timer_time",c.getTimeInMillis());
                editor.commit();
                set_timer_time.setSummary(""+min+" мин. "+sec+" сек.");
                d.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
}
