package myApp.trainingdiary.dialog;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.utils.Consts;

/**
 * Created by Lenovo on 29.09.13.
 */
public class ViewExerciseDialog {
    private AlertDialog dialog;

    public ViewExerciseDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public void show(Exercise exercise) {
        Log.i(Consts.LOG_TAG, "Ex: " + exercise);
        dialog.show();
        final EditText name_edit = (EditText) dialog.findViewById(R.id.name_edit);

        final Spinner type_spinner = (Spinner) dialog
                .findViewById(R.id.type_spinner);

        Log.i(Consts.LOG_TAG, "name_edit: " + name_edit + " type_spinner: " + type_spinner);
        name_edit.setText(exercise.getName());
        selectSpinnerItemByValue(type_spinner, exercise.getType().getId());
        type_spinner.setEnabled(false);
        name_edit.setEnabled(false);
    }

    public void cancel() {
        dialog.cancel();
    }

    public static void selectSpinnerItemByValue(Spinner spnr, long value) {
        SpinnerAdapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItemId(position) == value) {
                spnr.setSelection(position);
                return;
            }
        }
    }

}
