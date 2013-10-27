package myApp.trainingdiary.utils;

import java.util.Arrays;
import java.util.List;

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
     * @param statValue
     * @param pos позиция элемента, нумерация с 0
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
}
