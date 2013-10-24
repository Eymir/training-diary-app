package myApp.trainingdiary.customview.stat;

import java.util.ArrayList;
import java.util.List;

import myApp.trainingdiary.AndroidApplication;
import myApp.trainingdiary.R;

/**
 * Created by Lenovo on 22.10.13.
 */
public enum StatisticEnum {
    TRAINING_COUNT(AndroidApplication.getInstance().getString(R.string.tr_count_stat)),
    FAVORITE_EXERCISE(AndroidApplication.getInstance().getString(R.string.favorite_ex_stat)),
    TRAINING_DURATION_SUMM(AndroidApplication.getInstance().getString(R.string.tr_dur_sum)),
    LAST_TRAINING_DAY_COUNT(AndroidApplication.getInstance().getString(R.string.last_tr_time_passed)),
    LAST_TRAINING_DATE(AndroidApplication.getInstance().getString(R.string.last_tr_date_stat));
    private final String desc;

    StatisticEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static StatisticEnum getByDesc(String desc) {
        for (StatisticEnum stat : values()) {
            if (stat.getDesc().equals(desc))
                return stat;
        }
        throw new IllegalArgumentException("There is no enum with desc: " + desc);
    }

    public static String[] getDescNames() {
        StatisticEnum[] stats = values();
        String[] strings = new String[stats.length];

        for (int i = 0; i < stats.length; i++) {
            strings[i] = stats[i].getDesc();
        }
        return strings;
    }
}
