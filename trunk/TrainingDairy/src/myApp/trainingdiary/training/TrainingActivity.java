package myApp.trainingdiary.training;

import java.util.List;
import java.util.ArrayList;

import myApp.trainingdiary.excercise.ExerciseActivity;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter.ViewBinder;

import myApp.trainingdiary.R;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * �������� ������ �������������� ���
 */

public class TrainingActivity extends Activity {

	private static final int ID_ADD_TRAINING = 1;
	private static final int ID_RENAME_TRAINING = 2;
	private static final int ID_MOVE_TRAINING = 3;
	private static final int ID_DELETE_TRAINING = 4;


	private DBHelper dbHelper;
	private DragSortListView trainingList;
	private SimpleDragSortCursorAdapter trainingDragAdapter;
	private Dialog createTrainingDialog;
	private Dialog renameTrainingDialog;
	private AlertDialog deleteTrainingDialog;

	private QuickAction trainingActionTools;

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
		createTrainingTools();

		trainingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
                long tr_id = trainingDragAdapter.getItemId(pos);
                openExerciseActivity(tr_id);
			}
		});

		trainingList.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Log.i(Consts.LOG_TAG, "drop training");
				trainingDragAdapter.drop(from, to);
				dbHelper.changeTrainingPositions(getNewTrIdOrder());
				cur_drag_handler.setVisibility(View.GONE);
			}
		});
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
		refreshTrainings();
		super.onResume();
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

    protected void openExerciseActivity(long tr_id) {
        Intent intentOpenAddEx = new Intent(this, ExerciseActivity.class);
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
		String title = getResources().getString(R.string.Dialog_del_ex_title);
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

}
