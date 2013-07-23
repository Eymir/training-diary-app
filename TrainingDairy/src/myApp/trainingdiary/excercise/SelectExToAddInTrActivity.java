package myApp.trainingdiary.excercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/*
 * �������� �� ������� ������ ���������� ��� ���������� � ��������� ����������
 */

public class SelectExToAddInTrActivity extends Activity implements OnClickListener
{
	
	ListView lvExToAdd;
	DBHelper dbHelper;
	String trainingName;
	final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_ex_to_add_in_tr);		
		lvExToAdd = (ListView)findViewById(R.id.lvExToAdd);
		trainingName = getIntent().getExtras().getString("trainingName");
		getEx();
								
		lvExToAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{						
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
			    String Str = (String)lvExToAdd.getItemAtPosition(arg2).toString();
			    String ParsedName = ParserOnItemClick(Str);				
				writeExToTr(ParsedName);			
				setintent();
			}			
		});		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_select_ex_to_add_in_tr, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		
	}
	
	private void getEx()
	{
		dbHelper = DBHelper.getInstance(this);			    
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
        	//list.put("������� ���", "0");
        	//showEmtyDialog();
        	addDefaultEx();
		}
        
        int imgPow = R.drawable.power;
        int imgCyc = R.drawable.cycle;
        // ����� ��������� ��� Map
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
               
        // ������ ���� ���������, �� ������� ����� �������� ������
        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE };
        // ������ ID View-�����������, � ������� ����� ��������� ������
        int[] to = {R.id.label, R.id.icon};        
        // ������� �������
//        SimpleAdapter sAdapter = new SimpleAdapter(this, data, READ.layout.exerciseslv, from, to);
//        // ���������� ������ � ����������� ��� �������
//        lvExToAdd.setAdapter(sAdapter);	    		
	}
	
	private void writeExToTr(String exName)
	{
		dbHelper = DBHelper.getInstance(this);
		ContentValues cv = new ContentValues();
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
		//��� ��������� ����������� � ����������
	    String sqlQuery  = "select exercise from TrainingProgTable where trainingname = ?";
	    String[] args = {trainingName};
        Cursor cEx = db.rawQuery(sqlQuery, args);         
        int countEX = cEx.getCount();
        int numEx = countEX + 1;		
		//
        
	    cv.put("trainingname", trainingName);
	    cv.put("exercise", exName);
	    cv.put("exidintr", numEx);
	    db.insert("TrainingProgTable", null, cv);
	    Toast.makeText(this, "���������� ��������� � ����������", Toast.LENGTH_LONG).show();
	    dbHelper.close();    		
	}
	
	private void setintent()
	{	
//        Intent intentReturnToParent = new Intent(this, AddExerciseActivity.class);
//        intentReturnToParent.putExtra("name_string", trainingName);
//        startActivity(intentReturnToParent);
//        this.finish();
	}
	
	private String ParserOnItemClick(String nonParsed)
	{
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index+5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2-1);		
		return Parsed;
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
//		Intent BackToMain = new Intent(this, AddExerciseActivity.class);
//		BackToMain.putExtra("name_string", trainingName);
//		startActivity(BackToMain);
//		finish();
	}
	
//    private void showEmtyDialog() {
//
//    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
//	      adb.setTitle("��� ����������");
//	      adb.setMessage("� ���� ������ �� ������� �� ������ ����������. ���������� ������� ������ ��� ����������. ������� ����������?");     
//	      adb.setPositiveButton("��", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int id) {
//          	   startCreateEx();
//          	   
//             }
//         });
//      adb.setNegativeButton("���", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int id) {
//          	   startAddEx();
//             }
//         });        	    
//	    adb.create().show();
//
//	}
    
//    private void startCreateEx() {
//
//   	    Intent CreateExActivity = new Intent(this, CreateExActivity.class);
//   	    CreateExActivity.putExtra("parent", 1); //������� �������� = 1 - �������� ���������� �������� ���� � �������� ����������
//   	    CreateExActivity.putExtra("name_string", trainingName);
//        startActivity(CreateExActivity);
//        finish();
//
//	}
//    
//    private void startAddEx() {
//
//   	    Intent AddExerciseActivity = new Intent(this, AddExerciseActivity.class);
//   	    AddExerciseActivity.putExtra("name_string", trainingName);
//        startActivity(AddExerciseActivity);
//        finish();   	
//	}
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	getEx();
    	
    }
    
    private void addDefaultEx() 
    {	
	    	//�������� ������� ���������� �� ������� ;)
	    String[] Ex_name = {getResources().getString(R.string.BaseExBrus),
	    		getResources().getString(R.string.jim_ex_name_base),getResources().getString(R.string.BaseExPodtyag),
	    		getResources().getString(R.string.prised_ex_name_base),getResources().getString(R.string.stanovaya_ex_name_base)};
	    
	    ContentValues cv = new ContentValues();	
	    dbHelper = DBHelper.getInstance(this);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
		    //������ �� � ��������
		    for (int i = 0; i < Ex_name.length; i++) 
		    {
		      cv.clear();
		      cv.put("exercise", Ex_name[i]);
		      cv.put("type", "1");
		      db.insert("ExerciseTable", null, cv);
		    }
	    
	    dbHelper.close();
	}
	
}
