package myApp.trainingdiary.training;

import myApp.trainingdiary.R;
import myApp.trainingdiary.utils.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DialogProvider {
    private Context context;

    public DialogProvider(Context context) {
        this.context = context;
    }

    public static AlertDialog createInputTextDialog(final Activity activity, String title, String positiveTitle, String negativeTitle, final Validator validator, final InputTextDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.input_name_dialog, null);

        final EditText editText = (EditText) view.findViewById(R.id.name_input);
        builder.setView(view);

        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                assert editText != null;
                if (validator.validate(activity, editText.getText().toString())) {
                    listener.onPositiveClick(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton(negativeTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClick();
            }
        });

        return builder.create();
    }

    public static AlertDialog createSimpleDialog(final Activity activity, String title, String positiveTitle, String negativeTitle, final SimpleDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveClick();
            }
        });
        builder.setNegativeButton(negativeTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClick();
            }
        });
        return builder.create();
    }

    public static interface InputTextDialogClickListener {
        void onPositiveClick(String text);

        void onNegativeClick();
    }

    public static interface SimpleDialogClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }
}
