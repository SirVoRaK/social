package br.com.rodolfo.social.utils;

import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {
    private final Session session;

    private final String email;

    public SendEmail() {
        String email;
        String password;
        try {
            Dotenv dotenv = Dotenv.load();
            email = dotenv.get("EMAIL");
            password = dotenv.get("EMAIL_PASSWORD");
        } catch (Exception e) {
            email = System.getenv("EMAIL");
            password = System.getenv("EMAIL_PASSWORD");
        }
        this.email = email;
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        String finalEmail = email;
        String finalPassword = password;
        this.session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(finalEmail, finalPassword);
            }
        });
    }

    public void send(String to, String subject, String msg) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(this.email));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(msg);
        Transport.send(message);
    }
}
