package ru.adhocapp.instaprint.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.dialog.ObjectClickListener;

public class AddressAdapter extends ArrayAdapter {

    private Context context;
    private List<Address> items;
    private LayoutInflater vi;
    private ObjectClickListener editListener;
    private ObjectClickListener deleteListener;

    public AddressAdapter(Context context, List<Address> items, ObjectClickListener editListener, ObjectClickListener deleteListener) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Address item = items.get(position);
        v = vi.inflate(R.layout.address_row, null);
        TextView title = (TextView) v.findViewById(R.id.contact_title);
        title.setText(item.getFullName());
        TextView details = (TextView) v.findViewById(R.id.contact_details);
        details.setText(item.getFullAddress());
        ImageView remove_address = (ImageView) v.findViewById(R.id.remove_address);
        ImageView edit_address = (ImageView) v.findViewById(R.id.edit_address);
        remove_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.positiveClick(item);
            }
        });
        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.positiveClick(item);
            }
        });
        return v;
    }
}