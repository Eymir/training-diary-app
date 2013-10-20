package myApp.trainingdiary;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Measure;
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
    }

    protected void refreshTextViews() {
        TextView tvCountTr = (TextView) findViewById(R.id.tvCountTr);
        TextView tvCountEx = (TextView) findViewById(R.id.tvCountEx);
        TextView tvCountHist = (TextView) findViewById(R.id.tvCountHist);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int cTr = dbHelper.READ.getTrainingsCount(db);
        String messTr = getResources().getString(R.string.count_tr);
        messTr = messTr + ": " + cTr;
        tvCountTr.setText(messTr);

        int cEx = dbHelper.READ.getExerciseCount(db);
        String messEx = getResources().getString(R.string.count_ex);
        messEx = messEx + ": " + cEx;
        tvCountEx.setText(messEx);

        int cHs = dbHelper.READ.getTrainingStatCount(db);
        String messHist = getResources().getString(R.string.count_tr_stat);
        messHist = messHist + ": " + cHs;
        tvCountHist.setText(messHist);

        dbHelper.close();
    }

}
