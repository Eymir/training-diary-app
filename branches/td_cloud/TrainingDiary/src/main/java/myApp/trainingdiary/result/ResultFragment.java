package myApp.trainingdiary.result;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.MeasureFormatter;
import myApp.trainingdiary.utils.TrainingDurationManger;

public final class ResultFragment extends Fragment {
    private Exercise exercise;
    private static final String KEY_EXERCISE = "ResultFragment:exercise";

    public static ResultFragment newInstance(Exercise content) {
        ResultFragment fragment = new ResultFragment();
        fragment.setExercise(content);
        return fragment;
    }

    private String formTrainingStats(List<TrainingSet> sets) {
        String result = "";
        for (int i = 0; i < sets.size(); i++) {
            TrainingSet set = sets.get(i);
            result += (i + 1) + "._" + MeasureFormatter.valueFormat(set) + ";  ";
        }
        return result;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View face = inflater.inflate(R.layout.fragment_result, container, false);
        Log.d(Const.LOG_TAG, "onCreateView");
        refreshView(face);
        return face;
    }

    public void refreshView(Activity activity) {
        refreshView(activity.findViewById(R.id.scrollView));
    }

    private void refreshView(View view) {
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Const.THREE_HOURS);
        List<TrainingSet> tr_stats = DBHelper.getInstance(null).READ.getTrainingSetListInTrainingStampByExercise(exercise.getId(), tr_stamp_id);
        Log.d(Const.LOG_TAG, "tr_stats: " + tr_stats);
        Log.d(Const.LOG_TAG, "exercise: " + exercise.getName());
        String stats = formTrainingStats(tr_stats);
        TextView training_stat_text = (TextView) view.findViewById(R.id.cur_training_stats);
        training_stat_text.setText(stats);
        view.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EXERCISE, exercise);
    }
}