package myApp.trainingdiary.customview.stat;

import android.content.res.Resources;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import myApp.trainingdiary.AndroidApplication;
import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.utils.Const;

/**
 * Created by Lenovo on 22.10.13.
 */
public class StatisticValueFactory {

    public static StatItem create(StatisticEnum stat) {
        try {
            switch (stat) {
                case LAST_TRAINING_DATE: {
                    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                    TrainingStamp tr_stamp = DBHelper.getInstance(null).READ.getLastClosedTrainingStamp();
                    String value;
                    if (tr_stamp != null)
                        value = df.format(tr_stamp.getEndDate());
                    else
                        value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                    return new StatItem(stat, value);
                }
                case LAST_TRAINING_DAY_COUNT: {
                    TrainingStamp tr_stamp = DBHelper.getInstance(null).READ.getLastClosedTrainingStamp();
                    String value;
                    if (tr_stamp != null)
                        value = toTimeString(System.currentTimeMillis()
                                - ((tr_stamp.getEndDate() != null) ? tr_stamp.getEndDate().getTime() : tr_stamp.getStartDate().getTime()));
                    else
                        value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                    return new StatItem(stat, value);
                }
                case TRAINING_COUNT:
                    return new StatItem(stat, String.valueOf(DBHelper.getInstance(null).READ.getTrainingStampCount()));
                case FAVORITE_EXERCISE: {
                    Exercise ex = DBHelper.getInstance(null).READ.getFavoriteExercise();
                    String name = (ex != null) ? ex.getName() : AndroidApplication.getInstance().getResources().getString(R.string.no);
                    return new StatItem(stat, name);
                }
                case TRAINING_DURATION_SUMM: {
                    Log.i(Const.LOG_TAG, "TRAINING_DURATION_SUMM " );
                    long tr_dur_sum = DBHelper.getInstance(null).READ.getTrainingDurationSumm();
                    Log.i(Const.LOG_TAG, "TRAINING_DURATION_SUMM/tr_dur_sum: " + tr_dur_sum);
                    String value = toTimeString(tr_dur_sum);
                    Log.i(Const.LOG_TAG, "TRAINING_DURATION_SUMM/value: " + value);
                    return new StatItem(stat, value);
                }
                case MAX_RESULT_IN_FAVORTE_EX: {
                    Exercise exercise = DBHelper.getInstance(null).READ.getFavoriteExercise();
                    String value = null;
                    if (exercise != null) {
                        Double result = DBHelper.getInstance(null).READ.getMaxExerciseResultByPos(exercise.getId(), 0);
                        value = exercise.getName() + "(" + result + ")";
                    } else {
                        value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                    }
                    return new StatItem(stat, value);
                }
                default:
                    return new StatItem(stat, AndroidApplication.getInstance().getResources().getString(R.string.not_found));
            }
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, "Error while parse value for " + stat, e);
            return new StatItem(stat, AndroidApplication.getInstance().getResources().getString(R.string.error));
        }
    }

    private static String toTimeString(long tr_dur_sum) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(tr_dur_sum));
        int day = c.get(Calendar.DAY_OF_YEAR) - 1;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Resources res = AndroidApplication.getInstance().getResources();
        return day + res.getString(R.string.day_short) + " " + hour + res.getString(R.string.hour_short) + " " + min + res.getString(R.string.min_short);
    }

}
