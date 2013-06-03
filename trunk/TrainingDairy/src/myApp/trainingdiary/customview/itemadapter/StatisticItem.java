package myApp.trainingdiary.customview.itemadapter;


public class StatisticItem implements Item {

    private final Integer number;
    private final String title;

    public StatisticItem(Integer number, String title) {
        this.number = number;
        this.title = title;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public ItemType getType() {
        return ItemType.STATISTIC;
    }


    public String getTitle() {
        return title;
    }


}
