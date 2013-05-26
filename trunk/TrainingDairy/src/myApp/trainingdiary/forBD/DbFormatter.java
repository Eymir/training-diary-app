package myApp.trainingdiary.forBD;

public class DbFormatter {

	/**
	 * �������� �������� � ������ �������� � �� (102.5x3)
	 * @param measureValues - �������� ���������� ���������� � ��� ���� � ������� ������� �� ��������
	 * @return
	 */
	public static String toValue(String... measureValues) {
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
}
