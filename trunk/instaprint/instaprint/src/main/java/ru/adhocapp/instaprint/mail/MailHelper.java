package ru.adhocapp.instaprint.mail;

import android.os.AsyncTask;
import android.util.Log;

import ru.adhocapp.instaprint.db.DBHelper;
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

    public void sendMail(Order order) {
        SenderMailAsync async_sending = new SenderMailAsync();
        async_sending.execute(order);
    }

    private class SenderMailAsync extends AsyncTask<Object, String, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                Order order = (Order) params[0];
                MailSenderClass sender = new MailSenderClass(Const.MAIL_ACCOUNT, Const.MAIL_PASSWORD);
                sender.sendMail(order.toMailTitle(), order.toMailBody(), Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, order.getPhotoPath());
                order.setStatus(OrderStatus.EXECUTED);
                DBHelper.getInstance(null).EM.merge(order);
            } catch (Exception e) {
                Log.e(Const.LOG_TAG, e.toString(),e);
            }
            return false;
        }
    }
}