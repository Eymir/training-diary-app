package ru.adhocapp.instaprint.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
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
import ru.adhocapp.instaprint.exception.SaveImageException;
import ru.adhocapp.instaprint.mail.MailHelper;
import ru.adhocapp.instaprint.util.Const;
import ru.adhocapp.instaprint.util.FontsAdder;
import ru.adhocapp.instaprint.util.ZoomImageView;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by malugin on 09.04.14.
 */

public class CreatePostcardFragment extends Fragment implements XmlClickable {
    private static final String LOGTAG = "CreatePostcardFragment";

    private ViewPager pager;

    private static final int SELECT_FOTO_REQUEST_CODE = 199;
    private static final int SELECT_ADDRESS = 8080;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");

    private Order order;

    private IabHelper mHelper;

    private EntityManager em;
    private FontsAdder mFontsAdder;

    private static final Field sChildFragmentManagerField;

    private Bitmap mSelectedImage;
    private ImageView mIvUserPhoto;

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
        pager.setOffscreenPageLimit(5);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(onPageChangeListener);
        em = DBHelper.getInstance(getActivity()).EM;
        billingInit();
        return view;
    }


    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Log.i(LOGTAG, "OnPageSelected, position = " + position);
            switch (position) {
                case 1:
                    if (mFontsAdder == null) {
                        mFontsAdder = new FontsAdder(getActivity(), ((HorizontalScrollView) getActivity().findViewById(R.id.sc_fonts)));
                        mFontsAdder.init();
                    }
                    break;
                case 3:
                    if (mSelectedImage != null) {
                        ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_image);
                        iv.setImageBitmap(getCurrentImage());
                    }
                    break;
            }
        }
    };

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
                    String selectedImageFilePath = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView labelView = (ImageView) getActivity().findViewById(R.id.imageLabel);
                    labelView.setVisibility(View.GONE);
                    mSelectedImage = BitmapFactory.decodeFile(selectedImageFilePath);
                    mIvUserPhoto = (ImageView) getActivity().findViewById(R.id.ivUserFoto);

                    RelativeLayout borderFrame = (RelativeLayout) getActivity().findViewById(R.id.borderFrame);
                    borderFrame.setVisibility(View.VISIBLE);
                    mIvUserPhoto.setImageBitmap(mSelectedImage);
                    getActivity().findViewById(R.id.ll_rotate_panel).setVisibility(View.VISIBLE);
                    order.setPhotoPath(selectedImageFilePath);
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
        details.setText(address.getCityName()+", "+address.getStreetAddress());
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
            case R.id.rotate_left: {
                if (mSelectedImage != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    mSelectedImage = Bitmap.createBitmap(mSelectedImage, 0, 0, mSelectedImage.getWidth(), mSelectedImage.getHeight(), matrix, true);
                    mIvUserPhoto.setImageBitmap(mSelectedImage);
                }
                break;
            }
            case R.id.rotate_right: {
                if (mSelectedImage != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    mSelectedImage = Bitmap.createBitmap(mSelectedImage, 0, 0, mSelectedImage.getWidth(), mSelectedImage.getHeight(), matrix, true);
                    mIvUserPhoto.setImageBitmap(mSelectedImage);
                }
                break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        FrameLayout borderFrame = (FrameLayout) getActivity().findViewById(R.id.borderFrame);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            borderFrame.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            borderFrame.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            borderFrame.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            borderFrame.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }


    public void sendOrderWithPurchase() {
        //TODO: сделать валидацию
        try {
            EditText etUserText = (EditText) getActivity().findViewById(R.id.et_user_text);
            String etUserTextStr = (etUserText.getText() != null) ? etUserText.getText().toString() : null;
            String newPath = saveBitmapToSD(getCurrentImage());
            order.setPhotoPath(newPath);
            order.setText(etUserTextStr);
            order.setDate(new Date());
            order.setStatus(OrderStatus.PAYING);
            Log.d(Const.LOG_TAG, "sendOrderWithPurchase: " + order);
            em.persist(order);
            buyPurchase();
        } catch (SaveImageException e) {
            Toast.makeText(getActivity(), R.string.cannot_save_image_to_sd, Toast.LENGTH_SHORT);
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            Toast.makeText(getActivity(), R.string.cannot_save_image_to_sd, Toast.LENGTH_SHORT);
        }
    }

    private Bitmap getCurrentImage() {
        ZoomImageView imageView = (ZoomImageView) getActivity().findViewById(R.id.ivUserFoto);
        imageView.setDrawingCacheEnabled(true);
        Bitmap result = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        return result;
    }

    private String saveBitmapToSD(Bitmap result) throws SaveImageException {
        try {
            String sd_path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File folder = new File(sd_path + "/" + Const.SAVE_FOLDER);
            folder.mkdirs();
            File image_file = new File(folder.toString(), Const.IMAGE_FILE_NAME + "_" + SDF.format(new Date()) + "_" + folder.list().length + ".png");
            try {
                fOut = new FileOutputStream(image_file);
                result.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } finally {
                fOut.flush();
                fOut.close();
            }
            return image_file.toString();
        } catch (Throwable e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            throw new SaveImageException(e);
        }
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
