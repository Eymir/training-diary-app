package myApp.trainingdiary;

import java.util.ArrayList;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter.ViewBinder;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import myApp.trainingdiary.SetResultAct.SetCycleResultActivity;
import myApp.trainingdiary.SetResultAct.SetPowerResultActivity;

import myApp.trainingdiary.forBD.DBHelper;

/*
 * Активити со спиком упражнений для выбранного тренировочного дня и возможностью добавления новых уже имеющихся
 *  упражнений из базы
 */

public class AddExerciseActivity extends Activity implements OnClickListener {

	public final static String ATTRIBUTE_NAME_TEXT = "text";
	public final static String ATTRIBUTE_NAME_IMAGE = "image";

	private DBHelper dbHelper;
	private String strNameTr;
	private DragSortListView exerciseList;
	private String ParsedName;
	private SimpleDragSortCursorAdapter dragAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		dbHelper = new DBHelper(this);
		strNameTr = getIntent().getExtras().getString("name_string");
		exerciseList = (DragSortListView) findViewById(R.id.lvEx);
		View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
		exerciseList.addFooterView(addRowFooter);
		ImageButton v = (ImageButton) findViewById(R.id.add_button);
		v.setOnClickListener(this);

		fetchExcersices();
		Log.i(DBHelper.LOG_TAG, "onCreate1");
		setListeners();
	}

	private void setListeners() {
		exerciseList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Cursor cursor = (Cursor) exerciseList
								.getItemAtPosition(arg2);
						String name = cursor.getString(cursor
								.getColumnIndex("ex_name"));
						if (name.equalsIgnoreCase("Записей нет")) {
						} else {
							openExActivityToAddresult(name);
						}
					}
				});

		// exerciseList.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int pos, long id) {
		// Cursor cursor = (Cursor) exerciseList.getItemAtPosition(pos);
		// String name = cursor.getString(cursor.getColumnIndex("ex_name"));
		// // Log.d(LOG_TAG, "--- "+ ParsedName +" ---");
		// DelDialog();
		// return true;
		// }
		// });

		exerciseList.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Log.i(DBHelper.LOG_TAG, "drop!");
				dragAdapter.drop(from, to);
				dbHelper.changePositions(strNameTr, getNewIdOrder());
			}
		});

		exerciseList.setRemoveListener(new RemoveListener() {
			@Override
			public void remove(int which) {
				Log.i(DBHelper.LOG_TAG, "remove!");
				Cursor c = (Cursor) dragAdapter.getItem(which);
				showDelDialog(dragAdapter.getItemId(which),
						c.getString(c.getColumnIndex("ex_name")), which);
				// dragAdapter.reset();
			}
		});
	}

	private List<Long> getNewIdOrder() {
		List<Long> list = new ArrayList<Long>();
		Log.d(DBHelper.LOG_TAG,
				"getCursorPositions" + dragAdapter.getCursorPositions());
		for (Integer i = 0; i < dragAdapter.getCount(); i++) {
			list.add(dragAdapter.getItemId(i));
		}
		Log.d(DBHelper.LOG_TAG, "getNewIdOrder" + list);
		return list;
	}

	// получаем упражнения и выводим их во вьюшку....теперь ещё и сортируем
	protected void fetchExcersices() {
		Cursor c = dbHelper.getExcersices(strNameTr);
		// массив имен атрибутов, из которых будут читаться данные
		String[] from = { "ex_name", "type" };
		// массив ID View-компонентов, в которые будут вставлять данные
		int[] to = { R.id.label, R.id.icon };
		dragAdapter = new SimpleDragSortCursorAdapter(this,
				R.layout.exerciseslv, c, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		dragAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {
					ImageView v = (ImageView) view;
					int type = cursor.getInt(cursor.getColumnIndex("type"));
					switch (type) {
					case 1:
						v.setImageResource(R.drawable.power);
						break;
					case 2:
						v.setImageResource(R.drawable.cycle);
						break;
					default:
						v.setImageResource(R.drawable.ic_launcher);
						break;
					}
					return true;
				}
				return false;
			}
		});
		exerciseList.setAdapter(dragAdapter);
		dbHelper.close();
	}

	private void refreshExcercises() {
		Cursor c = dbHelper.getExcersices(strNameTr);
		dragAdapter.swapCursor(c);
		dbHelper.close();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.add_button:
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
	protected void onResume() {
		super.onResume();
		refreshExcercises();
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshExcercises();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent BackToMain = new Intent(this, MainActivity.class);
		startActivity(BackToMain);
		finish();
	}

	private void openExActivityToAddresult(String nameEx) {
		Log.d(DBHelper.LOG_TAG, "nameEx: " + nameEx);
		dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sqlQuery = "select type from ExerciseTable where exercise = ?";
		String[] args = { nameEx };
		Cursor c = db.rawQuery(sqlQuery, args);
		c.moveToFirst();
		int exNameIndex = c.getColumnIndex("type");
		String type = c.getString(exNameIndex);
		c.close();
		dbHelper.close();
		// Log.d(LOG_TAG, "--- type  --- = " + type);

		if (type.equalsIgnoreCase("1")) {
			Intent intentOpenPowerResultActivity = new Intent(this,
					SetPowerResultActivity.class);
			intentOpenPowerResultActivity.putExtra("nameEx", nameEx);
			intentOpenPowerResultActivity.putExtra("nameTr", strNameTr);
			startActivity(intentOpenPowerResultActivity);
		} else {
			Intent intentOpenCycleResultActivity = new Intent(this,
					SetCycleResultActivity.class);
			intentOpenCycleResultActivity.putExtra("nameEx", nameEx);
			intentOpenCycleResultActivity.putExtra("nameTr", strNameTr);
			startActivity(intentOpenCycleResultActivity);
		}
	}

	private String ParserOnItemClick(String nonParsed) {
		int index = nonParsed.indexOf("text=");
		String HalfParsed = nonParsed.substring(index + 5);
		int index2 = HalfParsed.length();
		String Parsed = HalfParsed.substring(0, index2 - 1);
		return Parsed;
	}

	private boolean deleteExcercise(long id) {
		dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] args = { String.valueOf(id) };
		int isDeleted = db.delete("TrainingProgTable", "id=?", args);
		dbHelper.close();
		// fetchExcersices();

		return (isDeleted > 0);
	}

	private void showDelDialog(final long id, final String name, final int pos) {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Удаление упражнения из тренировки");
		adb.setMessage("Удалить упражнение - " + name + " ?");
		adb.setNegativeButton("Нет", null);
		adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int _id) {
				if (deleteExcercise(id)) {
					Toast.makeText(AddExerciseActivity.this,
							"Упражнение удалено - " + name, Toast.LENGTH_SHORT)
							.show();
					dragAdapter.remove(pos);
				}
			}
		});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dragAdapter.notifyDataSetChanged();
			}
		});
		adb.create().show();
	}

	private void showEmtyDialog() {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Отсутствуют упражнения");
		adb.setMessage("В созданной Вами тренировке пока нет ни одного упражнения, "
				+ " Добавить упражнения в тренировку?");
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
		Intent SelectExToAddInTrActivity = new Intent(this,
				SelectExToAddInTrActivity.class);
		SelectExToAddInTrActivity.putExtra("trainingName", strNameTr);
		startActivity(SelectExToAddInTrActivity);
		finish();
	}

	private void startComeBackAct() {

		Intent MainActivity = new Intent(this, MainActivity.class);
		startActivity(MainActivity);
		finish();

	}

}
