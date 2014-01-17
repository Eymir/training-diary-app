package myApp.trainingdiary.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.CustomCursorAdapter;
import myApp.trainingdiary.customview.EntityArrayAdapter;
import myApp.trainingdiary.customview.stat.StatisticEnum;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.Validator;

public class DialogProvider {
    private Context context;

    public DialogProvider(Context context) {
        this.context = context;
    }

    public static EditDialog createInputTextDialog(final Activity activity, String title, String positiveTitle, String negativeTitle, final Validator validator, final InputTextDialogClickListener listener) {
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

        return new EditDialog(builder.create());
    }

    public static AlertDialog createSimpleDialog(final Activity activity, String title, String message, String positiveTitle, String negativeTitle, final SimpleDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (message != null) builder.setMessage(message);
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
                Log.d(Const.LOG_TAG, "getCheckedItemPositions.size: " + groupListView.getCheckedItemPositions().size());
                List<Double> list = getChosenObjects(groupListView);
                groupsButton.setText(list.toString());

                dialogInterface.dismiss();
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
                    fillGroupByMeasureSection(groupAdapter, measureSpinner, dbHelper, exerciseSpinner, groupSpinner, groupValueAdapter, groupsButton, groupListView);
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
                addAll(measureAdapter, dbHelper.READ.getMeasuresInExercise(exerciseSpinner.getSelectedItemId()));
                if (measureAdapter.getCount() > 1 && groupByCheckBox.isChecked()) {
                    fillGroupByMeasureSection(groupAdapter, measureSpinner, dbHelper, exerciseSpinner, groupSpinner, groupValueAdapter, groupsButton, groupListView);
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
                    fillGroupByMeasureSection(groupAdapter, measureSpinner, dbHelper, exerciseSpinner, groupSpinner, groupValueAdapter, groupsButton, groupListView);
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
                        Log.e(Const.LOG_TAG, "groupSpinner has invalid row");
                        return;
                    }
                    if (exerciseSpinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
                        Log.e(Const.LOG_TAG, "exerciseSpinner has invalid row");
                        return;
                    }
                    Measure m = (Measure) groupSpinner.getSelectedItem();
                    if (m != null) {
                        List<Double> list = getGroups(dbHelper, m.getId(),
                                exerciseSpinner.getSelectedItemId());
                        if (list != null) {
                            groupValueAdapter.clear();
                            addAll(groupValueAdapter, list);
                        } else {
                            Log.e(Const.LOG_TAG, "groups is null");
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
                    StatisticSettingsEvent event = null;
                    if (groupByCheckBox.isChecked()) {
                        List<Double> list = getChosenObjects(groupListView);
                        event = new StatisticSettingsEvent(exerciseSpinner.getSelectedItemId(),
                                measureId,
                                groupId,
                                list);
                    } else {
                        event = new StatisticSettingsEvent(exerciseSpinner.getSelectedItemId(),
                                measureId,
                                null,
                                null);
                    }
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

    public static void addAll(ArrayAdapter adapter, Collection collection) {
        for (Object o : collection) {
            adapter.add(o);
        }
    }

    public static void fillGroupByMeasureSection(EntityArrayAdapter groupAdapter, Spinner measureSpinner, DBHelper dbHelper, Spinner exerciseSpinner, Spinner groupSpinner, ArrayAdapter groupValueAdapter, Button groupsButton, ListView groupListView) {
        groupAdapter.clear();
        Measure m = (Measure) measureSpinner.getSelectedItem();
        if (m == null) return;
        addAll(groupAdapter, dbHelper.READ.getMeasuresInExerciseExceptParticularMeasure(
                exerciseSpinner.getSelectedItemId(), m.getId()));
        Measure m_g = (Measure) groupSpinner.getSelectedItem();
        groupsButton.setText("");
        Log.d(Const.LOG_TAG, "groupListView.dispatchSetSelected(false)");
        for (int i = 0; i < groupListView.getCount(); i++) {
            groupListView.setItemChecked(i, false);
        }

        groupListView.dispatchSetSelected(false);
        if (m_g != null) {
            List<Double> list = getGroups(dbHelper, m_g.getId(),
                    exerciseSpinner.getSelectedItemId());
            if (list != null) {
                groupValueAdapter.clear();
                addAll(groupValueAdapter, list);
            } else {
                Log.e(Const.LOG_TAG, "groups is null");
            }
        }
    }

    private static List<Double> getChosenObjects(ListView listView) {
        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                list.add((Double) listView.getItemAtPosition(i));
            }
        }

        return list;
    }

    private static List<Double> getGroups(DBHelper dbHelper, Long m_id, Long ex_id) {
        Log.d(Const.LOG_TAG, "getGroups m_id: " + m_id + " ex_id: " + ex_id);
        Long pos = dbHelper.READ.getMeasurePosInExercise(ex_id, m_id);
        if (pos == null) {
            Log.e(Const.LOG_TAG, "pos is null");
            return null;
        }
        Log.d(Const.LOG_TAG, "pos: " + pos);
        List<TrainingStamp> stats = dbHelper.READ.getTrainingStampWithExactExerciseDesc(ex_id);
        List<Double> list = new ArrayList<Double>();
        for (TrainingStamp stamp : stats) {
            for (TrainingSet set : stamp.getTrainingSetList()) {
                TrainingSetValue setValue = set.getValueByPos(pos);
                if (setValue != null) {
                    Double m_value = setValue.getValue();
                    if (!list.contains(m_value)) list.add(m_value);
                }
            }
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

    public static interface CreateExerciseDialogClickListener {
        void onPositiveClick(Exercise event);

        void onNegativeClick();
    }

    public static interface OkClickListener {
        void onPositiveClick();
    }

    public static ViewExerciseDialog createViewExerciseDialog(final Activity activity, final OkClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.view_exercise_dialog_title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_exercise_dialog, null);

        Cursor type_cursor = DBHelper.getInstance(activity).READ.getExerciseTypes();
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
        SimpleCursorAdapter typeAdapter = new SimpleCursorAdapter(
                activity, R.layout.exercise_type_row,
                type_cursor, from, to,
                android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        typeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    String name = cursor.getString(columnIndex);
                    ((ImageView) view).setImageResource(activity.getResources()
                            .getIdentifier(name,
                                    "drawable", activity.getPackageName()));

                    return true;
                }
                return false;
            }
        });

