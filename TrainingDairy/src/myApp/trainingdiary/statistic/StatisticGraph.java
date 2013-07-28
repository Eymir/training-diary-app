package myApp.trainingdiary.statistic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by Lenovo on 28.07.13.
 */
public class StatisticGraph {


    private final Context context;
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


    public StatisticGraph(Context context) {
        //set some properties on the main renderer
        this.context = context;
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
        createGraphView();
    }

    public void repaint() {
        mChartView.repaint();
    }

    private View createGraphView() {

        if (mChartView == null) {
            mChartView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
            // enable the chart click events
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // handle the click event on the chart
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Toast.makeText(context, "No chart element", Toast.LENGTH_SHORT).show();
                    } else {
                        // display information of the clicked point
                        Toast.makeText(
                                context,
                                "Chart element in series index " + seriesSelection.getSeriesIndex()
                                        + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                                        + " closest point value X=" + seriesSelection.getXValue() + ", Y="
                                        + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
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
        outState.putSerializable("current_renderer", mCurrentRenderer);

    }

    public void restoreState(Bundle savedState) {
        mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
        mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
        mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");

    }

    public void addSeries(String name) {
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


    public View getView() {
        return mChartView;
    }

    public XYSeries getSeries(String name) {
        for (XYSeries series : mDataset.getSeries()) {
            if (series.getTitle().equalsIgnoreCase(name)) {
                return series;
            }
        }
        return null;
    }
}
