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
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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

	// Layout-ы по клику на которые показывается или скрывается панель
	// создани\добавления упражнения
	private View createClickableLayout;
	private View chooseClickableLayout;

	// панели создания выбора упражнения
	private View createExGonablePanel;
	private ListView exerciseList;
	DBHelper dbHelper;
	private AddExGuiMediator guiMediator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_exercise);
		dbHelper = new DBHelper(this);
		tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);

		createClickableLayout = (View) findViewById(R.id.create_exercise_label_layout);
		chooseClickableLayout = (View) findViewById(R.id.exercise_list_label_Layout);

		createExGonablePanel = (View) findViewById(R.id.create_exercise_layout);
		exerciseList = (ListView) findViewById(R.id.exercise_list);

		guiMediator = new AddExGuiMediator(this, createClickableLayout,
				chooseClickableLayout, createExGonablePanel, exerciseList);

		initCreatePanel();
		initChoosePanel();

	}

	private void initChoosePanel() {
		Cursor ex_cursor = dbHelper.getExercisesExceptExInTr(tr_id);
		String[] from = { "name", "icon_res" };
		int[] to = { R.id.label, R.id.icon };
		SimpleCursorAdapter exerciseDragAdapter = new SimpleCursorAdapter(
				AddExerciseActivity.this, R.layout.exercise_row, ex_cursor,
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		exerciseDragAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.icon) {
					// Не проверял
					((ImageView) view).setImageResource(getResources()
							.getIdentifier(cursor.getString(columnIndex),
									"drawable", getPackageName()));
					return true;
				}
				return false;
			}
		});

		exerciseList.setAdapter(exerciseDragAdapter);
	}

	private void initCreatePanel() {
		EditText name_edit = (EditText) createExGonablePanel
				.findViewById(R.id.name_edit);
		Spinner type_spinner = (Spinner) createExGonablePanel
				.findViewById(R.id.type_spinner);
		Button create_button = (Button) createExGonablePanel
				.findViewById(R.id.create_button);
		//TODO: Дописать создание упражнения
	}

}
