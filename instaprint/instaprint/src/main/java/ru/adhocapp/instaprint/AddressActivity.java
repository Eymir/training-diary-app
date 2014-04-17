package ru.adhocapp.instaprint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;

import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.dialog.CreateEditAddressFragmentDialog;
import ru.adhocapp.instaprint.dialog.MapPositiveNegativeClickListener;
import ru.adhocapp.instaprint.dialog.ObjectClickListener;
import ru.adhocapp.instaprint.util.AddressAdapter;
import ru.adhocapp.instaprint.util.Const;

public class AddressActivity extends FragmentActivity {

    private DBHelper dbHelper;
    private ListView listView;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);
        dbHelper = DBHelper.getInstance(this);

        listView = (ListView) findViewById(R.id.list_view);
        createAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Address address = (Address) adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(Const.ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void createAdapter() {
        adapter = new AddressAdapter(this, dbHelper.EM.findAll(Address.class), new ObjectClickListener() {
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
                dialog.show(getFragmentManager(), "");
            }
        }, new ObjectClickListener() {
            @Override
            public void positiveClick(Object o) {
                final Address address = (Address) o;
                AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
