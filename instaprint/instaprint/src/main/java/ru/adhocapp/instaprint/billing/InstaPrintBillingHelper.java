package ru.adhocapp.instaprint.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by Lenovo on 01.07.2014.
 */
public class InstaPrintBillingHelper {
    private IabHelper mHelper;
    private Context context;

    private static volatile InstaPrintBillingHelper instance;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(Const.LOG_TAG, "Query inventory finished.");

            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.d(Const.LOG_TAG, "Failed to query inventory:" + result);
                return;
            }

            Log.d(Const.LOG_TAG, "Query inventory was successful.");

            Purchase note_1 = inventory.getPurchase(Const.PURCHASE_NOTE_TAG_1);
            if (note_1 != null) {
                Log.d(Const.LOG_TAG, "We have purchase PURCHASE_NOTE_TAG_1. Consuming it");
                mHelper.consumeAsync(inventory.getPurchase(Const.PURCHASE_NOTE_TAG_1), mConsumeFinishedListener);
                return;
            } else {

            }
            Log.d(Const.LOG_TAG, "Initial inventory query finished");
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(Const.LOG_TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if (mHelper == null) return;
            if (result.isSuccess()) {
            } else {
                Log.d(Const.LOG_TAG, "Error while consuming: " + result);
            }
            Log.d(Const.LOG_TAG, "End consumption flow.");
        }
    };

    private InstaPrintBillingHelper(Context context) {
        this.context = context;
        billingInit(context);
    }

    public static InstaPrintBillingHelper initInstance(Context context) {
        InstaPrintBillingHelper localInstance = instance;
        if (localInstance == null) {
            synchronized (InstaPrintBillingHelper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new InstaPrintBillingHelper(context);
                }
            }
        }
        return localInstance;
    }

    public static InstaPrintBillingHelper getInstance() {
        return initInstance(null);
    }

    private void billingInit(Context context) {
        mHelper = new IabHelper(context, Const.BASE64_PUBLIC_KEY);
        mHelper.enableDebugLogging(Const.IAB_DEBUG_LOGGING);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(Const.LOG_TAG, "billingInit.onIabSetupFinished");
                if (!result.isSuccess()) {
                    Log.d(Const.LOG_TAG, "!result.isSuccess()=true");
                    return;
                }
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    public void buyPurchase(Activity activity, IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener) {
        try {
            mHelper.launchPurchaseFlow(activity, Const.PURCHASE_NOTE_TAG_1, Const.RC_REQUEST,
                    mPurchaseFinishedListener, "");
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            Toast.makeText(activity, R.string.purchase_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;
        if (mHelper.handleActivityResult(requestCode, resultCode, data)) {
            Log.d(Const.LOG_TAG, "onActivityResult handled by IABUtil.");
        } else {
            Log.d(Const.LOG_TAG, "onActivityResult NOT handled by IABUtil.");
        }
    }

}
