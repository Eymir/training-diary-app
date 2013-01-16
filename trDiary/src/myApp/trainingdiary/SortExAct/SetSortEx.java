package myApp.trainingdiary.SortExAct;

import myApp.trainingdiary.R;
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
import android.widget.TextView;

public class SetSortEx extends Activity implements OnClickListener {
	
	String strNameTr;
	String strNameEx;
	int numEx;
	Button btnPlus;
	Button btnMinus;
	EditText etSetSort;
	Button btnSetSort;
	DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_sort_ex);
		
		strNameTr = getIntent().getExtras().getString("trainingName");
		strNameEx = getIntent().getExtras().getString("ExName");
		numEx = getIntent().getExtras().getInt("num");
		
		TextView tvNameEx = (TextView)findViewById(R.id.tvNameEx);
		tvNameEx.setText(strNameEx);
		
		btnPlus = (Button)findViewById(R.id.btnPlus);
		btnPlus.setOnClickListener(this);
		btnMinus = (Button)findViewById(R.id.btnMinus);
		btnMinus.setOnClickListener(this);
		etSetSort = (EditText)findViewById(R.id.etSetSort);
		etSetSort.setOnClickListener(this);		
		btnSetSort = (Button)findViewById(R.id.btnSetSort);
		btnSetSort.setOnClickListener(this);
		
		etSetSort.setText(Integer.toString(numEx));
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_set_sort_ex, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		
	    switch (arg0.getId()) 
	    {
	    case R.id.btnPlus:
	    	changeCountET(etSetSort, "+");
	      break;
	    case R.id.btnMinus:
	    	changeCountET(etSetSort, "-");
	      break;
	    case R.id.btnSetSort:
	    	setSort();
	      break;
	    }				
	}
	
	private void changeCountET(EditText Et, String type)
	{
		if(type.equalsIgnoreCase("+"))
		{
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j + 1;
			if(j > 19)
			{
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		}
		else if(type.equalsIgnoreCase("-"))
		{
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j - 1;			
			if(j < 0)
			{
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		}
	}	
	
	private void setSort() {
		
		dbHelper = new DBHelper(this);			    
	    SQLiteDatabase db = dbHelper.getWritableDatabase(); 
		ContentValues cv = new ContentValues();
		String strnum = etSetSort.getText().toString();
		int newNumEX = Integer.parseInt(strnum);
		cv.put("exidintr", newNumEX);
	    db.update("TrainingProgTable", cv, "trainingname = ? and exercise = ?", new String[] {strNameTr, strNameEx});
	   // Log.d("myLogs", "-count-- "+count +"---");
	    //Log.d("myLogs", "-strNameTr-- "+strNameTr +"---");
	    //Log.d("myLogs", "-strNameEx-- "+strNameEx +"---");	    
	    dbHelper.close();
	    onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
   	   	Intent SortEx = new Intent(this, SortExInTrainingDay.class);
   	  	SortEx.putExtra("trainingName", strNameTr);
        startActivity(SortEx);
        finish();
	}

}
