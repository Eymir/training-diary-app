package myApp.trainingdiary.training;

import myApp.trainingdiary.R;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TrainingDialogBuilder {
	private Context context;
	public TrainingDialogBuilder(Context context) {
		this.context = context;
	}
	
	private Dialog createCreateTrDialog() {
		Dialog createTrainingDialog = new Dialog(context);
		createTrainingDialog.setContentView(R.layout.input_name_dialog);
		createTrainingDialog.setTitle(R.string.title_create_training);
		final EditText name_input = (EditText) createTrainingDialog
				.findViewById(R.id.name_input);
		Button okButton = (Button) createTrainingDialog
				.findViewById(R.id.ok_button);
		okButton.setText(R.string.create_button);
		Button cancelButton = (Button) createTrainingDialog
				.findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				createTrainingDialog.cancel();
			}
		});

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (name_input.getText().length() > 0) {
//					String name = name_input.getText().toString();
////					SQLiteDatabase db = dbHelper.getWritableDatabase();
////					int count = dbHelper.getTrainingsCount(db);
//					dbHelper.insertTraining(db, name, count);
//					db.close();
//					createTrainingDialog.cancel();
//					Toast.makeText(TrainingActivity.this,
//							R.string.create_success, Toast.LENGTH_SHORT).show();
//					refreshTrainings();
//				} else {
//					Toast.makeText(TrainingActivity.this,
//							R.string.zero_input_notif, Toast.LENGTH_SHORT)
//							.show();
//				}
			}
		});
		return createTrainingDialog;
	}
	
}
