package myApp.trainingdiary.db;

import java.util.Arrays;
import java.util.List;

public class DbFormatter {

    /**
     * �������� �������� � ������ �������� � �� (102.5x3)
     *
     * @param measureValues - �������� ���������� ���������� � ��� ���� � ������� ������� �� ��������
     * @return
     */
    public static String toStatValue(String... measureValues) {
        String value = "";
        for (String m_v : measureValues) {
            if (value.isEmpty()) {
                value += m_v;
            } else {
                value += "x" + m_v;
            }
        }
        return value;
    }

    public static List<String> toMeasureValues(String statValue) {
        String[] values = statValue.split("x");
        return Arrays.asList(values);
    }

}
