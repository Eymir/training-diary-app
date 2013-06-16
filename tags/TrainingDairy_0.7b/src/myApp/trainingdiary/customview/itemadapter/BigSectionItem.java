package myApp.trainingdiary.customview.itemadapter;

public class BigSectionItem implements Item {

    private final String title;
    private final String icon;


    public BigSectionItem(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public ItemType getType() {
        return ItemType.BIG_SECTION;
    }

}
