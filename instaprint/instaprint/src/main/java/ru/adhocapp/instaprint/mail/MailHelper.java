package ru.adhocapp.instaprint.mail;

import android.os.AsyncTask;
import android.util.Log;

import ru.adhocapp.instaprint.util.Const;

public class MailHelper  {

    private static MailHelper instance;
    private String title;
    private String body;
    private String attach;

    public MailHelper(String t,String b,String a)  {
        title = t;
        body = b;
        attach = a;
    }

    public static MailHelper getInstance(String t, String b, String a){
        if (instance == null){
            instance = new MailHelper(t,b,a);
        }
        return instance;
    }

    public void sendMail(){
        senderMailAsync async_sending = new senderMailAsync();
	    async_sending.execute();
    }

    private class senderMailAsync extends AsyncTask<Object, String, Boolean> {

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
                MailSenderClass sender = new MailSenderClass(Const.MAIL_ACCOUNT, Const.MAIL_PASSWORD);
                sender.sendMail(title, body, Const.MAIL_ACCOUNT, Const.MAIL_ACCOUNT, attach);
			} catch (Exception e) {
                Log.e(Const.LOG_TAG, e.toString());
			}
			
			return false;
		}
	}
}