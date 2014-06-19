package ru.adhocapp.instaprint.mail;

import android.util.Log;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ru.adhocapp.instaprint.util.Const;

public class MailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.yandex.ru";
    private String user;
    private String password;
    private Session session;
    private Multipart _multipart;

    public MailSender(String user, String password) {
        this.user = user;
        this.password = password;
        _multipart = new MimeMultipart();
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);

            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(recipients));

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            _multipart.addBodyPart(messageBodyPart);

            if (filename!=null && !filename.equalsIgnoreCase("")) {
                BodyPart attachBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                attachBodyPart.setDataHandler(new DataHandler(source));
                attachBodyPart.setFileName(filename);
                _multipart.addBodyPart(attachBodyPart);
            }

            message.setContent(_multipart);
            Transport.send(message);

        } catch (Exception e) {
            Log.e(Const.LOG_TAG, "error sendOrderMail - " + e.toString(), e);
        }
    }
}
