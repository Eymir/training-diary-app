package myApp.trainingdiary.dialog;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import myApp.trainingdiary.R;
import myApp.trainingdiary.utils.Const;

/**
 * Created by Lenovo on 29.09.13.
 */
public class EditDialog {
    private AlertDialog dialog;

    public EditDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public void show(String name) {
        Log.i(Const.LOG_TAG, "name: " + name);
        dialog.show();
        final EditText name_edit = (EditText) dialog.findViewById(R.id.name_input);
        name_edit.setText(name);
    }

    public void show() {
        dialog.show();
        final EditText name_edit = (EditText) dialog.findViewById(R.id.name_input);
        name_edit.setText("");
    }

    public void cancel() {
        dialog.cancel();
    }

}
