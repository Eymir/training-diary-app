package ru.adhocapp.instaprint.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.ResourceAccess;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 4;
    private FragmentManager fm;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
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
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_select_foto);
                break;
            case 1:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_edit_text);
                break;
            case 2:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_edit_address);
                break;
            case 3:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_result);
                break;
        }
        return title;
    }

}