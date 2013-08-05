package myApp.trainingdiary.customview;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lenovo on 02.08.13.
 */
public class CustomCursorAdapter extends SimpleCursorAdapter {
    private final int[] to;
    private final String[] from;
    private final int dropDownLayout;
    private final int mainViewLayout;
    private final Activity activity;

    public CustomCursorAdapter(Activity activity, Context context, int mainViewLayout, int dropDownLayout, Cursor c, String[] from, int[] to, int flags) {
        super(context, mainViewLayout, c, from, to, flags);
        this.mainViewLayout = mainViewLayout;
        this.dropDownLayout = dropDownLayout;
        this.activity = activity;
        this.from = from;
        this.to = to;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, dropDownLayout, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, mainViewLayout, convertView, parent);
    }

    public View getCustomView(int position, int layout, View convertView, ViewGroup parent) {

        Cursor c = getCursor();
        c.moveToPosition(position);

        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(layout, null);
        TextView label = (TextView) row.findViewById(to[0]);
        label.setText(c.getString(c.getColumnIndex(from[0])));


        ImageView icon = (ImageView) row.findViewById(to[1]);
        icon.setImageResource(activity.getResources()
                .getIdentifier(c.getString(c.getColumnIndex(from[1])),
                        "drawable", activity.getPackageName()));

        return row;
    }

}