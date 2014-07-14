package ru.adhocapp.instaprint.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by Lenovo on 17.03.14.
 */
public class PostcardOrderValidtionFragmentDialog extends DialogFragment {

    private MapPositiveNegativeClickListener clickListener;
    private Map<ValidationKey, ValidationValue> validationMap;

    public enum ValidationKey {RESULT, POSTCARD_FRONT_PHOTO, POSTCARD_MESSAGE, ADDRESS_TO}

    public enum ValidationValue {OK, WARNING, ERROR}

    public PostcardOrderValidtionFragmentDialog() {
    }

    public static PostcardOrderValidtionFragmentDialog newInstance(MapPositiveNegativeClickListener listener, Map<ValidationKey, ValidationValue> validationMap) {
        PostcardOrderValidtionFragmentDialog f = new PostcardOrderValidtionFragmentDialog();
        f.setClickListener(listener);
        f.setValidationMap(validationMap);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final GridLayout linearLayout =
                (GridLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_postcard_order_validation, null);

        ImageView photoMark = (ImageView) linearLayout.findViewById(R.id.ib_front_photo_valid_mark);
        ImageView addressMark = (ImageView) linearLayout.findViewById(R.id.ib_address_to_valid_mark);
        ImageView messageMark = (ImageView) linearLayout.findViewById(R.id.ib_message_valid_mark);

        TextView photoText = (TextView) linearLayout.findViewById(R.id.tv_front_photo_valid_text);
        TextView addressText = (TextView) linearLayout.findViewById(R.id.tv_address_to_valid_text);
        TextView messageText = (TextView) linearLayout.findViewById(R.id.tv_message_valid_text);

        int positiveButtonString = R.string.send_button;

        for (ValidationKey key : validationMap.keySet()) {
            ValidationValue value = validationMap.get(key);
            switch (key) {
                case ADDRESS_TO:
                    setMark(addressMark, value);
                    setText(addressText, key, value);
                    break;
                case POSTCARD_FRONT_PHOTO:
                    setMark(photoMark, value);
                    setText(photoText, key, value);
                    break;
                case POSTCARD_MESSAGE:
                    setMark(messageMark, value);
                    setText(messageText, key, value);
                    break;
                case RESULT:
                    Log.d(Const.LOG_TAG, "RESULT: " + value.name());
                    switch (value) {
                        case OK:
                        case WARNING:
                            positiveButtonString = R.string.send_button;
                            break;
                        case ERROR:
                            positiveButtonString = R.string.choose_address;
                            break;
                    }
                    break;
            }
        }

        final int pb = positiveButtonString;
        return builder.setTitle(R.string.validation_dialog)
                .setView(linearLayout)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickListener.negativeClick();
                    }
                }).setPositiveButton(pb, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        if (pb == R.string.choose_address)
                            map.put("RESULT", "choose_address");
                        clickListener.positiveClick(map);
                    }
                }).create();
    }

    private void setText(TextView text, ValidationKey key, ValidationValue value) {
        switch (key) {
            case ADDRESS_TO:
                switch (value) {
                    case OK:
                        text.setText(R.string.address_to_chosen);
                        break;
                    case ERROR:
                    case WARNING:
                        text.setText(R.string.address_to_not_chosen);
                        break;
                }
                break;
            case POSTCARD_FRONT_PHOTO:
                switch (value) {
                    case OK:
                        text.setText(R.string.photo_chosen);
                        break;
                    case ERROR:
                    case WARNING:
                        text.setText(R.string.photo_not_chosen);
                        break;
                }
                break;
            case POSTCARD_MESSAGE:
                switch (value) {
                    case OK:
                        text.setText(R.string.message_chosen);
                        break;
                    case ERROR:
                    case WARNING:
                        text.setText(R.string.message_not_chosen);
                        break;
                }
                break;
        }
    }

    private void setMark(ImageView mark, ValidationValue value) {
        switch (value) {
            case OK:
                mark.setImageResource(R.drawable.ic_check);
                break;
            case ERROR:
                mark.setImageResource(R.drawable.ic_cross);
                break;
            case WARNING:
                mark.setImageResource(R.drawable.ic_warning);
                break;
        }
    }


    public void setClickListener(MapPositiveNegativeClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public MapPositiveNegativeClickListener getClickListener() {
        return clickListener;
    }

    private void saveUserData(String username, String email) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (username != null && !username.isEmpty()) {
            editor.putString(getString(R.string.username_property), username);
        }
        editor.putString(getString(R.string.useremail_property), email);
        editor.commit();
    }

    public void setValidationMap(Map<ValidationKey, ValidationValue> validationMap) {
        this.validationMap = validationMap;
    }

    public Map<ValidationKey, ValidationValue> getValidationMap() {
        return validationMap;
    }
}
