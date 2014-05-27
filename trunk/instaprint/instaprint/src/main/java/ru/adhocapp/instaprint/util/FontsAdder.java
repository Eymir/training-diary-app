package ru.adhocapp.instaprint.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ru.adhocapp.instaprint.R;

/**
 * Created by Игорь Ковган on 26.05.2014.
 */
public class FontsAdder {
    private Context mContext;
    private HashMap<String, String> mFontsList;
    private HorizontalScrollView mScAddView;

    public FontsAdder(Context context, HorizontalScrollView scView) {
        mContext = context;
        mScAddView = scView;
    }

    public void init() {
        if (mFontsList == null) {
            initList();
            new FontsTask().execute(mFontsList);
        }

        ((com.neopixl.pixlui.components.edittext.EditText) ((Activity) mContext)
                .findViewById(R.id.et_user_text))
                .setCustomFont(mContext, "Boomboom.otf");
    }


    private class FontsTask extends AsyncTask<HashMap<String, String>, View, Void> {
        @Override
        protected Void doInBackground(HashMap<String, String>... map) {
            try {
                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                Set<Map.Entry<String, String>> set = map[0].entrySet();
                for (final Map.Entry<String, String> me : set) {
                    View newReviewTag = ((Activity) mContext).getLayoutInflater().inflate(R.layout.font_tag, null, false);
                    com.neopixl.pixlui.components.textview.TextView tv = (com.neopixl.pixlui.components.textview.TextView) newReviewTag.findViewById(R.id.tv_font_example);
                    tv.setCustomFont(mContext, me.getKey());
                    newReviewTag.setOnClickListener(new OpenReviewListener(me.getKey()));
                    linearLayout.addView(newReviewTag);
                }

                publishProgress(linearLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(View... viewArray) {
            super.onProgressUpdate(viewArray);
            mScAddView.addView(viewArray[0]);
        }
    }


    private class OpenReviewListener implements View.OnClickListener {
        private String mFontName;

        OpenReviewListener(String fontName) {
            mFontName = fontName;
        }

        @Override
        public void onClick(View v) {
            ((com.neopixl.pixlui.components.edittext.EditText) ((Activity) mContext)
                    .findViewById(R.id.et_user_text))
                    .setCustomFont(mContext, mFontName);
        }
    }

    private void initList() {
        mFontsList = new HashMap();
        mFontsList.put("Boomboom.otf", "Boomboom");
        mFontsList.put("Calibri.ttf", "Calibri");
        mFontsList.put("Capture_It.ttf", "Capture It");
        mFontsList.put("Handwriting_Bickhamscr.ttf", "Handwriting Bickhamscr");
        mFontsList.put("Handwriting_Lazy.ttf", "Handwriting Lazy");
        mFontsList.put("Wild_honey.ttf", "Wild honey");
        mFontsList.put("Xiomara.ttf", "Xiomara");
        mFontsList.put("YesevaOne.otf", "YesevaOne");
    }
}
