package ru.adhocapp.instaprint.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.model.AddressAdapter;
import ru.adhocapp.instaprint.dialog.CreateEditAddressFragmentDialog;
import ru.adhocapp.instaprint.dialog.MapPositiveNegativeClickListener;
import ru.adhocapp.instaprint.dialog.ObjectClickListener;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by malugin on 09.04.14.
 */

public class AddressListFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter adapter;
    private DBHelper dbHelper;

    public static AddressListFragment newInstance() {
        AddressListFragment pageFragment = new AddressListFragment();
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment_address_list, null);
        listView = (ListView) view.findViewById(R.id.list_view);
        createAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    public void createAdapter() {
        adapter = new AddressAdapter(getActivity(), dbHelper.EM.findAll(Address.class), new ObjectClickListener() {
            @Override
            public void positiveClick(Object o) {
                Address address = (Address) o;
                CreateEditAddressFragmentDialog dialog = CreateEditAddressFragmentDialog.newInstance(address, new MapPositiveNegativeClickListener() {
                    @Override
                    public void positiveClick(Map<String, Object> map) {
                        Address ad = (Address) map.get(Const.ADDRESS);
                        dbHelper.EM.merge(ad);
                        createAdapter();
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void negativeClick() {
                    }
                });
                dialog.show(getChildFragmentManager(), "");
            }
        }, new ObjectClickListener() {
            @Override
            public void positiveClick(Object o) {
                final Address address = (Address) o;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.remove_address).setMessage(R.string.remove_address_message).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.remove_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.EM.remove(address);
                                createAdapter();
                                listView.setAdapter(adapter);
                            }
                        }
                ).create().show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_contact_action:
                CreateEditAddressFragmentDialog dialog = CreateEditAddressFragmentDialog.newInstance(new MapPositiveNegativeClickListener() {
                    @Override
                    public void positiveClick(Map<String, Object> map) {
                        Address ad = (Address) map.get(Const.ADDRESS);
                        dbHelper.EM.persist(ad);
                        createAdapter();
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void negativeClick() {
                    }
                });
                dialog.show(getFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
