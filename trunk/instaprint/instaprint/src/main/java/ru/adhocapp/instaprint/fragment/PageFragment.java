package ru.adhocapp.instaprint.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.adhocapp.instaprint.R;

/**
 * Created by malugin on 09.04.14.
 */

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    int pageNumber;

    public static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_fragment_select_foto, null);

        switch (pageNumber) {
            case 0:
                view = inflater.inflate(R.layout.page_fragment_select_foto, null);
                break;
            case 1:
                view = inflater.inflate(R.layout.page_fragment_edit_text, null);
                break;
            case 2: {
                view = inflater.inflate(R.layout.page_fragment_edit_address, null);
                View v_to = view.findViewById(R.id.address_to);
                TextView textView = (TextView) v_to.findViewById(R.id.contact_title);
                textView.setText(R.string.address_to_no_named);
                break;
            }
            case 3: {
                view = inflater.inflate(R.layout.page_fragment_preview, null);
                break;
            }
            case 4: {
                view = inflater.inflate(R.layout.page_fragment_result, null);
                break;
            }
        }

        return view;
    }
}
