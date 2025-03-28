/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
@Service
@Slf4j
public class EmailService
{

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.activation-url}")
    private String activationUrl;

    public void sendActivationEmail (String to, String activationCode) throws MessagingException
    {
        String subject = "Active sua conta na SkaWallet";
        String message = "<p>Olá,</p>"
                + "<p>Obrigado por se cadastrar na SkaWallet! Para activar sua conta, clique no link abaixo:</p>"
                + "<p><a href=\"" + activationUrl + activationCode + "\">Activar Conta</a></p>"
                + "<p>Se você não se cadastrou, ignore este e-mail.</p>"
                + "<p>Atenciosamente, <br> Equipe SkaWallet</p>";
        try
        {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
            log.info("✅ E-mail enviado com sucesso para {}", to);
        }
        catch (MailException e)
        {
            log.error("❌ Erro ao enviar e-mail para {}: {}", to, e.getMessage());
            throw new MessagingException("Não foi possível enviar o e-mail. Verifique se o endereço está correcto ou tente novamente mais tarde.");
        }
    }

    public void sendEmail (String to, String subject, String body)
    {
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("E-mail enviado para {}", to);
        }
        catch (MessagingException e)
        {
            log.error("Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail.");
        }
    }
}
