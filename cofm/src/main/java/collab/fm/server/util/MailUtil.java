package collab.fm.server.util;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class MailUtil {

	public static void sendFromGmail(String username, String pwd, String[] to,
			String title, String content) throws MessagingException {
		String host = "smtp.gmail.com";
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "true"); 
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", username);
	    props.put("mail.smtp.password", pwd);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");

	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(username));

	    InternetAddress[] toAddress = new InternetAddress[to.length];

	    // To get the array of addresses
	    for( int i=0; i < to.length; i++ ) { 
	        toAddress[i] = new InternetAddress(to[i]);
	    }

	    System.out.println(Message.RecipientType.TO);
	    
	    for( int i=0; i < toAddress.length; i++) { 
	        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	    }
	    message.setSubject(title);
	    message.setText(content);
	    Transport transport = session.getTransport("smtp");
	    transport.connect(host, username, pwd);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	}
}
