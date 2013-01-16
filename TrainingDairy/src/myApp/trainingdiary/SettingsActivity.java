package myApp.trainingdiary;

import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Активити с настроками и пока что добавлялкой упражнений 
 */

public class SettingsActivity extends Activity implements OnClickListener
{
	
	Button btnClearDBEx;
	Button btnClearDBTr;
	Button btnEditEx;
	TextView textViewNumTr, tvHistoryCount;
	TextView textViewNumEx;
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
		btnEditEx = (Button)findViewById(R.id.btnEditEx);
		btnEditEx.setOnClickListener(this);
		tvHistoryCount = (TextView)findViewById(R.id.tvHistoryCount);		
		textViewNumTr = (TextView)findViewById(R.id.textViewNumTr);
		textViewNumEx = (TextView)findViewById(R.id.textViewNumEx);
		
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
			dbHelper = new DBHelper(this);			    
		    SQLiteDatabase db = dbHelper.getWritableDatabase();	
		    int clearCount = db.delete("Trainingtable", null, null);
		    Toast.makeText(this, "Из БД удалено "+ clearCount + " записей", Toast.LENGTH_LONG).show();
	    	refreshTextViews();
	      break;
	    case R.id.btnClearDBEx:
	    	Toast.makeText(this, "Действие пока не назначено ", Toast.LENGTH_LONG).show();
	      break;
	    case R.id.btnEditEx:
	        Intent intentEditEx = new Intent(this, EditExActivity.class);
	        startActivity(intentEditEx);
	      break;
	    default:
	      break;
	    }	
	}

	protected void refreshTextViews()
	{	
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();		       	
        Cursor cTr = db.query("Trainingtable", null, null, null, null, null, null);
        textViewNumTr.setText(" "+ cTr.getCount());
        cTr.close();
        
        Cursor cEx = db.query("ExerciseTable", null, null, null, null, null, null);
        textViewNumEx.setText(" "+ cEx.getCount());                 
        cEx.close();
        
        Cursor cHistory = db.query("TrainingStat", null, null, null, null, null, null);
        tvHistoryCount.setText(" "+ cHistory.getCount());
        
        dbHelper.close();	
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		refreshTextViews();
	}
	
}
