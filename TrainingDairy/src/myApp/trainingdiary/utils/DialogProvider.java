package myApp.trainingdiary.utils;

import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.CustomCursorAdapter;
import myApp.trainingdiary.customview.EntityArrayAdapter;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.statistic.StatisticActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogProvider {
    private Context context;

    public DialogProvider(Context context) {
        this.context = context;
    }

    public static AlertDialog createInputTextDialog(final Activity activity, String title, String positiveTitle, String negativeTitle, final Validator validator, final InputTextDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.input_name_dialog, null);

        final EditText editText = (EditText) view.findViewById(R.id.name_input);
        builder.setView(view);

        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                assert editText != null;
                if (validator.validate(activity, editText.getText().toString())) {
                    listener.onPositiveClick(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton(negativeTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClick();
            }
        });

        return builder.create();
    }

    public static AlertDialog createSimpleDialog(final Activity activity, String title, String positiveTitle, String negativeTitle, final SimpleDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveClick();
            }
        });
        builder.setNegativeButton(negativeTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClick();
            }
        });
        return builder.create();
    }

    public static AlertDialog createStatisticSettingDialog(final StatisticActivity activity, final Long ex_id, final StatisticSettingsDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_statistic_setting_title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.stat_settings_dialog, null);

        final Spinner exerciseSpinner = (Spinner) view.findViewById(R.id.ex_stat_spinner);
        final Spinner measureSpinner = (Spinner) view.findViewById(R.id.measure_stat_spinner);
        final Spinner groupSpinner = (Spinner) view.findViewById(R.id.group_by_stat_spinner);
        final Button groupsButton = (Button) view.findViewById(R.id.groups_button);

        final TableRow tableRow3 = (TableRow) view.findViewById(R.id.tableRow3);
        final TableRow tableRow4 = (TableRow) view.findViewById(R.id.tableRow4);

        final DBHelper dbHelper = DBHelper.getInstance(activity);

        final CheckBox groupByCheckBox = (CheckBox) view.findViewById(R.id.group_by_checkbox);

        groupByCheckBox.setChecked(false);

        final SimpleCursorAdapter exerciseAdapter = getExerciseAdapter(activity, dbHelper, activity);
        exerciseSpinner.setAdapter(exerciseAdapter);
        final EntityArrayAdapter measureAdapter = getMeasureAdapter(dbHelper, activity, ex_id);
        measureSpinner.setAdapter(measureAdapter);
        final EntityArrayAdapter groupAdapter = getMeasureAdapterWithoutData(activity);
        groupSpinner.setAdapter(groupAdapter);
        final ArrayAdapter groupValueAdapter = getGroupValueAdapter(activity);

        final AlertDialog.Builder groupsDialogBuilder = new AlertDialog.Builder(activity);
        final ListView groupListView = (ListView) inflater.inflate(R.layout.list, null);
        groupListView.setAdapter(groupValueAdapter);
        groupListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        groupsDialogBuilder.setTitle(R.string.group_values_dialog_title);
        groupsDialogBuilder.setView(groupListView);
        groupsDialogBuilder.setPositiveButton(R.string.btn_txt_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(Consts.LOG_TAG, "getCheckedItemPositions.size: " + groupListView.getCheckedItemPositions().size());
                List<Double> list = getChosenObjects(groupValueAdapter, groupListView.getCheckedItemPositions());
                groupsButton.setText(list.toString());
            }
        });
        final AlertDialog groupsDialog = groupsDialogBuilder.create();
        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupsDialog.show();
            }
        });

        groupByCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    tableRow3.setVisibility(View.VISIBLE);
                    tableRow4.setVisibility(View.VISIBLE);
                    groupAdapter.clear();
                    Measure m = (Measure) measureSpinner.getSelectedItem();
                    groupAdapter.addAll(dbHelper.READ.getMeasuresInExerciseExceptParticularMeasure(
                            exerciseSpinner.getSelectedItemId(), m.getId()));
                } else {
                    tableRow3.setVisibility(View.GONE);
                    tableRow4.setVisibility(View.GONE);
                }
            }
        });

        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                measureAdapter.clear();
                measureAdapter.addAll(dbHelper.READ.getMeasuresInExercise(exerciseSpinner.getSelectedItemId()));
                if (measureAdapter.getCount() > 1 && groupByCheckBox.isChecked()) {
                    groupAdapter.clear();
                    Measure m = (Measure) measureSpinner.getSelectedItem();
                    groupAdapter.addAll(dbHelper.READ.getMeasuresInExerciseExceptParticularMeasure(
                            exerciseSpinner.getSelectedItemId(), m.getId()));
                    Measure m_g = (Measure) groupSpinner.getSelectedItem();
                    if (m_g != null) {
                        List<Double> list = getGroups(dbHelper, m_g.getId(),
                                exerciseSpinner.getSelectedItemId());
                        if (list != null) {
                            groupValueAdapter.clear();
                            groupValueAdapter.addAll(list);
                        } else {
                            Log.e(Consts.LOG_TAG, "groups is null");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        measureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (measureAdapter.getCount() > 1 && groupByCheckBox.isChecked()) {
                    groupAdapter.clear();
                    Measure m = (Measure) measureSpinner.getSelectedItem();
                    List<Measure> measures = dbHelper.READ.getMeasuresInExerciseExceptParticularMeasure(
                            exerciseSpinner.getSelectedItemId(), m.getId());
                    groupAdapter.addAll(measures);
                    Measure m_g = (Measure) groupSpinner.getSelectedItem();
                    if (m_g != null) {
                        List<Double> list = getGroups(dbHelper, m_g.getId(),
                                exerciseSpinner.getSelectedItemId());
                        if (list != null) {
                            groupValueAdapter.clear();
                            groupValueAdapter.addAll(list);
                        } else {
                            Log.e(Consts.LOG_TAG, "groups is null");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (groupSpinner.getSelectedItem() != null) {
                    if (groupSpinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
                        Log.e(Consts.LOG_TAG, "groupSpinner has invalid row");
                        return;
                    }
                    if (exerciseSpinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
                        Log.e(Consts.LOG_TAG, "exerciseSpinner has invalid row");
                        return;
                    }
                    Measure m = (Measure) groupSpinner.getSelectedItem();
                    if (m != null) {
                        List<Double> list = getGroups(dbHelper, m.getId(),
                                exerciseSpinner.getSelectedItemId());
                        if (list != null) {
                            groupValueAdapter.clear();
                            groupValueAdapter.addAll(list);
                        } else {
                            Log.e(Consts.LOG_TAG, "groups is null");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton(R.string.build_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (exerciseSpinner.getSelectedItemId() != AdapterView.INVALID_ROW_ID) {
                    Long measureId = (measureSpinner.getSelectedItem() != null) ? ((Measure) measureSpinner.getSelectedItem()).getId() : null;
                    Long groupId = (groupSpinner.getSelectedItem() != null) ? ((Measure) groupSpinner.getSelectedItem()).getId() : null;
                    List<Double> list = getChosenObjects(groupValueAdapter, groupListView.getCheckedItemPositions());
                    StatisticSettingsEvent event = new StatisticSettingsEvent(exerciseSpinner.getSelectedItemId(),
                            measureId,
                            groupId,
                            list);
                    listener.onPositiveClick(event);
                } else {
                    Toast.makeText(activity, R.string.exercise_not_chosen, Toast.LENGTH_SHORT);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClick();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private static List<Double> getChosenObjects(ArrayAdapter adapter, SparseBooleanArray checkedItemPositions) {
        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.get(i)) {
                list.add((Double) adapter.getItem(i));
            }
        }
        return list;
    }

    private static List<Double> getGroups(DBHelper dbHelper, Long m_id, Long ex_id) {
        Log.d(Consts.LOG_TAG, "getGroups m_id: " + m_id + " ex_id: " + ex_id);
        Long pos = dbHelper.READ.getMeasurePosInExercise(ex_id, m_id);
        if (pos == null) {
            Log.e(Consts.LOG_TAG, "pos is null");
            return null;
        }
        Log.d(Consts.LOG_TAG, "pos: " + pos);
        List<TrainingStat> stats = dbHelper.READ.getExerciseProgress(ex_id);
        List<Double> list = new ArrayList<Double>();
        for (TrainingStat stat : stats) {
            Double m_value = MeasureFormatter.getValueByPos(stat.getValue(), pos.intValue());
            if (!list.contains(m_value)) list.add(m_value);
        }
        return list;
    }

    private static ArrayAdapter getGroupValueAdapter(StatisticActivity activity) {
        ArrayList<Double> list = new ArrayList<Double>();
        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_multiple_choice, list);
        return adapter;
    }

    private static EntityArrayAdapter getMeasureAdapter(DBHelper dbHelper, Context context, Long ex_id) {
        ArrayList<Measure> list = (ArrayList<Measure>) dbHelper.READ.getMeasuresInExercise(ex_id);
        EntityArrayAdapter adapter = new EntityArrayAdapter(context, R.layout.entity_spinner_string, R.layout.entity_spinner_string_list, list);
        return adapter;
    }

    private static EntityArrayAdapter getMeasureAdapterWithoutData(Context context) {
        ArrayList<Measure> list = new ArrayList<Measure>();
        EntityArrayAdapter adapter = new EntityArrayAdapter(context, R.layout.entity_spinner_string, R.layout.entity_spinner_string_list, list);
        return adapter;
    }

    private static SimpleCursorAdapter getExerciseAdapter(Activity activity, DBHelper dbHelper, final Context context) {

        Cursor ex_cursor = dbHelper.READ.getExercisesWithStat();
        String[] from = {"ex_name", "icon"};
        int[] to = {R.id.label, R.id.icon};
        CustomCursorAdapter adapter = new CustomCursorAdapter(activity,
                context, R.layout.exercise_plain_row_spinner, R.layout.exercise_plain_row_spinner_list,
                ex_cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        return adapter;
    }


    public static interface InputTextDialogClickListener {
        void onPositiveClick(String text);

        void onNegativeClick();
    }

    public static interface SimpleDialogClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    public static interface StatisticSettingsDialogClickListener {
        void onPositiveClick(StatisticSettingsEvent event);

        void onNegativeClick();
    }

    public static class StatisticSettingsEvent {
        private Long exId;
        private Long drawMeasureId;
        private Long groupMeasureId;
        private List<Double> groups;

        public StatisticSettingsEvent() {
        }

        public StatisticSettingsEvent(Long exId, Long drawMeasureId, Long groupMeasureId, List<Double> groups) {
            this.exId = exId;
            this.drawMeasureId = drawMeasureId;
            this.groupMeasureId = groupMeasureId;
            this.groups = groups;
        }

        public Long getExId() {
            return exId;
        }

        public void setExId(Long exId) {
            this.exId = exId;
        }

        public Long getDrawMeasureId() {
            return drawMeasureId;
        }

        public void setDrawMeasureId(Long drawMeasureId) {
            this.drawMeasureId = drawMeasureId;
        }

        public Long getGroupMeasureId() {
            return groupMeasureId;
        }

        public void setGroupMeasureId(Long groupMeasureId) {
            this.groupMeasureId = groupMeasureId;
        }

        public List<Double> getGroups() {
            return groups;
        }

        public void setGroups(List<Double> groups) {
            this.groups = groups;
        }
    }
}
