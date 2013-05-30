package myApp.trainingdiary.customview;


public class ExerciseItem implements Item {

    private String title;
    private String icon;
    private long exId;

    public ExerciseItem(String title, String icon, long exId) {
        this.title = title;
        this.icon = icon;
        this.exId = exId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getExId() {
        return exId;
    }

    public void setExId(long exId) {
        this.exId = exId;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}
