package myApp.trainingdiary.addex;

import myApp.trainingdiary.TrainingActivity;
import myApp.trainingdiary.R;
import myApp.trainingdiary.R.id;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.constant.Consts;
import myApp.trainingdiary.forBD.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExerciseActivity extends Activity {
	private long tr_id;
	private String trainingName;
	// панели создани€ выбора упражнени€
	private View createExGonablePanel;
	private ListView exerciseList;
	private DBHelper dbHelper;
	private AddExGuiMediator guiMediator;

	private EditText name_edit;
	private Spinner type_spinner;
	private Button create_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		dbHelper = DBHelper.getInstance(this);
		tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);

		trainingName = dbHelper.getTrainingNameById(tr_id);

		View createClickableLayout = (View) findViewById(R.id.create_exercise_label_layout);
		View chooseClickableLayout = (View) findViewById(R.id.exercise_list_label_Layout);

		createExGonablePanel = (View) findViewById(R.id.create_exercise_layout);
		exerciseList = (ListView) findViewById(R.id.exercise_list);

		name_edit = (EditText) createExGonablePanel
				.findViewById(R.id.name_edit);
		type_spinner = (Spinner) createExGonablePanel
				.findViewById(R.id.type_spinner);
		create_button = (Button) createExGonablePanel
				.findViewById(R.id.create_button);

		guiMediator = new AddExGuiMediator(this, createClickableLayout,
				chooseClickableLayout, createExGonablePanel, exerciseList);

		initCreatePanel();
		initChoosePanel();
	}

	private void initChoosePanel() {
		Cursor ex_cursor = dbHelper.getExercisesExceptExInTr(tr_id);
		Log.d(Consts.LOG_TAG, "Exercise.count: " + ex_cursor.getCount());
		String[] from = { "name", "icon_res" };
		int[] to = { R.id.label, R.id.icon };
		SimpleCursorAdapter exerciseAdapter = new SimpleCursorAdapter(
				AddExerciseActivity.this, R.layout.exercise_row, ex_cursor,
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		exerciseAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {
					// Ќе провер€л
					((ImageView) view).setImageResource(getResources()
							.getIdentifier(cursor.getString(columnIndex),
									"drawable", getPackageName()));
					return true;
				}
				return false;
			}
		});

		exerciseList.setAdapter(exerciseAdapter);
	}

	private void initCreatePanel() {
		Cursor type_cursor = dbHelper.getExerciseTypes();
		String[] from = { "name", "icon_res" };
		int[] to = { R.id.label, R.id.icon };
		SimpleCursorAdapter typeAdapter = new SimpleCursorAdapter(
				AddExerciseActivity.this, R.layout.exercise_type_row,
				type_cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		typeAdapter.setViewBinder(new ViewBinder() {

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
		type_spinner.setAdapter(typeAdapter);

		create_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				if (validateCreateForm()) {
					createExercise();
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.create_exercise_success,
									name_edit.getText().toString(),
									trainingName), Toast.LENGTH_SHORT).show();
					finish();
				}
			}

		});
	}

	protected void createExercise() {
		Log.d(Consts.LOG_TAG, "tr_id: " + tr_id + " name_edit: "
				+ name_edit.getText().toString() + " type_spinner.id:"
				+ type_spinner.getSelectedItemId());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			long ex_id = dbHelper.insertExercise(db, name_edit.getText()
					.toString(), type_spinner.getSelectedItemId());
			dbHelper.insertExerciseInTrainingAtEnd(db, tr_id, ex_id);
		} catch (SQLException e) {
			Log.e(Consts.LOG_TAG, "Error while adding exercise", e);
		} finally {
			db.close();
		}
	}

	private boolean validateCreateForm() {
		if (name_edit.getText() == null
				|| name_edit.getText().toString() == null
				|| name_edit.getText().toString().isEmpty()) {
			Toast.makeText(this, R.string.input_exercise_name_notification,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (type_spinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
			Toast.makeText(this, R.string.input_exercise_type_notification,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

}
