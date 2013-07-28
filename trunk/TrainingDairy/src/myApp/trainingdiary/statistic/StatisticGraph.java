package myApp.trainingdiary.statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * Created by Lenovo on 28.07.13.
 */
public class StatisticGraph {


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

    public StatisticGraph() {
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

    }

    public void repaint(){
        mChartView.repaint();
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

    public void saveState(Bundle outState)

{
    outState.putSerializable("dataset", mDataset);
    outState.putSerializable("renderer", mRenderer);
    outState.putSerializable("current_series", mCurrentSeries);
    outState.putSerializable("current_renderer", mCurrentRenderer);

}
    public void restoreState(Bundle savedState){
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


}
