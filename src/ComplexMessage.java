package src;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/** 创建同时包含内嵌图片和附件的复杂邮件 */
public class ComplexMessage {
    public static void main(String[] args) throws IOException, MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = createMessage(session);
        message.writeTo(new FileOutputStream("ComplexMessage.eml"));
    }

    public static MimeMessage createMessage(Session session) throws MessagingException {
        String from = "1328335451@qq.com";
        String to = "1328335451@qq.com";
        String subject = "HTML邮件";
        String body = "测试嵌入图片和插入附件" +
           "<img src=\" https://www.runoob.com/wp-content/uploads/2013/06/image-icon.png\">";

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart contentPart = createContent(body, "img.png");
        MimeBodyPart attachPart = createAttachment("httpclient-4.2.3.jar");

        MimeMultipart allMultipart = new MimeMultipart("mixed");
        allMultipart.addBodyPart(contentPart);
        allMultipart.addBodyPart(attachPart);

        message.setContent(allMultipart);
        message.saveChanges();
        return message;
    }

    public static MimeBodyPart createContent(String body, String filename) throws MessagingException {
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeMultipart contentMultipart = new MimeMultipart("related");

        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(body, "text/html;charset=utf-8");
        contentMultipart.addBodyPart(htmlBodyPart);

        MimeBodyPart gifBodyPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(filename);
        gifBodyPart.setDataHandler(new DataHandler(fds));
        gifBodyPart.setContentID("img_png");
        contentMultipart.addBodyPart(gifBodyPart);

        contentPart.setContent(contentMultipart);
        return contentPart;
    }

    public static MimeBodyPart createAttachment(String filename) throws MessagingException {
        MimeBodyPart attachPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(filename);
        attachPart.setDataHandler(new DataHandler(fds));
        attachPart.setFileName(fds.getName());
        return attachPart;
    }
}
