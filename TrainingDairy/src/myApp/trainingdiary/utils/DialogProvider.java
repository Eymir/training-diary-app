package myApp.trainingdiary.utils;

import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.itemadapter.EntityArrayAdapter;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.statistic.StatisticActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    public static AlertDialog createStatisticSettingDialog(final StatisticActivity activity, Long ex_id, final StatisticSettingsDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_statistic_setting_title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.stat_settings_dialog, null);

        final Spinner exerciseSpinner = (Spinner) view.findViewById(R.id.ex_stat_spinner);
        final Spinner measureSpinner = (Spinner) view.findViewById(R.id.measure_stat_spinner);
        final Spinner groupSpinner = (Spinner) view.findViewById(R.id.group_by_stat_spinner);
        final Spinner groupCountSpinner = (Spinner) view.findViewById(R.id.group_count_stat_spinner);

        final TableRow tableRow3 = (TableRow) view.findViewById(R.id.tableRow3);
        final TableRow tableRow4 = (TableRow) view.findViewById(R.id.tableRow4);

        final DBHelper dbHelper = DBHelper.getInstance(activity);

        SimpleCursorAdapter exerciseAdapter = getExerciseAdapter(dbHelper, activity);
        final EntityArrayAdapter measureAdapter = getMeasureAdapter(dbHelper, activity, ex_id);
        final EntityArrayAdapter groupAdapter = getMeasureAdapter(dbHelper, activity, ex_id);
        final ArrayAdapter countAdapter = null;//getCountAdapter(dbHelper, activity, ex_id);

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

        exerciseSpinner.setAdapter(exerciseAdapter);
//        exerciseSpinner.

        builder.setPositiveButton(R.string.build_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (exerciseSpinner.getSelectedItemId() != AdapterView.INVALID_ROW_ID) {
                    StatisticSettingsEvent event = new StatisticSettingsEvent(exerciseSpinner.getSelectedItemId(),
                            ((Measure) measureSpinner.getSelectedItem()).getId(),
                            ((Measure) groupSpinner.getSelectedItem()).getId(), (Long) groupCountSpinner.getSelectedItem());
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

    private static ArrayAdapter getCountAdapter(DBHelper dbHelper, StatisticActivity activity, Long ex_id, Measure measure) {
//        dbHelper.READ.get
        ArrayList<Long> list = null;
        ArrayAdapter adapter = new ArrayAdapter(activity, R.layout.just_big_string, list);
        return adapter;
    }

    private static EntityArrayAdapter getMeasureAdapter(DBHelper dbHelper, Context context, Long ex_id) {
        ArrayList<Measure> list = (ArrayList<Measure>) dbHelper.READ.getMeasuresInExercise(ex_id);
        EntityArrayAdapter adapter = new EntityArrayAdapter(context, list);
        return adapter;
    }

    private static SimpleCursorAdapter getExerciseAdapter(DBHelper dbHelper, final Context context) {

        Cursor ex_cursor = dbHelper.READ.getExercisesWithStat();
        String[] from = {"ex_name", "icon"};
        int[] to = {R.id.label, R.id.icon};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context, R.layout.exercise_plain_row,
                ex_cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    String name = cursor.getString(columnIndex);
                    ((ImageView) view).setImageResource(context.getResources()
                            .getIdentifier(name,
                                    "drawable", context.getPackageName()));

                    return true;
                }
                return false;
            }
        });
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
        private Long groupCount;

        public StatisticSettingsEvent() {
        }

        public StatisticSettingsEvent(Long exId, Long drawMeasureId, Long groupMeasureId, Long groupCount) {
            this.exId = exId;
            this.drawMeasureId = drawMeasureId;
            this.groupMeasureId = groupMeasureId;
            this.groupCount = groupCount;
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

        public Long getGroupCount() {
            return groupCount;
        }

        public void setGroupCount(Long groupCount) {
            this.groupCount = groupCount;
        }
    }
}
