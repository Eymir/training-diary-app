package myApp.trainingdiary.billing;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * Created by malugin on 20.03.14.
 */
public class AdMobController implements AdsControllerBase, AdListener {

    private static final String ADMOB_ID = "ca-app-pub-6771093204089983/7896431558";
    private static final int REQUEST_TIMEOUT = 30000;
    private AdView adView;
    private Context c;
    private long last;

    public AdMobController(Context activity, LinearLayout layout) {

        this.c = activity;
        createView(layout);
        last = System.currentTimeMillis() - REQUEST_TIMEOUT;

    }

   @Override
    public void createView(LinearLayout layout) {

        if(BillingPreferencesHelper.isAdsDisabled()) return;
        adView = new AdView((Activity) c, AdSize.BANNER, ADMOB_ID);
//        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        adView.setAdListener(this);
        //layout.addView(adView, adParams);
        layout.addView(adView);
        adView.loadAd(new AdRequest());
    }

    @Override
    public void show(boolean show) {

        if(adView == null) return;
        adView.setVisibility((show) ? View.VISIBLE : View.GONE);
        if (show && (System.currentTimeMillis() - last > REQUEST_TIMEOUT)) {
            last = System.currentTimeMillis();
            adView.loadAd(new AdRequest());
        }

    }

    @Override
    public void onReceiveAd(Ad ad) {

    }

    @Override
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {

    }

    @Override
    public void onPresentScreen(Ad ad) {

    }

    @Override
    public void onDismissScreen(Ad ad) {

    }

    @Override
    public void onLeaveApplication(Ad ad) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

}
