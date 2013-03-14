package myApp.trainingdiary;

import myApp.trainingdiary.HistoryAct.HistoryMainAcrivity;
import myApp.trainingdiary.forBD.DBHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SuperMainActivity<T> extends Activity implements OnClickListener
{
	
	Button btnStart, btnAddEx, btnAddTr, btnStat, btnHis, btnSettings, btnExit;
	DBHelper dbHelper;
	 final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_super_main);
		
		btnStart = (Button)findViewById(R.id.btnStart);
		btnAddEx = (Button)findViewById(R.id.btnAddEx);
		btnAddTr = (Button)findViewById(R.id.btnAddTr);
		btnStat = (Button)findViewById(R.id.btnStat);
		btnHis = (Button)findViewById(R.id.btnHist);
		btnSettings = (Button)findViewById(R.id.btnSettings);
		btnExit = (Button)findViewById(R.id.btnExit);
		
		btnStart.setOnClickListener(this);
		btnAddEx.setOnClickListener(this);
		btnAddTr.setOnClickListener(this);
		btnStat.setOnClickListener(this);
		btnHis.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		
		//showinstructions();				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_super_main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0)
	{

	    switch (arg0.getId()) 
	    {
	    case R.id.btnStart:
		    Intent intentOpenMain = new Intent(this, MainActivity.class);
	        startActivity(intentOpenMain);
	      break;
	    case R.id.btnAddEx:
		    Intent intentAddEx = new Intent(this, EditExActivity.class);
	        startActivity(intentAddEx);
	      break;
	    case R.id.btnHist:
	    	//Log.d(LOG_TAG, "--- before run history ---");
		    Intent intentHist = new Intent(this, HistoryMainAcrivity.class);
	        startActivity(intentHist);
	        //Log.d(LOG_TAG, "--- run history ---");
		      break;
	    case R.id.btnStat:
		    Intent intentStat = new Intent(this, StatisticActivity.class);
	        startActivity(intentStat);
	      break;
	    case R.id.btnSettings:
		    Intent intentSet = new Intent(this, SettingsActivity.class);
	        startActivity(intentSet);
		      break;
	    case R.id.btnExit:
	    		//exit();
		      break;
	    default:
	     break;
	    }		
	}
	
//	private void exit()
//	{
//    	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
//	      adb.setTitle("Выйти из приложения?");
//	      adb.setMessage("Вы действительно хотите выйти?");
//	      adb.setNegativeButton("Нет", null);      
//	      adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int id) {
//              	   finish();
//              	   
//                 }
//             });
//          adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int id) {
//                     // User cancelled the dialog
//                 }
//             });        	    
//	    adb.create().show();
//	}
	
	
//	private void showinstructions()
//	{		
//		dbHelper = new DBHelper(this);			    
//	    SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cTr = db.query("Trainingtable", null, null, null, null, null, null);         
//        Cursor cEx = db.query("ExerciseTable", null, null, null, null, null, null);       
//        int sizeTr = cTr.getCount();         
//        int sizeEx = cEx.getCount();
//        
//        if(sizeTr == 0 || sizeEx == 0)
//        {       	
//        	AlertDialog.Builder adb = new AlertDialog.Builder(this);   	
//  	      adb.setTitle("Добро пожаловать в дневник тренировок");
//  	      adb.setMessage("Для того чтобы начать вести дневник необходимо: \n" +
//  	      		" 1. Создать тренирововчный день - кнопка создать тренировку \n" +
//  	      		" 2. Создать необходимое количество упражнений - кнопка создать упражнение \n" +
//  	      		" 3. Добавить Ваши упражнения в тренировочный день - кнопка начать тренировку - добавить упражнение." +
//  	      		" Далее Вы просто нажимаете любое упражнение в тренировке и начинаете записывать результаты." +
//  	      		" Для удаленитя тренировок или упражнений используйте длительное касание.");      
//  	      adb.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
//                   public void onClick(DialogInterface dialog, int id) {
//                   }
//               });       
//  	    adb.create().show();        	        	
//        }
//	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}
