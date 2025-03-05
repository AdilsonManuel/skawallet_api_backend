/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucan.skawallet.back.end.skawallet.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService
{

    private final JavaMailSender mailSender;

    @Value("${spring.email.from}")
    private String fromEmail;

    @Value("${spring.email.activation-url}")
    private String activationUrl;

    public void sendAccountActivationEmail(String recipientEmail, String activationCode)
    {
        String subject = "Activação de Conta - SkaWallet";
        String activationLink = activationUrl + activationCode;

        String content = """
                <html>
                <body>
                    <h2>Bem-vindo à SkaWallet!</h2>
                    <p>Clique no link abaixo para activar sua conta:</p>
                    <a href="%s">Activar Conta</a>
                    <br/><br/>
                    <p>Se você não solicitou este cadastro, ignore este e-mail.</p>
                </body>
                </html>
                """.formatted(activationLink);

        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("E-mail enviado com sucesso para {}", recipientEmail);
        }
        catch (MessagingException e)
        {
            log.error("Erro ao enviar e-mail: {}", e.getMessage());
        }
    }
}
