package myApp.trainingdiary.utils;

import java.util.List;

import myApp.trainingdiary.db.entity.TrainingSet;
import myApp.trainingdiary.db.entity.TrainingSetValue;
import myApp.trainingdiary.db.entity.TrainingStamp;

/**
 * Created by Lenovo on 08.01.14.
 */
public class StatisticUtils {
    public static Double maxResult(TrainingStamp stamp, Long pos) {
        Double max = null;
        for (TrainingSet set : stamp.getTrainingSetList()) {
            if (max == null || set.getValueByPos(pos).getValue() > max) {
                max = set.getValueByPos(pos).getValue();
            }
        }
        return max;
    }

    public static TrainingSet maxTrainingSetByGroupMeasure(TrainingStamp stamp, Long pos, Double g, Long g_pos) {
        TrainingSet max = null;
        for (TrainingSet set : stamp.getTrainingSetList()) {
            TrainingSetValue groupValue = set.getValueByPos(g_pos);
            if (groupValue != null && groupValue.getValue().equals(g)) {
                if (max == null || set.getValueByPos(pos).getValue() > max.getValueByPos(pos).getValue()) {
                    max = set;
                }
            }
        }
        return max;
    }

    public static Double minResult(TrainingStamp stamp, Long pos) {
        Double min = null;
        for (TrainingSet set : stamp.getTrainingSetList()) {
            if (min == null || set.getValueByPos(pos).getValue() < min) {
                min = set.getValueByPos(pos).getValue();
            }
        }
        return min;
    }

    public static Double avgResult(TrainingStamp stamp, Long pos) {
        Double avg = 0d;
        List<TrainingSet> list = stamp.getTrainingSetList();
        for (int i = 0; i < list.size(); i++) {
            TrainingSet set = list.get(i);
            avg += set.getValueByPos(pos).getValue();
        }
        if (list.size() > 0)
            avg /= new Integer(list.size()).doubleValue();
        return avg;
    }
}
