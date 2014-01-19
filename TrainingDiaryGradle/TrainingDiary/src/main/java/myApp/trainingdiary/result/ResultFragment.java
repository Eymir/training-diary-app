package myApp.trainingdiary.result;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            result += MeasureFormatter.valueFormat(set) + ";  ";
        }
        return result;
    }

    private Spannable createWhite(String str) {
        Spannable span1 = new SpannableString(str);
        span1.setSpan(new ForegroundColorSpan(R.color.white), 0, span1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span1;
    }

    private Spannable createTranparentWhite(String str) {
        Spannable span1 = new SpannableString(str);
        span1.setSpan(new ForegroundColorSpan(R.color.white), 0, span1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span1;
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
        TextView training_stat_text = (TextView) view.findViewById(R.id.cur_training_stats);
        String s = formTrainingStats(tr_stats);
        int color = view.getResources().getColor(R.color.white_little_transparent);
        training_stat_text.setText(makeSeparatorsTransparent(s,color));
        view.invalidate();
    }

    private SpannableStringBuilder makeSeparatorsTransparent(String s,int color) {
        final Pattern p1 = Pattern.compile("[x;]");
        final Matcher matcher = p1.matcher(s);

        final SpannableStringBuilder spannable = new SpannableStringBuilder(s);
        while (matcher.find()) {

            final ForegroundColorSpan span = new ForegroundColorSpan(color);
            spannable.setSpan(
                    span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EXERCISE, exercise);
    }
}