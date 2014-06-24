package ru.adhocapp.instaprint.db.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.model.data.OrderItem;
import ru.adhocapp.instaprint.dialog.ObjectClickListener;

public class OrdersAdapter extends ArrayAdapter {

    private Context context;
    private List<OrderItem> items;
    private LayoutInflater vi;
    private ObjectClickListener editListener;
    private ObjectClickListener deleteListener;

    public OrdersAdapter(Context context, List<OrderItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final OrderItem item = items.get(position);
        if (item.getIsCategory()) {
            v = vi.inflate(android.R.layout.preference_category, null);
            TextView title = (TextView) v;
            title.setText(item.getCategoryNameId());
        } else {
            v = vi.inflate(R.layout.order_row, null);
            TextView title = (TextView) v.findViewById(R.id.order_title);
            TextView details = (TextView) v.findViewById(R.id.order_details);
            TextView date = (TextView) v.findViewById(R.id.order_date);
            Address addressTo = item.getOrder().getAddressTo();
            title.setText(addressTo.getFullName());
            details.setText(addressTo.getCountryName() + ", " + addressTo.getCityName() + ", " + addressTo.getStreetAddress());
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
            date.setText(dateFormatter.format(item.getOrder().getDate()));
        }
        return v;
    }
}