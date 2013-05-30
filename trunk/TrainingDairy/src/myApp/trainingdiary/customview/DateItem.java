package myApp.trainingdiary.customview;


import java.util.Date;

public class DateItem implements Item {

    private String title;
    private Date date;

    public DateItem(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public boolean isSection() {
        return false;
    }

    public String getTitle() {
        return title;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
