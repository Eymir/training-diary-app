package myApp.trainingdiary.excercise;

import myApp.trainingdiary.R;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.utils.Consts;
import myApp.trainingdiary.db.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExerciseActivity extends Activity {
    private long tr_id = -1;
    private String trainingName;

    private View createExGonablePanel;
    private ListView exerciseList;
    private SimpleCursorAdapter exerciseAdapter;
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
        try {
            tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);
        } catch (NullPointerException e) {
        }

        if (tr_id > 0)
            trainingName = dbHelper.getTrainingNameById(tr_id);

        View createClickableLayout = findViewById(R.id.create_exercise_label_layout);
        View chooseClickableLayout = findViewById(R.id.exercise_list_label_Layout);

        createExGonablePanel = findViewById(R.id.create_exercise_layout);
        exerciseList = (ListView) findViewById(R.id.exercise_list);
        View exerciseListGonablePanel = findViewById(R.id.list_panel);

        if (tr_id > 0)
            exerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    long ex_id = exerciseAdapter.getItemId(pos);
                    String ex_name = dbHelper.getExerciseNameById(ex_id);
                    addExercise(ex_id);
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(
                                    R.string.add_exercise_success,
                                    ex_name,
                                    trainingName), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        name_edit = (EditText) createExGonablePanel
                .findViewById(R.id.name_edit);

        type_spinner = (Spinner) createExGonablePanel
                .findViewById(R.id.type_spinner);
        create_button = (Button) createExGonablePanel
                .findViewById(R.id.create_button);

        guiMediator = new AddExGuiMediator(this,name_edit, createClickableLayout,
                chooseClickableLayout, createExGonablePanel, exerciseListGonablePanel);

        initCreatePanel();
        initChoosePanel();
        guiMediator.clickCreatePanel();
        guiMediator.clickChoosePanel();
        View emptyView = findViewById(R.id.empty_view);
        exerciseList.setEmptyView(emptyView);
    }

    private void addExercise(long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            dbHelper.insertExerciseInTrainingAtEnd(db, tr_id, ex_id);
        } catch (SQLException e) {
            Log.e(Consts.LOG_TAG, "Error while adding exercise", e);
        } finally {
            db.close();
        }
    }

    private void refreshChooseList() {
        Cursor ex_cursor = dbHelper.getExercisesExceptExInTr(tr_id);
        exerciseAdapter.swapCursor(ex_cursor);
    }

    private void initChoosePanel() {
        Cursor ex_cursor = dbHelper.getExercisesExceptExInTr(tr_id);
        Log.d(Consts.LOG_TAG, "Exercise.count: " + dbHelper.getExerciseCount(dbHelper.getWritableDatabase()));
        Log.d(Consts.LOG_TAG, "Cursor.count: " + ex_cursor.getCount());
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
        exerciseAdapter = new SimpleCursorAdapter(
                AddExerciseActivity.this, layout.exercise_plain_row, ex_cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        exerciseAdapter.setViewBinder(new ViewBinder() {
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

        exerciseList.setAdapter(exerciseAdapter);
    }

    private void initCreatePanel() {
        Cursor type_cursor = dbHelper.getExerciseTypes();
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
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

                    if (tr_id > 0) {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(
                                        R.string.create_exercise_add_in_tr_success,
                                        name_edit.getText().toString(),
                                        trainingName), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        guiMediator.clickChoosePanel();
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(
                                        R.string.create_exercise_success,
                                        name_edit.getText().toString()
                                ), Toast.LENGTH_SHORT).show();
                        refreshChooseList();
                    }
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
            if (tr_id > 0)
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
        if (dbHelper.isExerciseInDB(name_edit.getText().toString())) {
            Toast.makeText(this, R.string.exercise_exist_notif,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
