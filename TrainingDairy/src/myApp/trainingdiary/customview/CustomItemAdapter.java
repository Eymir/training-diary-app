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

public class CustomItemAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;
    private LayoutInflater vi;

    public CustomItemAdapter(Context context, ArrayList items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final Item item = (Item) items.get(position);
        if (item != null) {
            if (item.isSection()) {
                SectionItem si = (SectionItem) item;
                v = vi.inflate(R.layout.section_row, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.label);
                sectionView.setText(si.getTitle());
            } else {
                if (item instanceof ExerciseItem) {
                    ExerciseItem exerciseItem = (ExerciseItem) item;
                    v = vi.inflate(R.layout.exercise_plain_row, null);
                    final TextView title = (TextView) v.findViewById(R.id.label);
                    final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                    if (title != null)
                        title.setText(exerciseItem.getTitle());
                    if (icon != null) {
                        icon.setImageResource(context.getResources()
                                .getIdentifier(exerciseItem.getIcon(),
                                        "drawable", context.getPackageName()));
                    }
                }
                if (item instanceof DateItem) {
                    DateItem dateItem = (DateItem) item;
                    v = vi.inflate(R.layout.exercise_plain_row, null);
                    final TextView title = (TextView) v.findViewById(R.id.label);
                    final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                    if (title != null)
                        title.setText(dateItem.getTitle());

                    icon.setImageResource(R.drawable.ico_train);
                }
            }
        }
        return v;
    }
}