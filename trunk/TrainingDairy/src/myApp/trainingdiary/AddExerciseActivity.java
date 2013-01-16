package myApp.trainingdiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import myApp.trainingdiary.SetResultAct.SetCycleResultActivity;
import myApp.trainingdiary.SetResultAct.SetPowerResultActivity;
import myApp.trainingdiary.SortExAct.SortExInTrainingDay;
import myApp.trainingdiary.forBD.DBHelper;

/*
 * Активити со спиком упражнений для выбранного тренировочного дня и возможностью добавления новых уже имеющихся
 *  упражнений из базы
 */

public class AddExerciseActivity extends Activity implements OnClickListener
{
	DBHelper dbHelper;
	String strNameTr;
	Button btnAddEx;
	final String LOG_TAG = "myLogs";
	ListView lvEx;
	String ParsedName;
	
	//Манюшка - для изменения порядка упражнений
	final int MENU_SORTING_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		
		strNameTr = getIntent().getExtras().getString("name_string");	
		
		btnAddEx = (Button)findViewById(R.id.btnAddEx);
		btnAddEx.setOnClickListener(this);
		lvEx = (ListView)findViewById(R.id.lvEx);
		getEx();
		
		lvEx.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
			    String Str = (String)lvEx.getItemAtPosition(arg2).toString();
			    String ParsedName = ParserOnItemClick(Str);	
			    if(ParsedName.equalsIgnoreCase("Записей нет")) {			    	
			    }
			    else{
			    	openExActivityToAddresult(ParsedName);
			    }				
			}			
		});	
		
		lvEx.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
            	
            	String Str = (String)lvEx.getItemAtPosition(pos).toString();
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
		menu.add(0, MENU_SORTING_ID, 1, "Изменить порядок");
		return true;
	}
	
	//получаем упражнения и выводим их во вьюшку....теперь ещё и сортируем
	protected void getEx()
	{
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase(); 
	    
	    String sqlQuery  = "select " +
	    						"prog.exercise as Ex, " +
	    						"prog.trainingname as Tr, " +
	    						"Ex.type as type " +
	    							"from TrainingProgTable as prog " +
	    									"inner join ExerciseTable as Ex " +
	    						"on prog.exercise=Ex.exercise " +
	    						"where Tr = ?";
	    
	    String[] args = {strNameTr};	    
        Cursor c = db.rawQuery(sqlQuery, args);
        int size = c.getCount();       
		SortedMap<String, String> list = new TreeMap<String, String>();
        
        if(size > 0)
        {                	              	
        	if (c.moveToFirst()) 
        	{        		
	          int nameColIndex = c.getColumnIndex("Ex");
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
        	//Toast.makeText(this, "Список упражнений пуст", Toast.LENGTH_SHORT).show();
        	list.put("Записей нет", "0");
        	showEmtyDialog();
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
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.exerciseslv, from, to);        
        // определяем список и присваиваем ему адаптер
        lvEx.setAdapter(sAdapter);            		
	}

	@Override
	public void onClick(View arg0)
	{ 		
	    switch (arg0.getId()) 
	    {
	    case R.id.btnAddEx:
	        Intent SelectEx = new Intent(this, SelectExToAddInTrActivity.class);
	        SelectEx.putExtra("trainingName", strNameTr);
	        startActivity(SelectEx);
	        this.finish();
	      break;
	    default:
	      break;
	    }		       
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		getEx();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getEx();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent BackToMain = new Intent(this, MainActivity.class);
		startActivity(BackToMain);
		finish();
	}
	
	private void openExActivityToAddresult(String nameEx)
	{
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();	    
	    String sqlQuery  = "select type from ExerciseTable where exercise = ?";	    
	    String[] args = {nameEx};	    
        Cursor c = db.rawQuery(sqlQuery, args);                       
        c.moveToFirst();          
        int exNameIndex = c.getColumnIndex("type");
        String type = c.getString(exNameIndex);	
        c.close();
        dbHelper.close();         
        //Log.d(LOG_TAG, "--- type  --- = " + type);
        
        if(type.equalsIgnoreCase("1")) 
        {
    	    Intent intentOpenPowerResultActivity = new Intent(this, SetPowerResultActivity.class);
    	    intentOpenPowerResultActivity.putExtra("nameEx", nameEx);
    	    intentOpenPowerResultActivity.putExtra("nameTr", strNameTr);
    	    startActivity(intentOpenPowerResultActivity);
        }
        else 
        {
    	    Intent intentOpenCycleResultActivity = new Intent(this, SetCycleResultActivity.class);
    	    intentOpenCycleResultActivity.putExtra("nameEx", nameEx);
    	    intentOpenCycleResultActivity.putExtra("nameTr", strNameTr);
    	    startActivity(intentOpenCycleResultActivity);
		}		
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
	    int isDeleted = db.delete("TrainingProgTable", "exercise=?", args);	                 
        dbHelper.close();
        if(isDeleted == 1)
        Toast.makeText(this, "Упражнение удалено - "+ ParsedName, Toast.LENGTH_SHORT).show();
        getEx();
	}  
    
    private void DelDialog() {
    	
    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle("Удаление упражнения из тренировки");
	      adb.setMessage("Удалить упражнение - "+ParsedName+" ?");
	      adb.setNegativeButton("Нет", null);      
	      adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   deleteEx();
               }
           });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
               }
           });        	    
	    adb.create().show();
	}
    
    private void showEmtyDialog() {

    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle("Отсутствуют упражнения");
	      adb.setMessage("В созданной Вами тренировке пока нет ни одного упражнения, " +
	      		" Добавить упражнения в тренировку?");     
	      adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
          	   startEditExAct();
          	   
             }
         });
      adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
          	   startComeBackAct();
             }
         });        	    
	    adb.create().show();

	}
    
    private void startEditExAct() {
		
   	    Intent SelectExToAddInTrActivity = new Intent(this, SelectExToAddInTrActivity.class);
        SelectExToAddInTrActivity.putExtra("trainingName", strNameTr);
        startActivity(SelectExToAddInTrActivity);
        finish();

	}
    
    private void startComeBackAct() {
	
   	    Intent MainActivity = new Intent(this, MainActivity.class);
        startActivity(MainActivity);	
        finish();

	}
     
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
		switch (item.getItemId()) 
		{
			case MENU_SORTING_ID:
		        Intent intentOpenSort = new Intent(this, SortExInTrainingDay.class);
		        intentOpenSort.putExtra("trainingName", strNameTr);
		        startActivity(intentOpenSort);
		        finish();
				break;
		}
    	return super.onOptionsItemSelected(item);
    }
	
}
