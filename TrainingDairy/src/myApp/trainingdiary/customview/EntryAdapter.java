package myApp.trainingdiary.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import myApp.trainingdiary.R;

import java.util.ArrayList;

public class EntryAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;
    private LayoutInflater vi;

    public EntryAdapter(Context context, ArrayList items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final Item i = (Item) items.get(position);
        if (i != null) {
            if (i.isSection()) {
                SectionItem si = (SectionItem) i;
                v = vi.inflate(R.layout.section_row, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.label);
                sectionView.setText(si.getTitle());
            } else {
                EntryItem ei = (EntryItem) i;
                v = vi.inflate(R.layout.exercise_plain_row, null);
                final TextView title = (TextView) v.findViewById(R.id.label);
                final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                if (title != null)
                    title.setText(ei.title);
                if (icon != null) {
                    icon.setImageResource(context.getResources()
                            .getIdentifier(ei.icon,
                                    "drawable", context.getPackageName()));
                }
            }
        }
        return v;
    }
}