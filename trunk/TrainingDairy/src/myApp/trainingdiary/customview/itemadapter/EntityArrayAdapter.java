package myApp.trainingdiary.customview.itemadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.entity.Measure;

/**
 * Created by Lenovo on 29.07.13.
 */
public class EntityArrayAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList items;
    private LayoutInflater vi;

    public EntityArrayAdapter(Context context, ArrayList items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final Object o = items.get(position);
        if (o instanceof Measure){
            Measure m = (Measure) o;
            v = vi.inflate(R.layout.just_big_string, null);
            final TextView title = (TextView) v.findViewById(R.id.label);

            if (title != null)
                title.setText(m.getName());

        }

        return v;
    }
}
