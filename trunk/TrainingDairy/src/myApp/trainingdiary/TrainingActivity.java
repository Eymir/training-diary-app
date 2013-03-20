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
import myApp.trainingdiary.addex.AddExerciseActivity;
import myApp.trainingdiary.constant.Consts;
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
	private Dialog chooseExerciseDialog = null;

	private QuickAction mQuickAction;

	// id тренировки по которой вызван toolbar
	private long cur_tr_id;
	private ImageView cur_drag_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_list);
		dbHelper = DBHelper.getInstance(this);
		trainingList = (DragSortListView) findViewById(R.id.training_list);

		View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
		trainingList.addFooterView(addRowFooter);
		trainingList.setItemsCanFocus(false);

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
		createTools();

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
	
	@Override
	protected void onResume() {
//		refreshTrainings();
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
				//TODO: Доделать
//				Cursor cursor = ((AdapterView<SimpleCursorAdapter>)exercise_list).getCursor();
//				cursor.moveToPosition(position);
//				long ex_id = cursor.getLong(cursor.getColumnIndex("_id"));
//				int count = dbHelper.getExerciseInTrainingCount(cur_tr_id);
//				SQLiteDatabase db = dbHelper.getWritableDatabase();
//				dbHelper.insertExerciseInTraining(db, cur_tr_id, ex_id , count);
//				db.close();
//				chooseExerciseDialog.cancel();
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
//		dbHelper.close();
	}

	private void createTools() {
		ActionItem addItem = new ActionItem(ID_ADD, getResources().getString(
				R.string.add_action), getResources().getDrawable(
				R.drawable.plus_orange));
		ActionItem renameItem = new ActionItem(ID_RENAME, getResources()
				.getString(R.string.rename_action), getResources().getDrawable(
				R.drawable.pencil));
		ActionItem moveItem = new ActionItem(ID_MOVE, getResources().getString(
				R.string.move_action), getResources().getDrawable(
				R.drawable.object_flip_vertical));
		ActionItem deleteItem = new ActionItem(ID_DELETE, getResources()
				.getString(R.string.delete_action), getResources().getDrawable(
				R.drawable.deletered));

		mQuickAction = new QuickAction(this);
		if (addItem == null)
			Log.d(Consts.LOG_TAG, "addItem is null");

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
							deleteTrainingDialog.setMessage(String.format(
									getResources().getString(
											R.string.Dialog_del_tr_msg),
									tr_name));
							deleteTrainingDialog.show();
							break;
						case ID_MOVE:
							cur_drag_handler.setVisibility(View.VISIBLE);
							break;
						case ID_ADD:
							openAddExerciseActivity(cur_tr_id);
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
					Toast.makeText(TrainingActivity.this, R.string.create_success,
							Toast.LENGTH_SHORT).show();
					refreshTrainings();
				} else {
					Toast.makeText(TrainingActivity.this,
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
					Toast.makeText(TrainingActivity.this, R.string.rename_success,
							Toast.LENGTH_SHORT).show();

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
		String[] from = { "_id", "name", "_id" };
		int[] to = { R.id.exercise_list, R.id.label, R.id.tools };
		trainingDragAdapter = new SimpleDragSortCursorAdapter(this,
				R.layout.training_row, tr_cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		trainingDragAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				// Возможно не самый лучший вариант здесь определять лиснеры,
				// так как может быть много лишних переназначений
				if (view.getId() == R.id.label) {

					view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							View cExerciseList = ((View) v.getParent()
									.getParent())
									.findViewById(R.id.exercise_list);
							if (cExerciseList == null)
								Log.d(Consts.LOG_TAG, "cExerciseList is null");
							else {
								Log.d(Consts.LOG_TAG, cExerciseList
										.getClass().toString());

								ExpandAnimation expandAni = new ExpandAnimation(
										cExerciseList, 500);
								cExerciseList.startAnimation(expandAni);
							}
						}
					});

					return false;
				}
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
					// if (exerciseList.getFooterViewsCount() == 0) {
					// View addRowFooter = getLayoutInflater().inflate(
					// R.layout.add_row, null);
					// exerciseList.addFooterView(addRowFooter);
					// addRowFooter.setFocusable(false);
					// }
					long tr_id = cursor.getLong(cursor.getColumnIndex("_id"));
					Cursor ex_cursor = dbHelper.getExercisesInTraining(tr_id);
					String[] from = { "name", "icon_res" };
					int[] to = { R.id.label, R.id.icon };
					SimpleDragSortCursorAdapter exerciseDragAdapter = new SimpleDragSortCursorAdapter(
							TrainingActivity.this, R.layout.exercise_row,
							ex_cursor, from, to,
							CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
					exerciseDragAdapter.setViewBinder(new ViewBinder() {

						@Override
						public boolean setViewValue(View view, Cursor cursor,
								int columnIndex) {
							if (view.getId() == R.id.icon) {
								// Не проверял
								((ImageView) view).setImageResource(getResources()
										.getIdentifier(
												cursor.getString(columnIndex),
												"drawable", getPackageName()));
								return true;
							}
							return false;
						}
					});
					exerciseList.setAdapter(exerciseDragAdapter);
					return true;
				}
				return false;
			}
		});
		trainingList.setAdapter(trainingDragAdapter);
//		dbHelper.close();
	}

	private void refreshTrainings() {
		Cursor c = dbHelper.getTrainings();
		trainingDragAdapter.swapCursor(c);
//		dbHelper.close();
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
