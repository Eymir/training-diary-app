package ru.adhocapp.instaprint.mail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.FeedbackMessage;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.util.Const;

public class MailHelper {

    private static MailHelper instance;
    private Context context;

    public MailHelper(Context context) {
        this.context = context;
    }

    enum MailType {SNAIL_MAIL, CLIENT_EMAIL, FEEDBACK}

    public MailHelper() {

    }

    public static MailHelper getInstance() {
        if (instance == null) {
            instance = new MailHelper();
        }
        return instance;
    }

    public static MailHelper initInstance(Context context) {
        instance = new MailHelper(context);
        return instance;
    }

    public void sendOrderMail(SendFinishListener listener, Order order) {
        SenderMailAsync async_sending = new SenderMailAsync(listener);
        async_sending.execute(MailType.SNAIL_MAIL, order);
    }

    public void sendOrderMailToPrivateMail(SendFinishListener listener, Order order, String username, String email) {
        SenderMailAsync async_sending = new SenderMailAsync(listener);
        async_sending.execute(MailType.CLIENT_EMAIL, order, username, email);
    }

    public void sendFeedbackMail(SendFinishListener listener, FeedbackMessage feedbackMessage) {
        SenderMailAsync async_sending = new SenderMailAsync(listener);
        async_sending.execute(MailType.FEEDBACK, feedbackMessage);
    }

    private class SenderMailAsync extends AsyncTask<Object, String, Boolean> {
        private SendFinishListener listener;

        public SenderMailAsync(SendFinishListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (listener != null) listener.finish(result);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                Object o = params[0];
                if (o != null) {
                    MailType type = (MailType) o;
                    MailSender sender = new MailSender(Const.MAIL_ACCOUNT, Const.MAIL_PASSWORD);

                    switch (type) {
                        case SNAIL_MAIL: {
                            Order order = (Order) params[1];
                            sender.sendMail(order.toMailTitle(), order.toMailBody(), Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, order.getFrontSidePhotoPath(), order.getBackSidePhotoPath());
                        }
                        break;
                        case CLIENT_EMAIL: {
                            Order order = (Order) params[1];
                            String username = (String) params[2];
                            String email = (String) params[3];
                            sender.sendMail(context.getString(R.string.client_mail_subject)
                                    , username + ", " + context.getString(R.string.client_email_body), Const.MAIL_ACCOUNT, email, order.getFrontSidePhotoPath(), order.getBackSidePhotoPath());
                        }
                        break;
                        case FEEDBACK: {
                            FeedbackMessage feedbackMessage = (FeedbackMessage) params[1];
                            sender.sendMail("[FEEDBACK] " + feedbackMessage.getUsername(),
                                    feedbackMessage.getMessage() + "\nUSERNAME:" + feedbackMessage.getUsername() + "\nEMAIL: " + feedbackMessage.getEmail(),
                                    Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, null);
                        }
                        break;

                    }
                }
            } catch (Exception e) {
                Log.e(Const.LOG_TAG, e.toString(), e);
                return false;
            }
            return true;
        }
    }
}