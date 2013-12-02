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
    Date trainingStart;
    Date trainingEnd;

    public StatisticElement() {
    }

    public StatisticElement(String deviceId, Date trainingStart, Date trainingEnd) {
        this.deviceId = deviceId;
        this.trainingStart = trainingStart;
        this.trainingEnd = trainingEnd;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getTrainingStart() {
        return trainingStart;
    }

    public void setTrainingStart(Date trainingStart) {
        this.trainingStart = trainingStart;
    }

    public Date getTrainingEnd() {
        return trainingEnd;
    }

    public void setTrainingEnd(Date trainingEnd) {
        this.trainingEnd = trainingEnd;
    }
}
