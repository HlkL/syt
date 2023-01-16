package com.hg.user.utils;

import com.hg.common.exception.SytException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author hougen
 * @program syt_parent
 * @description
 * @create 2023-01-16 15:42
 */
@Slf4j
@Component
public class MailUtil {
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private String text( String code ) {
        return "<div style=\"text-align: center\">" + "<br><br><br><br><br><br><br><br><br><br>" +
                    "请在验证码有效期输入验证码完成登录：" +
                    "<span style=\"color: red\">"+ code +"</span>" +
                "</div>";
    }

    public void sendSimpleMail( String to, String code ) {
        SimpleMailMessage message = new SimpleMailMessage();
        //发件人名称
        message.setFrom( from );
        //收件人邮件地址，可以是多个，使用逗号分割
        message.setTo(to);
        //邮件的主题
        message.setSubject("用户登录");
        //邮件的正文内容
        message.setText( text( code ) );

        log.info("开始发送文本邮件");
        try {
            mailSender.send(message);
            log.info("文本邮件已经发送");
        } catch (Exception e) {
            throw new SytException( "邮件发送失败",233 );
        }
    }
}


