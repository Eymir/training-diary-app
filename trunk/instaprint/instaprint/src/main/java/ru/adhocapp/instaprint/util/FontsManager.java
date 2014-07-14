package ru.adhocapp.instaprint.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ru.adhocapp.instaprint.R;

/**
 * Created by Игорь Ковган on 26.05.2014.
 */
public class FontsManager {
    private Context mContext;
    private static Map<String, Integer> sFontsList;
    private HorizontalScrollView mScAddView;
    public static String currentFont;
    public static String currentText;
    private int mValidity = 0;
    private int mWasLength = 0;

    private TextView mTvValidity;
    private com.neopixl.pixlui.components.edittext.EditText mEtUserText;

    public FontsManager(Context context, HorizontalScrollView scView) {
        mContext = context;
        mScAddView = scView;
    }

    public void drawFontsList() {
        if (mScAddView.getChildCount() == 0) {
            if (sFontsList == null) initList();
            new FontsTask().execute(sFontsList);
        }
    }

    public void init() {
        drawFontsList();

        mTvValidity = (TextView) ((Activity) mContext).findViewById(R.id.tv_validity);
        mEtUserText = ((com.neopixl.pixlui.components.edittext.EditText)
                ((Activity) mContext).findViewById(R.id.et_user_text));

        com.neopixl.pixlui.components.edittext.EditText et = ((com.neopixl.pixlui.components.edittext.EditText) ((Activity) mContext)
                .findViewById(R.id.et_user_text));
        et.setCustomFont(mContext, "Boomboom.otf");
        TextView tv = ((TextView) ((Activity) mContext).findViewById(R.id.tv_validity));
        tv.setText("0/" + sFontsList.get("Boomboom.otf"));
        currentFont = "Boomboom.otf";
        mValidity = sFontsList.get("Boomboom.otf");
        mEtUserText.addTextChangedListener(textWatcher);
    }

    public static Integer getValidity(String font) {
        return sFontsList.get(font);
    }


    private class FontsTask extends AsyncTask<Map<String, Integer>, View, Void> {
        @Override
        protected Void doInBackground(Map<String, Integer>... maps) {
            try {
                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                Map<String, Integer> map = maps[0];
                Set<String> set = map.keySet();
                for (String key : set) {
                    Log.d(Const.LOG_TAG, "font: " + key);
                    Integer value = map.get(key);
                    View newReviewTag = ((Activity) mContext).getLayoutInflater().inflate(R.layout.font_tag, null, false);
                    com.neopixl.pixlui.components.textview.TextView tv = (com.neopixl.pixlui.components.textview.TextView) newReviewTag.findViewById(R.id.tv_font_example);
                    tv.setCustomFont(mContext, key);
                    newReviewTag.setOnClickListener(new OpenReviewListener(key, value));
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
            if (viewArray[0] != null) {
                mScAddView.removeAllViews();
                mScAddView.addView(viewArray[0]);
            }
        }
    }


    private class OpenReviewListener implements View.OnClickListener {
        String fontName;
        Integer validity;

        OpenReviewListener(String fontName, Integer validity) {
            this.fontName = fontName;
            this.validity = validity;
        }

        @Override
        public void onClick(View v) {
            mEtUserText.setCustomFont(mContext, fontName);
            currentFont = fontName;
            mValidity = validity;
            textWatcher.afterTextChanged(mEtUserText.getText());
        }
    }

    public void setFont(String fontName, Integer validity) {
        mEtUserText.setCustomFont(mContext, fontName);
        currentFont = fontName;
        mValidity = validity;
        textWatcher.afterTextChanged(mEtUserText.getText());
    }

    public void setFont(String fontName) {
        setFont(fontName, sFontsList.get(fontName));
    }


    TextWatcher textWatcher = new TextWatcher() {
        int lastLength = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int currentLength = setCorrectedString(s.toString());
            mTvValidity.setText(currentLength + "/" + mValidity);
            if (((currentLength - 1) == mValidity && mWasLength != currentLength + 1)
                    || (currentText.length() > mWasLength && currentLength > mValidity)) {

                Log.e("FM", String.valueOf((currentLength - 1) == mValidity) + "; " + (mWasLength != currentLength + 1) + "; "
                        + (currentText.length() > mWasLength) + "; " + (currentLength > mValidity));

                String str = s.toString().substring(0, lastLength);
                mWasLength = setCorrectedString(str);
                mEtUserText.setText(str);
                mEtUserText.setSelection(lastLength);
            } else mWasLength = setCorrectedString(mEtUserText.getText().toString());
            if (currentLength >= mValidity) mTvValidity.setTextColor(Color.RED);
            else mTvValidity.setTextColor(Color.BLACK);
            lastLength = mEtUserText.length();
        }
    };

    private int setCorrectedString(String current) {
        if (current.length() > 1) {
            int lineMaxSize = getValidity(FontsManager.currentFont) / 14;
            for (int i = 1; i != current.length(); i++) {
                if (current.charAt(i) != ' ' && ((int) i / lineMaxSize) == ((double) i / lineMaxSize)) {
                    int lastSpaceIndex = current.lastIndexOf(' ', i);
                    if (lastSpaceIndex != -1 && current.substring(i - lineMaxSize, i + 1).contains(" ")) {
                        current = current.substring(0, lastSpaceIndex + 1) +
                                getSpacedString(i - lastSpaceIndex) +
                                current.substring(lastSpaceIndex + 1);
                    }
                }
                try {
                    if (!current.substring(i, current.length()).contains(" ")) break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        currentText = current;
        return current.length();
    }

    public static String getSpacedString(int count) {
        String str = "";
        for (int i = 0; i != count; i++) {
            str += " ";
        }
        return str;
    }

    public String getCurrentFont() {
        return currentFont;
    }

    private void initList() {
        sFontsList = new TreeMap<String, Integer>();
        sFontsList.put("Boomboom.otf", 336);
        sFontsList.put("Calibri.ttf", 308);
        sFontsList.put("Capture_It.ttf", 280);
        sFontsList.put("Handwriting_Bickhamscr.ttf", 532);
        sFontsList.put("Handwriting_Lazy.ttf", 308);
        sFontsList.put("Wild_honey.ttf", 280);
        sFontsList.put("Xiomara.ttf", 308);
        sFontsList.put("YesevaOne.otf", 238);
        sFontsList.put("Funny1.ttf", 252);
        sFontsList.put("Funny2.ttf", 308);
        sFontsList.put("Typewriting.ttf", 182);
        sFontsList.put("Simple.ttf", 266);
    }
}
