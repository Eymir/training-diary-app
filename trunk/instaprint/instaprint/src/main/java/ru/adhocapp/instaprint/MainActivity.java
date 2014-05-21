package ru.adhocapp.instaprint;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.fragment.AddressListFragment;
import ru.adhocapp.instaprint.fragment.CreatePostcardFragment;
import ru.adhocapp.instaprint.fragment.XmlClickable;
import ru.adhocapp.instaprint.util.ResourceAccess;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper.getInstance(this);
        ResourceAccess.getInstance(this);

        fragments.add(CreatePostcardFragment.newInstance());
        fragments.add(CreatePostcardFragment.newInstance());
        fragments.add(AddressListFragment.newInstance());
        fragments.add(AddressListFragment.newInstance());

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.action_list,
                    android.R.layout.simple_spinner_dropdown_item);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragments.get(itemPosition)).commit();
        currentPosition = itemPosition;
        return true;
    }

    public void myClickMethod(View v) {
        ((XmlClickable) fragments.get(currentPosition)).myClickMethod(v);
    }

}
