package myApp.trainingdiary.training;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class TrainingGuiBindMediator {
	private Context context;

	public TrainingGuiBindMediator(Context context) {
		this.context = context;
	}

	public void bindCreateTrainingButton(ImageButton addButton) {
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				createTrainingDialog.show();
			}
		});
	}

}
