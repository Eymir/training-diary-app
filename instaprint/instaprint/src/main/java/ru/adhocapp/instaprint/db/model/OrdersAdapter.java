package ru.adhocapp.instaprint.db.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.DateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.model.data.OrderItem;
import ru.adhocapp.instaprint.dialog.ObjectClickListener;
import ru.adhocapp.instaprint.util.Const;

public class OrdersAdapter extends ArrayAdapter {

    private Context context;
    private List<OrderItem> items;
    private LayoutInflater vi;
    private ObjectClickListener editListener;
    private ObjectClickListener deleteListener;
    private DisplayImageOptions options;
    private OrderListClickListener listener;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public OrdersAdapter(Context context, List<OrderItem> items, OrderListClickListener listener) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        this.listener = listener;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
            ImageView contextAction = (ImageView) v.findViewById(R.id.context_action);
            ImageView frontPostcard = (ImageView) v.findViewById(R.id.order_image);
            Address addressTo = item.getOrder().getAddressTo();
            title.setText(addressTo.getFullName());
            details.setText(addressTo.getCountryName() + ", " + addressTo.getCityName() + ", " + addressTo.getStreetAddress());
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
            date.setText(dateFormatter.format(item.getOrder().getDate()));
            contextAction.setImageResource(getContextActionImage(item.getOrder().getStatus()));
            contextAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(Const.LOG_TAG, "onContextButtonClick: " + item.getOrder());
                    listener.onContextButtonClick(item.getOrder());
                }
            });
            Log.d(Const.LOG_TAG, item.getOrder().getFrontSidePhotoPath());
            imageLoader.displayImage("file://" + item.getOrder().getFrontSidePhotoPath(), frontPostcard, options, animateFirstListener);
        }
        return v;
    }

    private int getContextActionImage(OrderStatus status) {
        switch (status) {
            case CREATING:
                return R.drawable.ic_edit_blank;

            case PAYING:
                return R.drawable.ic_buy;

            case EMAIL_SENDING:
                return R.drawable.ic_send_mail;

            case PRINTING_AND_SNAILMAILING:
                return R.drawable.ic_send_mail;

            case EXECUTED:
                return R.drawable.ic_copy;
        }
        return 0;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}