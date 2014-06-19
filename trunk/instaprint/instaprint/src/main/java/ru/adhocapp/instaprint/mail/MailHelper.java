package ru.adhocapp.instaprint.mail;

import android.os.AsyncTask;
import android.util.Log;

import ru.adhocapp.instaprint.db.DBHelper;
import ru.adhocapp.instaprint.db.entity.FeedbackMessage;
import ru.adhocapp.instaprint.db.entity.Order;
import ru.adhocapp.instaprint.db.entity.OrderStatus;
import ru.adhocapp.instaprint.util.Const;

public class MailHelper {

    private static MailHelper instance;

    public MailHelper() {

    }

    public static MailHelper getInstance() {
        if (instance == null) {
            instance = new MailHelper();
        }
        return instance;
    }

    public void sendOrderMail(SendFinishListener listener, Order order) {
        SenderMailAsync async_sending = new SenderMailAsync(listener);
        async_sending.execute(order);
    }

    public void sendFeedbackMail(SendFinishListener listener, FeedbackMessage feedbackMessage) {
        SenderMailAsync async_sending = new SenderMailAsync(listener);
        async_sending.execute(feedbackMessage);
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
                    if (o instanceof Order) {
                        Order order = (Order) params[0];
                        MailSender sender = new MailSender(Const.MAIL_ACCOUNT, Const.MAIL_PASSWORD);
                        sender.sendMail(order.toMailTitle(), order.toMailBody(), Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, order.getPhotoPath());
                        order.setStatus(OrderStatus.EXECUTED);
                        DBHelper.getInstance(null).EM.merge(order);
                    } else if (o instanceof FeedbackMessage) {
                        FeedbackMessage feedbackMessage = (FeedbackMessage) params[0];
                        MailSender sender = new MailSender(Const.MAIL_ACCOUNT, Const.MAIL_PASSWORD);
                        sender.sendMail("[FEEDBACK] " + feedbackMessage.getUsername(),
                                feedbackMessage.getMessage() + "\nUSERNAME:" + feedbackMessage.getUsername() + "\nEMAIL: " + feedbackMessage.getEmail(),
                                Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, null);
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