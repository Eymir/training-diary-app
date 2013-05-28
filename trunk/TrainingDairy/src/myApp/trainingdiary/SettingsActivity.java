package myApp.trainingdiary;

import myApp.trainingdiary.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * �������� � ���������� � ���� ��� ����������� ���������� 
 */

public class SettingsActivity extends Activity implements OnClickListener
{
	
	Button btnClearDBEx, btnClearDBTr, btnClearHist;
	TextView tvCountTr, tvCountEx, tvCountHist ;
	DBHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		btnClearDBEx = (Button)findViewById(R.id.btnClearDBEx);
		btnClearDBEx.setOnClickListener(this);				
		btnClearDBTr = (Button)findViewById(R.id.btnClearDBTr);
		btnClearDBTr.setOnClickListener(this);				
		btnClearHist = (Button)findViewById(R.id.btnClearHist);
		btnClearHist.setOnClickListener(this);			
		tvCountTr = (TextView)findViewById(R.id.tvCountTr);
		tvCountHist = (TextView)findViewById(R.id.tvCountHist);
		tvCountEx = (TextView)findViewById(R.id.tvCountEx);				
		refreshTextViews();	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{
	    switch (arg0.getId()) 
	    {
	    case R.id.btnClearDBTr:
			dbHelper = DBHelper.getInstance(this);			    
		    SQLiteDatabase dbTr = dbHelper.getWritableDatabase();	
		    int clearCountTr = dbTr.delete("Trainingtable", null, null);
		    Toast.makeText(this, "�� �� ������� "+ clearCountTr + " �������", Toast.LENGTH_LONG).show();
		    dbHelper.close();
	    	refreshTextViews();
	      break;
	    case R.id.btnClearDBEx:
			dbHelper = DBHelper.getInstance(this);			    
		    SQLiteDatabase dbEx = dbHelper.getWritableDatabase();	
		    int clearCountEx = dbEx.delete("ExerciseTable", null, null);
		    Toast.makeText(this, "�� �� ������� "+ clearCountEx + " �������", Toast.LENGTH_LONG).show();
		    dbHelper.close();
	    	refreshTextViews();
	      break;
	    case R.id.btnClearHist:
			dbHelper = DBHelper.getInstance(this);			    
		    SQLiteDatabase dbHis = dbHelper.getWritableDatabase();	
		    int clearCountHist = dbHis.delete("TrainingStat", null, null);
		    Toast.makeText(this, "�� �� ������� "+ clearCountHist + " �������", Toast.LENGTH_LONG).show();
		    dbHelper.close();
	    	refreshTextViews();
	      break;
	    default:
	      break;
	    }		
	}

	protected void refreshTextViews()
	{	
		dbHelper = DBHelper.getInstance(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();		       	
        Cursor cTr = db.query("Trainingtable", null, null, null, null, null, null);
        String messTr = getResources().getString(R.string.SettingsAct_CountTr);
        messTr = messTr + " = "+ cTr.getCount(); 
        tvCountTr.setText(messTr);
        cTr.close();
              
        Cursor cEx = db.query("ExerciseTable", null, null, null, null, null, null);
        String messEx = getResources().getString(R.string.SettingsAct_CountEx);
        messEx = messEx + " = "+ cEx.getCount(); 
        tvCountEx.setText(messEx);                
        cEx.close();
                
        Cursor cHistory = db.query("TrainingStat", null, null, null, null, null, null);
        String messHist = getResources().getString(R.string.SettingsAct_CountHist);
        messHist = messHist + " = "+ cHistory.getCount(); 
        tvCountHist.setText(messHist);   
                
        dbHelper.close();	
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		refreshTextViews();
	}
	
}
