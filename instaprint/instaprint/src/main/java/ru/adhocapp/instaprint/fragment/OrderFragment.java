package ru.adhocapp.instaprint.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.EntityManager;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.model.OrdersFragmentPagerAdapter;
import ru.adhocapp.instaprint.util.Const;
import ru.adhocapp.instaprint.util.FontsManager;

/**
 * Created by bshestakov on 09.04.14.
 */

public class OrderFragment extends Fragment {
    private static final String LOGTAG = "OrderFragment";

    private ViewPager pager;

    private EntityManager em;
    private FontsManager mFontsManager;

    private static final Field sChildFragmentManagerField;


    //Костыль для Pager BEGIN
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(Const.LOG_TAG, "Error getting mChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Log.e(Const.LOG_TAG, "Error setting mChildFragmentManager field", e);
            }
        }
    }
    //Костыль для Pager END

    public static OrderFragment newInstance() {
        OrderFragment pageFragment = new OrderFragment();
        return pageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_orders, null);
        pager = (ViewPager) view.findViewById(R.id.pager);
        OrdersFragmentPagerAdapter pagerAdapter = new OrdersFragmentPagerAdapter(getChildFragmentManager());
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(onPageChangeListener);
        pager.setCurrentItem(1);
        em = DBHelper.getInstance(getActivity()).EM;
        return view;
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Log.i(LOGTAG, "OnPageSelected, position = " + position);
            Log.i(LOGTAG, "em.getOrders: " + DBHelper.getInstance(getActivity()).EM.findAll(Order.class));
            switch (position) {
                case 1:
                    break;
                case 3:
                    break;
            }
        }
    };

}
