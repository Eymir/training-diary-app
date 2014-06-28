package ru.adhocapp.instaprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.util.Const;

public class AddressActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int address_type = getIntent().getIntExtra(Const.ADDRESS_TYPE, -1);
                Log.d(Const.LOG_TAG, "getIntent().getIntExtra(Const.ADDRESS_TYPE, -1): " + address_type);
                Address address = (Address) parent.getAdapter().getItem(position);
                Intent intent = new Intent();
                intent.putExtra(Const.ADDRESS, address);
                intent.putExtra(Const.ADDRESS_TYPE, address_type);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
