package myApp.trainingdiary;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
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

	private static final int ID_ADD = 1;
	private static final int ID_RENAME = 2;
	private static final int ID_MOVE = 3;
	private static final int ID_DELETE = 4;

	private DBHelper dbHelper;
	private DragSortListView trainingList;
	private SimpleDragSortCursorAdapter trainingDragAdapter;
	private Dialog createTrainingDialog;
	private Dialog renameTrainingDialog;
	private AlertDialog deleteTrainingDialog;

	private QuickAction mQuickAction;

	// id тренировки по которой вызван toolbar
	private long cur_tr_id;
	private ImageView cur_drag_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_list);
		dbHelper = new DBHelper(this);
		trainingList = (DragSortListView) findViewById(R.id.training_list);

		View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
		trainingList.addFooterView(addRowFooter);
		trainingList.setItemsCanFocus(false);
		trainingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				View cExerciseList = v.findViewById(R.id.exercise_list);
				Log.d(DBHelper.LOG_TAG, "height: " + v.getHeight());
				ExpandAnimation expandAni = new ExpandAnimation(cExerciseList,
						500);
				cExerciseList.startAnimation(expandAni);
			}
		});

		ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createTrainingDialog.show();
			}
		});

		fetchTrainings();

		createCreateTrDialog();
		createRenameTrDialog();
		createDeletionDialog();
		createTools();

		trainingList.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Log.i(DBHelper.LOG_TAG, "drop training");
				trainingDragAdapter.drop(from, to);
				// dbHelper.changeTrainingPositions(getNewTrIdOrder());
				cur_drag_handler.setVisibility(View.GONE);
			}
		});
	}

	private void createTools() {
		ActionItem addItem = new ActionItem(ID_ADD, "Add", getResources()
				.getDrawable(R.drawable.add_32));
		ActionItem renameItem = new ActionItem(ID_RENAME, "Rename",
				getResources().getDrawable(R.drawable.ic_launcher));
		ActionItem moveItem = new ActionItem(ID_MOVE, "Move", getResources()
				.getDrawable(R.drawable.ic_launcher));
		ActionItem deleteItem = new ActionItem(ID_DELETE, "Delete",
				getResources().getDrawable(R.drawable.ic_launcher));

		mQuickAction = new QuickAction(this);
		if (addItem == null)
			Log.d(DBHelper.LOG_TAG, "addItem is null");

		mQuickAction.addActionItem(addItem);
		mQuickAction.addActionItem(renameItem);
		mQuickAction.addActionItem(moveItem);
		mQuickAction.addActionItem(deleteItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						switch (actionId) {
						case ID_RENAME:
							renameTrainingDialog.show();
							break;
						case ID_DELETE:
							String tr_name = dbHelper
									.getTrainingNameById(cur_tr_id);
							deleteTrainingDialog.setMessage(getResources()
									.getString(R.string.Dialog_del_tr_msg)
									+ " " + tr_name + "?");
							deleteTrainingDialog.show();
							break;
						case ID_MOVE:
							cur_drag_handler.setVisibility(View.VISIBLE);
							break;
						case ID_ADD:
							openAddExerciseActivity();
							break;
						}
					}
				});
	}

	protected void openAddExerciseActivity() {

	}

	private void createCreateTrDialog() {
		createTrainingDialog = new Dialog(this);
		createTrainingDialog.setContentView(R.layout.input_name_dialog);
		createTrainingDialog.setTitle(R.string.title_create_training);
		final EditText name_input = (EditText) createTrainingDialog
				.findViewById(R.id.name_input);
		Button okButton = (Button) createTrainingDialog
				.findViewById(R.id.ok_button);
		okButton.setText(R.string.create_button);
		Button cancelButton = (Button) createTrainingDialog
				.findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createTrainingDialog.cancel();
			}
		});

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name_input.getText().length() > 0) {
					String name = name_input.getText().toString();
					int count = dbHelper.getTrainingsCount();
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					dbHelper.insertTraining(db, name, count);
					createTrainingDialog.cancel();
					Toast.makeText(MainActivity.this, R.string.create_success,
							Toast.LENGTH_SHORT).show();
					refreshTrainings();
				} else {
					Toast.makeText(MainActivity.this,
							R.string.zero_input_notif, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	private void createRenameTrDialog() {
		renameTrainingDialog = new Dialog(this);
		renameTrainingDialog.setContentView(R.layout.input_name_dialog);
		renameTrainingDialog.setTitle(R.string.title_rename_training);
		final EditText name_input = (EditText) renameTrainingDialog
				.findViewById(R.id.name_input);
		Button okButton = (Button) renameTrainingDialog
				.findViewById(R.id.ok_button);
		okButton.setText(R.string.rename_button);
		Button cancelButton = (Button) renameTrainingDialog
				.findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				renameTrainingDialog.cancel();
			}
		});

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name_input.getText().length() > 0) {
					String name = name_input.getText().toString();
					dbHelper.renameTraining(cur_tr_id, name);
					renameTrainingDialog.cancel();
					Toast.makeText(MainActivity.this, R.string.rename_success,
							Toast.LENGTH_SHORT).show();

					refreshTrainings();
				} else {
					Toast.makeText(MainActivity.this,
							R.string.zero_input_notif, Toast.LENGTH_SHORT)
							.show();
				}
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

			break;
		default:
			break;
		}
	}

	protected void fetchTrainings() {
		Cursor tr_cursor = dbHelper.getTrainings();
		String[] from = { "_id", "name", "_id" };
		int[] to = { R.id.exercise_list, R.id.label, R.id.tools };
		trainingDragAdapter = new SimpleDragSortCursorAdapter(this,
				R.layout.training_row, tr_cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		trainingDragAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.tools) {
					final long tr_id = cursor.getLong(columnIndex);
					view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mQuickAction.show(v);
							cur_tr_id = tr_id;
							cur_drag_handler = (ImageView) ((View) v
									.getParent())
									.findViewById(R.id.drag_handler);

						}
					});
					return true;
				}
				if (view.getId() == R.id.exercise_list) {
					//
					DragSortListView exerciseList = (DragSortListView) view;
					exerciseList.setVisibility(View.GONE);
//					if (exerciseList.getFooterViewsCount() == 0) {
//						View addRowFooter = getLayoutInflater().inflate(
//								R.layout.add_row, null);
//						exerciseList.addFooterView(addRowFooter);
//						addRowFooter.setFocusable(false);
//					}
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

	private void refreshTrainings() {
		Cursor c = dbHelper.getTrainings();
		trainingDragAdapter.swapCursor(c);
		dbHelper.close();
	}

	private void createDeletionDialog() {

		// Получаем тексты из ресов
		String title = getResources().getString(R.string.Dialog_del_tr_title);
		String Msg = getResources().getString(R.string.Dialog_del_tr_msg);
		String btnRename = getResources().getString(R.string.cancel_button);
		String btnDel = getResources().getString(R.string.delete_button);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setTitle(title);

		adb.setPositiveButton(btnDel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dbHelper.deleteTraining(cur_tr_id);
				refreshTrainings();
				Toast.makeText(MainActivity.this, R.string.deleted,
						Toast.LENGTH_SHORT).show();
			}
		});
		adb.setNegativeButton(btnRename, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteTrainingDialog.cancel();
			}
		});

		deleteTrainingDialog = adb.create();
	}

	private void showEmptyDilog() {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Список тренировок пуст");
		adb.setMessage("Вы ещё не создали ни одной тренировки, для того чтобы начать вести дневник создайте хотя бы одну тренировку"
				+ " и добавьте в неё упражнения. Вы хотите создать тренировку?");
		adb.setPositiveButton(getResources().getString(R.string.YES),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						createTrainingDialog.show();
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

	private void startSuperMain() {

		Intent intentOpenAddEx = new Intent(this, SuperMainActivity.class);
		startActivity(intentOpenAddEx);
		finish();
	}

}
