package myApp.trainingdiary.db.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lenovo on 27.12.13.
 */
public class TrainingSetValue {
    private Long id;
    private Long trainingSetId;
    private Double value;
    private Long position;
    private Measure measure;

    public TrainingSetValue(Long id, Long trainingSetId, Double value, Long position) {
        this.id = id;
        this.trainingSetId = trainingSetId;
        this.value = value;
        this.position = position;
    }

    public TrainingSetValue(Long id, Long trainingSetId, Double value, Long position, Measure measure) {
        this.id = id;
        this.trainingSetId = trainingSetId;
        this.value = value;
        this.position = position;
        this.measure = measure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainingSetId() {
        return trainingSetId;
    }

    public void setTrainingSetId(Long trainingSetId) {
        this.trainingSetId = trainingSetId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        switch (measure.getType()) {
            case Numeric:
                if (measure.getStep() < 1) {
                    return String.valueOf(Math.round(value * 10000.0) / 10000.0);
                } else {
                    return String.valueOf(value.longValue());
                }
            case Temporal:
                String result = null;
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(value.longValue()));
                for (int i = measure.getMax(); i >= measure.getStep().intValue(); i--) {
                    if (result != null && !result.equals(""))
                        result += ":";
                    switch (i) {
                        case 0:
                            result += c.get(Calendar.MILLISECOND);
                            break;
                        case 1:
                            result += c.get(Calendar.SECOND);
                            break;
                        case 2:
                            result += c.get(Calendar.MINUTE);
                            break;
                        case 3:
                            result += c.get(Calendar.HOUR_OF_DAY);
                            break;
                    }
                }
                return result;
        }
        return null;
    }
}
