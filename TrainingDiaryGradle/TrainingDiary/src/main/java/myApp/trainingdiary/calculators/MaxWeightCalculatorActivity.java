package myApp.trainingdiary.calculators;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import myApp.trainingdiary.R;

public class MaxWeightCalculatorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_weight_calculator);

        //google analytics
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().setContext(this);

    }

    public void onPressCalculateButton(View view){

        EditText etWeight = (EditText)findViewById(R.id.et_maxweight_weight);
        EditText etRepeat = (EditText)findViewById(R.id.et_maweight_repeat);

        if(etWeight.getText().length() == 0){
            Toast.makeText(this, R.string.no_weight, Toast.LENGTH_SHORT).show();
            return;
        }

        if(etRepeat.getText().length() == 0){
            Toast.makeText(this, R.string.no_repeat, Toast.LENGTH_SHORT).show();
            return;
        }

        int weight = Integer.parseInt(etWeight.getText().toString());
        int repeat = Integer.parseInt(etRepeat.getText().toString());

        if(weight <= 0){
            Toast.makeText(this, R.string.no_weight, Toast.LENGTH_SHORT).show();
            return;
        }
        if(repeat <= 0){
            Toast.makeText(this, R.string.no_repeat, Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        Расчет максимума:
        Формула Бржыки
        1ПМ = вес /(1.0278 - 0.0278 * повторения)

        Формула Эйпли
        1ПМ = (вес * повторения * 0.033)+ вес

        Формула Лэндера
        1ПМ = вес /(1.013 - 0.0267123 * повторения)
        */

        int maxBrijka = (int) (weight/(1.028-0.0278*repeat));
        int maxEpli = (int) ((weight*repeat*0.033)+weight);
        int maxLender = (int) (weight/(1.013-0.0267123*repeat));

        TextView tvResult = (TextView)findViewById(R.id.tv_maxweight_result);
        String result = R.string.by_brizka+" "+maxBrijka+"\n"+
                R.string.by_epli+" "+maxEpli+"\n"+
                R.string.by_lender+" "+maxLender;
        tvResult.setText(result);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().activityStop(this);
    }

}
