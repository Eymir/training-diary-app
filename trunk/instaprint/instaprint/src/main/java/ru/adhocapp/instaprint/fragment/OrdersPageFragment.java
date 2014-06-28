package ru.adhocapp.instaprint.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import ru.adhocapp.instaprint.OrderDetailsActivity;
import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.model.DataConverter;
import ru.adhocapp.instaprint.db.model.OrderListClickListener;
import ru.adhocapp.instaprint.db.model.OrdersAdapter;
import ru.adhocapp.instaprint.db.model.data.OrderItem;
import ru.adhocapp.instaprint.util.Const;

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
        OrderListClickListener orderListClickListener = new OrderListClickListener() {
            @Override
            public void onContextButtonClick(Order order) {
                switch (order.getStatus()) {
                    case CREATING:
                        break;
                    case PAYING:
                        break;
                    case PRINTING_AND_SNAILMAILING:
                        break;
                    case EMAIL_SENDING:
                        break;
                    case EXECUTED:
                        break;
                }
            }
        };
        switch (pageNumber) {
            case 0: {
                adapter = createRoughDraftListAdapter(orderListClickListener);
            }
            break;
            case 1: {
                adapter = createOrdersInProcessListAdapter(orderListClickListener);
            }
            break;
            case 2: {
                adapter = createOrdersHistoryListAdapter(orderListClickListener);
            }
            break;

        }
        final ListAdapter _adapter = adapter;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderItem item = (OrderItem) _adapter.getItem(i);
                if (!item.getIsCategory()) {
                    Order o = item.getOrder();
                    Intent intent = new Intent(OrdersPageFragment.this.getActivity(), OrderDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(Const.ORDER, o);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(adapter);
        return view;
    }

    private ListAdapter createOrdersHistoryListAdapter(OrderListClickListener orderListClickListener) {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.EXECUTED), false), orderListClickListener);
        return adapter;
    }

    private ListAdapter createOrdersInProcessListAdapter(OrderListClickListener orderListClickListener) {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.PAYING, OrderStatus.EMAIL_SENDING), true), orderListClickListener);
        return adapter;
    }

    private ListAdapter createRoughDraftListAdapter(OrderListClickListener orderListClickListener) {
        OrdersAdapter adapter = new OrdersAdapter(getActivity(), DataConverter.toDataItems(dbHelper.READ.getOrdersWithStatus(OrderStatus.CREATING), false), orderListClickListener);
        return adapter;
    }
}
