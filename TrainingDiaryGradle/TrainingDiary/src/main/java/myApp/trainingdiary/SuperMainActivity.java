package myApp.trainingdiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import myApp.trainingdiary.billing.AdMobController;
import myApp.trainingdiary.billing.AdsControllerBase;
import myApp.trainingdiary.billing.BillingHelper;
import myApp.trainingdiary.billing.BillingPreferencesHelper;
import myApp.trainingdiary.billing.util.IabHelper;
import myApp.trainingdiary.billing.util.IabResult;
import myApp.trainingdiary.billing.util.Inventory;
import myApp.trainingdiary.billing.util.Purchase;
import myApp.trainingdiary.calendar.CalendarActivity;
import myApp.trainingdiary.customview.stat.StatItem;
import myApp.trainingdiary.customview.stat.StatItemArrayAdapter;
import myApp.trainingdiary.customview.stat.StatisticEnum;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingStamp;
import myApp.trainingdiary.dialog.DialogProvider;
import myApp.trainingdiary.excercise.AddExerciseActivity;
import myApp.trainingdiary.service.GetCommonStatisticsTask;
import myApp.trainingdiary.statistic.StatisticActivity;
import myApp.trainingdiary.training.TrainingActivity;
import myApp.trainingdiary.utils.Const;
import myApp.trainingdiary.utils.TrainingDurationManger;

import com.google.analytics.tracking.android.EasyTracker;


public class SuperMainActivity extends ActionBarActivity implements View.OnClickListener {

    private DBHelper dbHelper;
    private AlertDialog editStatListDialog;
    private AlertDialog stopWorkoutDialog;
    public Context context;

    ////////////////billing////////////
    private IabHelper mHelper;
    private AdsControllerBase ads;
    private LinearLayout adsLayout;
    private BillingHelper billingHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_main);
        context = this;
        billingHelper = BillingHelper.getInstance(this);
        adsLayout = (LinearLayout)findViewById(R.id.ads_layout_main_activity);

        //google analytics
        if(getResources().getBoolean(R.bool.analytics_enable))
            EasyTracker.getInstance().setContext(this);

        dbHelper = DBHelper.getInstance(this);
        try {
            Log.i(Const.LOG_TAG, "DB version: " + String.valueOf(dbHelper.getVersion()));
        } catch (Throwable e) {
            if (e.getMessage() != null && !e.getMessage().equals(""))
                Log.e(Const.LOG_TAG, e.getMessage(), e);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        initStatPref();

        Button exercise = (Button) findViewById(R.id.exercise_main_button);
        exercise.setOnClickListener(this);

        Button history = (Button) findViewById(R.id.history_main_button);
        history.setOnClickListener(this);

        Button stat = (Button) findViewById(R.id.stat_main_button);
        stat.setOnClickListener(this);

        ImageButton editStat = (ImageButton) findViewById(R.id.edit_user_activity);
        editStat.setOnClickListener(this);

        createEditStatListDialog();
        createDeletionDialog();

        //billingHelper.adsShow(adsLayout);

    }

