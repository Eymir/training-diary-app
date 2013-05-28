package myApp.trainingdiary.SortExAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;


public class SortExInTrainingDay extends Activity {

	String strNameTr;
	DBHelper dbHelper;
	final String LOG_TAG = "myLogs";
	TableLayout TableLaySetSort;
	ListView lvExToSort;
	String ParsedName;
	int ParsedId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort_ex_in_training_day);		
		strNameTr = getIntent().getExtras().getString("trainingName");
		lvExToSort = (ListView)findViewById(R.id.lvExToSort);
		
		getExtoSort();
		
		lvExToSort.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
			    String Str = (String)lvExToSort.getItemAtPosition(arg2).toString();
			    ParsedName = ParserName(Str);
			    ParsedId = ParserId(Str);
			    
			    if(ParsedName.equalsIgnoreCase("������� ���")) {			    	
			    }
			    else{

			    	openSetSortEx();			    	
			    }				
			}			
		});					
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sort_ex_in_training_day, menu);
		return true;
	}
	
	private void getExtoSort() {
				
		dbHelper = DBHelper.getInstance(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase(); 
	    
	    String sqlQuery  = "select " +
				"prog.exercise as Ex, " +
				"prog.trainingname as Tr, " +
				"prog.exidintr as id," +
				"Ex.type as type " +
					"from TrainingProgTable as prog " +
							"inner join ExerciseTable as Ex " +
				"on prog.exercise=Ex.exercise " +
				"where Tr = ? "+
				"ORDER BY id";
	    
	    String[] args = {strNameTr};	    
        Cursor c = db.rawQuery(sqlQuery, args);
        int size = c.getCount();  
        LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>();
        
        if(size > 0)
        {                	              	
        	if (c.moveToFirst()) 
        	{        		
	          int nameColIndex = c.getColumnIndex("Ex");	      
	          int idid = c.getColumnIndex("id");

	          do 
	          { 	        	             
	            list.put(c.getString(nameColIndex), c.getInt(idid));
	            
	          } while (c.moveToNext());
	        } 	
	        c.close();
	        dbHelper.close();	              	        
        }
        else 
        {
        	Toast.makeText(this, "������ ���������� ����", Toast.LENGTH_SHORT).show();
        	return;
		}
        
        final String ATTRIBUTE_NAME_TEXT = "text";
        final String ATTRIBUTE_NAME_ID = "id";
        final String ATTRIBUTE_NAME_R = "raz";
        
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(list.size());
        Map<String, Object> m;
        
        for (Entry<String, Integer> entry: list.entrySet()) {
            String name= entry.getKey();
            Integer id = entry.getValue();
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TEXT, name);            
            m.put(ATTRIBUTE_NAME_ID, id);
            m.put(ATTRIBUTE_NAME_R, " - ");
            data.add(m);           
        }
        
        // ������ ���� ���������, �� ������� ����� �������� ������
        String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_R, ATTRIBUTE_NAME_ID};
        // ������ ID View-�����������, � ������� ����� ��������� ������
        int[] to = {R.id.lvOnlyText2, R.id.lvOnlyFormat, R.id.lvOnlyText1};        
        // ������� �������
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.lvonlytrxt, from, to);        
        // ���������� ������ � ����������� ��� �������
        lvExToSort.setAdapter(sAdapter);          
	}
		
	@Override
	public void onBackPressed() {
//
//		super.onBackPressed();
//        Intent AddExerciseActivity = new Intent(this, AddExerciseActivity.class);
//        AddExerciseActivity.putExtra("name_string", strNameTr);
//        startActivity(AddExerciseActivity);
//        finish();
	}
	
	private String ParserName(String nonParsed)
	{
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index+5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);		
		int index3 = Parsed.indexOf(", raz=");		
		String Parsed2 = Parsed.substring(0, index3);
		return Parsed2;

	}
	
	private int ParserId(String nonParsed) {
		
		int index = nonParsed.indexOf("id=");
		String HalfParsed = nonParsed.substring(index+3);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);			
		int index3 = Parsed.indexOf(", text=");		
		String Parsed2 = Parsed.substring(0, index3);
		int ID = Integer.parseInt(Parsed2);
		return ID;		
	}
	
	//���������� �������� ��� ��������� ������ ID  ����������
	private void openSetSortEx() {
		
   	   	Intent SetSortEx = new Intent(this, SetSortEx.class);
   	  	SetSortEx.putExtra("trainingName", strNameTr);
   	  	SetSortEx.putExtra("ExName", ParsedName);
   	  	SetSortEx.putExtra("num", ParsedId);
        startActivity(SetSortEx);
        finish();

	}

}
