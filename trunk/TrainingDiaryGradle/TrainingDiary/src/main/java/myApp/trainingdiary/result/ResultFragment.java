package myApp.trainingdiary.result;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;

public final class ResultFragment extends Fragment {
    private Exercise exercise;
    private static final String KEY_EXERCISE = "ResultFragment:exercise";

    public static ResultFragment newInstance(Exercise content) {
        ResultFragment fragment = new ResultFragment();
        fragment.setExercise(content);
        return fragment;
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
        }
        Log.d(Const.LOG_TAG, "onCreate FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View face = inflater.inflate(R.layout.fragment_result, container, false);
        TextView training_stat_text = (TextView) face.findViewById(R.id.cur_training_stats);
        training_stat_text.setTag(Const.STAT_VIEW + exercise.getId());
        Log.d(Const.LOG_TAG, "onCreateView");
        MeasureFormatter.writeResultStatView(training_stat_text, exercise.getId());
        return face;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EXERCISE, exercise);
    }
}