//    private void adsShow(){
//        BillingPreferencesHelper.loadSettings(this);
//        billingInit();
//        ads = new AdMobController(this, adsLayout);
//        // если отключили рекламу, то не будем показывать
//        ads.show(!BillingPreferencesHelper.isAdsDisabled());
//    }
//
//    private void buy() {
//        if (!BillingPreferencesHelper.isAdsDisabled()) {
//			/*
//			 * для безопасности сгенерьте payload для верификации. В данном
//			 * примере просто пустая строка юзается. Но в реальном приложение
//			 * подходить к этому шагу с умом.
//			 */
//            String payload = "";
//            mHelper.launchPurchaseFlow(this, Const.SKU_ADS_DISABLE, Const.RC_REQUEST,
//                    mPurchaseFinishedListener, payload);
//        }
//    }
//
//    private void billingInit() {
//        mHelper = new IabHelper(this, Const.BASE64_PUBLIC_KEY);
//        // включаем дебагинг (в релизной версии ОБЯЗАТЕЛЬНО выставьте в false)
//        mHelper.enableDebugLogging(true);
//        // инициализируем; запрос асинхронен
//        // будет вызван, когда инициализация завершится
//        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                if (!result.isSuccess()) {
//                    return;
//                }
//                // чекаем уже купленное
//                mHelper.queryInventoryAsync(mGotInventoryListener);
//            }
//        });
//    }
//
//    // Слушатель для востановителя покупок.
//    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            if (result.isFailure()) {
//                return;
//            }
//			/*
//			 * Проверяются покупки. Обратите внимание, что надо проверить каждую
//			 * покупку, чтобы убедиться, что всё норм! см.
//			 * verifyDeveloperPayload().
//			 */
//            Purchase purchase = inventory.getPurchase(Const.SKU_ADS_DISABLE);
//            BillingPreferencesHelper.savePurchase(context,
//                    BillingPreferencesHelper.Purchase.DISABLE_ADS, purchase != null
//                    && verifyDeveloperPayload(purchase));
//            ads.show(!BillingPreferencesHelper.isAdsDisabled());
//        }
//    };
//
//    boolean verifyDeveloperPayload(Purchase p) {
//        String payload = p.getDeveloperPayload();
//		/*
//		 * TODO: здесь необходимо свою верификацию реализовать Хорошо бы ещё с
//		 * использованием собственного стороннего сервера.
//		 */
//        return true;
//    }
//
//    // Прокает, когда покупка завершена
//    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
//        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
//            if (result.isFailure()) {
//                return;
//            }
//            if (!verifyDeveloperPayload(purchase)) {
//                return;
//            }
//            //LOG.d(TAG, "Purchase successful.");
//            if (purchase.getSku().equals(Const.SKU_ADS_DISABLE)) {
//
//                Toast.makeText(getApplicationContext(), "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
//                // сохраняем в настройках, что отключили рекламу
//                BillingPreferencesHelper.savePurchase(context, BillingPreferencesHelper.Purchase.DISABLE_ADS, true);
//                // отключаем рекламу
//                ads.show(!BillingPreferencesHelper.isAdsDisabled());
//            }
//
//        }
//    };

    private void manageWorkoutButtons() {
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(this).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Log.d(Const.LOG_TAG, "workoutExpiringTimeout: " + workoutExpiringTimeout);
        TrainingDurationManger.closeExpiredTrainingStamps(Integer.valueOf(workoutExpiringTimeout));
        TrainingStamp tr_stamp = dbHelper.READ.getOpenTrainingStamp();
        if (tr_stamp != null && dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp.getId()) != null) {
            findViewById(R.id.start_training_button).setVisibility(View.GONE);
            findViewById(R.id.continue_workout_panel).setVisibility(View.VISIBLE);
            Button cont = (Button) findViewById(R.id.continue_workout_button);
            cont.setOnClickListener(this);
            ImageButton stop = (ImageButton) findViewById(R.id.stop_workout_button);
            stop.setOnClickListener(this);
        } else {
            findViewById(R.id.start_training_button).setVisibility(View.VISIBLE);
            findViewById(R.id.continue_workout_panel).setVisibility(View.GONE);
            Button start = (Button) findViewById(R.id.start_training_button);
            start.setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageWorkoutButtons();
        showCommonStatisticList();
    }

    private void initStatPref() {
        SharedPreferences sp = getSharedPreferences(Const.CHOSEN_STATISTIC, MODE_PRIVATE);

        if (sp.getAll().size() < StatisticEnum.values().length) {
            SharedPreferences.Editor editor = sp.edit();
            int i = 0;
            for (StatisticEnum stat : StatisticEnum.values()) {
                editor.putBoolean(stat.name(), (i < 5) ? true : false);
                i++;
            }
            editor.commit();
        }
        Log.d(Const.LOG_TAG, "SharedPreferences." + Const.CHOSEN_STATISTIC + ".count: " + sp.getAll().size());
    }

    private void createEditStatListDialog() {
        editStatListDialog = DialogProvider.createStatListDialog(this, new DialogProvider.OkClickListener() {
            @Override
            public void onPositiveClick() {
                showCommonStatisticList();
            }
        });

    }

    private void showCommonStatisticList() {

        final ListView listView = (ListView) findViewById(R.id.statListView);
        GetCommonStatisticsTask.start(this, new GetCommonStatisticsTask.GetCommonStatisticsTaskCallback() {

            @Override
            protected void onUploadSuccess(List<StatItem> result) {
                StatItemArrayAdapter statListadapter = new StatItemArrayAdapter(SuperMainActivity.this, R.layout.stat_plain_row, R.layout.stat_plain_row, result);
                listView.setAdapter(statListadapter);
            }

            @Override
            protected void onUploadError() {
                Toast.makeText(SuperMainActivity.this, R.string.error,
                        Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_training_button:
                Intent intent = new Intent(SuperMainActivity.this, TrainingActivity.class);
                startActivity(intent);
                break;
            case R.id.continue_workout_button:
                Intent intentCon = new Intent(SuperMainActivity.this, TrainingActivity.class);
                startActivity(intentCon);
                break;
            case R.id.stop_workout_button:
                stopWorkoutDialog.show();
                break;
            case R.id.exercise_main_button:
                Intent intentAddEx = new Intent(SuperMainActivity.this, AddExerciseActivity.class);
                startActivity(intentAddEx);
                break;
            case R.id.history_main_button:
                Intent intentHistCalendar = new Intent(SuperMainActivity.this, CalendarActivity.class);
                startActivity(intentHistCalendar);
                break;
            case R.id.stat_main_button:
                Intent intentStat = new Intent(SuperMainActivity.this, StatisticActivity.class);
                startActivity(intentStat);
                break;
            case R.id.edit_user_activity:
                editStatListDialog.show();
                break;
        }
    }

    private void createDeletionDialog() {

        String title = getResources().getString(R.string.stop_training_dialog_title);
        String cancelButton = getResources().getString(R.string.cancel_button);
        String btnDel = getResources().getString(R.string.stop_button);

        stopWorkoutDialog = DialogProvider.createSimpleDialog(this, title, null, btnDel, cancelButton, new DialogProvider.SimpleDialogClickListener() {
            @Override
            public void onPositiveClick() {
                TrainingDurationManger.closeOpenTrainingStamps();
                Toast.makeText(SuperMainActivity.this, R.string.workout_stopped,
                        Toast.LENGTH_SHORT).show();
                manageWorkoutButtons();
            }

            @Override
            public void onNegativeClick() {
                stopWorkoutDialog.cancel();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actsettings_menu, menu);

        if(!getResources().getBoolean(R.bool.show_ads)){
            MenuItem item = menu.findItem(R.id.action_disable_ads);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentStat = new Intent(SuperMainActivity.this, SettingsActivity.class);
                startActivity(intentStat);
                return true;

        }
        return true;
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
