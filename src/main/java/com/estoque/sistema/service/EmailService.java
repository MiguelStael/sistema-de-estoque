package com.estoque.sistema.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.notificacao.email.destino:}")
    private String emailDestino;

    @Async
    public void enviarAlertaEstoqueCritico(String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, 
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                StandardCharsets.UTF_8.name());

            String para = (emailDestino != null) ? emailDestino : "";
            helper.setTo(para);
            helper.setSubject("ALERTA: Itens em Estoque Crítico - CoreEstoque");
            helper.setText(htmlContent, true);
            helper.setFrom("sistema@coreestoque.com");

            mailSender.send(mimeMessage);
            log.info("E-mail de alerta enviado com sucesso para: {}", emailDestino);
            
        } catch (MessagingException e) {
            log.error("Falha ao enviar e-mail de alerta: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar envio de e-mail.");
        }
    }

    @Async
    public void enviarEmailPersonalizado(com.estoque.sistema.dto.EmailRequestDTO dto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    StandardCharsets.UTF_8.name());

            helper.setTo(dto.getPara());
            helper.setSubject(dto.getAssunto());
            helper.setText(dto.getConteudo(), true);
            helper.setFrom("sistema@coreestoque.com");

            mailSender.send(mimeMessage);
            log.info("E-mail personalizado enviado com sucesso para: {}", dto.getPara());

        } catch (MessagingException e) {
            log.error("Falha ao enviar e-mail personalizado: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar envio de e-mail.");
        }
    }
}
