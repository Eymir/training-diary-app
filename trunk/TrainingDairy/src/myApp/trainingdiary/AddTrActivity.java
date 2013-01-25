package myApp.trainingdiary;

import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import myApp.trainingdiary.R;


public class AddTrActivity extends Activity implements OnClickListener
{
	
	Button btnCreateTr;
	Button btnCancelTr;	
	EditText editTextName;	
	DBHelper dbHelper;	
	final String LOG_TAG = "myLogs";
	boolean NewRecord;
	String NameTr;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_tr);
		
		btnCancelTr = (Button)findViewById(R.id.btnCancelTr);
		btnCancelTr.setOnClickListener(this);		
		btnCreateTr = (Button)findViewById(R.id.btnCreateTr);
		btnCreateTr.setOnClickListener(this);				
		editTextName = (EditText)findViewById(R.id.editTextName);		
		dbHelper = new DBHelper(this);	
		
		//Признак новой записи или переименование текущей
		NewRecord = getIntent().getExtras().getBoolean("NewRecord");
		
		//Если запись не новая то получаем имя тренировки для вывода на активити
		if (!NewRecord) {
			NameTr = getIntent().getExtras().getString("trName");
			editTextName.setText(NameTr);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_add_tr, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{		
	   
	    ContentValues cv = new ContentValues();	    
	    String name = editTextName.getText().toString();	    	    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
		
	    switch (arg0.getId()) 
	    {
	    case R.id.btnCancelTr:
	        Intent intentCancel = new Intent(this, MainActivity.class);
	        startActivity(intentCancel);
	        finish();
	      break;
	    case R.id.btnCreateTr:
	    	
	    	//Проверям поле ввода на пустоту
		    if(name.length() == 0)
		    {
		    	 Toast.makeText(this, "Не указано название тренировки", Toast.LENGTH_LONG).show();
		    	 return;
		    }
		    
	    	//Пишем новую тренировку
	    	if (NewRecord)
	    	{	    			    		    	    
			    cv.put("name", name);
			    long rowID = db.insert("Trainingtable", null, cv);
			    Log.d(LOG_TAG, "row inserted, ID = " + rowID);		    
			    Toast.makeText(this, "Новая тренировка добавлена", Toast.LENGTH_LONG).show();
			    //editTextName.setText("");
			    db.close();
		        Intent intentMain = new Intent(this, MainActivity.class);
		        startActivity(intentMain);
		        finish();
	    	}
	    	else //переименовываем имеющуюся тренировку 
	    	{
	    		cv.put("name", name);
	    		db.update("Trainingtable", cv, "name = ?", new String[] {NameTr});	 
	    		db.close();
		        Intent intentMain = new Intent(this, MainActivity.class);
		        startActivity(intentMain);
		        finish();
			}
	      break;	   
	    default:
	      break;
	    }		
	    dbHelper.close();	    
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
        finish();
	}
		
}
