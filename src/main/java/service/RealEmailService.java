package service;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RealEmailService {
    private String smtpHost = "smtp.gmail.com";
    private String username = "s12219919@stu.najah.edu";
    private String password = "dbedhiuxfbymhpbw";

    public void sendEmail(String to, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(username));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            email.setSubject("Library Overdue Reminder");
            email.setText(message);

            Transport.send(email);
            System.out.println("Real email sent to " + to);
        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }
}
