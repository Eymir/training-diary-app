package myApp.trainingdiary;

import myApp.trainingdiary.history.HistoryMainActivity;
import myApp.trainingdiary.excercise.AddExerciseActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.training.TrainingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SuperMainActivity<T> extends Activity implements OnClickListener {

    Button btnStart, btnAddEx, btnStat, btnHis, btnSettings, btnExit;
    DBHelper dbHelper;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);

        btnStart = (Button) findViewById(R.id.start_btn);
        btnAddEx = (Button) findViewById(R.id.ex_btn);
        btnStat = (Button) findViewById(R.id.btnStat);
        btnHis = (Button) findViewById(R.id.btnHist);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnExit = (Button) findViewById(R.id.btnExit);

        btnStart.setOnClickListener(this);
        btnAddEx.setOnClickListener(this);
        btnStat.setOnClickListener(this);
        btnHis.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        dbHelper = DBHelper.getInstance(this);
        int count = dbHelper.getTrainingsCount(dbHelper.getWritableDatabase());
        if (count > 0) {
            btnStart.performClick();
        }
        //showinstructions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_super_main, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.start_btn:
                Intent intentOpenMain = new Intent(this, TrainingActivity.class);
                startActivity(intentOpenMain);
                break;
            case R.id.ex_btn:
                Intent intentAddEx = new Intent(this, AddExerciseActivity.class);
                startActivity(intentAddEx);
                break;
            case R.id.btnHist:
                //Log.d(LOG_TAG, "--- before run history ---");
                Intent intentHist = new Intent(this, HistoryMainActivity.class);
                startActivity(intentHist);
                //Log.d(LOG_TAG, "--- run history ---");
                break;
            case R.id.btnStat:
                Intent intentStat = new Intent(this, StatisticActivity.class);
                startActivity(intentStat);
                break;
            case R.id.btnSettings:
                Intent intentSet = new Intent(this, SettingsActivity.class);
                startActivity(intentSet);
                break;
            case R.id.btnExit:
                finish();
                break;
            default:
                break;
        }
    }

//	private void showinstructions()
//	{		
//		dbHelper = DBHelper.getInstance(this);			    
//	    SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cTr = db.query("Trainingtable", null, null, null, null, null, null);         
//        Cursor cEx = db.query("ExerciseTable", null, null, null, null, null, null);       
//        int sizeTr = cTr.getCount();         
//        int sizeEx = cEx.getCount();
//        
//        if(sizeTr == 0 || sizeEx == 0)
//        {       	
//        	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
//  	      adb.setTitle("����� ���������� � ������� ����������");
//  	      adb.setMessage("��� ���� ����� ������ ����� ������� ����������: \n" +
//  	      		" 1. ������� �������������� ���� - ������ ������� ���������� \n" +
//  	      		" 2. ������� ����������� ���������� ���������� - ������ ������� ���������� \n" +
//  	      		" 3. �������� ���� ���������� � ������������� ���� - ������ ������ ���������� - �������� ����������." +
//  	      		" ����� �� ������ ��������� ����� ���������� � ���������� � ��������� ���������� ����������." +
//  	      		" ��� ��������� ���������� ��� ���������� ����������� ���������� �������.");      
//  	      adb.setPositiveButton("����������", new DialogInterface.OnClickListener() {
//                   public void onClick(DialogInterface dialog, int id) {
//                   }
//               });       
//  	    adb.create().show();        	        	
//        }
//	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
