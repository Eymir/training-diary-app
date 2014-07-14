package ru.adhocapp.instaprint.fragment.postcard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardEditAddressPageFragment extends Fragment {


    public static CreatePostcardEditAddressPageFragment newInstance() {
        CreatePostcardEditAddressPageFragment createPostcardPageFragment = new CreatePostcardEditAddressPageFragment();
        return createPostcardPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final CreatePostcardMainFragment parent = (CreatePostcardMainFragment) getParentFragment();
        View view = inflater.inflate(R.layout.page_fragment_edit_address, null);
        View v_to = view.findViewById(R.id.address_to);
        TextView textView = (TextView) v_to.findViewById(R.id.contact_title);
        textView.setText(R.string.address_to_no_named);
        return view;
    }
}
