package myApp.trainingdiary.result;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import myApp.trainingdiary.R;
import myApp.trainingdiary.customview.NumericRightOrderWheelAdapter;
import myApp.trainingdiary.customview.StringRightOrderWheelAdapter;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.utils.Const;

public class MeasureWheels {
    private Measure measure;
    private List<MeasureWheel> measureWheelList;
    private LinearLayout mainLayout;
    private Context activity;
    private LayoutInflater inflater;
    private WheelTickListener listener;

    public MeasureWheels(Context context, Measure measure, WheelTickListener listener) {
        this.activity = context;
        this.measure = measure;
        this.listener = listener;
        measureWheelList = new ArrayList<MeasureWheel>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainLayout = (LinearLayout) inflater.inflate(R.layout.wheel_column, null);
        LinearLayout wheelLayout = (LinearLayout) mainLayout.findViewById(R.id.wheel_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1F);
        wheelLayout.setLayoutParams(params);
        TextView textView = (TextView) mainLayout.findViewById(R.id.label);
        textView.setText(measure.getName() + ":");
        switch (measure.getType()) {
            case Numeric:
                MeasureWheel measureWheel = createNumWheel(measure.getMax(), measure.getStep(), false);
                wheelLayout.addView(measureWheel.wheelView);
                measureWheelList.add(measureWheel);
                if (measure.getStep() < 1) {
                    MeasureWheel measureTailWheel = createTailWheel(measure.getStep());
                    measureWheelList.add(measureTailWheel);
                    wheelLayout.addView(measureTailWheel.wheelView);
                }
                break;
            case Temporal:
                for (int i = measure.getMax(); i >= measure.getStep().intValue(); i--) {
                    MeasureWheel measureTimeWheel = null;
                    switch (i) {
                        case 0:
                            measureTimeWheel = createNumWheel(999, 1d, true);
                            break;
                        case 1:
                            measureTimeWheel = createNumWheel(59, 1d, true);
                            break;
                        case 2:
                            measureTimeWheel = createNumWheel(59, 1d, true);
                            break;
                        case 3:
                            measureTimeWheel = createNumWheel(23, 1d, true);
                            break;

                    }
                    measureWheelList.add(measureTimeWheel);
                    wheelLayout.addView(measureTimeWheel.wheelView);
                }
                break;
        }
        float weight = 1F / measureWheelList.size();
        LinearLayout.LayoutParams main_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        main_params.setMargins(10, 0, 10, 0);
        mainLayout.setLayoutParams(main_params);
    }

    private MeasureWheel createTailWheel(Double step) {
        MeasureWheel measureWheel = new MeasureWheel();
        List<String> tails = new ArrayList<String>();
        BigDecimal _step = BigDecimal.valueOf(0d);
        Log.d(Const.LOG_TAG, "step: " + step);
        while (_step.doubleValue() < 1) {
            tails.add(String.valueOf(_step).substring(1));
            Log.d(Const.LOG_TAG, "_step: " + _step);

            _step = _step.add(BigDecimal.valueOf(step));

        }
        ArrayWheelAdapter<String> wheelAdapter = new StringRightOrderWheelAdapter<String>(activity,
                tails.toArray(new String[tails.size()]));

        WheelView wheelView = (WheelView) inflater.inflate(R.layout.wheel, null);
        wheelView.setViewAdapter(wheelAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 2F);
        wheelView.setLayoutParams(params);
        measureWheel.wheelAdapter = wheelAdapter;
        measureWheel.wheelView = wheelView;
        wheelView.setCurrentItem(tails.size() - 1);
        wheelView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (listener != null)
                    listener.tick(null);
            }
        });
        return measureWheel;
    }

    private MeasureWheel createNumWheel(Integer max, Double step, boolean withZeros) {
        MeasureWheel measureWheel = new MeasureWheel();
        int length = String.valueOf(max).length();
        String zero = (withZeros) ? "0" : "";
        NumericRightOrderWheelAdapter wheelAdapter = new NumericRightOrderWheelAdapter(activity, 0, max,
                "%" + zero + length + "d");

        WheelView wheelView = (WheelView) inflater.inflate(R.layout.wheel, null);
        wheelView.setViewAdapter(wheelAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1F);
        wheelView.setLayoutParams(params);
        wheelView.setCyclic(true);
        wheelView.setCurrentItem(max);
        measureWheel.wheelAdapter = wheelAdapter;
        measureWheel.wheelView = wheelView;
        wheelView.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (listener != null)
                    listener.tick(null);
            }
        });
        return measureWheel;
    }

    public Measure getMeasure() {
        return measure;
    }

    private void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public View getView() {
        return mainLayout;
    }

    private List<MeasureWheel> getMeasureWheelList() {
        return measureWheelList;
    }


    public String getStringValue() {
        String result = "";

        switch (measure.getType()) {
            case Numeric:
                for (MeasureWheel measureWheel : measureWheelList) {
                    if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                        result += String.valueOf(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getItem(measureWheel.wheelView
                                .getCurrentItem()));
                    } else if (measureWheel.wheelAdapter instanceof ArrayWheelAdapter) {
                        result += String.valueOf(((ArrayWheelAdapter) measureWheel.wheelAdapter).getItemText(measureWheel.wheelView
                                .getCurrentItem()));
                    }
                }
                break;
            case Temporal:
                for (MeasureWheel measureWheel : measureWheelList) {
                    if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                        result += ":" + String.valueOf(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getItem(measureWheel.wheelView
                                .getCurrentItem()));
                    }
                }
                result = result.substring(1);
                break;
        }
        return result;
    }


    public void setValue(Double measureValue) {
        switch (measure.getType()) {
            case Numeric:
                for (MeasureWheel measureWheel : measureWheelList) {
                    if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                        int intValue = measureValue.intValue();
                        Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                        measureWheel.wheelView.setCurrentItem(((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                    } else if (measureWheel.wheelAdapter instanceof StringRightOrderWheelAdapter) {
                        String intValue = measureValue.toString().substring(measureValue.toString().indexOf("."));
                        Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                        measureWheel.wheelView.setCurrentItem(((StringRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                    }
                }
                break;
            case Temporal:
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(measureValue.longValue()));
                int i = 0;
                for (MeasureWheel measureWheel : measureWheelList) {
                    if (measureWheel.wheelAdapter instanceof NumericRightOrderWheelAdapter) {
                        int intValue = (i == 0) ? c.get(Calendar.MINUTE) : c.get(Calendar.SECOND);
                        Log.d(Const.LOG_TAG, measureValue + ": " + intValue);
                        measureWheel.wheelView.setCurrentItem(
                                ((NumericRightOrderWheelAdapter) measureWheel.wheelAdapter).getIndexByValue(intValue));
                        i++;
                    }
                }
                break;
        }
    }
}

