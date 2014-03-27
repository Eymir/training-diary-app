package myApp.trainingdiary.billing;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;

import myApp.trainingdiary.R;
import myApp.trainingdiary.billing.util.IabHelper;
import myApp.trainingdiary.billing.util.IabResult;
import myApp.trainingdiary.billing.util.Inventory;
import myApp.trainingdiary.billing.util.Purchase;
import myApp.trainingdiary.utils.Const;


/**
 * Created by root on 27.03.14.
 */
public class BillingHelper {

    private Context context;
    private static BillingHelper instance;
    private IabHelper mHelper;
    private AdsControllerBase ads;

    public BillingHelper(Context c)  {
        context = c;

    }

    public static BillingHelper getInstance(Context ctx){
        if (instance == null){
            instance = new BillingHelper(ctx);
        }
        return instance;
    }

    public void adsShow(LinearLayout adsLayout){
        BillingPreferencesHelper.loadSettings(context);
        billingInit();
        ads = new AdMobController(context, adsLayout);
        ads.show(!BillingPreferencesHelper.isAdsDisabled());
    }

    private void billingInit() {
        mHelper = new IabHelper(context, Const.BASE64_PUBLIC_KEY);
        // включаем дебагинг (в релизной версии ОБЯЗАТЕЛЬНО выставьте в false)
        mHelper.enableDebugLogging(context.getResources().getBoolean(R.bool.iab_debug_logging));
        // инициализируем; запрос асинхронен
        // будет вызван, когда инициализация завершится
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                // чекаем уже купленное
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    // Слушатель для востановителя покупок.
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                return;
            }
			/*
			 * Проверяются покупки. Обратите внимание, что надо проверить каждую
			 * покупку, чтобы убедиться, что всё норм! см.
			 * verifyDeveloperPayload().
			 */
            Purchase purchase = inventory.getPurchase(Const.SKU_ADS_DISABLE);
            BillingPreferencesHelper.savePurchase(context,
                    BillingPreferencesHelper.Purchase.DISABLE_ADS, purchase != null
                    && verifyDeveloperPayload(purchase));
            ads.show(!BillingPreferencesHelper.isAdsDisabled());
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
		/*
		 * TODO: здесь необходимо свою верификацию реализовать Хорошо бы ещё с
		 * использованием собственного стороннего сервера.
		 */
        return true;
    }

    public void adsBuy() {
        if (!BillingPreferencesHelper.isAdsDisabled()) {
			/*
			 * для безопасности сгенерьте payload для верификации. В данном
			 * примере просто пустая строка юзается. Но в реальном приложение
			 * подходить к этому шагу с умом.
			 */
            String payload = "";
            mHelper.launchPurchaseFlow((Activity) context, Const.SKU_ADS_DISABLE, Const.RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        }
    }

    // Прокает, когда покупка завершена
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }
            //LOG.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(Const.SKU_ADS_DISABLE)) {

                Toast.makeText(context, "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
                // сохраняем в настройках, что отключили рекламу
                BillingPreferencesHelper.savePurchase(context, BillingPreferencesHelper.Purchase.DISABLE_ADS, true);
                // отключаем рекламу
                ads.show(!BillingPreferencesHelper.isAdsDisabled());
            }

        }
    };

}
