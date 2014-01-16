package myApp.trainingdiary.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 28.12.13.
 */
public class TrainingStamp implements Serializable {
    private Long id;
    private Date startDate;
    private Date endDate;
    private String comment;
    private TrainingStampStatus status;
    private List<TrainingSet> trainingSetList;

    public TrainingStamp(Long id, Date startDate, Date endDate, String comment, TrainingStampStatus status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
        this.status = status;
    }

    public TrainingStamp(Long id, Date startDate, Date endDate, String comment, TrainingStampStatus status, List<TrainingSet> trainingSetList) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
        this.status = status;
        this.trainingSetList = trainingSetList;
    }

    public List<TrainingSet> getTrainingSetList() {
        return trainingSetList;
    }

    public void setTrainingSetList(List<TrainingSet> trainingSetList) {
        this.trainingSetList = trainingSetList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TrainingStampStatus getStatus() {
        return status;
    }

    public void setStatus(TrainingStampStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TrainingStamp{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                ", trainingSetList=" + trainingSetList +
                '}';
    }
}
