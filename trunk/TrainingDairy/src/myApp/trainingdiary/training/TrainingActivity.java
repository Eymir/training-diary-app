package myApp.trainingdiary.training;

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

import myApp.trainingdiary.R;
import myApp.trainingdiary.HistoryAct.HistoryMainAcrivity;
import myApp.trainingdiary.R.drawable;
import myApp.trainingdiary.R.id;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.R.string;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.customview.ExpandAnimation;
import myApp.trainingdiary.excercise.AddExerciseActivity;
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
import android.support.v4.widget.SimpleCursorAdapter;
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

public class TrainingActivity extends Activity {

	private static final int ID_ADD_TRAINING = 1;
	private static final int ID_RENAME_TRAINING = 2;
	private static final int ID_MOVE_TRAINING = 3;
	private static final int ID_DELETE_TRAINING = 4;
	private static final int ID_RENAME_EXERCISE = 5;
	private static final int ID_REMOVE_EXERCISE = 6;
	private static final int ID_MOVE_EXERCISE = 7;

	private DBHelper dbHelper;
	private DragSortListView trainingList;
	private SimpleDragSortCursorAdapter trainingDragAdapter;
	private Dialog createTrainingDialog;
	private Dialog renameTrainingDialog;
	private AlertDialog deleteTrainingDialog;
	private Dialog chooseExerciseDialog = null;

	private QuickAction trainingActionTools;
	private QuickAction exerciseActionTools;

	// id тренировки по которой вызван toolbar
	private long cur_tr_id;
	private ImageView cur_drag_handler;

