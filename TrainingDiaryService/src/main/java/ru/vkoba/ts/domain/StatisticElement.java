package ru.vkoba.ts.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * User: vkoba_000
 * Date: 12/2/13
 * Time: 7:52 PM
 */

@XmlRootElement
public class StatisticElement {
    String deviceId;
    Long trainingStart;
    Long trainingEnd;

    public StatisticElement() {
    }

    public StatisticElement(String deviceId, Date trainingStart, Date trainingEnd) {
        this.deviceId = deviceId;
        this.trainingStart = (trainingStart != null) ? trainingStart.getTime() : null;
        this.trainingEnd = (trainingEnd != null) ? trainingEnd.getTime() : null;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getTrainingEnd() {
        return trainingEnd;
    }

    public void setTrainingEnd(Long trainingEnd) {
        this.trainingEnd = trainingEnd;
    }

    public Long getTrainingStart() {
        return trainingStart;
    }

    public void setTrainingStart(Long trainingStart) {
        this.trainingStart = trainingStart;
    }

    public Date getTrainingEndDate() {
        return new Date(trainingEnd);
    }

    public void setTrainingEndDate(Date trainingEnd) {
        this.trainingEnd = (trainingEnd != null) ? trainingEnd.getTime() : null;
    }

    public Date getTrainingStartDate() {
        return new Date(trainingStart);
    }

    public void setTrainingStartDate(Date trainingStart) {
        this.trainingStart = (trainingStart != null) ? trainingStart.getTime() : null;
    }


    @Override
    public String toString() {
        return "StatisticElement{" +
                "deviceId='" + deviceId + '\'' +
                ", trainingStart=" + getTrainingStartDate() +
                ", trainingEnd=" + getTrainingEndDate() +
                '}';
    }
}
