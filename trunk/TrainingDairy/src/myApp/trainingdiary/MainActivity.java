package myApp.trainingdiary;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import myApp.trainingdiary.HistoryAct.HistoryMainAcrivity;
import myApp.trainingdiary.forBD.DBHelper;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/*
 * активити выбора тренировочного дня
 */

public class MainActivity extends Activity implements OnClickListener
{
	Button btnAddTr;
	DBHelper dbHelper;
	ListView lvTrainings; 		
	String strNamePressedTr;
	String TrainingNameToDel;
	
	final String LOG_TAG = "myLogs";
	final int MENU_SETTINGS_ID = 1;
	final int MENU_COMPLETE_ID = 3;
	final int MENU_STATISTIC_ID = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnAddTr = (Button)findViewById(R.id.btnAddTr);
		btnAddTr.setOnClickListener(this);
		lvTrainings = (ListView)findViewById(R.id.lvTrainings);	
		registerForContextMenu(lvTrainings);				
		GetTrainings();
				
		lvTrainings.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
			    String Str = (String)lvTrainings.getItemAtPosition(arg2).toString();
			    String ParsedNameTr = ParserOnItemClick(Str);	
			    if(ParsedNameTr.equalsIgnoreCase("<Записей нет>")) {			    	
			    }
			    else{
			    	openAddExActivity(ParsedNameTr);
			    }				
			}			
		});	
		
		lvTrainings.setOnItemLongClickListener(new OnItemLongClickListener() {
          
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {           	
            	String Str = (String)lvTrainings.getItemAtPosition(pos).toString();
            	TrainingNameToDel = ParserOnItemClick(Str);           	
            	DelDialog();
                return true;
            }
        }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
    	menu.add(0, MENU_SETTINGS_ID, 3, "Настройки");
    	menu.add(0, MENU_COMPLETE_ID, 1, "История");
    	menu.add(0, MENU_STATISTIC_ID, 2, "Статистика");
		return true;
	}
	
	@Override
	public void onClick(View arg0)
	{
	    switch (arg0.getId()) 
	    {
	    case R.id.btnAddTr:
	        Intent intentAddTr = new Intent(this, AddTrActivity.class);
	        intentAddTr.putExtra("NewRecord", true);
	        startActivity(intentAddTr);
	      break;
	    default:
	      break;
	    }		
	}

	protected void GetTrainings()
	{		
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("Trainingtable", null, null, null, null, null, null);        
        int size = c.getCount();       
		SortedMap<String, String> list = new TreeMap<String, String>();
               
        if(size > 0)
        {               	              	
        	if (c.moveToFirst()) 
        	{        		
	          int nameColIndex = c.getColumnIndex("name");	                    	
	          do 
	          {  	        	
	            list.put(c.getString(nameColIndex), "0");
	          } while (c.moveToNext());
	        } 
	
	        c.close();
	        dbHelper.close();	        	        
	        int imgTr = R.drawable.gant;
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
	        lvTrainings.setAdapter(sAdapter);  
	
        }
        else 
        {
        	showEmptyDilog();
        	String[] arrTrainings = new String[]{"<Записей нет>"};
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrTrainings);       	
	        lvTrainings.setAdapter(adapter);
        	return;
		}       		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		GetTrainings();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
		{
			case MENU_SETTINGS_ID:
		        Intent intentOpenSettings = new Intent(this, SettingsActivity.class);
		        startActivity(intentOpenSettings);
				break;
			case MENU_COMPLETE_ID:
		        Intent intentOpenHistory = new Intent(this, HistoryMainAcrivity.class);
		        startActivity(intentOpenHistory);
				break;
			case MENU_STATISTIC_ID:
		        Intent intentOpenStat = new Intent(this, StatisticActivity.class);
		        startActivity(intentOpenStat);
				break;
		}	
		return super.onOptionsItemSelected(item);
	}
	
	protected void openAddExActivity(String name)
	{
	    Intent intentOpenAddEx = new Intent(this, AddExerciseActivity.class);
	    intentOpenAddEx.putExtra("name_string", name);
        startActivity(intentOpenAddEx);	
        finish();
	}
	
	@Override
	public void onBackPressed()
	{			
	    Intent intentOpenSuper = new Intent(this, SuperMainActivity.class);
        startActivity(intentOpenSuper);	
        finish();
	}
		   
    private void deleteTr()
	{
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();	    
	    String[] args = {TrainingNameToDel};
	    int isDeleted1 = db.delete("Trainingtable", "name=?", args);
	    int isDeleted2 = db.delete("TrainingProgTable", "trainingname=?", args);
        dbHelper.close();
        if(isDeleted1 == 1 && isDeleted2 == 1)
        Toast.makeText(this, "Упражнение удалено - "+ TrainingNameToDel, Toast.LENGTH_SHORT).show();
    	GetTrainings();
	}
    
    private void DelDialog() {
    	
    	//Получаем тексты из ресов
    	String title = getResources().getString(R.string.Dialog_del_tr_title);
    	String Msg = getResources().getString(R.string.Dialog_del_tr_msg);
    	String btnRename = getResources().getString(R.string.Dialog_del_tr_btn_rename);
    	String btnDel = getResources().getString(R.string.Dialog_del_tr_btn_del);  
    	//
    	
    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle(title);
	      adb.setMessage(Msg + " "+ TrainingNameToDel + " ?");     
	      adb.setPositiveButton(btnDel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   deleteTr();
               }
           });
        adb.setNegativeButton(btnRename, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   renameTr();
               }
           });          
	    adb.create().show();
	}
    
    private void renameTr() {

	    Intent addtrIntent = new Intent(this, AddTrActivity.class);
	    addtrIntent.putExtra("NewRecord", false);
	    addtrIntent.putExtra("trName", TrainingNameToDel);
        startActivity(addtrIntent);	
        finish();

	}
    
	private String ParserOnItemClick(String nonParsed)
	{
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index+5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);		
		return Parsed;
	}
	
	private void showEmptyDilog() {

    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
	      adb.setTitle("Список тренировок пуст");
	      adb.setMessage("Вы ещё не создали ни одной тренировки, для того чтобы начать вести дневник создайте хотя бы одну тренировку" +
	      		" и добавьте в неё упражнения. Вы хотите создать тренировку?");     
	      adb.setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   startCreateTrActivity();
            	   
               }
           });
        adb.setNegativeButton(getResources().getString(R.string.NO), new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   startSuperMain();
               }
           });        	    
	    adb.create().show();

	}
	
	private void startCreateTrActivity() {
		
   	    Intent intentOpenAddEx = new Intent(this, AddTrActivity.class);
   	    intentOpenAddEx.putExtra("NewRecord", true);
        startActivity(intentOpenAddEx);	
        finish();
	}
	
	private void startSuperMain() {
		
   	    Intent intentOpenAddEx = new Intent(this, SuperMainActivity.class);
        startActivity(intentOpenAddEx);	
        finish();
	}
	
}
