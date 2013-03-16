package myApp.trainingdiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import myApp.trainingdiary.addex.AddExerciseActivity;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Активити в котором мы создаём новые упражнения
 */

public class EditExActivity extends Activity implements OnClickListener
{
	Button btnAddEx;
	final String LOG_TAG = "myLogs";
	DBHelper dbHelper;
	ListView lvExinTrain;
	String ParsedName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG, "--- onCreate ---");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_ex);
		btnAddEx = (Button)findViewById(R.id.btnAddEx);
		btnAddEx.setOnClickListener(this);
		lvExinTrain = (ListView)findViewById(R.id.lvExinTrain);
		
		getEx();
		
		lvExinTrain.setOnItemClickListener(new OnItemClickListener() {
			
			   @Override
			   public void onItemClick(AdapterView<?> Adapter, View view, int position, long arg) {	     			      
			   } 				
		});	
						
		lvExinTrain.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
            	
            	String Str = (String)lvExinTrain.getItemAtPosition(pos).toString();
            	ParsedName = ParserOnItemClick(Str);
            	//Log.d(LOG_TAG, "--- "+ ParsedName +" ---");
            	DelDialog();
                return true;
            }
        }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_edit_ex, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{
	    switch (arg0.getId()) 
	    {
	    case R.id.btnAddEx:
	    	finish();
	        Intent intentOpenCreateEx = new Intent(this, AddExerciseActivity.class);
	        intentOpenCreateEx.putExtra("parent", 0); // родитель = 0 значит вывоз без передачи названия тренировки
	        startActivity(intentOpenCreateEx);
	      break;
	    default:
	      break;
	    }
	}
	
	private void getEx()
	{			
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("ExerciseTable", null, null, null, null, null, null);       
        int size = c.getCount();               
        SortedMap<String, String> list = new TreeMap<String, String>();
        
        if(size > 0)
        {                	              	
        	if (c.moveToFirst()) 
        	{        		
	          int nameColIndex = c.getColumnIndex("exercise");
	          int typeIndex = c.getColumnIndex("type");

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
        	Toast.makeText(this, "Список упражнений пуст", Toast.LENGTH_SHORT).show();
        	list.put("Записей нет", "0");
		}
        
        int imgPow = R.drawable.power;
        int imgCyc = R.drawable.cycle;
        // имена атрибутов для Map
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
               
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {R.id.label, R.id.icon};        
        // создаем адаптер
//        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.exerciseslv, from, to);        
//        // определяем список и присваиваем ему адаптер
//        lvExinTrain.setAdapter(sAdapter);                  	   	
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		getEx();
	}
		
	private String ParserOnItemClick(String nonParsed)
	{
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index+5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);		
		return Parsed;
	}
		    
    private void deleteEx()
	{
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();	    
	    String[] args = {ParsedName};	
	    int isDeleted = db.delete("ExerciseTable", "exercise=?", args);	    
//	    String sqlQuery  = "delete from ExerciseTable where exercise = ?";
//	    String[] args = {ParsedName};	    
//      db.rawQuery(sqlQuery, args);                
        dbHelper.close();
        if(isDeleted == 1)
        Toast.makeText(this, "Упражнение удалено - "+ ParsedName, Toast.LENGTH_SHORT).show();
        getEx();
	}
    
    private void DelDialog() {
    	
    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle("Удаление упражнение?");
	      adb.setMessage("Удалить упражнение - "+ParsedName+" из тренировки?");     
	      adb.setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   deleteEx();
               }
           });
        adb.setNegativeButton(getResources().getString(R.string.NO), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
               }
           });        	    
	    adb.create().show();
	}
    
    @Override
    public void onBackPressed() {
    	finish();
    	super.onBackPressed();
        Intent SuperMainActivity = new Intent(this, SuperMainActivity.class);
        startActivity(SuperMainActivity);
    	
    }
    
}
