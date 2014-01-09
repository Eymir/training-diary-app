package myApp.trainingdiary.db.entity;

import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 27.12.13.
 */
public class TrainingSet {
    private Long id;
    private Long trainingStampId;
    private Date date;
    private Long exerciseId;
    private Long trainingId;
    private List<TrainingSetValue> values;

    public TrainingSet(Long id, Long trainingStampId, Date date, Long exerciseId, Long trainingId) {
        this.id = id;
        this.trainingStampId = trainingStampId;
        this.date = date;
        this.exerciseId = exerciseId;
        this.trainingId = trainingId;
    }

    public TrainingSet(Long id, Long trainingStampId, Date date, Long exerciseId, Long trainingId, List<TrainingSetValue> values) {
        this.id = id;
        this.trainingStampId = trainingStampId;
        this.date = date;
        this.exerciseId = exerciseId;
        this.trainingId = trainingId;
        this.values = values;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainingStampId() {
        return trainingStampId;
    }

    public void setTrainingStampId(Long trainingStampId) {
        this.trainingStampId = trainingStampId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public List<TrainingSetValue> getValues() {
        return values;
    }

    public void setValues(List<TrainingSetValue> values) {
        this.values = values;
    }

    public TrainingSetValue getValueByPos(Long pos) {
        for (TrainingSetValue setValue : getValues()) {
            if (setValue.getPosition().equals(pos)) {
                return setValue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TrainingSet{" +
                "id=" + id +
                ", trainingStampId=" + trainingStampId +
                ", date=" + date +
                ", exerciseId=" + exerciseId +
                ", trainingId=" + trainingId +
                ", values=" + values +
                '}';
    }
}
