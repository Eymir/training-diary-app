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

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
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
                    intent.setData(Uri.parse("market://details?id=ru.adhoc.truealarm"));
                    startActivity(intent);
                } catch (Throwable e) {
                    Log.e(Consts.LOG_TAG, e.getMessage());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=ru.adhoc.truealarm"));
                    startActivity(intent);
                }
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