	private TrainingGuiBindMediator trMediator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_list);
		dbHelper = DBHelper.getInstance(this);
		trainingList = (DragSortListView) findViewById(R.id.training_list);
		View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
		trainingList.addFooterView(addRowFooter);

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
		createChooseExerciseDialog();
		createTrainingTools();

		trainingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Toast.makeText(getApplicationContext(), String.valueOf(id),
						Toast.LENGTH_SHORT).show();
			}
		});

		trainingList.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Log.i(Consts.LOG_TAG, "drop training");
				trainingDragAdapter.drop(from, to);
				// dbHelper.changeTrainingPositions(getNewTrIdOrder());
				cur_drag_handler.setVisibility(View.GONE);
			}
		});
	}

	private void createCreateTrDialog() {
		createTrainingDialog = new Dialog(this);
		createTrainingDialog.setContentView(R.layout.input_name_dialog);
		createTrainingDialog.setTitle(R.string.title_rename_training);
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
					dbHelper.createTraining(name);
					createTrainingDialog.cancel();
					Toast.makeText(TrainingActivity.this,
							R.string.create_success, Toast.LENGTH_SHORT).show();
					refreshTrainings();
				} else {
					Toast.makeText(TrainingActivity.this,
							R.string.zero_input_notif, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// refreshTrainings();
		super.onResume();
	}

	private void createChooseExerciseDialog() {
		chooseExerciseDialog = new Dialog(this);
		chooseExerciseDialog.setContentView(R.layout.choose_exercise_list);
		chooseExerciseDialog.setTitle(R.string.choose_exercise);
		final ListView exercise_list = (ListView) chooseExerciseDialog
				.findViewById(R.id.exercise_list);
		exercise_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int arg2,
					long position) {
				// TODO: Доделать
				// Cursor cursor =
				// ((AdapterView<SimpleCursorAdapter>)exercise_list).getCursor();
				// cursor.moveToPosition(position);
				// long ex_id = cursor.getLong(cursor.getColumnIndex("_id"));
				// int count = dbHelper.getExerciseInTrainingCount(cur_tr_id);
				// SQLiteDatabase db = dbHelper.getWritableDatabase();
				// dbHelper.insertExerciseInTraining(db, cur_tr_id, ex_id ,
				// count);
				// db.close();
				// chooseExerciseDialog.cancel();
			}
		});
		Cursor cursor = dbHelper.getExercisesExceptExInTr(cur_tr_id);
		String[] from = { "name", "icon_res" };
		int[] to = { R.id.label, R.id.icon };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.choose_exercise_row, cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {
					((ImageView) view).setImageResource(getResources()
							.getIdentifier(cursor.getString(columnIndex),
									"drawable", getPackageName()));
					return true;
				}
				return false;
			}
		});
		exercise_list.setAdapter(adapter);
		dbHelper.close();
	}

	private void createTrainingTools() {
		ActionItem renameItem = new ActionItem(ID_RENAME_TRAINING,
				getResources().getString(R.string.rename_action),
				getResources().getDrawable(R.drawable.pencil));
		ActionItem moveItem = new ActionItem(ID_MOVE_TRAINING, getResources()
				.getString(R.string.move_action), getResources().getDrawable(
				R.drawable.object_flip_vertical));
		ActionItem deleteItem = new ActionItem(ID_DELETE_TRAINING,
				getResources().getString(R.string.delete_action),
				getResources().getDrawable(R.drawable.deletered));

		trainingActionTools = new QuickAction(this);

		trainingActionTools.addActionItem(renameItem);
		trainingActionTools.addActionItem(moveItem);
		trainingActionTools.addActionItem(deleteItem);

		// setup the action item click listener
		trainingActionTools
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						switch (actionId) {
						case ID_RENAME_TRAINING:
							renameTrainingDialog.show();
							break;
						case ID_DELETE_TRAINING:
							String tr_name = dbHelper
									.getTrainingNameById(cur_tr_id);
							deleteTrainingDialog.setMessage(String.format(
									getResources().getString(
											R.string.Dialog_del_tr_msg),
									tr_name));
							deleteTrainingDialog.show();
							break;
						case ID_MOVE_TRAINING:
							cur_drag_handler.setVisibility(View.VISIBLE);
							break;
						}
					}
				});
	}

	private void createExcerciseTools() {
		ActionItem renameItem = new ActionItem(ID_RENAME_EXERCISE,
				getResources().getString(R.string.rename_action),
				getResources().getDrawable(R.drawable.pencil));
		ActionItem moveItem = new ActionItem(ID_MOVE_EXERCISE, getResources()
				.getString(R.string.move_action), getResources().getDrawable(
				R.drawable.object_flip_vertical));
		ActionItem removeItem = new ActionItem(ID_REMOVE_EXERCISE,
				getResources().getString(R.string.delete_action),
				getResources().getDrawable(R.drawable.deletered));

		exerciseActionTools = new QuickAction(this);

		exerciseActionTools.addActionItem(renameItem);
		exerciseActionTools.addActionItem(moveItem);
		exerciseActionTools.addActionItem(removeItem);

		// setup the action item click listener
		exerciseActionTools
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						switch (actionId) {
						case ID_RENAME_EXERCISE:
							// renameExerciseDialog.show();
							break;
						case ID_REMOVE_EXERCISE:
							// removeExerciseDIalog.show();
							break;
						case ID_MOVE_EXERCISE:
							// cur_drag_handler.setVisibility(View.VISIBLE);
							break;

						}
					}
				});
	}

	protected void openAddExerciseActivity(long tr_id) {
		Intent intentOpenAddEx = new Intent(this, AddExerciseActivity.class);
		intentOpenAddEx.putExtra(Consts.TRAINING_ID, tr_id);
		startActivity(intentOpenAddEx);
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
					Toast.makeText(TrainingActivity.this,
							R.string.rename_success, Toast.LENGTH_SHORT).show();

					refreshTrainings();
				} else {
					Toast.makeText(TrainingActivity.this,
							R.string.zero_input_notif, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	private List<Long> getNewTrIdOrder() {
		List<Long> list = new ArrayList<Long>();
		Log.d(Consts.LOG_TAG,
				"getCursorPositions" + trainingDragAdapter.getCursorPositions());
		for (Integer i = 0; i < trainingDragAdapter.getCount(); i++) {
			list.add(trainingDragAdapter.getItemId(i));
		}
		Log.d(Consts.LOG_TAG, "getNewIdOrder" + list);
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, MENU_SETTINGS_ID, 3, "Настройки");
		// menu.add(0, MENU_COMPLETE_ID, 1, "История");
		// menu.add(0, MENU_STATISTIC_ID, 2, "Статистика");
		return true;
	}

	protected void fetchTrainings() {
		Cursor tr_cursor = dbHelper.getTrainings();
		String[] from = { "name", "_id" };
		int[] to = { R.id.label, R.id.tools };
		trainingDragAdapter = new SimpleDragSortCursorAdapter(this,
				R.layout.training_row, tr_cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		trainingDragAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				// Возможно не самый лучший вариант здесь определять лиснеры,
				// так как может быть много лишних переназначений
				if (view.getId() == R.id.tools) {
					final long tr_id = cursor.getLong(columnIndex);
					view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							trainingActionTools.show(v);
							cur_tr_id = tr_id;
							cur_drag_handler = (ImageView) ((View) v
									.getParent())
									.findViewById(R.id.drag_handler);
						}
					});
					return true;
				}

				return false;
			}
		});
		trainingList.setAdapter(trainingDragAdapter);
		// dbHelper.close();
	}

	private void refreshTrainings() {
		Cursor c = dbHelper.getTrainings();
		trainingDragAdapter.swapCursor(c);
		// dbHelper.close();
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
				Toast.makeText(TrainingActivity.this, R.string.deleted,
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

		finish();
	}

}
