package myApp.trainingdiary.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

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
}
