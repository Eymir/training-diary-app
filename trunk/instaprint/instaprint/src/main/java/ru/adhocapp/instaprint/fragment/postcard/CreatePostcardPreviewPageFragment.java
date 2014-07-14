package ru.adhocapp.instaprint.fragment.postcard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardPreviewPageFragment extends Fragment {


    public static CreatePostcardPreviewPageFragment newInstance() {
        CreatePostcardPreviewPageFragment createPostcardPageFragment = new CreatePostcardPreviewPageFragment();
        return createPostcardPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Const.LOG_TAG, "CreatePostcardPageFragment.ParentFragment: " + (CreatePostcardMainFragment) getParentFragment());
        final CreatePostcardMainFragment parent = (CreatePostcardMainFragment) getParentFragment();
        View view = inflater.inflate(R.layout.page_fragment_preview, null);
        return view;
    }


}
