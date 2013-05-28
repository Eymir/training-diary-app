package myApp.trainingdiary.db;

/**
 * Created by Boris on 28.05.13.
 */
public enum MeasureType {
    Numeric(0), Temporal(1);
    int code;

    private MeasureType(int code) {
        this.code = code;
    }

    static public MeasureType valueOf(int code) {
        for (MeasureType type : MeasureType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("There is no MeasureType for code: " + code);
    }
}
