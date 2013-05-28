package myApp.trainingdiary.customview;


public class EntryItem implements Item{

	public final String title;
	public final String icon;

	public EntryItem(String title, String icon) {
		this.title = title;
		this.icon = icon;
	}

    @Override
	public boolean isSection() {
		return false;
	}

}
