package myApp.trainingdiary.customview.stat;

import android.content.res.Resources;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import myApp.trainingdiary.AndroidApplication;
import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.TrainingStat;

/**
 * Created by Lenovo on 22.10.13.
 */
public class StatisticValueFactory {

    public static StatItem create(StatisticEnum stat) {
        switch (stat) {
            case LAST_TRAINING_DATE: {
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                TrainingStat tr_stat = DBHelper.getInstance(null).READ.getLastTrainingStat();
                String value;
                if (tr_stat != null)
                    value = df.format(tr_stat.getTrainingDate());
                else
                    value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                return new StatItem(stat, value);
            }
            case LAST_TRAINING_DAY_COUNT: {
                TrainingStat tr_stat = DBHelper.getInstance(null).READ.getLastTrainingStat();
                String value;
                if (tr_stat != null)
                    value = toTimeString(System.currentTimeMillis() - tr_stat.getTrainingDate().getTime());
                else
                    value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                return new StatItem(stat, value);
            }
            case TRAINING_COUNT:
                return new StatItem(stat, String.valueOf(DBHelper.getInstance(null).READ.getTrainingCount()));
            case FAVORITE_EXERCISE: {
                Exercise ex = DBHelper.getInstance(null).READ.getFavoriteExercise();
                String name = (ex != null) ? ex.getName() : AndroidApplication.getInstance().getResources().getString(R.string.no);
                return new StatItem(stat, name);
            }
            case TRAINING_DURATION_SUMM: {
                long tr_dur_sum = DBHelper.getInstance(null).READ.getTrainingDurationSumm();
                String value = toTimeString(tr_dur_sum);
                return new StatItem(stat, value);
            }
        }
        return null;
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
