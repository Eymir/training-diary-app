package myApp.trainingdiary.statistic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import myApp.trainingdiary.utils.Consts;

/**
 * Created by Lenovo on 28.07.13.
 */
public class StatisticGraph {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM HH:mm");
    public static final String FORMAT = "dd";
    private final Context context;
    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer mRenderer;
    /**
     * The most recently added series.
     */
    private XYSeries mCurrentSeries;

    /**
     * The chart view that displays the data.
     */
    private GraphicalView mChartView;


    public StatisticGraph(Context context) {
        //set some properties on the main renderer
        this.context = context;
        createRender();
//        mRenderer.setShowCustomTextGrid(true);
        createGraphView();
    }

    private void createRender() {
        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        mRenderer.setGridColor(Color.GRAY);
//        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setAxesColor(Color.LTGRAY);
        mRenderer.setShowGrid(true);
        mRenderer.setClickEnabled(false);
        mRenderer.setAxisTitleTextSize(32);
        mRenderer.setAntialiasing(true);
        mRenderer.setChartTitleTextSize(38);
        mRenderer.setLabelsTextSize(18);
        mRenderer.setLegendTextSize(22);
        mRenderer.setMargins(new int[]{20, 40, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPointSize(4);
        mRenderer.addYTextLabel(0, "0");
        mRenderer.setZoomEnabled(true, false);

//        mRenderer.setPanEnabled(false, false);
    }

    public void addMonthAndYear(Date a, Date b) {

        Calendar c = Calendar.getInstance();
        c.setTime(a);
        int yearA = c.get(Calendar.YEAR);
        c.setTime(b);
        int yearB = c.get(Calendar.YEAR);
        c.setTime(new Date(0L));
        for (int i = yearA - 1; i <= yearB + 1; i++) {
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, 0);
            mRenderer.addXTextLabel(c.getTime().getTime(), String.valueOf(i));
            for (int m = 0; m < 12; m++) {
                c.set(Calendar.MONTH, m);
                mRenderer.addXTextLabel(c.getTime().getTime(),
                        c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
        }
    }

    public void repaint() {
        mChartView.repaint();
    }

    private View createGraphView() {

        if (mChartView == null) {
            mChartView = ChartFactory.getTimeChartView(context, mDataset, mRenderer, null);

            // enable the chart click events
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // handle the click event on the chart
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
//                        Toast.makeText(context, "No chart element", Toast.LENGTH_SHORT).show();
                    } else {
                        // display information of the clicked point
                        Toast.makeText(
                                context, "x[" + SDF.format(new Date(Double.valueOf(seriesSelection.getXValue()).longValue())) + "], y[" +
                                String.valueOf(seriesSelection.getValue()) + "]", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            mChartView.repaint();
        }

        return mChartView;
    }

    public void saveState(Bundle outState)

    {
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);

    }

    public void restoreState(Bundle savedState) {
        mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
        mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");

    }

    public void addSeries(String name) {
        Log.d(Consts.LOG_TAG, "addSeries: " + name);
        // create a new series of data
        TimeSeries series = new TimeSeries(name);
        mCurrentSeries = series;
        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        // set some renderer properties
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayChartValuesDistance(30);
        renderer.setColor(getColor(mDataset.getSeriesCount()));
        renderer.setLineWidth(4);
        renderer.setChartValuesTextSize(16);
        mDataset.addSeries(series);
        mChartView.repaint();
    }

    private int getColor(int seriesCount) {
        Log.d(Consts.LOG_TAG, "seriesCount: " + seriesCount);
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


    public View getView() {
        return mChartView;
    }

    public TimeSeries getSeries(String name) {
        for (XYSeries series : mDataset.getSeries()) {
            if (series.getTitle().equalsIgnoreCase(name)) {
                return (TimeSeries) series;
            }
        }
        return null;
    }

    public void clear() {
        if (mDataset.getSeriesCount() > 0) {
            mDataset.clear();
            mRenderer.removeAllRenderers();
        }
    }

    public void matchSeries() {
        double x1 = new Date().getTime();
        double x2 = 0;
        double y1 = new Date().getTime();
        double y2 = 0;
        for (int i = 0; i < mDataset.getSeries().length; i++) {
            XYSeries xySeries = mDataset.getSeries()[i];
            if (x1 > xySeries.getMinX()) x1 = xySeries.getMinX();
            if (x2 < xySeries.getMaxX()) x2 = xySeries.getMaxX();

            if (y1 > xySeries.getMinY()) y1 = xySeries.getMinY();
            if (y2 < xySeries.getMaxY()) y2 = xySeries.getMaxY();
        }
        Log.i(Consts.LOG_TAG, "x1: " + x1 + "x2: " + x2 + "y1: " + y1 + "y2: " + y2);
        double diff = x2 - x1;

        mRenderer.setPanLimits(new double[]{x1 - diff,
                x2 + diff,
                (y1 - y1 / 10 < 0) ? 0 : y1 - y1 / 10,
                y2 + y2 / 10});
        mRenderer.setXAxisMin(x1 - diff / 10);
        mRenderer.setXAxisMax(x2 + diff / 10);
        mRenderer.setYAxisMin(y1);
        mRenderer.setYAxisMax(y2);

    }
}
