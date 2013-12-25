package myApp.trainingdiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.utils.BackupManager;
import myApp.trainingdiary.utils.Consts;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends PreferenceActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference about = findPreference("about");
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Throwable e) {
            Log.e(Consts.LOG_TAG, e.getMessage(), e);
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
                    Log.e(Consts.LOG_TAG, e.getMessage());
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
                    Log.e(Consts.LOG_TAG, e.getMessage(), e);
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
                    Log.e(Consts.LOG_TAG, e.getMessage(), e);
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

        Preference max_Weight = findPreference("max_Weight");
        assert max_Weight !=null;
        max_Weight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                return false;
            }
        });

        Preference max_Repeat = findPreference("max_Repeat");
        assert max_Repeat !=null;
        max_Repeat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                return false;
            }
        });

        Preference work_Weight = findPreference("work_Weight");
        assert work_Weight !=null;
        work_Weight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                return false;
            }
        });

        Preference percent = findPreference("percent");
        assert percent !=null;
        percent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
