package myApp.trainingdiary.utils;

import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;

public class MeasureFormatter {

    /**
     * �������� �������� � ������ �������� � �� (102.5x3)
     *
     * @param measureValues - �������� ���������� ���������� � ��� ���� � ������� ������� �� ��������
     * @return
     */
    public static String toStatValue(String... measureValues) {
        String value = "";
        for (String m_v : measureValues) {
            if (value.length() == 0) {
                value += m_v;
            } else {
                value += "x" + m_v;
            }
        }
        return value;
    }

    public static List<String> toMeasureValues(String statValue) {
        statValue = statValue.replaceAll(",", ".");
        String[] values = statValue.split("x");
        return Arrays.asList(values);
    }

    /**
     * Возвращает значение по позиции (нумерация с 0) из строки формата ААхББхСС
     *
     * @param statValue
     * @param pos       позиция элемента, нумерация с 0
     * @return
     */
    public static double getValueByPos(String statValue, int pos) {
        List<String> values = toMeasureValues(statValue);
        String value = values.get(pos);
        value = value.replaceAll(",", ".");
        return Double.valueOf(value);
    }

    public static double getTimeValueByPos(String statValue, int pos) {
        List<String> values = toMeasureValues(statValue);
        String value = values.get(pos);
        value = value.replaceAll(":", ".");
        return Double.valueOf(value);
    }

    public static String valueFormat(TrainingSet tr_set) {
        String result = "";
        List<TrainingSetValue> list = tr_set.getValues();
        Log.d(Const.LOG_TAG, "TrainingSet.getValues:" + list);
        if (list != null)
            for (TrainingSetValue value : list) {
                if (result.length() == 0) {
                    result += value.toString();
                } else {
                    result += "x" + value.toString();
                }
            }
        return result;
    }

    public static Double getDoubleValue(String stringValue, Measure measure) {
        switch (measure.getType()) {
            case Numeric:
                return Double.valueOf(stringValue);
            case Temporal:
                List<String> str_values = MeasureFormatter.toMeasureValues(stringValue);
                try {
                    String[] time_list = stringValue.split(":");
                    int min = Integer.valueOf(time_list[0]);
                    int sec = Integer.valueOf(time_list[1]);
                    return Integer.valueOf((min * 60 + sec) * 1000).doubleValue();

                } catch (Throwable e) {
                    Log.e(Const.LOG_TAG, e.getMessage(), e);
                }
        }
        return null;
    }

    public static void writeResultStatView(TextView view, Long exercise_id) {
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Long tr_stamp_id = TrainingDurationManger.getTrainingStamp(Integer.valueOf(workoutExpiringTimeout));
        List<TrainingSet> tr_stats = DBHelper.getInstance(null).READ.getTrainingSetListInTrainingStampByExercise(exercise_id, tr_stamp_id);
        Log.d(Const.LOG_TAG, "tr_stats: " + tr_stats);
        String s = formTrainingStats(tr_stats);
        int color = view.getResources().getColor(R.color.white_little_transparent);
        view.setText(makeSeparatorsTransparent(s, color));
    }

    private static String formTrainingStats(List<TrainingSet> sets) {
        String result = "";
        for (int i = 0; i < sets.size(); i++) {
            TrainingSet set = sets.get(i);
            result += MeasureFormatter.valueFormat(set) + ";  ";
        }
        return result;
    }

    private static SpannableStringBuilder makeSeparatorsTransparent(String s, int color) {
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
}
