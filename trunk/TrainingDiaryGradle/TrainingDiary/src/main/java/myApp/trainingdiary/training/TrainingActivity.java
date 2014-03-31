package myApp.trainingdiary.training;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter.ViewBinder;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.util.ArrayList;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.SettingsActivity;
import myApp.trainingdiary.billing.BillingHelper;
import myApp.trainingdiary.billing.BillingPreferencesHelper;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.dialog.EditDialog;
import myApp.trainingdiary.excercise.ExerciseActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.EmptyStringValidator;
import myApp.trainingdiary.utils.TrainingExistValidator;

/*
 * �������� ������ �������������� ���
 */

public class TrainingActivity extends ActionBarActivity {

    private static final int ID_RENAME_TRAINING = 2;
    private static final int ID_MOVE_TRAINING = 3;
    private static final int ID_DELETE_TRAINING = 4;

    private DBHelper dbHelper;
    private DragSortListView trainingList;
    private SimpleDragSortCursorAdapter trainingDragAdapter;
    private EditDialog createInputTextDialog;
    private EditDialog renameTrainingDialog;
    private AlertDialog deleteTrainingDialog;

    private QuickAction trainingActionTools;

    private long cur_tr_id;
    private ImageView cur_drag_handler;

    ////////////////billing////////////
    private LinearLayout adsLayout;
    private BillingHelper billingHelper;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //google analytics
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().setContext(this);

