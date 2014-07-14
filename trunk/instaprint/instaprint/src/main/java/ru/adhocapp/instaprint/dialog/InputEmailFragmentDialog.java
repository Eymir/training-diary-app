package ru.adhocapp.instaprint.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by Lenovo on 17.03.14.
 */
public class InputEmailFragmentDialog extends DialogFragment {

    private EditText clientEmailEdit;
    private EditText clientUsernameEdit;
    private MapPositiveNegativeClickListener clickListener;

    public InputEmailFragmentDialog() {
    }

    public static InputEmailFragmentDialog newInstance(MapPositiveNegativeClickListener listener) {
        InputEmailFragmentDialog f = new InputEmailFragmentDialog();
        f.setClickListener(listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LinearLayout linearLayout =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_fill_email, null);
        clientUsernameEdit = (EditText) linearLayout.findViewById(R.id.client_username);
        clientEmailEdit = (EditText) linearLayout.findViewById(R.id.client_email);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username_property), null);
        String email = sharedPref.getString(getString(R.string.useremail_property), null);
        if (username != null && !username.isEmpty()) {
            clientUsernameEdit.setText(username);
        }
        if (email != null && !email.isEmpty()) {
            clientEmailEdit.setText(email);
        }

        return builder.setTitle(getResources().getString(R.string.send_poscard_to_email))
                .setView(linearLayout)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickListener.negativeClick();
                    }
                }).setPositiveButton(getResources().getString(R.string.send_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Const.LOG_TAG, "onStart");
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                Log.d(Const.LOG_TAG, "positiveButton != null");
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(Const.LOG_TAG, "onClick");
                        EditText uEdit = (EditText) d.findViewById(R.id.client_username);
                        EditText cEdit = (EditText) d.findViewById(R.id.client_email);
                        String username = uEdit.getText().toString();
                        String email = cEdit.getText().toString();
                        Log.d(Const.LOG_TAG, "username: " + username + " email: " + email);
                        if (!validate(email)) {
                            Log.d(Const.LOG_TAG, "validate");
                            Map<String, Object> map = new HashMap<String, Object>();
                            if (username != null && !username.isEmpty()) {
                                map.put("USERNAME", username);
                            }
                            map.put("EMAIL", email);
                            saveUserData(username, email);
                            Log.d(Const.LOG_TAG, "before dismiss");
                            d.dismiss();
                            Log.d(Const.LOG_TAG, "after dismiss");
                            clickListener.positiveClick(map);
                        }
                    }
                });
            }
        }
    }

    private boolean validate(String email) {
        boolean error = false;
        if (email.length() == 0 || !isValidEmail(email)) {
            clientEmailEdit.setError(getResources().getString(R.string.validation_email));
            error = true;
        }
        return error;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
}
