package ru.adhocapp.instaprint.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.fragment.postcard.CreatePostcardMainFragment;

/**
 * Created by Игорь Ковган on 22.06.2014.
 */
public class FrameManager {
    private Context mContext;
    private LinearLayout mLlFramesList;
    private ImageView mIvPicture;
    public Bitmap currentPicture;
    private int mCurrentSession = 0;
    private int mLastEffect = 0;
    private String currentFrame;

    public FrameManager(Context context, View view, final Bitmap currentPicture, String startFrame) {
        mContext = context;
        mLlFramesList = (LinearLayout) view.findViewById(R.id.ll_frames);
        mIvPicture = (ImageView) view.findViewById(R.id.iv_image);
        this.currentPicture = currentPicture;
        drawFramesList(currentPicture);
        if (startFrame != null) {
            final int frame_res = context.getResources().getIdentifier(startFrame, "drawable", context.getPackageName());
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return setUpImage(frame_res, FrameManager.this.currentPicture);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    mIvPicture.setImageBitmap(result);
                    CreatePostcardMainFragment.setGraphedImage(result);
                }
            }.execute();
        }
    }

    public void drawFramesList(Bitmap currentBitmap) {
        this.currentPicture = currentBitmap;
        mCurrentSession++;
        final int lastSession = mCurrentSession;
        final Bitmap currentPicture = currentBitmap == null ? Bitmap.createBitmap(105, 148, Bitmap.Config.ARGB_8888) : currentBitmap;
        mLlFramesList.removeAllViews();
        final Handler handler = new Handler();
        // тут добавляешь ссылки на новые рамки, 0 - без рамки
        final int[] resArray = {0,
                R.drawable.frame_0,
                R.drawable.frame_1,
                R.drawable.frame_2,
                R.drawable.frame_4,
                R.drawable.frame_6,
                R.drawable.frame_8,
                R.drawable.frame_10};
        for (int i = 0; i != resArray.length; i++) {
            final int k = i;
            new AsyncTask<Void, Void, View>() {
                @Override
                protected View doInBackground(Void... params) {
                    View newFrameTag = null;
                    try {
                        Log.d(Const.LOG_TAG, "mContext: " + mContext);
                        newFrameTag = ((Activity) mContext).getLayoutInflater().inflate(R.layout.frame_tag, null, false);

                        ImageButton btn = (ImageButton) newFrameTag;
                        if (resArray[k] != 0) {
                            Bitmap preview = Bitmap.createBitmap(currentPicture, 0, 0, currentPicture.getWidth() / 4, currentPicture.getWidth() / 4);
                            Canvas canvas = new Canvas(preview);
                            Bitmap frame = BitmapFactory.decodeResource(mContext.getResources(), resArray[k]);
                            Bitmap scaledCroppedFrame = Bitmap.createScaledBitmap(frame, currentPicture.getWidth(), currentPicture.getHeight(), false);
                            Rect src = new Rect();
                            src.set(0, 0, preview.getWidth(), preview.getHeight());

                            Rect dst = new Rect();
                            dst.set(0, 0, preview.getWidth(), preview.getHeight());
                            canvas.drawBitmap(scaledCroppedFrame, src, dst, null);
                            btn.setImageBitmap(preview);
//                            if (mLastEffect == k) {
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        new AsyncTask<Void, Void, Bitmap>() {
//                                            @Override
//                                            protected Bitmap doInBackground(Void... params) {
//                                                return setUpImage(resArray[k], currentPicture);
//                                            }
//
//                                            @Override
//                                            protected void onPostExecute(Bitmap result) {
//                                                mIvPicture.setImageBitmap(result);
//                                                CreatePostcardMainFragment.setGraphedImage(result);
//                                            }
//                                        }.execute();
//                                    }
//                                });
//                            }
                        } else {
                            Bitmap preview = Bitmap.createBitmap(currentPicture, 0, 0, currentPicture.getWidth() / 4, currentPicture.getWidth() / 4);
                            btn.setImageBitmap(preview);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mIvPicture.setImageBitmap(currentPicture);
                                }
                            });
                        }

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLastEffect = k;
                                Log.d(Const.LOG_TAG, "onClick.k: " + k + " onClick.mLastEffect: " + mLastEffect);
                                if (k != 0) {
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... params) {
                                            return setUpImage(resArray[k], currentPicture);
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap result) {
                                            mIvPicture.setImageBitmap(result);
                                            CreatePostcardMainFragment.setGraphedImage(result);
                                        }
                                    }.execute();
                                } else {
                                    mIvPicture.setImageBitmap(currentPicture);
                                    CreatePostcardMainFragment.setGraphedImage(currentPicture);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(Const.LOG_TAG, e.getMessage(), e);
                    }
                    return newFrameTag;
                }

                @Override
                protected void onPostExecute(View result) {
                    if (lastSession == mCurrentSession && mLlFramesList != null)
                        mLlFramesList.addView(result);
                }
            }.execute();
        }
    }

    private Bitmap setUpImage(int resArrayK, Bitmap currentPicture) {
        Log.d(Const.LOG_TAG, "resArrayK: " + resArrayK + " currentPicture: " + currentPicture);
        Bitmap result = Bitmap.createBitmap(currentPicture.getWidth(), currentPicture.getHeight(), currentPicture.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(currentPicture, new Matrix(), null);
        Bitmap frame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), resArrayK), currentPicture.getWidth(), currentPicture.getHeight(), false);
        currentFrame = mContext.getResources().getResourceEntryName(resArrayK);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                int framePixel = frame.getPixel(x, y);
                int r = Color.red(framePixel);
                int g = Color.green(framePixel);
                int b = Color.blue(framePixel);
                int alpha = Color.alpha(framePixel);
                if (r == 255 && g == 255 && b == 255) {
                    int resultPixel = result.getPixel(x, y);
                    int rR = Color.red(resultPixel);
                    int rG = Color.green(resultPixel);
                    int rB = Color.blue(resultPixel);
                    result.setPixel(x, y, Color.argb(255 - alpha, rR, rG, rB));
                }
            }
        }
        return result;
    }

    public String getCurrentFrame() {
        return currentFrame;
    }
}
