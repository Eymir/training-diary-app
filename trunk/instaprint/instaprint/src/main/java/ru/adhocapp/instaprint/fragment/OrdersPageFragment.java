package ru.adhocapp.instaprint.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.model.DataConverter;
import ru.adhocapp.instaprint.db.model.OrdersAdapter;

/**
 * Created by malugin on 09.04.14.
 */

public class OrdersPageFragment extends Fragment {

    public static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private DBHelper dbHelper;
    private int pageNumber;

    public static OrdersPageFragment newInstance(int page) {
        OrdersPageFragment createPostcardPageFragment = new OrdersPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        createPostcardPageFragment.setArguments(arguments);
        return createPostcardPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        dbHelper = DBHelper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_fragment_order_list, null);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        ListAdapter adapter = null;
        switch (pageNumber) {
            case 0: {
                adapter = createRoughDraftListAdapter();
            }
            break;
            case 1: {
                adapter = createOrdersInProcessListAdapter();
            }
            break;
            case 2: {
                adapter = createOrdersHistoryListAdapter();
            }
            break;

        }
        listView.setAdapter(adapter);
        return view;
    }

    private ListAdapter createOrdersHistoryListAdapter() {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.PAYING, OrderStatus.EXECUTED), false));
        return adapter;
    }

    private ListAdapter createOrdersInProcessListAdapter() {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.PAYING, OrderStatus.EMAIL_SENDING), true));
        return adapter;
    }

    private ListAdapter createRoughDraftListAdapter() {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.CREATING), false));
        return adapter;
    }
}
