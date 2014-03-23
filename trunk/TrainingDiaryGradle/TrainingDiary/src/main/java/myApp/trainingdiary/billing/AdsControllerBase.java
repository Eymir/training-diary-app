package myApp.trainingdiary.billing;

import android.widget.LinearLayout;

/**
 * Created by malugin on 23.03.14.
 */
public interface AdsControllerBase {

    public void createView(LinearLayout layout);
    public void show(boolean show);
    public void onStart();
    public void onDestroy();
    public void onResume();
    public void onStop();

}
