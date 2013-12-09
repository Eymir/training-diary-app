package myApp.trainingdiary.excercise;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.util.ArrayList;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.R.id;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.EditDialog;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.result.ResultActivity;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.utils.Consts;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.utils.EmptyStringValidator;
import myApp.trainingdiary.utils.ExerciseExistValidator;

public class ExerciseActivity extends ActionBarActivity {

    private static final int ID_RENAME_EXERCISE = 5;
    private static final int ID_REMOVE_EXERCISE = 6;
    private static final int ID_MOVE_EXERCISE = 7;
    private static final int ID_STAT_EXERCISE = 8;
    private static final int ID_HISTORY_EXERCISE = 9;

    private long tr_id;
    private String trainingName;
    private DragSortListView exerciseList;
    private SimpleDragSortCursorAdapter exerciseAdapter;

    private DBHelper dbHelper;

    private QuickAction exerciseActionTools;
    private long cur_ex_id;
    private ImageView cur_drag_handler;
    private EditDialog renameExerciseDialog;
    private AlertDialog removeExerciseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_excercise);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = DBHelper.getInstance(this);
        tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);

        trainingName = dbHelper.READ.getTrainingNameById(tr_id);
        setTitle(getTitle() + ": " + trainingName);

        exerciseList = (DragSortListView) findViewById(R.id.exercise_in_training_list);

        fetchExercises();
        createExcerciseTools();
        createRenameExDialog();
        createDeletionDialog();
        exerciseList.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                Log.i(Consts.LOG_TAG, "drop exercise");
                exerciseAdapter.drop(from, to);
                dbHelper.WRITE.changeExercisePositions(tr_id, getNewExIdOrder());

                cur_drag_handler.setVisibility(View.GONE);
            }
        });
        exerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                long ex_id = exerciseAdapter.getItemId(pos);
                openResultActivity(ex_id);
            }
        });
    }

    private void openResultActivity(long ex_id) {
        Intent intentOpenResultAct = new Intent(this, ResultActivity.class);
        intentOpenResultAct.putExtra(Consts.EXERCISE_ID, ex_id);
        intentOpenResultAct.putExtra(Consts.TRAINING_ID, tr_id);
        startActivity(intentOpenResultAct);
    }

    private void openStatActivity(long ex_id) {
        Intent activity = new Intent(this, StatisticActivity.class);
        activity.putExtra(Consts.EXERCISE_ID, ex_id);
        startActivity(activity);
    }

    private void printLogExInTr() {
        Cursor c = dbHelper.READ.getExercisesInTraining(tr_id);
        while (c.moveToNext()) {
            Log.i(Consts.LOG_TAG, "exercise_id: " + c.getLong(c.getColumnIndex("_id")) + " pos: " + c.getLong(c.getColumnIndex("position")));
        }
        c.close();

    }

    private List<Long> getNewExIdOrder() {
        List<Long> list = new ArrayList<Long>();
        Log.d(Consts.LOG_TAG,
                "getCursorPositions" + exerciseAdapter.getCursorPositions());
        for (Integer i = 0; i < exerciseAdapter.getCount(); i++) {
            list.add(exerciseAdapter.getItemId(i));
        }
        Log.d(Consts.LOG_TAG, "getNewIdOrder" + list);
        return list;
    }

    @Override
    protected void onResume() {
        refreshExercise();
        super.onResume();
    }

    private void refreshExercise() {
        Cursor c = dbHelper.READ.getExercisesInTraining(tr_id);
        exerciseAdapter.swapCursor(c);
    }

    private void fetchExercises() {
        Cursor ex_cursor = dbHelper.READ.getExercisesInTraining(tr_id);
        Log.d(Consts.LOG_TAG, "Exercise.count: " + ex_cursor.getCount());
        String[] from = {"name", "icon_res", "_id"};
        int[] to = {R.id.label, R.id.icon, id.ex_tools};
        exerciseAdapter = new SimpleDragSortCursorAdapter(
                ExerciseActivity.this, R.layout.exercise_row, ex_cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        exerciseAdapter.setViewBinder(new SimpleDragSortCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.icon) {
                    ((ImageView) view).setImageResource(getResources()
                            .getIdentifier(cursor.getString(columnIndex),
                                    "drawable", getPackageName()));
                    return true;
                }
//                if (view.getId() == READ.id.label) {
//                    TextView textView = (TextView) view;
//                    textView.setMovementMethod(new ScrollingMovementMethod());
//                    textView.setText(cursor.getString(columnIndex));
//                    textView.setClickable(true);
//                    return true;
//                }
                if (view.getId() == R.id.ex_tools) {
                    final long ex_id = cursor.getLong(columnIndex);
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(Consts.LOG_TAG, "ex tool click");
                            exerciseActionTools.show(v);
                            cur_ex_id = ex_id;
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

        exerciseList.setAdapter(exerciseAdapter);
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
                Toast.makeText(ExerciseActivity.this,
                        R.string.rename_success, Toast.LENGTH_SHORT).show();
                refreshExercise();
            }

            @Override
            public void onNegativeClick() {
                renameExerciseDialog.cancel();
            }
        };
        renameExerciseDialog = DialogProvider.createInputTextDialog(this, title, positiveButton, negativeButton,
                new ExerciseExistValidator(new EmptyStringValidator()), listener);
    }

    protected void openAddExerciseActivity(long tr_id) {
        Intent intentOpenAddEx = new Intent(this, AddExerciseActivity.class);
        intentOpenAddEx.putExtra(Consts.TRAINING_ID, tr_id);
        startActivity(intentOpenAddEx);
    }

    protected void openHistoryDetailActivity(long ex_id) {
        Intent intentOpenAct = new Intent(this, HistoryDetailActivity.class);
        intentOpenAct.putExtra(Consts.EXERCISE_ID, ex_id);
        intentOpenAct.putExtra(Consts.HISTORY_TYPE, Consts.EXERCISE_TYPE);
        startActivity(intentOpenAct);
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
        ActionItem moveItem = new ActionItem(ID_MOVE_EXERCISE, getResources()
                .getString(R.string.move_action), getResources().getDrawable(
                R.drawable.icon_content_import_export_white));
        ActionItem removeItem = new ActionItem(ID_REMOVE_EXERCISE,
                getResources().getString(R.string.delete_action),
                getResources().getDrawable(R.drawable.icon_content_remove_white));

        exerciseActionTools = new QuickAction(this);
        exerciseActionTools.addActionItem(renameItem);
        exerciseActionTools.addActionItem(moveItem);
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
                                break;
                            }
                            case ID_REMOVE_EXERCISE:
                                String ex_name = dbHelper
                                        .READ.getExerciseNameById(cur_ex_id);
                                removeExerciseDialog.setMessage(String.format(
                                        getResources().getString(
                                                R.string.Dialog_del_ex_msg),
                                        ex_name, trainingName));
                                removeExerciseDialog.show();
                                break;
                            case ID_MOVE_EXERCISE:
                                cur_drag_handler.setVisibility(View.VISIBLE);
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

    private void initChoosePanel() {
        Cursor ex_cursor = dbHelper.READ.getExercisesExceptExInTr(tr_id);
        Log.d(Consts.LOG_TAG, "Exercise.count: " + ex_cursor.getCount());
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
        SimpleCursorAdapter exerciseAdapter = new SimpleCursorAdapter(
                ExerciseActivity.this, R.layout.exercise_row, ex_cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        exerciseAdapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    // �� ��������
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


    private void createDeletionDialog() {

        String title = getResources().getString(R.string.Dialog_del_ex_title);
        String cancelButton = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.delete_button);

        removeExerciseDialog = DialogProvider.createSimpleDialog(this, title,null, btnDel, cancelButton, new DialogProvider.SimpleDialogClickListener() {
            @Override
            public void onPositiveClick() {
                dbHelper.WRITE.deleteExerciseFromTraining(tr_id, cur_ex_id);
                refreshExercise();
                Toast.makeText(ExerciseActivity.this, R.string.deleted,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegativeClick() {
                removeExerciseDialog.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actadd_actsettings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentStat = new Intent(ExerciseActivity.this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case R.id.action_add:
                openAddExerciseActivity(tr_id);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return true;
    }

}
