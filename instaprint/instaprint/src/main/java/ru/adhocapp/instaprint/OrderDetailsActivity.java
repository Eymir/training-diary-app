package ru.adhocapp.instaprint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DateFormat;

import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.PurchaseDetails;
import ru.adhocapp.instaprint.util.Const;

public class OrderDetailsActivity extends FragmentActivity {
    protected Order order;
    private static DisplayImageOptions options;
    protected static final ImageLoader imageLoader = ImageLoader.getInstance();
    protected static final double ratio = 1.40952380952381d;
    private ViewPager pager;
    private CirclePageIndicator titleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            order = (Order) b.getSerializable(Const.ORDER);
        }
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PostcardSidePagerAdapter(order, getSupportFragmentManager()));
        titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        titleIndicator.setViewPager(pager);

        if (options == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        if (order.getAddressTo() != null)
            fillAddressTo(order.getAddressTo());
        if (order.getPurchaseDetails() != null)
            fillPurchaseDetails(order.getPurchaseDetails());
        fillOrderInf(order);

        //Костыль для ViewPager-a
        ViewTreeObserver vto = pager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = pager.getMeasuredWidth();
                int height = pager.getMeasuredHeight();
                ViewGroup.LayoutParams p = pager.getLayoutParams();
                Log.d(Const.LOG_TAG, "p.w:" + p.width);
                Log.d(Const.LOG_TAG, "pager.width:" + width);
                p.height = (int) (width / ratio) + 10;
            }
        });
    }

    private void fillOrderInf(Order order) {
        EditText order_number = (EditText) findViewById(R.id.order_number);
        order_number.setText(getString(R.string.order_with_number) + order.getId());

        if (order.getDate() != null) {
            EditText order_date = (EditText) findViewById(R.id.order_date);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
            order_date.setText(dateFormatter.format(order.getDate()));
        }

        EditText message = (EditText) findViewById(R.id.message);
        message.setText(order.getText());
    }

    private void fillPurchaseDetails(PurchaseDetails purchaseDetails) {
        EditText transaction_id = (EditText) findViewById(R.id.transaction_id);
        transaction_id.setText(purchaseDetails.getOrderNumber());

        if (purchaseDetails.getPayDate() != null) {
            EditText pay_date = (EditText) findViewById(R.id.pay_date);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
            pay_date.setText(dateFormatter.format(purchaseDetails.getPayDate()));
        }

        if (purchaseDetails.getPrice() != null) {
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance();
            EditText price = (EditText) findViewById(R.id.price);
            price.setText(format.format(purchaseDetails.getPrice()));
        }
    }

    private void fillAddressTo(Address addressTo) {
        EditText full_name = (EditText) findViewById(R.id.full_name);
        full_name.setText(addressTo.getFullName());

        EditText address = (EditText) findViewById(R.id.address);
        address.setText(addressTo.getStreetAddress());

        EditText zipcode = (EditText) findViewById(R.id.zipcode);
        zipcode.setText(addressTo.getZipCode());

        EditText city = (EditText) findViewById(R.id.city);
        city.setText(addressTo.getCityName());

        EditText country = (EditText) findViewById(R.id.country);
        country.setText(addressTo.getCountryName());

    }

    private class PostcardSidePagerAdapter extends FragmentStatePagerAdapter {
        private Order order;

        public PostcardSidePagerAdapter(Order order, FragmentManager fm) {
            super(fm);
            this.order = order;
            Log.d(Const.LOG_TAG, "PostcardSidePagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(Const.LOG_TAG, "getItem:" + position);
            switch (position) {
                case 0:
                    return PostcardSidePageFragment.newInstance(order.getFrontSidePhotoPath());
                case 1:
                    return PostcardSidePageFragment.newInstance(order.getBackSidePhotoPath());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    private static class PostcardSidePageFragment extends Fragment {
        private static final String ARGUMENT_SIDE_PATH = "SIDE_PATH";

        public static PostcardSidePageFragment newInstance(String path) {
            PostcardSidePageFragment createPostcardPageFragment = new PostcardSidePageFragment();
            Bundle arguments = new Bundle();
            arguments.putString(ARGUMENT_SIDE_PATH, path);
            createPostcardPageFragment.setArguments(arguments);
            return createPostcardPageFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.page_fragment_postcard_side, null);
            ImageView image = (ImageView) view.findViewById(R.id.postcard_side);
            String path = getArguments().getString(ARGUMENT_SIDE_PATH);
            if (path != null) {
                imageLoader.displayImage("file://" + path, image, options, null);
                Log.d(Const.LOG_TAG, "load:" + path);
            }
            return view;
        }

    }

}
