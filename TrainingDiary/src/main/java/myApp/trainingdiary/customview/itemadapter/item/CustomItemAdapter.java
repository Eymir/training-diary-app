package myApp.trainingdiary.customview.itemadapter.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import myApp.trainingdiary.R;

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

        switch (item.getType()) {
            case SMALL_SECTION: {
                SmallSectionItem si = (SmallSectionItem) item;
                v = vi.inflate(R.layout.small_section_row, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                v.setClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.label);
                sectionView.setText(si.getTitle());
                break;
            }
            case BIG_SECTION: {
                BigSectionItem bigSectionItem = (BigSectionItem) item;
                v = vi.inflate(R.layout.big_section_row, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                v.setClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.label);
                sectionView.setText(bigSectionItem.getTitle());
                final ImageView imageView = (ImageView) v.findViewById(R.id.icon);
                imageView.setImageResource(context.getResources()
                        .getIdentifier(bigSectionItem.getIcon(),
                                "drawable", context.getPackageName()));
                break;
            }
            case DATE: {
                DateItem dateItem = (DateItem) item;
                v = vi.inflate(R.layout.exercise_plain_row, null);
                final TextView title = (TextView) v.findViewById(R.id.label);
                final ImageView icon = (ImageView) v.findViewById(R.id.icon);

                if (title != null)
                    title.setText(dateItem.getTitle());

                icon.setImageResource(R.drawable.ico_train);
                break;
            }
            case EXERCISE: {
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
                break;
            }
            case STATISTIC: {
                StatisticItem statItem = (StatisticItem) item;
                v = vi.inflate(R.layout.small_stat_row, null);
                final TextView title = (TextView) v.findViewById(R.id.label);
                if (title != null) {
                    title.setText(statItem.getNumber() + ".  " + statItem.getTitle());
                }
                break;
            }

        }
        return v;
    }
}