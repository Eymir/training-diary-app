package myApp.trainingdiary.customview.stat;

import java.io.Serializable;

/**
 * Created by Lenovo on 22.10.13.
 */
public class StatItem implements Serializable{
    private StatisticEnum item;
    private String value;

    public StatItem(StatisticEnum item, String value) {
        this.item = item;
        this.value = value;
    }

    public StatisticEnum getItem() {
        return item;
    }

    public void setItem(StatisticEnum item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
