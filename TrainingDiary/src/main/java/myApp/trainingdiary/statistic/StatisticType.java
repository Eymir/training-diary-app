package myApp.trainingdiary.statistic;

/**
 * Created by bshestakov on 25.07.13.
 */
public enum StatisticType {
    MAXIMUM(0);
    int code;

    private StatisticType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StatisticType valueOf(int code) {
        for (StatisticType type : values()) {
            return type;
        }
        return null;
    }

}
