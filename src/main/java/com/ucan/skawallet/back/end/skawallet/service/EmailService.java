/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService
{

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService (JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail (String toEmail, String verificationCode)
    {
        String subject = "Ativação da Conta - Skawallet";
        String message = String.format("""
                Olá,

                Obrigado por se cadastrar na Skawallet! 
                Para ativar sua conta, clique no link abaixo:

                http://localhost:8080/api/v1/users/verify?code=%s

                Se você não solicitou este cadastro, ignore este e-mail.

                Atenciosamente,
                Equipe Skawallet
                """, verificationCode);

        sendEmail(toEmail, subject, message);
    }

    public void sendEmail (String toEmail, String subject, String message)
    {
        try
        {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(message, false);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);

            mailSender.send(mimeMessage);
            log.info("📧 E-mail enviado para: {}", toEmail);
        }
        catch (MessagingException e)
        {
            log.error("❌ Erro ao enviar e-mail: {}", e.getMessage());
        }
    }
}
