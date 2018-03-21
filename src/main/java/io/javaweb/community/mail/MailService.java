package io.javaweb.community.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


/**
 * Created by KevinBlandy on 2018/2/5 12:58
 */
@Component
public class MailService {

//    @Autowired
//    private MailSender mailSender;
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单的邮件
     * @param to
     * @param title
     * @param content
     * @throws Exception
     */
    public void sendSimpleMail(String to,String title,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        javaMailSender.send(message);
    }
    
    /**
     * 发送html邮件
     * @param to
     * @param title
     * @param content
     * @throws MessagingException
     */
    public void sendHTMLMail(String to,String title,String content) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(this.from);
		helper.setTo(to);
		helper.setSubject(title);
		helper.setText(content, true);
		javaMailSender.send(message);
    }
}
