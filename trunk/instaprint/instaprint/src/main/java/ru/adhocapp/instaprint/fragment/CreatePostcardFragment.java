package ru.adhocapp.instaprint.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.adhocapp.instaprint.AddressActivity;
import ru.adhocapp.instaprint.R;
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
import ru.adhocapp.instaprint.dialog.CreateEditAddressFragmentDialog;
import ru.adhocapp.instaprint.dialog.MapPositiveNegativeClickListener;
import ru.adhocapp.instaprint.mail.MailHelper;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardFragment extends Fragment implements XmlClickable {

    private ViewPager pager;

    private static final int SELECT_FOTO_REQUEST_CODE = 199;
    private static final int SELECT_ADDRESS = 8080;

    private Order order;

    private IabHelper mHelper;

    private EntityManager em = DBHelper.getInstance().EM;

    private static final Field sChildFragmentManagerField;

    //Костыль для Pager BEGIN
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(Const.LOG_TAG, "Error getting mChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Log.e(Const.LOG_TAG, "Error setting mChildFragmentManager field", e);
            }
        }
    }
    //Костыль для Pager END

    public static CreatePostcardFragment newInstance() {
        CreatePostcardFragment pageFragment = new CreatePostcardFragment();
        pageFragment.setOrder(new Order(OrderStatus.CREATING));
        return pageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_create_postcart, null);
        pager = (ViewPager) view.findViewById(R.id.pager);
        FragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(pagerAdapter);
        billingInit();
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_FOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageURI = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImageURI, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String selectedImagefilePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap selectedImage = BitmapFactory.decodeFile(selectedImagefilePath);
                    ImageView imageView = (ImageView) getActivity().findViewById(R.id.ivUserFoto);
                    imageView.setImageBitmap(selectedImage);
                    order.setPhotoPath(selectedImagefilePath);
                    break;
                }
            case SELECT_ADDRESS: {
                if (resultCode == Activity.RESULT_OK) {
                    final Address address = (Address) data.getSerializableExtra(Const.ADDRESS);
                    final int addressType = data.getIntExtra(Const.ADDRESS_TYPE, -1);
                    Log.w(Const.LOG_TAG, "addressType: " + addressType);
                    if (resultCode == Activity.RESULT_OK && addressType != -1) {
                        fillAddressField(addressType, address);
                    }
                }
                break;
            }
        }
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(Const.LOG_TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void fillAddressField(final int addressType, Address address) {
        View addressView = getActivity().findViewById(addressType);
        if (addressView.getId() == R.id.address_from) {
            order.setAddressFrom(address);
        }
        if (addressView.getId() == R.id.address_to) {
            order.setAddressTo(address);
        }
        TextView title = (TextView) addressView.findViewById(R.id.contact_title);
        title.setText(address.getFullName());
        TextView details = (TextView) addressView.findViewById(R.id.contact_details);
        details.setText(address.getFullAddress());
        View action_area = addressView.findViewById(R.id.action_area);
        action_area.setVisibility(View.VISIBLE);
        ImageView remove_address = (ImageView) action_area.findViewById(R.id.remove_address);
        remove_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View addressView = getActivity().findViewById(addressType);

                TextView title = (TextView) addressView.findViewById(R.id.contact_title);
                TextView details = (TextView) addressView.findViewById(R.id.contact_details);
                details.setText(R.string.press_to_choose);
                View action_area = addressView.findViewById(R.id.action_area);
                action_area.setVisibility(View.GONE);

                if (addressType == R.id.address_from) {
                    order.setAddressFrom(null);
                    title.setText(R.string.address_from_no_named);

                } else if (addressType == R.id.address_to) {
                    order.setAddressTo(null);
                    title.setText(R.string.address_to_no_named);
                }
            }
        });
    }

    private void billingInit() {
        mHelper = new IabHelper(this.getActivity(), Const.BASE64_PUBLIC_KEY);
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


    @Override
    public void myClickMethod(final View v) {
        Log.d(Const.LOG_TAG, "myClickMethod: " + v.getContentDescription());
        switch (v.getId()) {
            case R.id.nextPage: {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                break;
            }
            case R.id.prevPage: {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
                break;
            }
            case R.id.photoArea: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_FOTO_REQUEST_CODE);
                break;
            }
            case R.id.address_from:
            case R.id.address_to: {
                final EntityManager em = DBHelper.getInstance().EM;
                List all = em.findAll(Address.class);
                if (all == null || all.isEmpty()) {
                    CreateEditAddressFragmentDialog createAddressFragmentDialog = CreateEditAddressFragmentDialog.newInstance(new MapPositiveNegativeClickListener() {
                        @Override
                        public void positiveClick(Map<String, Object> map) {
                            Address address = (Address) map.get(Const.ADDRESS);
                            em.persist(address);
                            fillAddressField(v.getId(), address);
                        }

                        @Override
                        public void negativeClick() {

                        }
                    });
                    createAddressFragmentDialog.show(getFragmentManager(), "");
                } else {
                    Intent intent;
                    intent = new Intent(getActivity(), AddressActivity.class);
                    intent.putExtra(Const.ADDRESS_TYPE, v.getId());
                    startActivityForResult(intent, SELECT_ADDRESS);
                }
                break;
            }
            case R.id.send_postcard_with_purchase: {
                sendOrderWithPurchase();
                break;
            }
        }
    }

    public void sendOrderWithPurchase() {
        //TODO: сделать валидацию
        EditText etUserText = (EditText) getActivity().findViewById(R.id.et_user_text);
        String etUserTextStr = (etUserText.getText() != null) ? etUserText.getText().toString() : null;
        order.setText(etUserTextStr);
        order.setDate(new Date());
        order.setStatus(OrderStatus.PAYING);
        Log.d(Const.LOG_TAG, "sendOrderWithPurchase: " + order);
        em.persist(order);
        buyPurchase();
    }

    //Стартует покупку
    private void buyPurchase() {
        try {
            mHelper.launchPurchaseFlow(getActivity(), Const.PURCHASE_NOTE_TAG_1, Const.RC_REQUEST,
                    mPurchaseFinishedListener, "");
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            Toast.makeText(getActivity(), R.string.purchase_not_available, Toast.LENGTH_SHORT).show();
        }
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

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(Const.LOG_TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                Log.d(Const.LOG_TAG, "Error purchasing: " + result);
                return;
            }
            Log.d(Const.LOG_TAG, "Purchase successful.");
            if (purchase.getSku().equals(Const.PURCHASE_NOTE_TAG_1)) {
                order.setStatus(OrderStatus.SENDING);
                order.setPurchaseDetails(new PurchaseDetails(purchase.getOrderId(), new Date(purchase.getPurchaseTime()), null));
                sendOrder(order);
                em.merge(order);
                Log.d(Const.LOG_TAG, "PURCHASE_NOTE_TAG_1 is done!!");
                Toast.makeText(getActivity(), "Заказ отправлен.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void sendOrder(Order order) {
        //Toast.makeText(this, order.toString(), Toast.LENGTH_LONG).show();
        MailHelper.getInstance().sendMail(order);
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}
