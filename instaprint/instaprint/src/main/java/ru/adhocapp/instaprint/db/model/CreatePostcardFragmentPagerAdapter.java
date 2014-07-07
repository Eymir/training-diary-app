package ru.adhocapp.instaprint.db.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.fragment.CreatePostcardPageFragment;
import ru.adhocapp.instaprint.util.ResourceAccess;

public class CreatePostcardFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 6;
    private FragmentManager fm;

    public CreatePostcardFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return CreatePostcardPageFragment.newInstance(position);
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
                title = ResourceAccess.getInstance().getResources().getString(R.string.graphics);
                break;
            case 2:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_edit_text);
                break;
            case 3:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_edit_address);
                break;
            case 4:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_preview);
                break;
            case 5:
                title = ResourceAccess.getInstance().getResources().getString(R.string.page_title_result);
                break;
        }
        return title;
    }
}