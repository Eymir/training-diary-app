package myApp.trainingdiary.customview.stat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myApp.trainingdiary.R;
import myApp.trainingdiary.utils.Consts;

/**
 * Created by Lenovo on 29.07.13.
 */
public class StatItemArrayAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList items;
    private LayoutInflater vi;
    private int mainLayout;
    private int dropLayout;

    public StatItemArrayAdapter(Context context, int mainLayout, int dropLayout, ArrayList items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mainLayout = mainLayout;
        this.dropLayout = dropLayout;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, dropLayout, convertView, parent);
    }

    private View getCustomView(int position, int layout, View convertView, ViewGroup parent) {
        View v = convertView;
        final Object o = items.get(position);
        if (o instanceof StatItem) {
            StatItem m = (StatItem) o;
            v = vi.inflate(layout, null);
            final TextView name = (TextView) v.findViewById(R.id.text_name);
            if (name != null) {
                name.setText(m.getItem().getDesc());
            } else {
                Log.e(Consts.LOG_TAG, "text_label is null");
            }

            final TextView value = (TextView) v.findViewById(R.id.text_value);
            if (value != null) {
                value.setText(m.getValue());
            } else {
                Log.e(Consts.LOG_TAG, "text_value is null");
            }

        } else {
            Log.e(Consts.LOG_TAG, "item is not measure");
        }

        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, mainLayout, convertView, parent);
    }
}
