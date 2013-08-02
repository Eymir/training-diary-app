package myApp.trainingdiary.utils;

import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.CustomCursorAdapter;
import myApp.trainingdiary.customview.itemadapter.EntityArrayAdapter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
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
        final Spinner groupValueSpinner = (Spinner) view.findViewById(R.id.group_count_stat_spinner);

        final TableRow tableRow3 = (TableRow) view.findViewById(R.id.tableRow3);
        final TableRow tableRow4 = (TableRow) view.findViewById(R.id.tableRow4);

        final DBHelper dbHelper = DBHelper.getInstance(activity);

        final CheckBox groupByCheckBox = (CheckBox) view.findViewById(R.id.group_by_checkbox);

        groupByCheckBox.setChecked(false);
        groupByCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    tableRow3.setVisibility(View.VISIBLE);
                    tableRow4.setVisibility(View.VISIBLE);
                } else {
                    tableRow3.setVisibility(View.GONE);
                    tableRow4.setVisibility(View.GONE);
                }
            }
        });

        final SimpleCursorAdapter exerciseAdapter = getExerciseAdapter(activity, dbHelper, activity);
        exerciseSpinner.setAdapter(exerciseAdapter);
        final EntityArrayAdapter measureAdapter = getMeasureAdapter(dbHelper, activity, ex_id);
        measureSpinner.setAdapter(measureAdapter);
        final EntityArrayAdapter groupAdapter = getMeasureAdapterWithoutData(activity);
        groupSpinner.setAdapter(groupAdapter);
        final ArrayAdapter groupValueAdapter = getGroupValueAdapter(activity);
        groupValueSpinner.setAdapter(groupValueAdapter);

        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                measureAdapter.clear();
                measureAdapter.addAll(dbHelper.READ.getMeasuresInExercise(exerciseSpinner.getSelectedItemId()));
                if (measureAdapter.getCount() > 1) {
                    groupAdapter.clear();
                    groupAdapter.addAll(dbHelper.READ.getMeasuresInExercise(exerciseSpinner.getSelectedItemId()));
                    groupAdapter.remove(measureSpinner.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        measureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (measureAdapter.getCount() > 1) {
                    groupAdapter.clear();
                    groupAdapter.addAll(dbHelper.READ.getMeasuresInExercise(exerciseSpinner.getSelectedItemId()));
                    groupAdapter.remove(measureSpinner.getSelectedItem());
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
                    List<Double> list = getGroups(dbHelper, groupSpinner.getSelectedItemId(), exerciseSpinner.getSelectedItemId());
                    if (list != null) {
                        groupValueAdapter.clear();
                        groupValueAdapter.addAll(list);
                    } else {
                        Log.e(Consts.LOG_TAG, "groups is null");
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
                    StatisticSettingsEvent event = new StatisticSettingsEvent(exerciseSpinner.getSelectedItemId(),
                            ((Measure) measureSpinner.getSelectedItem()).getId(),
                            ((Measure) groupSpinner.getSelectedItem()).getId(), new ArrayList<Double>());
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
        ArrayAdapter adapter = new ArrayAdapter(activity, R.layout.just_big_string, R.id.text_label, list);
        return adapter;
    }

    private static EntityArrayAdapter getMeasureAdapter(DBHelper dbHelper, Context context, Long ex_id) {
        ArrayList<Measure> list = (ArrayList<Measure>) dbHelper.READ.getMeasuresInExercise(ex_id);
        EntityArrayAdapter adapter = new EntityArrayAdapter(context, list);
        return adapter;
    }

    private static EntityArrayAdapter getMeasureAdapterWithoutData(Context context) {
        ArrayList<Measure> list = new ArrayList<Measure>();
        EntityArrayAdapter adapter = new EntityArrayAdapter(context, list);
        return adapter;
    }


    private static SimpleCursorAdapter getExerciseAdapter(Activity activity, DBHelper dbHelper, final Context context) {

        Cursor ex_cursor = dbHelper.READ.getExercisesWithStat();
        String[] from = {"ex_name", "icon"};
        int[] to = {R.id.label, R.id.icon};
        CustomCursorAdapter adapter = new CustomCursorAdapter(activity,
                context, R.layout.exercise_plain_row_spinner,
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
