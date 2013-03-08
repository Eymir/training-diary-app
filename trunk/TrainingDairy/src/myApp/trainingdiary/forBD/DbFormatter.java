package myApp.trainingdiary.forBD;

public class DbFormatter {

	/**
	 * Приводит значения в формат хранения в БД (102.5x3)
	 * @param mesureValues - значения необходимо передавать в том виде в котором хочется их записать
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
