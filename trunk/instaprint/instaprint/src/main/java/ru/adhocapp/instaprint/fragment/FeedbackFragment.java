package ru.adhocapp.instaprint.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.FeedbackMessage;
import ru.adhocapp.instaprint.mail.MailHelper;
import ru.adhocapp.instaprint.mail.SendFinishListener;

/**
 * Created by malugin on 09.04.14.
 */

public class FeedbackFragment extends Fragment implements View.OnClickListener, SendFinishListener {
    private EditText clientEmailEdit;
    private EditText clientUsernameEdit;
    private EditText clientMessageEdit;
    private Button sendButton;
    private Button clearButton;

    private ArrayAdapter adapter;
    private DBHelper dbHelper;

    public static FeedbackFragment newInstance() {
        FeedbackFragment pageFragment = new FeedbackFragment();
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_feedback, container, false);
        clientUsernameEdit = (EditText) view.findViewById(R.id.client_username);
        clientEmailEdit = (EditText) view.findViewById(R.id.client_email);
        clientMessageEdit = (EditText) view.findViewById(R.id.client_message);
        sendButton = (Button) view.findViewById(R.id.send_button);
        clearButton = (Button) view.findViewById(R.id.clear_button);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username_property), null);
        String email = sharedPref.getString(getString(R.string.useremail_property), null);
        if (username != null && !username.isEmpty()) {
            clientUsernameEdit.setText(username);
        }
        if (email != null && !email.isEmpty()) {
            clientEmailEdit.setText(email);
        }
        clearButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button: {
                boolean error = validate();
                if (error) {
                    return;
                }
                saveUserData();
                sendEmail();
            }
            break;
            case R.id.clear_button: {
                clientUsernameEdit.setText("");
                clientEmailEdit.setText("");
                clientMessageEdit.setText("");
            }
            break;
        }
    }

    private void sendEmail() {
        MailHelper.getInstance().sendFeedbackMail(null, new FeedbackMessage(clientUsernameEdit.getText().toString(),
                clientEmailEdit.getText().toString(), clientMessageEdit.getText().toString()));
    }

    private boolean validate() {
        boolean error = false;
        if (clientUsernameEdit.getText().toString().length() == 0) {
            clientUsernameEdit.setError(getResources().getString(R.string.validation_cannot_be_empty));
            error = true;
        }
        String email = clientEmailEdit.getText().toString();
        if (email.toString().length() == 0 || !isValidEmail(email)) {
            clientEmailEdit.setError(getResources().getString(R.string.validation_email));
            error = true;
        }
        if (clientMessageEdit.getText().toString().length() == 0) {
            clientMessageEdit.setError(getResources().getString(R.string.validation_cannot_be_empty));
            error = true;
        }
        return error;
    }

    private void saveUserData() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username_property), clientUsernameEdit.getText().toString());
        editor.putString(getString(R.string.useremail_property), clientEmailEdit.getText().toString());
        editor.commit();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void finish(Boolean result) {
        if (result) {
            Toast.makeText(getActivity(), getString(R.string.feedback_message_successfuly_sent), Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getActivity(), getString(R.string.feedback_message_sending_error), Toast.LENGTH_SHORT);
        }
    }
}
