package ru.adhocapp.instaprint.fragment.postcard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;
import ru.adhocapp.instaprint.util.FrameManager;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardGraphicsPageFragment extends Fragment {


    public static CreatePostcardGraphicsPageFragment newInstance() {
        CreatePostcardGraphicsPageFragment createPostcardPageFragment = new CreatePostcardGraphicsPageFragment();
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
        View view = inflater.inflate(R.layout.page_fragment_graphics, null);
        return view;
    }




}
