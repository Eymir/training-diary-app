package myApp.trainingdiary.dialog;

import java.util.List;

public  class StatisticSettingsEvent {
        private Long exId;
        private Long drawMeasureId;
        private Long groupMeasureId;
        private List<Double> groups;

        public StatisticSettingsEvent() {
        }

        public StatisticSettingsEvent(Long exId, Long drawMeasureId, Long groupMeasureId, List<Double> groups) {
            this.exId = exId;
            this.drawMeasureId = drawMeasureId;
            this.groupMeasureId = groupMeasureId;
            this.groups = groups;
        }

        public Long getExId() {
            return exId;
        }

        public void setExId(Long exId) {
            this.exId = exId;
        }

        public Long getDrawMeasureId() {
            return drawMeasureId;
        }

        public void setDrawMeasureId(Long drawMeasureId) {
            this.drawMeasureId = drawMeasureId;
        }

        public Long getGroupMeasureId() {
            return groupMeasureId;
        }

        public void setGroupMeasureId(Long groupMeasureId) {
            this.groupMeasureId = groupMeasureId;
        }

        public List<Double> getGroups() {
            return groups;
        }

        public void setGroups(List<Double> groups) {
            this.groups = groups;
        }
    }