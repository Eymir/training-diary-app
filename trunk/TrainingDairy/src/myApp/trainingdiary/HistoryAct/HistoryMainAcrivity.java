package myApp.trainingdiary.HistoryAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import myApp.trainingdiary.R;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/*
 * Основная активити с историей тренировок, тренировки сгруппированы по дате
 */

public class HistoryMainAcrivity extends Activity
{
	ListView lvMainHistory;
	DBHelper dbHelper;
	 final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "--- on create ---");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_main_acrivity);
		lvMainHistory = (ListView)findViewById(R.id.lvMainHistory);
		Log.d(LOG_TAG, "--- init ---");
		GetTrainingsDay();
		
		lvMainHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
			    String Str = (String)lvMainHistory.getItemAtPosition(arg2).toString();
			    String ParsedName = ParserOnItemClick(Str);	
			    if(ParsedName.equalsIgnoreCase("<Записей нет>")) {			    	
			    }
			    else{
			    	openHistoryDetails(ParsedName);
			    }	
			}			
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_history_main_acrivity, menu);
		return true;
	}
	
	private void GetTrainingsDay()
	{
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    String[] Column = {"trainingdate"};	       	 
        Cursor c = db.query("TrainingStat", Column, null, null, "trainingdate", null, null);        
        int size = c.getCount();
        SortedMap<String, String> list = new TreeMap<String, String>();
		
        if(size > 0)
        {                	              	
        	if (c.moveToFirst()) 
        	{        		        	  
	          int nameColIndex = c.getColumnIndex("trainingdate");	
	          
	          do 
	          {  	        	
	            list.put(c.getString(nameColIndex), "0");
	          } while (c.moveToNext());
	        } 
	
	        c.close();
	        dbHelper.close();
	        
	        int imgTr = R.drawable.ico_train;
	        final String ATTRIBUTE_NAME_TEXT = "text";
	        final String ATTRIBUTE_NAME_IMAGE = "image";	
	        
	        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(list.size());
	        Map<String, Object> m;
	        
	        for (Map.Entry<String, String> entry: list.entrySet()) 
	        {
	            String name= entry.getKey();	          
	            m = new HashMap<String, Object>();
	            m.put(ATTRIBUTE_NAME_TEXT, name);
	            m.put(ATTRIBUTE_NAME_IMAGE, imgTr);		            
	            data.add(m);
	        }
	        
	        String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE};
	        int[] to = {R.id.label, R.id.icon};
	        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.exerciseslv, from, to);        
	        lvMainHistory.setAdapter(sAdapter);  	        
        }
        else 
        {
        	Toast.makeText(this, "В истории нет ни одной тренировки", Toast.LENGTH_LONG).show();
        	String[] arrTrainings = new String[]{"<Записей нет>"};
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrTrainings);		
	        lvMainHistory.setAdapter(adapter);
        	return;
		}   
	}
	
	private void openHistoryDetails(String DateTraining)
	{
	    Intent intentOpenHistoryDetails = new Intent(this, History_detailsv2.class);
	    intentOpenHistoryDetails.putExtra("AllEx", true);
	    intentOpenHistoryDetails.putExtra("strDateTr", DateTraining);	    
        startActivity(intentOpenHistoryDetails);	
	}
	
	private String ParserOnItemClick(String nonParsed)
	{
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index+5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);		
		return Parsed;
	}

}
