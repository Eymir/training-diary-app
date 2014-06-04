package ru.adhocapp.instaprint.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
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
import ru.adhocapp.instaprint.util.FontsManager;
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
    private FontsManager mFontsManager;

    private static final Field sChildFragmentManagerField;

    protected static Bitmap sSelectedImage;
    private Bitmap mCurrentPostcard;
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
                    if (mFontsManager == null) {
                        mFontsManager = new FontsManager(getActivity(), ((HorizontalScrollView) getActivity().findViewById(R.id.sc_fonts)));
                        mFontsManager.init();
                    } else mFontsManager.drawFontsList();
                    break;
                case 3:
                    new DrawPreviewTask().execute();
                    break;
            }
        }
    };

    private class DrawPreviewTask extends AsyncTask<Void, Void, Void> {
        private AlertDialog mAd;
        private Bitmap mPostcard;
        private Bitmap mImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mAd == null || !mAd.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View input = LayoutInflater.from(getActivity()).inflate(R.layout.wait, null);
                builder.setView(input);
                mAd = builder.create();
                mAd.show();
            } else Log.i(LOGTAG, "Dialog is showing");
        }

        @Override
        protected Void doInBackground(Void... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inSampleSize = 2;
            mPostcard = BitmapFactory.decodeResource(getResources(), R.drawable.postcard_background, options);
            Log.e(LOGTAG, mPostcard.getWidth() + "; " +mPostcard.getHeight());

            Canvas c = new Canvas(mPostcard);
            c.drawColor(0x000000);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setTextSize(60.0f);
            p.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + FontsManager.currentFont));

            if (FontsManager.currentText != null) {
                char[] currentText = FontsManager.currentText.toCharArray();
                String printString = "";
                int lineSizeCounter = 0;
                int linesCounter = 0;
                int lastY = 160;
                for (int i = 0; i != currentText.length && linesCounter < 14; i++, lineSizeCounter++) {
                    printString += currentText[i];
                    if (lineSizeCounter >= (FontsManager.getValidity(FontsManager.currentFont) / 14) || i == (currentText.length - 1)) {
                        c.drawText(printString.trim(), 75, lastY, p);
                        lastY += 75;
                        lineSizeCounter = 0;
                        printString = "";
                        linesCounter++;
                    }
                }
            }

            if (order.getAddressTo() != null) {
                int maxLineSize = 20;
                Paint p2 = new Paint();
                p2.setColor(Color.BLACK);
                p2.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                p2.setTextSize(60.0f);

                String fullName = order.getAddressTo().getFullName();
                if (fullName.length() > maxLineSize) fullName = fullName.substring(0, maxLineSize);
                c.drawText(fullName, 970, 460, p2);

                String address = order.getAddressTo().getStreetAddress();
                for (int i = 1; i != address.length(); i++) {
                    if (address.charAt(i) != ' ' && ((int) i / maxLineSize) == ((double) i / maxLineSize)) {
                        int lastSpaceIndex = address.lastIndexOf(' ', i);
                        if (lastSpaceIndex != -1 && address.substring(i - maxLineSize, i + 1).contains(" ")) {
                            address = address.substring(0, lastSpaceIndex + 1) +
                                    FontsManager.getSpacedString(i - lastSpaceIndex) +
                                    address.substring(lastSpaceIndex + 1);
                        }
                    }
                }
                if (address.length() > maxLineSize) {
                    String secondAddressPart = address.substring(maxLineSize);
                    if (secondAddressPart.length() > maxLineSize) {
                        secondAddressPart = secondAddressPart.substring(0, maxLineSize).trim();
                    }
                    c.drawText(secondAddressPart, 970, 700, p2);
                    address = address.substring(0, maxLineSize);
                }
                c.drawText(address, 970, 580, p2);

                String cityAndZip = order.getAddressTo().getZipCode() + ", " + order.getAddressTo().getCityName();
                if (cityAndZip.length() > maxLineSize) cityAndZip = cityAndZip.substring(0, maxLineSize);
                c.drawText(cityAndZip, 970, 820, p2);

                String country = order.getAddressTo().getCountryName();
                if (country.length() > maxLineSize) country = country.substring(0, maxLineSize);
                c.drawText(country, 970, 940, p2);
            }

            mCurrentPostcard = mPostcard;
            mPostcard = Bitmap.createScaledBitmap(mPostcard, mPostcard.getWidth() / 2, mPostcard.getHeight() / 2, true);
            if (sSelectedImage != null) mImage = getCurrentImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAd.cancel();
            ImageView ivPostcard = (ImageView) getActivity().findViewById(R.id.iv_postcard);
            ivPostcard.setImageBitmap(mPostcard);

            if (mImage != null) {
                ImageView ivImage = (ImageView) getActivity().findViewById(R.id.iv_image);
                ivImage.setImageBitmap(mImage);
            }
        }
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
                    String selectedImageFilePath = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView labelView = (ImageView) getActivity().findViewById(R.id.imageLabel);
                    labelView.setVisibility(View.GONE);
                    sSelectedImage = getCheckedOnSizeBitmap(selectedImageFilePath);
                    mIvUserPhoto = (ImageView) getActivity().findViewById(R.id.ivUserFoto);

                    RelativeLayout borderFrame = (RelativeLayout) getActivity().findViewById(R.id.borderFrame);
                    borderFrame.setVisibility(View.VISIBLE);
                    mIvUserPhoto.setImageBitmap(sSelectedImage);
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

    private Bitmap getCheckedOnSizeBitmap(String selectedPath) {
        int inSampleSize = 1;
        Bitmap resizedBitmap = null;
        try {
            FileInputStream optionsStream = new FileInputStream(selectedPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(optionsStream, null, options);
            optionsStream.close();
            int width = options.outWidth;
            int height = options.outHeight;
            options.inJustDecodeBounds = false;
            if (width >= 2048) {
                inSampleSize = width/1024;
            }
            if (height >= 2048) {
                inSampleSize = width/1024;
            }
            options.inSampleSize = inSampleSize;
            FileInputStream input = new FileInputStream(selectedPath);
            resizedBitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return resizedBitmap;
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
                if (sSelectedImage != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    sSelectedImage = Bitmap.createBitmap(sSelectedImage, 0, 0, sSelectedImage.getWidth(), sSelectedImage.getHeight(), matrix, true);
                    mIvUserPhoto.setImageBitmap(sSelectedImage);
                }
                break;
            }
            case R.id.rotate_right: {
                if (sSelectedImage != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    sSelectedImage = Bitmap.createBitmap(sSelectedImage, 0, 0, sSelectedImage.getWidth(), sSelectedImage.getHeight(), matrix, true);
                    mIvUserPhoto.setImageBitmap(sSelectedImage);
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
            // тут сохранение
            saveBitmapToSD(mCurrentPostcard);
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
        PhotoView imageView = (PhotoView) getActivity().findViewById(R.id.ivUserFoto);
        RectF rect = getCropRect(imageView);
        Log.d(Const.LOG_TAG, "rect: " + rect);
        Log.d(Const.LOG_TAG, "selectedImage, w: " + sSelectedImage.getWidth() + " h:" + sSelectedImage.getHeight());
        return Bitmap.createBitmap(sSelectedImage, (int) rect.left, (int) rect.top,
                (int) rect.width(), (int) rect.height());
    }

    private RectF getCropRect(PhotoView imageView) {
        RectF rect = imageView.getDisplayRect();
        float viewScale = imageView.getScale();

        float dw = rect.width() / viewScale;
        float dh = rect.height() / viewScale;
        Drawable drawable = imageView.getDrawable();
        int bitmapWidth = drawable.getIntrinsicWidth();
        int bitmapHeight = drawable.getIntrinsicHeight();

        float w_ratio = bitmapWidth / dw;
        float h_ratio = bitmapHeight / dh;
        int b_off_x = (int) (w_ratio * (Math.abs(rect.left) / viewScale));
        int b_off_y = (int) (h_ratio * (Math.abs(rect.top) / viewScale));

        int dbw = (int) (imageView.getWidth() * w_ratio);
        int dbh = (int) (imageView.getHeight() * h_ratio);

        RectF result = new RectF();
        Log.d(Const.LOG_TAG, "viewScale: " + viewScale);

        result.left = b_off_x;
        result.top = b_off_y;

        result.right = b_off_x + dbw / viewScale;
        result.bottom = b_off_y + dbh / viewScale;

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
