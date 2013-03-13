package myApp.trainingdiary;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mobeta.android.dslv.DragSortItemView;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter.ViewBinder;

import myApp.trainingdiary.HistoryAct.HistoryMainAcrivity;
import myApp.trainingdiary.customview.ExpandAnimation;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/*
 * активити выбора тренировочного дня
 */

public class MainActivity extends Activity implements OnClickListener {
	private DBHelper dbHelper;
	private DragSortListView trainingList;
	private SimpleDragSortCursorAdapter trainingDragAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_list);
		dbHelper = new DBHelper(this);
		trainingList = (DragSortListView) findViewById(R.id.training_list);

		View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
		trainingList.addFooterView(addRowFooter);

		ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
		addButton.setOnClickListener(this);

		fetchTrainings();

		// trainingList
		// .setOnItemClickListener(new AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int arg2, long arg3) {
		//
		// View cExerciseList = view
		// .findViewById(R.id.exercise_list);
		// Log.d(DBHelper.LOG_TAG, "height: " + view.getHeight());
		// // Creating the expand animation for the item
		// ExpandAnimation expandAni = new ExpandAnimation(
		// cExerciseList, 500);
		// // Start the animation on the toolbar
		// cExerciseList.startAnimation(expandAni);
		// // /TODO: Сделать открытие\закрытие аккордиона
		// }
		// });

		trainingList.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Log.i(DBHelper.LOG_TAG, "drop training");
				trainingDragAdapter.drop(from, to);
				// dbHelper.changeTrainingPositions(getNewTrIdOrder());
			}
		});

		trainingList.setRemoveListener(new RemoveListener() {
			@Override
			public void remove(int which) {
				// /TODO: showDelDialog
				// Log.i(DBHelper.LOG_TAG, "remove!");
				// Cursor c = (Cursor) trainingDragAdapter.getItem(which);
				// showDelDialog(trainingDragAdapter.getItemId(which),
				// c.getString(c.getColumnIndex("ex_name")), which);
				// dragAdapter.reset();
			}
		});

	}

	private List<Long> getNewTrIdOrder() {
		List<Long> list = new ArrayList<Long>();
		Log.d(DBHelper.LOG_TAG,
				"getCursorPositions" + trainingDragAdapter.getCursorPositions());
		for (Integer i = 0; i < trainingDragAdapter.getCount(); i++) {
			list.add(trainingDragAdapter.getItemId(i));
		}
		Log.d(DBHelper.LOG_TAG, "getNewIdOrder" + list);
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, MENU_SETTINGS_ID, 3, "Настройки");
		// menu.add(0, MENU_COMPLETE_ID, 1, "История");
		// menu.add(0, MENU_STATISTIC_ID, 2, "Статистика");
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.add_button:
			showCreateTrainingDialog();
			break;
		default:
			break;
		}
	}

	private void showCreateTrainingDialog() {
		// TODO Реализовать диалог создания тренировки

	}

	protected void fetchTrainings() {
		Cursor tr_cursor = dbHelper.getTrainings();
		String[] from = { "name", "_id", "_id" };
		int[] to = { R.id.label, R.id.exercise_list, R.id.icon };
		trainingDragAdapter = new SimpleDragSortCursorAdapter(this,
				R.layout.training_row, tr_cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		trainingDragAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {
					
					view.getRootView().findViewById(R.id.expand_layout)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									View cExerciseList = v.getRootView()
											.findViewById(R.id.exercise_list);
									Log.d(DBHelper.LOG_TAG,
											"height: " + v.getHeight());
									// Creating the expand animation for the
									// item
									ExpandAnimation expandAni = new ExpandAnimation(
											cExerciseList, 500);
									// Start the animation on the toolbar
									cExerciseList.startAnimation(expandAni);
								}
							});
					Log.d(DBHelper.LOG_TAG, "expand_layout");

					// /TODO: Сделать открытие\закрытие аккордиона
				}
				if (view.getId() == R.id.exercise_list) {
					DragSortListView exerciseList = (DragSortListView) view;
					exerciseList.setVisibility(View.GONE);
					if (exerciseList.getFooterViewsCount() == 0) {
						View addRowFooter = getLayoutInflater().inflate(
								R.layout.add_row, null);
						exerciseList.addFooterView(addRowFooter);
					}
					long tr_id = cursor.getLong(cursor.getColumnIndex("_id"));
					Cursor ex_cursor = dbHelper.getExercises(tr_id);
					String[] from = { "name", "icon_res" };
					int[] to = { R.id.label, R.id.icon };
					SimpleDragSortCursorAdapter exerciseDragAdapter = new SimpleDragSortCursorAdapter(
							MainActivity.this, R.layout.training_row,
							ex_cursor, from, to,
							CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
					exerciseList.setAdapter(exerciseDragAdapter);

					return true;
				}
				return false;
			}
		});
		trainingList.setAdapter(trainingDragAdapter);
		dbHelper.close();
	}

	private void refreshExcercises() {
		// Cursor c = dbHelper.getExcersices(strNameTr);
		// trainingDragAdapter.swapCursor(c);
		// dbHelper.close();
	}

	private void DelDialog() {
		//
		// //Получаем тексты из ресов
		// String title =
		// getResources().getString(R.string.Dialog_del_tr_title);
		// String Msg = getResources().getString(R.string.Dialog_del_tr_msg);
		// String btnRename =
		// getResources().getString(R.string.Dialog_del_tr_btn_rename);
		// String btnDel =
		// getResources().getString(R.string.Dialog_del_tr_btn_del);
		// //
		//
		// AlertDialog.Builder adb = new AlertDialog.Builder(this);
		// adb.setTitle(title);
		// adb.setMessage(Msg + " "+ TrainingNameToDel + " ?");
		// adb.setPositiveButton(btnDel, new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// deleteTr();
		// }
		// });
		// adb.setNegativeButton(btnRename, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// renameTr();
		// }
		// });
		// adb.create().show();
	}

	private void renameTr() {
		//
		// Intent addtrIntent = new Intent(this, AddTrActivity.class);
		// addtrIntent.putExtra("NewRecord", false);
		// addtrIntent.putExtra("trName", TrainingNameToDel);
		// startActivity(addtrIntent);
		// finish();

	}

	private void showEmptyDilog() {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Список тренировок пуст");
		adb.setMessage("Вы ещё не создали ни одной тренировки, для того чтобы начать вести дневник создайте хотя бы одну тренировку"
				+ " и добавьте в неё упражнения. Вы хотите создать тренировку?");
		adb.setPositiveButton(getResources().getString(R.string.YES),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startCreateTrActivity();

					}
				});
		adb.setNegativeButton(getResources().getString(R.string.NO),
				new DialogInterface.OnClickListener() {
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
