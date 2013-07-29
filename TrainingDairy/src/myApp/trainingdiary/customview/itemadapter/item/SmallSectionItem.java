package myApp.trainingdiary.customview.itemadapter.item;

public class SmallSectionItem implements Item {

    private final String title;

    public SmallSectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }



    @Override
    public ItemType getType() {
        return ItemType.SMALL_SECTION;
    }
}
