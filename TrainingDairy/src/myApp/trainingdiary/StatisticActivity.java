package myApp.trainingdiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/*
 * јктивити статистики по упражнени€м
 */

public class StatisticActivity extends Activity
{
	
	ListView lvExStat;
	final String LOG_TAG = "myLogs";
	DBHelper dbHelper;
	String ParsedName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic);		
		lvExStat = (ListView)findViewById(R.id.lvExStat);		
		getEx();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_statistic, menu);
		return true;
	}

	private void getEx()
	{			
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    String[] Col = {"exercise","exercisetype"};	    
        Cursor c = db.query("TrainingStat", Col, null, null, "exercise", null, null);       
        int size = c.getCount();  
        Log.d(LOG_TAG, "--- size  --- = " + size);
        SortedMap<String, String> list = new TreeMap<String, String>();
        
        if(size > 0)
        {                	              	
        	if (c.moveToFirst()) 
        	{        		
	          int nameColIndex = c.getColumnIndex("exercise");
	          int typeIndex = c.getColumnIndex("exercisetype");

	          do 
	          { 	        	  
	            list.put(c.getString(nameColIndex), c.getString(typeIndex));
	          } while (c.moveToNext());
	        } 	
	        c.close();
	        dbHelper.close();	              	        
        }
        else 
        {
        	Toast.makeText(this, "—писок упражнений пуст", Toast.LENGTH_SHORT).show();
        	list.put("«аписей нет", "0");
		}
        
        int imgPow = R.drawable.power;
        int imgCyc = R.drawable.cycle;
        // имена атрибутов дл€ Map
        final String ATTRIBUTE_NAME_TEXT = "text";
        final String ATTRIBUTE_NAME_IMAGE = "image";
               
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(list.size());
        Map<String, Object> m;
              
        for (Map.Entry<String, String> entry: list.entrySet()) {
            String name= entry.getKey();
            String type = entry.getValue();
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TEXT, name);
            if(type.equalsIgnoreCase("1")){
            	m.put(ATTRIBUTE_NAME_IMAGE, imgPow);	
            }
            else if (type.equalsIgnoreCase("2")) {
            	m.put(ATTRIBUTE_NAME_IMAGE, imgCyc);
			}
            else
            {
            	m.put(ATTRIBUTE_NAME_IMAGE, R.drawable.ic_launcher);
            }           
            data.add(m);
        }
               
        // массив имен атрибутов, из которых будут читатьс€ данные
        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE };
        // массив ID View-компонентов, в которые будут вставл€ть данные
        int[] to = {R.id.label, R.id.icon};        
        // создаем адаптер
//        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.exerciseslv, from, to);        
//        // определ€ем список и присваиваем ему адаптер
//        lvExStat.setAdapter(sAdapter);                  	   	
	}
	
}
