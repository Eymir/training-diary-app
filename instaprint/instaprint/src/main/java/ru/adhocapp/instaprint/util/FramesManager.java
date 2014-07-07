package ru.adhocapp.instaprint.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.fragment.CreatePostcardFragment;

/**
 * Created by Игорь Ковган on 22.06.2014.
 */
public class FramesManager {
    private Context mContext;
    private LinearLayout mLlFramesList;
    private ImageView mIvPicture;
    private int mCurrentSession = 0;
    private int mLastEffect = 0;

    public FramesManager(Context context, LinearLayout framesList, Bitmap currentPicture) {
        mContext = context;
        mLlFramesList = framesList;
        mIvPicture = (ImageView) ((Activity) mContext).findViewById(R.id.iv_image);
        drawFramesList(currentPicture);
    }

    public void drawFramesList(Bitmap currentBitmap) {
        mCurrentSession++;
        final int lastSession = mCurrentSession;
        final Bitmap currentPicture = currentBitmap == null ? Bitmap.createBitmap(105, 148, Bitmap.Config.ARGB_8888) : currentBitmap;
        mLlFramesList.removeAllViews();
        final Handler handler = new Handler();
        // тут добавляешь ссылки на новые рамки, 0 - без рамки
        final int[] resArray = { 0, R.drawable.frame_bycicle, R.drawable.frame_chaotic_brush, R.drawable.frame_flower };
        for (int i = 0; i != resArray.length; i++) {
            final int k = i;
            new AsyncTask<Void, Void, View>() {
                @Override
                protected View doInBackground(Void... params) {
                    View newFrameTag = null;
                    try {
                        newFrameTag = ((Activity) mContext).getLayoutInflater().inflate(R.layout.frame_tag, null, false);
                        ImageButton btn = (ImageButton) newFrameTag.findViewById(R.id.btn_frame);

                        if (resArray[k] != 0) {
                            Bitmap preview = Bitmap.createBitmap(currentPicture.getWidth() / 3, currentPicture.getHeight() / 3, currentPicture.getConfig());
                            Canvas canvas = new Canvas(preview);
                            canvas.drawBitmap(Bitmap.createScaledBitmap(currentPicture, currentPicture.getWidth() / 3, currentPicture.getHeight() / 3, false), new Matrix(), null);
                            Bitmap frame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), resArray[k]), currentPicture.getWidth() / 3, currentPicture.getHeight() / 3, false);
                            canvas.drawBitmap(frame, new Matrix(), null);
                            btn.setBackgroundDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(Bitmap.createBitmap(preview, 0, 0, preview.getWidth() / 4, preview.getHeight() / 4), 150, 150, false)));
                            if (mLastEffect == k) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AsyncTask<Void, Void, Bitmap>() {
                                            @Override
                                            protected Bitmap doInBackground(Void... params) {
                                                return setUpImage(resArray[k], currentPicture);
                                            }

                                            @Override
                                            protected void onPostExecute(Bitmap result) {
                                                mIvPicture.setImageBitmap(result);
                                                CreatePostcardFragment.setGraphedImage(result);
                                            }
                                        }.execute();
                                    }
                                });
                            }
                        } else {
                            btn.setBackgroundDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(Bitmap.createBitmap(currentPicture, 0, 0, currentPicture.getWidth() / 4, currentPicture.getHeight() / 4), 150, 150, false)));
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
                                if (k != 0) {
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... params) {
                                            return setUpImage(resArray[k], currentPicture);
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap result) {
                                            mIvPicture.setImageBitmap(result);
                                            CreatePostcardFragment.setGraphedImage(result);
                                        }
                                    }.execute();
                                } else {
                                    mIvPicture.setImageBitmap(currentPicture);
                                    CreatePostcardFragment.setGraphedImage(currentPicture);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newFrameTag;
                }

                @Override
                protected void onPostExecute(View result) {
                    if (lastSession == mCurrentSession) mLlFramesList.addView(result);
                }
            }.execute();
        }
    }

    Bitmap setUpImage(int resArrayK, Bitmap currentPicture) {
        Bitmap result = Bitmap.createBitmap(currentPicture.getWidth(), currentPicture.getHeight(), currentPicture.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(currentPicture, new Matrix(), null);
        Bitmap frame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), resArrayK), currentPicture.getWidth(), currentPicture.getHeight(), false);

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
}