        setContentView(R.layout.training_list);
        dbHelper = DBHelper.getInstance(this);
        trainingList = (DragSortListView) findViewById(R.id.training_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        fetchTrainings();

        createCreateTrDialog();
        createRenameTrDialog();
        createDeletionDialog();
        createTrainingTools();

        context = this;
        billingHelper = BillingHelper.getInstance(this);
        adsLayout = (LinearLayout)findViewById(R.id.ads_layout_trainig_list_activity);

        //ads
        if(getResources().getBoolean(R.bool.show_ads))
            billingHelper.adsShow(adsLayout);

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
                Log.i(Const.LOG_TAG, "drop training");
                trainingDragAdapter.drop(from, to);
                dbHelper.WRITE.changeTrainingPositions(getNewTrIdOrder());
                cur_drag_handler.setVisibility(View.GONE);
            }
        });
    }

    private void createCreateTrDialog() {
        String title = getResources().getString(R.string.title_create_training);
        String positiveButton = getResources().getString(R.string.create_button);
        String negativeButton = getResources().getString(R.string.cancel_button);
        DialogProvider.InputTextDialogClickListener listener = new DialogProvider.InputTextDialogClickListener() {
            @Override
            public void onPositiveClick(String text) {
                dbHelper.WRITE.createTraining(text, dbHelper.READ.getTrainingDayCount(dbHelper.getReadableDatabase()));
//                createInputTextDialog.cancel();
                Toast.makeText(TrainingActivity.this,
                        R.string.create_success, Toast.LENGTH_SHORT).show();
                refreshTrainings();
            }

            @Override
            public void onNegativeClick() {
                createInputTextDialog.cancel();
            }
        };
        createInputTextDialog = DialogProvider.createInputTextDialog(this,
                title,
                positiveButton,
                negativeButton,
                new EmptyStringValidator(new TrainingExistValidator()),
                listener);

    }

    @Override
    protected void onResume() {
        refreshTrainings();
        super.onResume();
    }

    private void createTrainingTools() {
        ActionItem renameItem = new ActionItem(ID_RENAME_TRAINING,
                getResources().getString(R.string.rename_action),
                getResources().getDrawable(R.drawable.icon_content_edit_white));
        ActionItem moveItem = new ActionItem(ID_MOVE_TRAINING, getResources()
                .getString(R.string.move_action), getResources().getDrawable(
                R.drawable.icon_content_import_export_white));
        ActionItem deleteItem = new ActionItem(ID_DELETE_TRAINING,
                getResources().getString(R.string.delete_action),
                getResources().getDrawable(R.drawable.icon_content_remove_white));

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
                            case ID_RENAME_TRAINING: {
                                String tr_name = dbHelper
                                        .READ.getTrainingNameById(cur_tr_id);
                                renameTrainingDialog.show(tr_name);
                                break;
                            }
                            case ID_DELETE_TRAINING:
                                String tr_name = dbHelper
                                        .READ.getTrainingNameById(cur_tr_id);
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
        intentOpenAddEx.putExtra(Const.TRAINING_ID, tr_id);
        startActivity(intentOpenAddEx);
    }

    private void createRenameTrDialog() {
        String title = getResources().getString(R.string.title_rename_training);
        String positiveButton = getResources().getString(R.string.rename_button);
        String negativeButton = getResources().getString(R.string.cancel_button);
        DialogProvider.InputTextDialogClickListener listener = new DialogProvider.InputTextDialogClickListener() {
            @Override
            public void onPositiveClick(String text) {
                dbHelper.WRITE.renameTraining(cur_tr_id, text);
//                renameTrainingDialog.cancel();
                Toast.makeText(TrainingActivity.this,
                        R.string.rename_success, Toast.LENGTH_SHORT).show();
                refreshTrainings();
            }

            @Override
            public void onNegativeClick() {
                renameTrainingDialog.cancel();
            }
        };
        renameTrainingDialog = DialogProvider.createInputTextDialog(this, title, positiveButton, negativeButton,
                new TrainingExistValidator(new EmptyStringValidator()), listener);
    }

    private List<Long> getNewTrIdOrder() {
        List<Long> list = new ArrayList<Long>();
        Log.d(Const.LOG_TAG,
                "getCursorPositions" + trainingDragAdapter.getCursorPositions());
        for (Integer i = 0; i < trainingDragAdapter.getCount(); i++) {
            list.add(trainingDragAdapter.getItemId(i));
        }
        Log.d(Const.LOG_TAG, "getNewIdOrder" + list);
        return list;
    }

    protected void fetchTrainings() {
        Cursor tr_cursor = dbHelper.READ.getTrainings();
        String[] from = {"name", "_id"};
        int[] to = {R.id.label, R.id.tools};
        trainingDragAdapter = new SimpleDragSortCursorAdapter(this,
                R.layout.training_row, tr_cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        trainingDragAdapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {

                if (view.getId() == R.id.tools) {
                    final long tr_id = cursor.getLong(columnIndex);
                    view.setOnClickListener(new OnClickListener() {
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
        trainingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long tr_id = trainingDragAdapter.getItemId(position);
                cur_tr_id = tr_id;
                trainingActionTools.show(view);
                cur_drag_handler = (ImageView) (view
                )
                        .findViewById(R.id.drag_handler);
                return false;
            }
        });
        trainingList.setAdapter(trainingDragAdapter);
        // dbHelper.close();
    }

    private void refreshTrainings() {
        Cursor c = dbHelper.READ.getTrainings();
        trainingDragAdapter.swapCursor(c);
        // dbHelper.close();
    }

    private void createDeletionDialog() {
        String title = getResources().getString(R.string.dialog_del_tr_title);
        String cancelButton = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.delete_button);

        deleteTrainingDialog = DialogProvider.createSimpleDialog(this, title, null, btnDel, cancelButton, new DialogProvider.SimpleDialogClickListener() {
            @Override
            public void onPositiveClick() {
                dbHelper.WRITE.deleteTraining(cur_tr_id);
                refreshTrainings();
                Toast.makeText(TrainingActivity.this, R.string.deleted,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegativeClick() {
                deleteTrainingDialog.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actadd_actsettings_menu, menu);

        //make buy button invisible if ads is not disable
        BillingPreferencesHelper.loadSettings(context);
        if(!getResources().getBoolean(R.bool.show_ads) || BillingPreferencesHelper.isAdsDisabled()){
            MenuItem item = menu.findItem(R.id.action_disable_ads_actadd);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentStat = new Intent(TrainingActivity.this, SettingsActivity.class);
                startActivity(intentStat);
                return true;
            case R.id.action_add:
                createInputTextDialog.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_disable_ads_actadd:
                billingHelper.adsBuy();
                return true;

        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStop(this);
    }

}
