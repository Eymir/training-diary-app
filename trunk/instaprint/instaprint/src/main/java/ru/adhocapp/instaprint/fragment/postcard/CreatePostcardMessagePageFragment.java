package ru.adhocapp.instaprint.fragment.postcard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ru.adhocapp.instaprint.R;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardMessagePageFragment extends Fragment {

    private String currentFont;
    private String messageText;

    public static CreatePostcardMessagePageFragment newInstance() {
        CreatePostcardMessagePageFragment createPostcardPageFragment = new CreatePostcardMessagePageFragment();
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
        View view = inflater.inflate(R.layout.page_fragment_edit_text, null);
        return view;
    }

    public void setCurrentFont(String currentFont) {
        this.currentFont = currentFont;
    }

    public String getCurrentFont() {
        return currentFont;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }
}
