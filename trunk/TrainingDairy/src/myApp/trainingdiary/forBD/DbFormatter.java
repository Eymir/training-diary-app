package myApp.trainingdiary.forBD;

public class DbFormatter {

	/**
	 * �������� �������� � ������ �������� � �� (102.5x3)
	 * @param mesureValues - �������� ���������� ���������� � ��� ���� � ������� ������� �� ��������
	 * @return
	 */
	public static String toValue(String... mesureValues) {
		String value = "";
		for (String m_v : mesureValues) {
			if (value.isEmpty()) {
				value += m_v;
			} else {
				value += "x" + m_v;
			}
		}
		return value;
	}
}
