package fr.polytech;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MailerServlet")
public class MailerServlet extends HttpServlet {

  private static ResourceBundle bundle = ResourceBundle.getBundle("configMailer");
  private String appName = bundle.getString("app.name");

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String address = request.getParameter("address");
    String subject = request.getParameter("subject");
    String content = request.getParameter("content");
    if (address != null && content != null) {
      try {
        sendSimpleMail(address, subject, content);
      } catch (AddressException e) {
        response.setStatus(400);
        response.getWriter().print("Wrong address.");
        return;
      } catch (MessagingException e) {
        response.setStatus(400);
        response.getWriter().print("Messaging problem.");
        return;
      } catch (UnsupportedEncodingException e) {
        response.setStatus(400);
        response.getWriter().print("Unsupported encoding.");
        return;
      }
      response.getWriter().print("Sending email to " + address);
    } else {
      response.getWriter().print("Need address/content.");
    }
  }

  private void sendSimpleMail(String address, String subject, String content)
      throws AddressException, MessagingException, UnsupportedEncodingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress("no-reply@" + appName + ".appspotmail.com", "polyshare"));
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address, "User"));
    msg.setSubject(subject);
    msg.setText(content);
    Transport.send(msg);
  }
}
