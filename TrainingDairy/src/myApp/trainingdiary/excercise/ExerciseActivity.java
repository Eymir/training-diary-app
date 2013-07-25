package myApp.trainingdiary.excercise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.*;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import myApp.trainingdiary.R;
import myApp.trainingdiary.R.id;
import myApp.trainingdiary.R.layout;
import myApp.trainingdiary.history.HistoryDetailActivity;
import myApp.trainingdiary.result.ResultActivity;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.utils.Consts;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.utils.Validator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.util.ArrayList;
import java.util.List;

public class ExerciseActivity extends Activity {

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
    private Dialog renameExerciseDialog;
    private AlertDialog removeExerciseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_excercise);
        dbHelper = DBHelper.getInstance(this);
        tr_id = getIntent().getExtras().getLong(Consts.TRAINING_ID);

        trainingName = dbHelper.READ.getTrainingNameById(tr_id);
        setTitle(getTitle() + ": " + trainingName);

        exerciseList = (DragSortListView) findViewById(R.id.exercise_in_training_list);
        View addRowFooter = getLayoutInflater().inflate(R.layout.add_row, null);
        exerciseList.addFooterView(addRowFooter);

        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddExerciseActivity(tr_id);
            }
        });

        fetchExercises();
        createExcerciseTools();
        createRenameTrDialog();
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
                    view.setOnClickListener(new View.OnClickListener() {
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

    private void createRenameTrDialog() {
        renameExerciseDialog = new Dialog(this);
        renameExerciseDialog.setContentView(R.layout.input_name_dialog);
        renameExerciseDialog.setTitle(R.string.title_rename_exercise);
        final EditText name_input = (EditText) renameExerciseDialog
                .findViewById(R.id.name_input);
        Button okButton = (Button) renameExerciseDialog
                .findViewById(R.id.ok_button);
        okButton.setText(R.string.rename_button);
        Button cancelButton = (Button) renameExerciseDialog
                .findViewById(R.id.cancel_button);
        cancelButton.setText(R.string.cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                renameExerciseDialog.cancel();
            }
        });

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_input.getText().toString();
                if (Validator.validateEmpty(ExerciseActivity.this, name)
                        && Validator.validateTraining(ExerciseActivity.this, name)) {
                    dbHelper.WRITE.renameExercise(cur_ex_id, name);
                    renameExerciseDialog.cancel();
                    Toast.makeText(ExerciseActivity.this,
                            R.string.rename_success, Toast.LENGTH_SHORT).show();
                    refreshExercise();
                }
            }
        });
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
                getResources().getDrawable(R.drawable.pencil));
        ActionItem statItem = new ActionItem(ID_STAT_EXERCISE,
                getResources().getString(R.string.stat_action),
                getResources().getDrawable(R.drawable.statistics));
        ActionItem historyItem = new ActionItem(ID_HISTORY_EXERCISE,
                getResources().getString(R.string.history_action),
                getResources().getDrawable(R.drawable.carving_time_icon_12));
        ActionItem moveItem = new ActionItem(ID_MOVE_EXERCISE, getResources()
                .getString(R.string.move_action), getResources().getDrawable(
                R.drawable.object_flip_vertical));
        ActionItem removeItem = new ActionItem(ID_REMOVE_EXERCISE,
                getResources().getString(R.string.delete_action),
                getResources().getDrawable(R.drawable.deletered));

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
                            case ID_RENAME_EXERCISE:
                                renameExerciseDialog.show();
                                break;
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

        String title = getResources().getString(R.string.dialog_del_tr_title);
        String btnRename = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.delete_button);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle(title);

        adb.setPositiveButton(btnDel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbHelper.WRITE.deleteExerciseFromTraining(tr_id, cur_ex_id);
                refreshExercise();
                Toast.makeText(ExerciseActivity.this, R.string.deleted,
                        Toast.LENGTH_SHORT).show();
            }
        });
        adb.setNegativeButton(btnRename, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeExerciseDialog.cancel();
            }
        });

        removeExerciseDialog = adb.create();
    }


}
