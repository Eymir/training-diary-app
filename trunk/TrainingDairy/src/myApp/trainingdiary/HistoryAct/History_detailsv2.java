package myApp.trainingdiary.HistoryAct;

import myApp.trainingdiary.R;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class History_detailsv2 extends Activity {
	
    Button btn;
    int counter = 0;
    DBHelper dbHelper;
    String strDateTr;
    String nameEx;
    final String LOG_TAG = "myLogs";
    boolean AllEx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_detailsv2);		
		AllEx = getIntent().getExtras().getBoolean("AllEx");
		
		if(AllEx)
		{
			strDateTr = getIntent().getExtras().getString("strDateTr");
		}
		else
		{
			nameEx = getIntent().getExtras().getString("nameEx");
		}
		
		getHistory();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history_detailsv2, menu);
		return true;
	}
	
	private void getHistory() {
		
		TableLayout table = (TableLayout) findViewById(R.id.tableLay1);
		
		dbHelper = new DBHelper(this); 
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    String sqlQuery;
	    String param = "";
	    	    
	    if(AllEx)
	    {
	    	sqlQuery  = "select exercise, trainingdate  from TrainingStat where trainingdate = ? group by exercise";
	    	param = strDateTr;
	    }
	    else
	    {
		    sqlQuery  = "select exercise, trainingdate from TrainingStat where exercise = ? group by exercise";
		    param = nameEx;	
	    }
	    
	    String[] args = {param};
	    	    
        Cursor cEx = db.rawQuery(sqlQuery, args);             
        String result = "";
        
        if(cEx.moveToFirst())
        {     
        	int exNameIndex = cEx.getColumnIndex("exercise");
        	int exTrDateIndex = cEx.getColumnIndex("trainingdate");
        	
	        do
	        {		        
		        String Exercise = cEx.getString(exNameIndex);
		        String TrDate = cEx.getString(exTrDateIndex);
		        
		        //Вывод названия упражнения
				TableRow row0 = new TableRow(this);
				LayoutParams lprow0 = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				row0.setLayoutParams(lprow0);
				
				TextView textName = new TextView(this);
				textName.setText(Exercise + " " + TrDate);
				textName.setTextSize(30);
				textName.setTextColor(Color.WHITE);
				LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				textName.setLayoutParams(lp1);
				
				row0.addView(textName);
				table.addView(row0, new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				
        		View vv1 = new View(this);
        		vv1.setBackgroundColor(Color.GRAY);
        		LayoutParams lpvv1 = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        		vv1.setLayoutParams(lpvv1);
        		table.addView(vv1);	
		        //
		        
		        
			    String sqlQuery2  = "select power, count from TrainingStat where trainingdate = ? and exercise = ?";
			    String[] args2 = {TrDate,Exercise};				 
		        Cursor cExHis = db.rawQuery(sqlQuery2, args2);
		        
		        	if(cExHis.moveToFirst())
		        	{
		        		int indPow = cExHis.getColumnIndex("power");
		        		int indCou = cExHis.getColumnIndex("count");
		        		int countRep = 0;
		        		Float maxPow = 0.0F;
		        		Float maxInt = 0.0F;
		        		//String maxIntRes = "";
		        		Float fullmass = 0.0F;
		        		
		        		do 
		        		{  	        		
		        			Float pow = cExHis.getFloat(indPow);
		        			int cou = cExHis.getInt(indCou); 
		        			countRep++;	
		        			result = countRep +".  "+ pow+"x"+cou;	
		        			fullmass = pow * cou;
		        			if(pow > maxPow) maxPow = pow;
		        			if(fullmass > maxInt)
		        				{
		        					maxInt = fullmass;
		        				}
		        			
		        			//Вывод Результата подхода
		        			TableRow row1 = new TableRow(this);
		        			LayoutParams lprow1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
		        					LayoutParams.MATCH_PARENT);
		        			row1.setLayoutParams(lprow1);
		        			
		        			TextView textRep = new TextView(this);
		        			textRep.setText(result);
		        			textRep.setTextColor(Color.WHITE);
		        			LayoutParams lp0 = new LayoutParams(LayoutParams.WRAP_CONTENT,
		        					LayoutParams.WRAP_CONTENT);
		        			textRep.setLayoutParams(lp0);
		        			
		        			row1.addView(textRep);
		        			table.addView(row1, new TableLayout.LayoutParams(
		        					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
		        			
		        			//Выыод разделителя между подходами
		            		View vv2 = new View(this);
		            		vv2.setBackgroundColor(Color.GRAY);
		            		LayoutParams lpvv2 = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
		            		vv2.setLayoutParams(lpvv2);
		            		table.addView(vv2);	
		        			
		        		} while (cExHis.moveToNext()); 
		        	
		        		//Выыод максимального веса упражнения
						TableRow row2 = new TableRow(this);
						LayoutParams lprow2 = new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						row0.setLayoutParams(lprow2);
						
						TextView textMax = new TextView(this);
						textMax.setText("Максимум = "+ maxPow);
						textMax.setTextSize(15);
						textMax.setTextColor(Color.YELLOW);
						LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						textMax.setLayoutParams(lp2);
						
						row2.addView(textMax);
						table.addView(row2, new TableLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
								        				        		
		        		//Выыод разделителя упражнений
		        		View vv = new View(this);
		        		vv.setBackgroundColor(Color.BLACK);
		        		LayoutParams lpvv = new LayoutParams(LayoutParams.MATCH_PARENT, 3);
		        		vv.setLayoutParams(lpvv);
		        		table.addView(vv);			        		        		
		        	}		        	
		        	
		        	cExHis.close();
		     		        	
	        } while(cEx.moveToNext()); 	        	        
        }            
        cEx.close();
        dbHelper.close();
	}

}
