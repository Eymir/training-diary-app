package myApp.trainingdiary.statistic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.MeasureFormatter;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.Consts;

public class StatisticActivity extends Activity {
    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    /**
     * The most recently added series.
     */
    private XYSeries mCurrentSeries;
    /**
     * The most recently created renderer, customizing the current series.
     */
    private XYSeriesRenderer mCurrentRenderer;
    /**
     * The chart view that displays the data.
     */
    private GraphicalView mChartView;

    private ImageButton settingButton;
    private TextView label;
    private LinearLayout graphLayout;
    private DBHelper dbHelper;
    private Long ex_id;
    private ImageView icon;

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        settingButton = (ImageButton) findViewById(R.id.setting_button);
        label = (TextView) findViewById(R.id.label);
        graphLayout = (LinearLayout) findViewById(R.id.graph);
        icon = (ImageView) findViewById(R.id.icon);
        //set some properties on the main renderer
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setMarginsColor(Color.TRANSPARENT);
        mRenderer.setGridColor(Color.BLACK);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setAxisTitleTextSize(32);
        mRenderer.setChartTitleTextSize(40);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPointSize(10);
        dbHelper = DBHelper.getInstance(this);
        createGraphView();
        try {
            ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
        } catch (NullPointerException e) {
        }

        if (ex_id != null) {
            drawExerciseProgress(ex_id, null, null, null);
        }

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void drawExerciseProgress(Long ex_id, Long measure_id, Long group_measure_id, Long group_count) {
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
                addSeries(measure.getName());
                for (TrainingStat stat : progress) {
                    mCurrentSeries.add(stat.getDate().getTime(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                }
            } else {
                int m_g_pos = getPosByMeasureId(group_measure_id, exercise.getType());
                Measure group_measure = getMeasureById(measure_id, exercise.getType());
                if (m_g_pos == pos) {
                    addSeries(measure.getName());
                    for (TrainingStat stat : progress) {
                        mCurrentSeries.add(stat.getDate().getTime(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                    }
                } else {
                    Map<String, List<Pair>> map = new HashMap<String, List<Pair>>();
                    for (TrainingStat stat : progress) {
                        String groupValue = MeasureFormatter.toMeasureValues(stat.getValue()).get(m_g_pos);
                        Double value = MeasureFormatter.getValueByPos(stat.getValue(), pos);
                        if (map.containsKey(groupValue)) {
                            map.get(groupValue).add(new Pair(stat.getDate(), value));
                        } else {
                            List list = new ArrayList<Pair>();
                            list.add(new Pair(stat.getDate(), value));
                            map.put(groupValue, list);
                        }
                    }
                    if (group_count == null || group_count < 1) {
                        group_count = 1L;
                    }
                    while (map.size() > group_count) {
                        String minKey = null;
                        int minKeySize = Integer.MAX_VALUE;
                        for (String key : map.keySet()) {
                            if (minKeySize > map.get(key).size()) {
                                minKeySize = map.get(key).size();
                                minKey = key;
                            }
                        }
                        map.remove(minKey);
                    }
                    for (String key : map.keySet()) {
                        addSeries(group_measure.getName() + "_" + key);
                        for (Pair p : map.get(key)) {
                            mCurrentSeries.add(((Date) p.first).getTime(), (Double) p.second);
                        }
                    }
                }
            }
        } else {
            label.setText(exercise.getName() + " - " + getString(R.string.exercise_nothing_to_show));
        }
        mChartView.repaint();
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

    private void addSeries(String name) {
        // create a new series of data
        XYSeries series = new XYSeries(name);
        mDataset.addSeries(series);
        mCurrentSeries = series;
        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        // set some renderer properties
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayChartValuesDistance(10);
        renderer.setColor(getColor(mDataset.getSeriesCount()));
        mCurrentRenderer = renderer;
        mChartView.repaint();
    }

    private int getColor(int seriesCount) {

        switch (seriesCount) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.RED;
            case 3:
                return Color.GRAY;
            case 4:
                return Color.BLACK;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.DKGRAY;
            case 7:
                return Color.YELLOW;
            default:
                return Color.CYAN;
        }
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
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        // restore the current data, for instance when changing the screen
        // orientation
        mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
        mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
        mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
    }

    @Override
    protected void onResume() {
        super.onResume();
        createGraphView();
    }

    private void createGraphView() {

        if (mChartView == null) {
            mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
            // enable the chart click events
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // handle the click event on the chart
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Toast.makeText(StatisticActivity.this, "No chart element", Toast.LENGTH_SHORT).show();
                    } else {
                        // display information of the clicked point
                        Toast.makeText(
                                StatisticActivity.this,
                                "Chart element in series index " + seriesSelection.getSeriesIndex()
                                        + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                                        + " closest point value X=" + seriesSelection.getXValue() + ", Y="
                                        + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            graphLayout.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

}
