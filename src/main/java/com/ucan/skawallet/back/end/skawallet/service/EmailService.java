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

    public void sendActivationEmail (String to, String activationCode)
    {
        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Activação de Conta");
            helper.setText("<p>Para activar sua conta, clique no link abaixo:</p>"
                    + "<a href=\"" + activationUrl + activationCode + "\">Activar Conta</a>", true);

            mailSender.send(message);
        }
        catch (MessagingException e)
        {
            // Trate a exceção conforme necessário
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
