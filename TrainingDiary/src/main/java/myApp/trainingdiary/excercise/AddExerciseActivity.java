package myApp.trainingdiary.excercise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import myApp.trainingdiary.R;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.dialog.EditDialog;
import myApp.trainingdiary.dialog.ViewExerciseDialog;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.EmptyStringValidator;
import myApp.trainingdiary.utils.ExerciseExistValidator;

public class AddExerciseActivity extends ActionBarActivity {

    private static final int ID_VIEW_EXERCISE = 7;
    private static final int ID_RENAME_EXERCISE = 5;
    private static final int ID_REMOVE_EXERCISE = 6;
    private static final int ID_STAT_EXERCISE = 8;
    private static final int ID_HISTORY_EXERCISE = 9;

    private long tr_id = -1;
    private String trainingName;

    private long cur_ex_id;
    private ListView exerciseList;
    private SimpleCursorAdapter exerciseAdapter;
    private DBHelper dbHelper;

    private Dialog createExerciseDialog;
    private EditDialog renameExerciseDialog;
    private AlertDialog removeExerciseDialog;

    private QuickAction exerciseActionTools;
    private ViewExerciseDialog viewExerciseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_add_exercise);
        dbHelper = DBHelper.getInstance(this);

        createCreateExerciseDialog();
        createViewExerciseDialog();
        createExcerciseTools();
        createRenameExDialog();
        createDeletionDialog();

        try {
            tr_id = getIntent().getExtras().getLong(Const.TRAINING_ID);
        } catch (NullPointerException e) {
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (tr_id > 0)
            trainingName = dbHelper.READ.getTrainingNameById(tr_id);

        exerciseList = (ListView) findViewById(R.id.exercise_list);

        if (tr_id > 0) {
            exerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    long ex_id = exerciseAdapter.getItemId(pos);
                    String ex_name = dbHelper.READ.getExerciseNameById(ex_id);
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
        } else {
            exerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    long ex_id = exerciseAdapter.getItemId(pos);
                    viewExerciseDialog.show(dbHelper.READ.getExerciseById(ex_id));
                }
            });

        }
        initExerciseList();
        View emptyView = findViewById(R.id.empty_view);
        exerciseList.setEmptyView(emptyView);
    }

    private void createViewExerciseDialog() {
        viewExerciseDialog = DialogProvider.createViewExerciseDialog(this, new DialogProvider.OkClickListener() {
            @Override
            public void onPositiveClick() {
                viewExerciseDialog.cancel();
            }
        });
    }

    private void createCreateExerciseDialog() {
        createExerciseDialog = DialogProvider.createCreateExerciseDialog(this, new DialogProvider.CreateExerciseDialogClickListener() {

            @Override
            public void onPositiveClick(Exercise exercise) {
                createExercise(exercise);
                if (tr_id > 0) {
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(
                                    R.string.create_exercise_add_in_tr_success,
                                    exercise.getName(),
                                    trainingName), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(
                                    R.string.create_exercise_success,
                                    exercise.getName()
                            ), Toast.LENGTH_SHORT).show();
                    refreshChooseList();
                }

            }

            @Override
            public void onNegativeClick() {
                createExerciseDialog.cancel();
            }
        });
    }

    private void createRenameExDialog() {
        String title = getResources().getString(R.string.title_rename_exercise);
        String positiveButton = getResources().getString(R.string.rename_button);
        String negativeButton = getResources().getString(R.string.cancel_button);
        DialogProvider.InputTextDialogClickListener listener = new DialogProvider.InputTextDialogClickListener() {
            @Override
            public void onPositiveClick(String text) {
                dbHelper.WRITE.renameExercise(cur_ex_id, text);
                renameExerciseDialog.cancel();
                Toast.makeText(AddExerciseActivity.this,
                        R.string.rename_success, Toast.LENGTH_SHORT).show();
                refreshChooseList();
            }

            @Override
            public void onNegativeClick() {
                renameExerciseDialog.cancel();
            }
        };
        renameExerciseDialog = DialogProvider.createInputTextDialog(this, title, positiveButton, negativeButton,
                new ExerciseExistValidator(new EmptyStringValidator()), listener);
    }

    private void createDeletionDialog() {

        String title = getResources().getString(R.string.Dialog_del_ex_title);
        String cancelButton = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.delete_button);

        removeExerciseDialog = DialogProvider.createSimpleDialog(this, title,null, btnDel, cancelButton, new DialogProvider.SimpleDialogClickListener() {
            @Override
            public void onPositiveClick() {
                if (dbHelper.WRITE.deleteExerciseWithStat(cur_ex_id)) {
                    refreshChooseList();
                    Toast.makeText(AddExerciseActivity.this, R.string.deleted,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddExerciseActivity.this, R.string.delete_not_succesful,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeClick() {
                removeExerciseDialog.cancel();
            }
        });
    }

    private void addExercise(long ex_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            dbHelper.WRITE.insertExerciseInTrainingAtEnd(db, tr_id, ex_id);
        } catch (SQLException e) {
            Log.e(Const.LOG_TAG, "Error while adding exercise", e);
        } finally {
            db.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actadd_actsettings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentStat = new Intent(this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case R.id.action_add:
                createExerciseDialog.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return true;
    }

    private void refreshChooseList() {
        Cursor ex_cursor = dbHelper.READ.getExercisesExceptExInTr(tr_id);
        exerciseAdapter.swapCursor(ex_cursor);
        exerciseList.refreshDrawableState();
    }

    private void initExerciseList() {
        Cursor ex_cursor = dbHelper.READ.getExercisesExceptExInTr(tr_id);
        Log.d(Const.LOG_TAG, "Exercise.count: " + dbHelper.READ.getExerciseCount(dbHelper.getWritableDatabase()));
        Log.d(Const.LOG_TAG, "Cursor.count: " + ex_cursor.getCount());
        String[] from = {"name", "icon_res", "_id"};
        int[] to = {R.id.label, R.id.icon, R.id.ex_tools};
        exerciseAdapter = new SimpleCursorAdapter(
                AddExerciseActivity.this, layout.exercise_row, ex_cursor,
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
                if (view.getId() == R.id.ex_tools) {
                    final long ex_id = cursor.getLong(columnIndex);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(Const.LOG_TAG, "ex tool click");
                            exerciseActionTools.show(v);
                            cur_ex_id = ex_id;
                        }
                    });
                    return true;
                }
                return false;

            }
        });

        exerciseList.setAdapter(exerciseAdapter);
    }


    protected void createExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long ex_id = dbHelper.WRITE.insertExercise(db, exercise.getName(),
                    exercise.getType().getId());
            if (tr_id > 0)
                dbHelper.WRITE.insertExerciseInTrainingAtEnd(db, tr_id, ex_id);
        } catch (SQLException e) {
            Log.e(Const.LOG_TAG, "Error while adding exercise", e);
        } finally {
            db.close();
        }
    }

    private void createExcerciseTools() {
        ActionItem renameItem = new ActionItem(ID_RENAME_EXERCISE,
                getResources().getString(R.string.rename_action),
                getResources().getDrawable(R.drawable.icon_content_edit_white));
        ActionItem statItem = new ActionItem(ID_STAT_EXERCISE,
                getResources().getString(R.string.stat_action),
                getResources().getDrawable(R.drawable.icon_action_graph_white));
        ActionItem historyItem = new ActionItem(ID_HISTORY_EXERCISE,
                getResources().getString(R.string.history_action),
                getResources().getDrawable(R.drawable.icon_action_history_white));
        ActionItem viewItem = new ActionItem(ID_VIEW_EXERCISE, getResources()
                .getString(R.string.browse), getResources().getDrawable(
                R.drawable.icon_action_info));
        ActionItem removeItem = new ActionItem(ID_REMOVE_EXERCISE,
                getResources().getString(R.string.delete_action),
                getResources().getDrawable(R.drawable.icon_content_remove_white));

        exerciseActionTools = new QuickAction(this);
        exerciseActionTools.addActionItem(viewItem);
        exerciseActionTools.addActionItem(renameItem);
        exerciseActionTools.addActionItem(historyItem);
        exerciseActionTools.addActionItem(statItem);
        exerciseActionTools.addActionItem(removeItem);

        // setup the action item click listener
        exerciseActionTools
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction quickAction, int pos,
                                            int actionId) {
                        ActionItem actionItem = quickAction.getActionItem(pos);

                        switch (actionId) {
                            case ID_RENAME_EXERCISE: {
                                String ex_name = dbHelper
                                        .READ.getExerciseNameById(cur_ex_id);
                                renameExerciseDialog.show(ex_name);
                            }
                            break;
                            case ID_REMOVE_EXERCISE:
                                String ex_name = dbHelper
                                        .READ.getExerciseNameById(cur_ex_id);
                                removeExerciseDialog.setMessage(String.format(
                                        getResources().getString(
                                                R.string.Dialog_delete_completely_txt),
                                        ex_name));
                                removeExerciseDialog.show();
                                break;
                            case ID_VIEW_EXERCISE:
                                viewExerciseDialog.show(dbHelper
                                        .READ.getExerciseById(cur_ex_id));
                                break;
                            case ID_HISTORY_EXERCISE:
                                openHistoryDetailActivity(cur_ex_id);
                                break;
                            case ID_STAT_EXERCISE:
                                openStatActivity(cur_ex_id);
                                break;
                        }
                    }
                });
    }

    private void openHistoryDetailActivity(long ex_id) {
        Intent intentOpenAct = new Intent(this, HistoryDetailActivity.class);
        intentOpenAct.putExtra(Const.EXERCISE_ID, ex_id);
        intentOpenAct.putExtra(Const.HISTORY_TYPE, Const.EXERCISE_TYPE);
        startActivity(intentOpenAct);
    }

    private void openStatActivity(long ex_id) {
        Intent activity = new Intent(this, StatisticActivity.class);
        activity.putExtra(Const.EXERCISE_ID, ex_id);
        startActivity(activity);
    }
}
