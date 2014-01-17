package myApp.trainingdiary.result;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.utils.Const;

/**
 * Created by Lenovo on 14.01.14.
 */
public class WheelFragment extends Fragment {
    private static final String KEY_EXERCISE = "WheelFragment:exercise";
    private static final String KEY_CURVALUES = "WheelFragment:training_set";

    private Exercise exercise;
    private TrainingSet trainingSet;
    private List<MeasureWheels> measureWheelsList = new ArrayList<MeasureWheels>();

    public static WheelFragment newInstance(Exercise content, TrainingSet set) {
        WheelFragment fragment = new WheelFragment();
        fragment.setExercise(content);
        fragment.setTrainingSet(set);
        return fragment;
    }

    public TrainingSet getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(TrainingSet trainingSet) {
        this.trainingSet = trainingSet;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null)) {
            if (savedInstanceState.containsKey(KEY_EXERCISE))
                exercise = (Exercise) savedInstanceState.getSerializable(KEY_EXERCISE);
            if (savedInstanceState.containsKey(KEY_CURVALUES))
                trainingSet = (TrainingSet) savedInstanceState.getSerializable(KEY_CURVALUES);
        }
    }

    private void setLastTrainingStatOnWheels(TrainingSet tr_stat) {
        List<TrainingSetValue> values = tr_stat.getValues();
        Log.d(Const.LOG_TAG, "setLastTrainingStatOnWheels:" + values);
        for (int i = 0; i < measureWheelsList.size(); i++) {
            MeasureWheels measureWheels = measureWheelsList.get(i);
            TrainingSetValue measureValue = values.get(i);
            measureWheels.setValue(measureValue.getValue());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_wheel, container, false);
        List<Measure> measureList = DBHelper.getInstance(null).READ.getMeasuresInExercise(exercise.getId());
        measureWheelsList.clear();
        for (Measure measure : measureList) {
            MeasureWheels measureWheels = new MeasureWheels(inflater.getContext(), measure);
            linearLayout.addView(measureWheels.getView());
            measureWheelsList.add(measureWheels);
        }
        if (trainingSet != null)
            setLastTrainingStatOnWheels(trainingSet);
        return linearLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EXERCISE, exercise);
        outState.putSerializable(KEY_CURVALUES, trainingSet);
    }

    public List<MeasureWheels> getMeasureWheelsList() {
        return measureWheelsList;
    }

}
