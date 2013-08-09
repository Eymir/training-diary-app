package myApp.trainingdiary.statistic;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.utils.MeasureFormatter;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.DialogProvider;
import myApp.trainingdiary.utils.Consts;

public class StatisticActivity extends Activity {

    private ImageView settingButton;
    private TextView label;
    private LinearLayout graphLayout;
    private DBHelper dbHelper;
    private Long ex_id;
    private ImageView icon;
    private AlertDialog settingDialog;

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yy");
    private StatisticGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        settingButton = (ImageView) findViewById(R.id.setting_button);
        label = (TextView) findViewById(R.id.label);
        graphLayout = (LinearLayout) findViewById(R.id.graph);
        icon = (ImageView) findViewById(R.id.icon);
        dbHelper = DBHelper.getInstance(this);
        createGraph();
        try {
            ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
        } catch (NullPointerException e) {
        }

        if (ex_id != null) {
            drawExerciseProgress(ex_id, null, null, null);
        }
        createSettingDialog();

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.show();
            }
        });

        graphLayout.addView(graph.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void createSettingDialog() {
        settingDialog = DialogProvider.createStatisticSettingDialog(this, ex_id, new DialogProvider.StatisticSettingsDialogClickListener() {
            @Override
            public void onPositiveClick(DialogProvider.StatisticSettingsEvent event) {
                ex_id = event.getExId();
                drawExerciseProgress(ex_id, event.getDrawMeasureId(), event.getGroupMeasureId(), event.getGroups());
            }

            @Override
            public void onNegativeClick() {
                settingDialog.cancel();
            }
        });
    }

    private StatisticGraph createGraph() {
        if (graph == null) {
            graph = new StatisticGraph(this);
        }
        return graph;
    }

    private void drawExerciseProgress(Long ex_id, Long measure_id, Long group_measure_id, List<Double> groups) {
        Log.d(Consts.LOG_TAG, "ex_id: " + ex_id + " measure_id: " + measure_id + " group_measure_id: " + group_measure_id + " groups: " + groups);
        graph.clear();
        Exercise exercise = dbHelper.READ.getExerciseById(ex_id);
        exercise.getType().getMeasures().addAll(dbHelper.READ.getMeasuresInExercise(ex_id));
        List<TrainingStat> progress = dbHelper.READ.getExerciseProgress(ex_id);
        if (!progress.isEmpty()) {
            label.setText(exercise.getName() + " (" + SDF_DATE.format(progress.get(0).getDate()) + ((progress.size() > 1) ?
                    (" - " + SDF_DATE.format(progress.get(progress.size() - 1).getDate())) : "") + ")");
            icon.setImageResource(getResources()
                    .getIdentifier(exercise.getType().getIcon(),
                            "drawable", getPackageName()));
            Measure measure = null;
            int pos = 0;
            if (measure_id != null) {
                measure = getMeasureById(measure_id, exercise.getType());
                pos = getPosByMeasureId(measure_id, exercise.getType());
            } else {
                measure = exercise.getType().getMeasures().get(0);
            }
            if (group_measure_id == null) {
                graph.addSeries(measure.getName());
                for (TrainingStat stat : progress) {
                    graph.getSeries(measure.getName()).add(stat.getDate(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                }
            } else {
                int m_g_pos = getPosByMeasureId(group_measure_id, exercise.getType());
                Measure group_measure = getMeasureById(group_measure_id, exercise.getType());
                if (m_g_pos == pos) {
                    graph.addSeries(measure.getName());
                    for (TrainingStat stat : progress) {
                        graph.getSeries(measure.getName()).add(stat.getDate(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                    }
                } else {
                    if (groups == null || groups.size() == 0) {
//                        groups = 1L;
                    }
                    Map<String, List<Pair>> map = new HashMap<String, List<Pair>>();
                    for (TrainingStat stat : progress) {
                        String groupValue = MeasureFormatter.toMeasureValues(stat.getValue()).get(m_g_pos);
                        if (groups.contains(Double.valueOf(groupValue))) {
                            Double value = MeasureFormatter.getValueByPos(stat.getValue(), pos);
                            if (map.containsKey(groupValue)) {
                                map.get(groupValue).add(new Pair(stat.getDate(), value));
                            } else {
                                List list = new ArrayList<Pair>();
                                list.add(new Pair(stat.getDate(), value));
                                map.put(groupValue, list);
                            }
                        }
                    }
                    for (String key : map.keySet()) {
                        graph.addSeries(group_measure.getName() + "_" + key);
                        for (Pair p : map.get(key)) {
                            graph.getSeries(group_measure.getName() + "_" + key).add(((Date) p.first), (Double) p.second);
                        }
                    }
                }
            }
//            graph.addMonthAndYear(progress.get(0).getDate(), progress.get(progress.size() - 1).getDate());
        } else {
            label.setText(exercise.getName() + " - " + getString(R.string.exercise_nothing_to_show));
        }
        graph.repaint();
    }

    private int getPosByMeasureId(Long measure_id, ExerciseType type) {
        for (int i = 0; i < type.getMeasures().size(); i++) {
            if (measure_id == type.getMeasures().get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    private Measure getMeasureById(Long measure_id, ExerciseType type) {
        for (Measure m : type.getMeasures()) {
            if (measure_id == m.getId()) {
                return m;
            }
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_statistic, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        graph.saveState(outState);
        // save the current data, for instance when changing screen orientation
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        // restore the current data, for instance when changing the screen
        // orientation
        graph.restoreState(savedState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        graph.repaint();
    }


}