        final EditText name_edit = (EditText) view
                .findViewById(R.id.name_edit);

        final Spinner type_spinner = (Spinner) view
                .findViewById(R.id.type_spinner);

        type_spinner.setAdapter(typeAdapter);

        builder.setPositiveButton(R.string.btn_txt_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositiveClick();
            }
        });

        builder.setView(view);
        return new ViewExerciseDialog(builder.create());
    }

    public static AlertDialog createCreateExerciseDialog(final Activity activity, final CreateExerciseDialogClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.create_ex_dialog_title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_exercise_dialog, null);

        Cursor type_cursor = DBHelper.getInstance(activity).READ.getExerciseTypes();
        String[] from = {"name", "icon_res"};
        int[] to = {R.id.label, R.id.icon};
        SimpleCursorAdapter typeAdapter = new SimpleCursorAdapter(
                activity, R.layout.exercise_type_row,
                type_cursor, from, to,
                android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        typeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (view.getId() == R.id.icon) {
                    String name = cursor.getString(columnIndex);
                    ((ImageView) view).setImageResource(activity.getResources()
                            .getIdentifier(name,
                                    "drawable", activity.getPackageName()));

                    return true;
                }
                return false;
            }
        });

        final EditText name_edit = (EditText) view
                .findViewById(R.id.name_edit);

        final Spinner type_spinner = (Spinner) view
                .findViewById(R.id.type_spinner);

        type_spinner.setAdapter(typeAdapter);

        builder.setPositiveButton(R.string.create_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (validateCreateForm(activity, name_edit, type_spinner)) {
                    Log.d(Const.LOG_TAG, " name_edit: "
                            + name_edit.getText().toString() + " type_spinner.id:"
                            + type_spinner.getSelectedItemId());
                    Exercise exercise = new Exercise(null,
                            new ExerciseType(type_spinner.getSelectedItemId(), null, null),
                            name_edit.getText().toString());
                    listener.onPositiveClick(exercise);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegativeClick();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createStatListDialog(final Activity activity, final OkClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.stat_list_dialog_title);
        final SharedPreferences sp = activity.getSharedPreferences(Const.CHOSEN_STATISTIC, activity.MODE_PRIVATE);
        final StatisticEnum[] stats = StatisticEnum.values();
        boolean[] checked = new boolean[stats.length];
        for (int i = 0; i < StatisticEnum.values().length; i++) {
            if (sp.getBoolean(stats[i].name(), false)) {
                checked[i] = sp.getBoolean(stats[i].name(), false);
            }
        }
        final String[] strings = StatisticEnum.getDescNames();
        builder.setMultiChoiceItems(strings, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(stats[which].name(), isChecked);
                editor.commit();
            }
        });

        builder.setPositiveButton(R.string.btn_txt_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                listener.onPositiveClick();
            }
        });

        return builder.create();
    }


    public static boolean validateCreateForm(Activity activity, EditText name_edit, Spinner type_spinner) {
        if (name_edit.getText() == null
                || name_edit.getText().toString() == null
                || name_edit.getText().toString().length() == 0) {
            Toast.makeText(activity, R.string.input_exercise_name_notification,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (type_spinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
            Toast.makeText(activity, R.string.input_exercise_type_notification,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (DBHelper.getInstance(null).READ.isExerciseInDB(name_edit.getText().toString())) {
            Toast.makeText(activity, R.string.exercise_exist_notif,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static AlertDialog createAboutDialog(Context context, String version) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.about_title));
        final TextView message = new TextView(context);
        final SpannableString s =
                new SpannableString("\t\t" + context.getString(R.string.about_summary, version) + "\n\n" + context.getString(R.string.about_detail));
        Linkify.addLinks(s, Linkify.WEB_URLS);
//        builder.setIcon(R.drawable.ic_dairy);
        message.setPadding(10, 10, 10, 10);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setTextColor(Color.BLACK);
        builder.setView(message);
        builder.setPositiveButton(R.string.btn_txt_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        return builder.create();
    }

}
