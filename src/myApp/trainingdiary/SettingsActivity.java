package myApp.trainingdiary;

import myApp.trainingdiary.db.DBHelper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends Activity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dbHelper = DBHelper.getInstance(this);
        refreshTextViews();
        Button backup = (Button) findViewById(R.id.backup_button);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getResources().getString(R.string.coming_soon),
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        int cHs = dbHelper.READ.getAllTrainingStats(db);
        String messHist = getResources().getString(R.string.count_tr_stat);
        messHist = messHist + ": " + cHs;
        tvCountHist.setText(messHist);

        dbHelper.close();
    }

}
