package com.langyastudio.edu.common.third;

import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


/**
 * 邮件服务
 * @author langyastuodo
 */
@Log4j2
@Component
public class JavaMail
{
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.fromMail.addr}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    public Boolean sendSimpleMail(String to, String subject, String content)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try
        {
            mailSender.send(message);
            return true;
        }
        catch (Exception e)
        {
            throw new MyException(e.getMessage());
        }
    }

    /**
     * 发送html邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    public Boolean sendHtmlMail(String to, String subject, String content)
    {
        MimeMessage message = mailSender.createMimeMessage();

        try
        {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            return true;
        }
        catch (MessagingException e)
        {
            throw new MyException(e.getMessage());
        }
    }


    /**
     * 发送带附件的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public Boolean sendAttachmentsMail(String to, String subject, String content, String filePath)
    {
        MimeMessage message = mailSender.createMimeMessage();

        try
        {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file     = new FileSystemResource(new File(filePath));
            String             fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            //helper.addAttachment("test"+fileName, file);

            mailSender.send(message);
            return true;
        }
        catch (MessagingException e)
        {
            throw new MyException(e.getMessage());
        }
    }


    /**
     * 发送正文中有静态资源（图片）的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param rscPath
     * @param rscId
     */
    public Boolean sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId)
    {
        MimeMessage message = mailSender.createMimeMessage();

        try
        {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            mailSender.send(message);
            return true;
        }
        catch (MessagingException e)
        {
            throw new MyException(e.getMessage());
        }
    }
}
