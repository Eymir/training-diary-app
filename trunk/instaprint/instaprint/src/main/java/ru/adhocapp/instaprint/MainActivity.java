package ru.adhocapp.instaprint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.adhocapp.instaprint.billing.IabHelper;
import ru.adhocapp.instaprint.billing.IabResult;
import ru.adhocapp.instaprint.billing.Inventory;
import ru.adhocapp.instaprint.billing.Purchase;
import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.db.entity.EntityManager;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.db.entity.PurchaseDetails;
import ru.adhocapp.instaprint.mail.MailHelper;
import ru.adhocapp.instaprint.util.Const;
import ru.adhocapp.instaprint.util.PageFragment;
import ru.adhocapp.instaprint.util.ResourceAccess;

public class MainActivity extends FragmentActivity {

    private static final int PAGE_COUNT = 4;
    private static final int SELECT_FOTO_REQUEST_CODE = 199;
    private Bitmap selectedImage;
    private String selectedImagefilePath = "";
    private Context context;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private Order order;

    private IabHelper mHelper;
    private EntityManager em;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        em = DBHelper.getInstance(this).EM;
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(pagerAdapter);
        ResourceAccess.getInstance(this);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(Const.LOG_TAG, "onPageSelected, positioLog.en = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String title = "";
            switch (position) {
                case 0:
                    title = getString(R.string.page_title_select_foto);
                    break;
                case 1:
                    title = getString(R.string.page_title_edit_text);
                    break;
                case 2:
                    title = getString(R.string.page_title_edit_address);
                    break;
                case 3:
                    title = getString(R.string.page_title_result);
                    break;
            }
            return title;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void clickNexеtSelectFoto(View view) {
        pager.setCurrentItem(1);
    }

    public void clickPreviousEditText(View view) {
        pager.setCurrentItem(0);
    }

    public void clickNextEditText(View view) {
        pager.setCurrentItem(2);
    }

    public void clickPreviousEditAddress(View view) {
        pager.setCurrentItem(1);
    }

    public void clickNextEditAddress(View view) {
        pager.setCurrentItem(3);
    }

    public void clickPreviousResult(View view) {
        pager.setCurrentItem(2);
    }

    public void clickSelectFoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FOTO_REQUEST_CODE);
    }

    public void sendOrderWithPurchase(View view) {
        //TODO: сделать валидацию
        EditText etUserText = (EditText) findViewById(R.id.et_user_text);
        String etUserTextStr = (etUserText.getText() != null) ? etUserText.getText().toString() : null;

        EditText etFromFio = (EditText) findViewById(R.id.et_from_fio);
        EditText etFromAddress = (EditText) findViewById(R.id.et_from_address);
        EditText etFromZip = (EditText) findViewById(R.id.et_from_zip);
        String etFromFioStr = (etFromFio.getText() != null) ? etFromFio.getText().toString() : null;
        String etFromAddressStr = (etFromAddress.getText() != null) ? etFromAddress.getText().toString() : null;
        String etFromZipStr = (etFromZip.getText() != null) ? etFromZip.getText().toString() : null;
        Address fromAddress = new Address(etFromAddressStr, etFromZipStr, etFromFioStr);

        EditText etToFio = (EditText) findViewById(R.id.et_to_fio);
        EditText etToAddress = (EditText) findViewById(R.id.et_to_address);
        EditText etToZip = (EditText) findViewById(R.id.et_to_zip);
        String etToFioStr = (etToFio.getText() != null) ? etToFio.getText().toString() : null;
        String etToAddressStr = (etToAddress.getText() != null) ? etToAddress.getText().toString() : null;
        String etToZipStr = (etToZip.getText() != null) ? etToZip.getText().toString() : null;
        Address toAddress = new Address(etToAddressStr, etToZipStr, etToFioStr);

        order = new Order(fromAddress, toAddress, etUserTextStr, selectedImagefilePath, new Date(), null, OrderStatus.PAYING);

        em.persist(order);
        buyPurchase();

    }

    public void sendOrderWithoutPurchase(View view) {

        EditText etUserText = (EditText) findViewById(R.id.et_user_text);
        String etUserTextStr = (etUserText.getText() != null) ? etUserText.getText().toString() : null;

        EditText etFromFio = (EditText) findViewById(R.id.et_from_fio);
        EditText etFromAddress = (EditText) findViewById(R.id.et_from_address);
        EditText etFromZip = (EditText) findViewById(R.id.et_from_zip);
        String etFromFioStr = (etFromFio.getText() != null) ? etFromFio.getText().toString() : null;
        String etFromAddressStr = (etFromAddress.getText() != null) ? etFromAddress.getText().toString() : null;
        String etFromZipStr = (etFromZip.getText() != null) ? etFromZip.getText().toString() : null;
        Address fromAddress = new Address(etFromAddressStr, etFromZipStr, etFromFioStr);

        EditText etToFio = (EditText) findViewById(R.id.et_to_fio);
        EditText etToAddress = (EditText) findViewById(R.id.et_to_address);
        EditText etToZip = (EditText) findViewById(R.id.et_to_zip);
        String etToFioStr = (etToFio.getText() != null) ? etToFio.getText().toString() : null;
        String etToAddressStr = (etToAddress.getText() != null) ? etToAddress.getText().toString() : null;
        String etToZipStr = (etToZip.getText() != null) ? etToZip.getText().toString() : null;
        Address toAddress = new Address(etToAddressStr, etToZipStr, etToFioStr);

        order = new Order(fromAddress, toAddress, etUserTextStr, selectedImagefilePath, new Date(), null, OrderStatus.PAYING);
        MailHelper.getInstance().sendMail(order);

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_FOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageURI = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImageURI, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagefilePath = cursor.getString(columnIndex);
                    cursor.close();

                    selectedImage = BitmapFactory.decodeFile(selectedImagefilePath);
                    ImageView imageView = (ImageView) findViewById(R.id.ivUserFoto);
                    imageView.setImageBitmap(selectedImage);

                }
        }
    }

    //Стартует покупку
    private void buyPurchase() {
        billingInit();

    }

    private void billingInit() {
        mHelper = new IabHelper(MainActivity.this, Const.BASE64_PUBLIC_KEY);
        mHelper.enableDebugLogging(Const.IAB_DEBUG_LOGGING);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

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
            }
            else {
                //Старутем покупку так как ещё не покупали ни одного раза.
                mHelper.launchPurchaseFlow((Activity) context, Const.PURCHASE_NOTE_TAG_1, Const.RC_REQUEST,
                        mPurchaseFinishedListener, "");

            }
            Log.d(Const.LOG_TAG, "Initial inventory query finished");
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(Const.LOG_TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            if (mHelper == null) return;

            if (result.isSuccess()) {
                //Стартуем покупку тут после успешного использования
                mHelper.launchPurchaseFlow((Activity) context, Const.PURCHASE_NOTE_TAG_1, Const.RC_REQUEST,
                        mPurchaseFinishedListener, "");

            }
            else {
                Log.d(Const.LOG_TAG, "Error while consuming: " + result);
            }
            Log.d(Const.LOG_TAG, "End consumption flow.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            }
            if (purchase.getSku().equals(Const.PURCHASE_NOTE_TAG_1)) {
                order.setStatus(OrderStatus.SENDING);
                order.setPurchaseDetails(new PurchaseDetails(purchase.getOrderId(), new Date(purchase.getPurchaseTime()), null));
                sendOrder(order);
                //em.merge(order);
                Toast.makeText(context, "Заказ отправлен.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void sendOrder(Order order) {
        Toast.makeText(this, order.toString(), Toast.LENGTH_LONG).show();
        MailHelper.getInstance().sendMail(order);
    }

}
