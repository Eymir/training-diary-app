package myApp.trainingdiary.forBD;

import java.util.Date;

/**
 * Created by Boris on 26.05.13.
 */
public class TrainingStat {
    private Long id;
    private Date trainingDate;
    private Long exerciseId;
    private Long trainingId;
    private String value;

    public TrainingStat(Long id, Date trainingDate, Long exerciseId, Long trainingId, String value) {
        this.id = id;
        this.trainingDate = trainingDate;
        this.exerciseId = exerciseId;
        this.trainingId = trainingId;
        this.value = value;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
