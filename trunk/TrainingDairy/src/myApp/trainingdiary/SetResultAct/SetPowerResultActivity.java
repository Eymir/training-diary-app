package myApp.trainingdiary.SetResultAct;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import myApp.trainingdiary.R;
import myApp.trainingdiary.forBD.DBHelper;
import myApp.trainingdiary.wheel.NumericRightOrderWheelAdapter;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Актитвити для записи резульатов силовых упражнений 
 */

public class SetPowerResultActivity extends Activity implements OnClickListener {

	TextView tvnameEx, tvEndedRep;
	String strNameEx;
	String strNameTr;
	DBHelper dbHelper;
	final int MENU_DEL_LAST_SET = 1;
	final int MENU_SHOW_LAST_RESULT = 2;

	// forms
	Button btnW1p, btnW2p, btnW3p, btnW1m, btnW2m, btnW3m, btnW4p, btnW4m,
			btnRepp, btnRepm, btnSet;
	EditText editTextW1, editTextW2, editTextW3, editTextW4, editTextRep;
	//
	private WheelView bigNumWheel;
	private WheelView smallNumWheel;
	private WheelView repeatWheel;

	private ArrayWheelAdapter<String> smallNumWheelAdapter;
	private NumericRightOrderWheelAdapter repeatWheelAdapter;
	private NumericRightOrderWheelAdapter bigNumWheelAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_power_result);

		tvnameEx = (TextView) findViewById(R.id.tvNameEx);
		tvEndedRep = (TextView) findViewById(R.id.tvEndedRep);
		strNameEx = getIntent().getExtras().getString("nameEx");
		strNameTr = getIntent().getExtras().getString("nameTr");
		tvnameEx.setText(strNameEx);
		//

		smallNumWheelAdapter = new ArrayWheelAdapter<String>(this,
				new String[] { "0", "5" });
		repeatWheelAdapter = new NumericRightOrderWheelAdapter(this, 0, 99,
				"%02d");
		bigNumWheelAdapter = new NumericRightOrderWheelAdapter(this, 0, 500,
				"%02d");

		bigNumWheel = (WheelView) findViewById(R.id.big_num_wheel);
		bigNumWheel.setViewAdapter(bigNumWheelAdapter);
		bigNumWheel.setCyclic(true);

		smallNumWheel = (WheelView) findViewById(R.id.small_num_wheel);
		smallNumWheel.setViewAdapter(smallNumWheelAdapter);

		repeatWheel = (WheelView) findViewById(R.id.repeat_wheel);
		repeatWheel.setViewAdapter(repeatWheelAdapter);
		repeatWheel.setCyclic(true);

		btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(this);

		RefreshTvEndedRep();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DEL_LAST_SET, 1, "Удалить последний подход");
		menu.add(0, MENU_SHOW_LAST_RESULT, 1, "Показать историю упражнения");
		return true;
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.btnSet:
			setRepOnDB();
			RefreshTvEndedRep();
			break;
		default:
			break;
		}

	}

	private void changeCountET(EditText Et, String type) {
		if (type.equalsIgnoreCase("+")) {
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j + 1;
			if (j > 9) {
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		} else if (type.equalsIgnoreCase("-")) {
			String i = Et.getText().toString();
			int j = Integer.parseInt(i);
			j = j - 1;
			if (j < 0) {
				j = 0;
			}
			String res = Integer.toString(j);
			Et.setText(res);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setRepOnDB() {
		dbHelper = new DBHelper(this);
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// готовим дату
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String Date = sdf.format(Calendar.getInstance().getTime());

		// готовим вес
		String strPower = bigNumWheelAdapter.getItem(bigNumWheel
				.getCurrentItem())
				+ "."
				+ smallNumWheelAdapter.getItemText(smallNumWheel
						.getCurrentItem());

		Float floPower = (float) Float.parseFloat(strPower);

		// готовим повторения
		// String strRep = repeatWheel.getCurrentItem();
		int intRep = repeatWheelAdapter.getItem(repeatWheel.getCurrentItem());
		// Integer.parseInt(strRep);

		cv.put("trainingdate", Date);
		cv.put("exercise", strNameEx);
		cv.put("power", floPower);
		cv.put("count", intRep);
		cv.put("trainingday", strNameTr);
		cv.put("exercisetype", "1");
		db.insert("TrainingStat", null, cv);

		// db.close();
		dbHelper.close();
	}

	@SuppressLint("SimpleDateFormat")
	private void RefreshTvEndedRep() {
		dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String Date = sdf.format(Calendar.getInstance().getTime());

		String sqlQuery = "select power, count from TrainingStat where trainingdate = ? and exercise = ?";
		String[] args = { Date, strNameEx };
		Cursor c = db.rawQuery(sqlQuery, args);

		String result = "";

		if (c.moveToFirst()) {
			int exNameIndex = c.getColumnIndex("power");
			int exNameIndex2 = c.getColumnIndex("count");
			int i = 0;
			do {
				Float pow = c.getFloat(exNameIndex);
				int cou = c.getInt(exNameIndex2);
				result = result + pow + "x" + cou + "; ";
				i++;
			} while (c.moveToNext());

			result = result + "\nВсего подходов: " + i;
		}

		c.close();
		dbHelper.close();
		tvEndedRep.setText(result);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_DEL_LAST_SET:

			DelDialog();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void DelDialog() {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Удаление подхода!!!");
		adb.setMessage("Удалить последний подход?");
		adb.setPositiveButton(getResources().getString(R.string.YES),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DelLastEx();
					}
				});
		adb.setNegativeButton(getResources().getString(R.string.NO),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		adb.create().show();
	}

	@SuppressLint("SimpleDateFormat")
	private void DelLastEx() {

		dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String Date = sdf.format(Calendar.getInstance().getTime());
		// Получаем индекс последнего подхода в тренировке
		String sqlQuery = "SELECT " + "id " + "FROM TrainingStat "
				+ "WHERE trainingdate = ? AND exercise = ? "
				+ "ORDER BY id DESC LIMIT 1";

		String[] args = { Date, strNameEx };
		Cursor c = db.rawQuery(sqlQuery, args);
		c.moveToFirst();
		int index = c.getColumnIndex("id");
		int idEx = c.getInt(index);
		// Удаляем подход
		db.delete("TrainingStat", "id = " + idEx, null);
		// обновляем таблицу подходов
		RefreshTvEndedRep();

	}

}
