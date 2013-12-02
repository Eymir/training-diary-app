package myApp.trainingdiary.excercise;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import myApp.trainingdiary.R;

/**
 * ����� ��� ������ �������������� ��������� ����������, �������� - ����������
 * ����������� �������
 *
 * @author Boris
 */
public class AddExGuiMediator {
    // Layout-� �� ����� �� ������� ������������ ��� ���������� ������
    // �������\���������� ����������
    private View createClickableLayout;
    private View chooseClickableLayout;

    // ������ �������� ������ ����������
    private View createExGonablePanel;
    private View chooseExGonablePanel;

    // ������ ������������ ������� �� ������
    private ImageView createExpandIndicator;
    private ImageView chooseExpandIndicator;

    private EditText name_edit;

    public AddExGuiMediator(Context context, EditText name_edit, View createClickableLayout,
                            View chooseClickableLayout, View createExGonablePanel,
                            View chooseExGonablePanel) {
        super();
        this.createClickableLayout = createClickableLayout;
        this.chooseClickableLayout = chooseClickableLayout;
        this.createExGonablePanel = createExGonablePanel;
        this.chooseExGonablePanel = chooseExGonablePanel;
        this.name_edit = name_edit;
//        createExpandIndicator = (ImageView) createClickableLayout
//                .findViewById(R.id.cr_ex_expand_indicator);
//        chooseExpandIndicator = (ImageView) chooseClickableLayout
//                .findViewById(R.id.add_ex_expand_indicator);

        setExpandColapseBehavior();
    }

    private void setExpandColapseBehavior() {

        chooseClickableLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chooseExGonablePanel.getVisibility() != View.VISIBLE) {
                    chooseExGonablePanel.setVisibility(View.VISIBLE);
                    createExGonablePanel.setVisibility(View.GONE);
                    createExpandIndicator.setImageResource(R.drawable.arrow_exp_right);
                    chooseExpandIndicator.setImageResource(R.drawable.arrow_exp_down);
                    name_edit.requestFocus();
                }
            }
        });

        createClickableLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (createExGonablePanel.getVisibility() != View.VISIBLE) {
                    chooseExGonablePanel.setVisibility(View.GONE);
                    createExGonablePanel.setVisibility(View.VISIBLE);
                    createExpandIndicator.setImageResource(R.drawable.arrow_exp_down);
                    chooseExpandIndicator.setImageResource(R.drawable.arrow_exp_right);
                }
            }
        });

    }

    public void clickChoosePanel() {
        chooseClickableLayout.performClick();
    }

    public void clickCreatePanel() {
        createClickableLayout.performClick();
    }
}
