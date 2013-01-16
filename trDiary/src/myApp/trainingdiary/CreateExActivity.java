package myApp.trainingdiary;

import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CreateExActivity extends Activity implements OnClickListener
{
	
	EditText etNameEx;
	Button btnCreate;
	Button btnCancel;
	RadioButton radioPower;
	RadioButton radioCycle;
	DBHelper dbHelper;
	final String LOG_TAG = "myLogs";
	String trainingName; 
	int parent; 

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_ex);				
		etNameEx = (EditText)findViewById(R.id.etNameEx);
		btnCreate = (Button)findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);	
		radioPower = (RadioButton)findViewById(R.id.radioPower);
		radioCycle = (RadioButton)findViewById(R.id.radioCycle);
		
		parent = getIntent().getExtras().getInt("parent");
		
		// если родитель = 1 значит активность вызвана из диалога выбора упражнений, необходимо получать и передавать название тренировки
		if(parent == 1) 
		trainingName = getIntent().getExtras().getString("name_string");
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_create_ex, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{
	    ContentValues cv = new ContentValues();
	    String nameEx = etNameEx.getText().toString();	
	    dbHelper = new DBHelper(this);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    String typeEx;

	    switch (arg0.getId()) 
	    {
	    case R.id.btnCancel:
	        Intent intentCancel = new Intent(this, EditExActivity.class);
	        startActivity(intentCancel);
	      break;
	    case R.id.btnCreate:
		    if(nameEx.length() == 0)
		    {
		    	 Toast.makeText(this, "Ќе указано название упражнени€", Toast.LENGTH_SHORT).show();
		    	 return;
		    }
		    
		    if(radioPower.isChecked())
		    {
		    	typeEx = "1";
		    }
		    else if(radioCycle.isChecked())
		    {
		    	typeEx = "2";
		    }
		    else
		    {
		    	typeEx = "1";
		    }
		    
		    cv.put("exercise", nameEx); //Ќазвание упр
		    cv.put("type", typeEx);     //тип упр 1-силовое 2-циклическое	
		    db.insert("ExerciseTable", null, cv);		    
		    Toast.makeText(this, "Ќовое упражнение добавлено", Toast.LENGTH_SHORT).show();
		    etNameEx.setText("");
		    dbHelper.close();
	      break;	   
	    default:
	      break;
	    }	    	    
	}
	
	@Override
	public void onBackPressed() {

		super.onBackPressed();		
		if(parent == 1)
		{
	        Intent SelectExToAddInTrActivity = new Intent(this, SelectExToAddInTrActivity.class);
	        SelectExToAddInTrActivity.putExtra("trainingName", trainingName);
	        startActivity(SelectExToAddInTrActivity);
			finish();				
		}
		else
		{
	        Intent SelectExToAddInTrActivity = new Intent(this, EditExActivity.class);
	        startActivity(SelectExToAddInTrActivity);
			finish();	
		}
	}
	
}
