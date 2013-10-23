package myApp.trainingdiary.customview.stat;

import java.text.DateFormat;

import myApp.trainingdiary.AndroidApplication;
import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingStat;

/**
 * Created by Lenovo on 22.10.13.
 */
public class StatisticValueFactory {

    public static StatItem create(StatisticEnum stat) {
        switch (stat) {
            case LAST_TRAINING_DATE:
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                TrainingStat tr_stat = DBHelper.getInstance(null).READ.getLastTrainingStat();
                String value;
                if (tr_stat != null)
                    value = df.format(tr_stat.getTrainingDate());
                else
                    value = AndroidApplication.getInstance().getResources().getString(R.string.empty);
                return new StatItem(stat, value);
            case TRAINING_COUNT:
                return new StatItem(stat, String.valueOf(DBHelper.getInstance(null).READ.getTrainingCount()));

        }
        return null;
    }
}
