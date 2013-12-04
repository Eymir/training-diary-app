package myApp.trainingdiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import java.util.Date;

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.service.IServiceListener;
import myApp.trainingdiary.service.TrainingDiaryService;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends PreferenceActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference sendStatistic = findPreference("sendStatistic");
        assert sendStatistic != null;
        sendStatistic.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();

            public boolean onPreferenceClick(Preference arg0) {
                new TrainingDiaryService().sendTrainingStamp(new IServiceListener() {
                    @Override
                    public void sendTrainingStampResponse(String result) {
                        Toast.makeText(getApplication().getApplicationContext(), result, Toast.LENGTH_LONG);
                    }
                }, macAddress, new Date(System.currentTimeMillis() - 5 * 60 * 1000), new Date());
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
