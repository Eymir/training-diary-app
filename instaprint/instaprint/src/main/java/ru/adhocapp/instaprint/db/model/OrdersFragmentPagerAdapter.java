package ru.adhocapp.instaprint.db.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.fragment.OrdersPageFragment;
import ru.adhocapp.instaprint.util.ResourceAccess;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    private FragmentManager fm;

    public OrdersFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return OrdersPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_rough_draft);
                break;
            case 1:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_proccess_orders);
                break;
            case 2:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_orders_history);
                break;
        }
        return title;
    }

}