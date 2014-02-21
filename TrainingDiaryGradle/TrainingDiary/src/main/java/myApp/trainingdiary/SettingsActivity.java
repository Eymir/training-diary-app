package myApp.trainingdiary;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Calendar;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.service.ResponseUserData;
import myApp.trainingdiary.service.TrainingDiaryCloudService;
import myApp.trainingdiary.service.UserData;
import myApp.trainingdiary.utils.BackupManager;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.SoundPlayer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    public static final String GOOGLE_AUTH_TYPE = "com.google";
    private DBHelper dbHelper;
    private Context context;

    private CheckBoxPreference checkBoxUseStopWatch;
    private CheckBoxPreference checkBoxUseTimer;

    private Preference set_timer_time;
    private Preference set_timer_sound;
    private Preference cloud_account;

    private Cursor cursorMelody;

    SharedPreferences preferences;

    private static final String KEY_STOPWATCH = "use_stopwatch";
    private static final String KEY_TIMER = "use_timer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = this.getApplicationContext();

        //google analytics
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().setContext(this);

        preferences = getSharedPreferences("preferences", MODE_PRIVATE);


        ListPreference workout_expiring = (ListPreference) findPreference(Const.KEY_WORKOUT_EXPIRING);
        workout_expiring.setOnPreferenceChangeListener(this);
        workout_expiring.setSummary(workout_expiring.getEntry());
        checkBoxUseStopWatch = (CheckBoxPreference) findPreference(KEY_STOPWATCH);
        checkBoxUseTimer = (CheckBoxPreference) findPreference(KEY_TIMER);
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
                getString(R.string.restoration_data_base), getString(R.string.restoration_ask_for_continue),
                getResources().getString(R.string.YES), getResources().getString(R.string.NO), new DialogProvider.SimpleDialogClickListener() {

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


        cloud_account = findPreference("cloud_account");
        assert cloud_account != null;
        cloud_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                chooseAccount();
                return false;
            }
        });

        Preference cloud_upload = findPreference("cloud_upload");
        assert cloud_upload != null;
        cloud_upload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                try {
                    SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                    String account = pref.getString(Const.ACCOUNT_PREF, null);
                    if (account != null) {
                        UserData userData = new UserData();
                        File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
                        userData.setDb(IOUtils.toString(new FileInputStream(currentDB), Const.UTF_8));
                        userData.setRegistrationId(account);
                        userData.setRegistrationChannel(GOOGLE_AUTH_TYPE);
                        TrainingDiaryCloudService.API.uploadCloudBackup(userData, new Callback<UserData>() {
                            @Override
                            public void success(UserData userData, Response response) {
                                Log.d(Const.LOG_TAG, "Callback.success");
                                Toast.makeText(
                                        SettingsActivity.this,
                                        getResources().getString(
                                                R.string.cloud_backup_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                Log.d(Const.LOG_TAG, "Callback.failure: " + retrofitError);
                                Log.d(Const.LOG_TAG, "Callback.failure.getMessage(): " + retrofitError.getMessage());
                                Log.d(Const.LOG_TAG, "Callback.failure.getResponse(): " + retrofitError.getResponse());
                                Toast.makeText(
                                        SettingsActivity.this,
                                        getResources().getString(
                                                R.string.cloud_backup_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        chooseAccount();
                    }

                } catch (Exception e) {
                    Log.e(Const.LOG_TAG, e.getMessage(), e);
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        Preference cloud_download = findPreference("cloud_download");
        assert cloud_download != null;
        cloud_download.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                try {
                    SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                    String account = pref.getString(Const.ACCOUNT_PREF, null);
                    if (account != null) {
                        UserData userData = new UserData();
                        userData.setRegistrationId(account);
                        userData.setRegistrationChannel(GOOGLE_AUTH_TYPE);
                        Log.d(Const.LOG_TAG, "cloud_download, userData: " + userData);

                        TrainingDiaryCloudService.API.downloadCloudBackup(userData.getRegistrationId(), userData.getRegistrationChannel(),
                                new Callback<ResponseUserData>() {
                                    @Override
                                    public void success(ResponseUserData userData, Response response) {
                                        try {
                                            if (userData != null && userData.getEntity() != null && userData.getEntity().getDb() != null) {
                                                Log.d(Const.LOG_TAG, userData.toString());
                                                Log.d(Const.LOG_TAG, "response: " + response.toString());
                                                File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
                                                Log.d(Const.LOG_TAG, "currentDB: " + currentDB.getAbsolutePath());
                                                Log.d(Const.LOG_TAG, "currentDB.exists: " + currentDB.exists());
                                                if (currentDB.exists()) {
                                                    FileUtils.writeStringToFile(currentDB, userData.getEntity().getDb(), Const.UTF_8);
                                                    Toast.makeText(
                                                            SettingsActivity.this,
                                                            getResources().getString(
                                                                    R.string.backup_cloud_download_success), Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(
                                                        SettingsActivity.this,
                                                        getResources().getString(
                                                                R.string.backup_cloud_download_fail), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(
                                                    SettingsActivity.this,
                                                    getResources().getString(
                                                            R.string.backup_cloud_download_fail), Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        Log.e(Const.LOG_TAG, "retrofitError: " + retrofitError);
                                        Toast.makeText(
                                                SettingsActivity.this,
                                                getResources().getString(
                                                        R.string.backup_cloud_download_fail), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        chooseAccount();
                    }
                } catch (Throwable e) {
                    Log.e(Const.LOG_TAG, e.getMessage(), e);
                    Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                return false;
            }
        }

        );

        set_timer_time =

                findPreference("set_timer_time");

        Long storedTime = preferences.getLong("set_timer_time", 0L);
        int min = 5;
        int sec = 0;
        if (storedTime != 0L)

        {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(storedTime);
            min = cal.get(Calendar.MINUTE);
            sec = cal.get(Calendar.SECOND);
        }

        set_timer_time.setSummary("" + min + " " +

                getResources()

                        .

                                getString(R.string.min)

                + " " + sec + " " +

                getResources()

                        .

                                getString(R.string.sec)

                + "");
        set_timer_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

        {
            public boolean onPreferenceClick(Preference arg0) {
                showTimePickerDialog();
                return false;
            }
        }

        );

        set_timer_sound =

                findPreference("set_timer_sound");

        String uriStr = preferences.getString("set_timer_sound", "");
        String path = "def uri";
        if (uriStr.length() != 0)
            path = getRealPathFromURI(Uri.parse(uriStr)

                    );
        set_timer_sound.setSummary(path);
        set_timer_sound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

        {
            public boolean onPreferenceClick(Preference arg0) {
                selectSound();
                return false;
            }
        }

        );

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

    private void chooseAccount() {
        try {
            AccountManager accountManager = AccountManager.get(SettingsActivity.this);
            assert accountManager != null;
            Account[] accounts = accountManager.getAccountsByType(GOOGLE_AUTH_TYPE);
            if (accountManager.getAccounts() != null)
                Log.d(Const.LOG_TAG, Arrays.asList(accountManager.getAccounts()).toString());
            if (accounts == null || accounts.length == 0) {
                throw new Exception(getString(R.string.accounts_not_found));
            }
            DialogProvider.createChooseStringDialog(SettingsActivity.this, toStringArray(accountManager.getAccounts()), new DialogProvider.ChooseStringDialogListener() {
                public void onClick(String text) {
                    SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Const.ACCOUNT_PREF, text);
                    editor.commit();
                    cloud_account.setSummary(text);
                }
            }).show();

        } catch (Exception e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            Toast.makeText(SettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String[] toStringArray(Account[] accounts) {
        String[] strings = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            strings[i] = accounts[i].name;
        }
        return strings;
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

        String key = preference.getKey();

        if (key.equalsIgnoreCase(KEY_TIMER)) {
            if (!checkBoxUseTimer.isChecked()) {
                checkBoxUseStopWatch.setChecked(false);
            } else {
                if (!checkBoxUseStopWatch.isChecked())
                    return false;
            }
        } else if (key.equalsIgnoreCase(KEY_STOPWATCH)) {
            if (!checkBoxUseStopWatch.isChecked()) {
                checkBoxUseTimer.setChecked(false);
            } else {
                if (!checkBoxUseTimer.isChecked())
                    return false;
            }
        }
        if (preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            CharSequence[] mass = listPref.getEntries();
            preference.setSummary(mass[listPref.findIndexOfValue((String) o)]);
        }


        return true;
    }


    private void showTimePickerDialog() {

        final Dialog d = new Dialog(this);
        SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
        Long storedTime = pref.getLong("set_timer_time", 0L);
        int min = 5;
        int sec = 0;
        if (storedTime != 0L) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(storedTime);
            min = cal.get(Calendar.MINUTE);
            sec = cal.get(Calendar.SECOND);
        }
        d.setTitle("" + min + " " + getResources().getString(R.string.min)
                + " " + sec + " " + getResources().getString(R.string.sec) + "");
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
                d.setTitle("" + newValue + " " + getResources().getString(R.string.min)
                        + " " + wheelSec.getCurrentItem() + " " + getResources().getString(R.string.sec) + "");
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
                d.setTitle("" + wheelMin.getCurrentItem() + " " + getResources().getString(R.string.min)
                        + " " + newValue + " " + getResources().getString(R.string.sec) + "");
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = wheelMin.getCurrentItem();
                int sec = wheelSec.getCurrentItem();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE, min);
                c.set(Calendar.SECOND, sec);
                SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("set_timer_time", c.getTimeInMillis());
                editor.commit();
                set_timer_time.setSummary("" + min + " " + getResources().getString(R.string.min)
                        + " " + sec + " " + getResources().getString(R.string.sec) + "");
                d.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void showSelectSoundDialog() {
        final Uri[] uri = new Uri[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_sound);
        builder.setSingleChoiceItems(cursorMelody, -1, "title", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView lv = ((AlertDialog) dialogInterface).getListView();
                if (cursorMelody.moveToPosition(lv.getCheckedItemPosition())) {
                    int idColumn = cursorMelody.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                    Long thisId = cursorMelody.getLong(idColumn);
                    uri[0] = ContentUris
                            .withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                    SoundPlayer.getInstance(context).playSound(uri[0]);
                }
            }
        });

        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!(((AlertDialog) dialog).getListView().getCheckedItemPosition() == -1)) {
                    set_timer_sound.setSummary(getRealPathFromURI(uri[0]));
                    SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("set_timer_sound", uri[0].toString());
                    editor.commit();
                } else {
                    String uriStr = preferences.getString("set_timer_sound", "");
                    String path = "def uri";
                    if (uriStr.length() != 0)
                        path = getRealPathFromURI(Uri.parse(uriStr));
                    set_timer_sound.setSummary(path);
                }
                SoundPlayer.getInstance(context).stopPlaySound();
            }
        });
        builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SoundPlayer.getInstance(context).stopPlaySound();
            }
        });
        AlertDialog AD = builder.create();
        AD.show();
    }

    private void selectSound() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        cursorMelody = contentResolver.query(uri, null, null, null, null);

        if (cursorMelody != null) {
            if (cursorMelody.getCount() == 0)
                Toast.makeText(context, getResources().getText(R.string.no_media_toast), Toast.LENGTH_SHORT).show();
            else
                showSelectSoundDialog();
        } else {
            Toast.makeText(context, getResources().getText(R.string.no_media_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(idx);
            cursor.close();
            return realPath;
        } else {
            cursor.close();
            return "";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStop(this);
    }


}
