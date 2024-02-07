package com.shiraikuroko.smtpviahttp;

import com.alibaba.fastjson2.JSON;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;

@Controller
public class Redirector {
    private JavaMailSenderImpl createMailSender(EmailRequest emailRequest) {
        // 创建JavaMailSenderImpl实例
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 设置SMTP服务器地址和端口
        mailSender.setHost(emailRequest.getSmtpServer());
        mailSender.setPort(emailRequest.getSmtpPort());
        if (emailRequest.isUseSsl()){
            mailSender.setProtocol("smtps");
        }

        // 设置用户名和密码
        mailSender.setUsername(emailRequest.getUsername());
        mailSender.setPassword(emailRequest.getPassword());

        // 配置邮件客户端属性
        Properties props = mailSender.getJavaMailProperties();
        if (emailRequest.isUseSsl()){
            props.put("mail.transport.protocol", "smtps");
        }else {
            props.put("mail.transport.protocol", "smtp");
        }
        props.put("mail.smtp.auth", "true");

        return mailSender;
    }

    @PostMapping("/sendEmail")
    @ResponseBody
    public String sendEmail(@RequestBody String jsonString) {
        try {
            // 使用Fastjson反序列化JSON字符串为EmailRequest对象
            EmailRequest emailRequest = JSON.parseObject(jsonString, EmailRequest.class);

            // 创建邮件发送器
            JavaMailSenderImpl mailSender = createMailSender(emailRequest);

            // 创建 MimeMessage 实例
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 设置邮件发送者
            helper.setFrom(emailRequest.getFrom());

            // 设置邮件接收者
            helper.setTo(emailRequest.getTo());

            // 设置邮件主题
            helper.setSubject(emailRequest.getSubject());

            // 设置邮件内容
            helper.setText(emailRequest.getBody(), true);

            // 发送邮件
            mailSender.send(message);

            return JSON.toJSONString(new EmailResponse(true,null));
        } catch (Exception ex) {
            return JSON.toJSONString(new EmailResponse(false,ex.getLocalizedMessage()));
        }
    }
}
