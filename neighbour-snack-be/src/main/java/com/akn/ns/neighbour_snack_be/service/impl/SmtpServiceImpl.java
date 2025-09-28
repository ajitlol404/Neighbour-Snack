package com.akn.ns.neighbour_snack_be.service.impl;

import com.akn.ns.neighbour_snack_be.dto.EmailRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpResponseDto;
import com.akn.ns.neighbour_snack_be.entity.Smtp;
import com.akn.ns.neighbour_snack_be.exception.SmtpException;
import com.akn.ns.neighbour_snack_be.repository.SmtpRepository;
import com.akn.ns.neighbour_snack_be.service.SmtpService;
import com.akn.ns.neighbour_snack_be.utility.AppUtil;
import com.akn.ns.neighbour_snack_be.utility.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.*;
import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class SmtpServiceImpl implements SmtpService {

    private final SmtpRepository smtpRepository;

    @Override
    public SmtpResponseDto createSmtp(SmtpDto.SmtpRequestDto smtpRequestDto) {

        // Check if the max limit of 5 configurations has been reached
        if (smtpRepository.count() >= MAX_SMTP_CONFIGURATIONS) {
            throw new SmtpException("Maximum of " + MAX_SMTP_CONFIGURATIONS + " SMTP configurations allowed");
        }


        if (smtpRepository.existsByName(smtpRequestDto.name().strip().toLowerCase())) {
            throw new SmtpException("SMTP with name [" + smtpRequestDto.name() + "] already exists");
        }

        // Deactivate all if new one is set active
        if (smtpRequestDto.isActive()) {
            smtpRepository.deactivateAll();
        }

        Smtp saved = smtpRepository.save(smtpRequestDto.toEntity());

        return SmtpResponseDto.fromEntity(saved);

    }

    @Override
    public SmtpResponseDto updateSmtp(String code, SmtpDto.SmtpRequestDto smtpRequestDto) {
        Smtp existing = smtpRepository.findSmtpByCode(code);

        // Prevent disabling the only active config
        if (existing.isActive() && !smtpRequestDto.isActive()
                && smtpRepository.countByIsActiveTrue() == 1) {
            throw new SmtpException("Cannot disable the only active SMTP configuration");
        }

        // Ensure the configuration works before saving
        testSmtpConfiguration(smtpRequestDto);

        if (smtpRequestDto.isActive()) {
            smtpRepository.deactivateAll();
        }

        return SmtpResponseDto.fromEntity(smtpRepository.save(smtpRequestDto.updateSmtp(existing)));
    }

    @Override
    public void deleteSmtp(String code) {
        Smtp smtp = smtpRepository.findSmtpByCode(code);

        // Prevent deleting if it's the only active config
        if (smtp.isActive() && smtpRepository.countByIsActiveTrue() == 1) {
            throw new SmtpException("Cannot delete the active SMTP configuration");
        }

        smtpRepository.delete(smtp);
    }

    @Override
    public List<SmtpResponseDto> getAllSmtps() {
        return smtpRepository.findAll().stream()
                .map(SmtpResponseDto::fromEntity)
                .toList();
    }

    @Override
    public SmtpResponseDto getSmtpByCode(String code) {
        return SmtpResponseDto.fromEntity(smtpRepository.findSmtpByCode(code));
    }

    @Override
    public SmtpResponseDto getActiveSmtp() {
        return SmtpResponseDto.fromEntity(smtpRepository.findActiveSmtp());
    }

    @Override
    public SmtpResponseDto toggleSmtpStatus(String code, SmtpDto.SmtpToggleRequestDto smtpToggleRequestDto) {
        Smtp smtp = smtpRepository.findSmtpByCode(code);
        boolean isActive = smtpToggleRequestDto.isActive();

        // Prevent disabling the only active SMTP
        if (!isActive && smtp.isActive() && smtpRepository.countByIsActiveTrue() == 1) {
            throw new SmtpException("Cannot disable the only active SMTP configuration");
        }

        if (isActive && !smtp.isActive()) {
            smtpRepository.deactivateAll();
        }

        smtp.setActive(isActive);

        return SmtpResponseDto.fromEntity(smtpRepository.save(smtp));
    }

    @Override
    public Smtp getActiveSmtpEntity() {
        Smtp activeSmtp = smtpRepository.findActiveSmtp();
        return Smtp.builder()
                .name(activeSmtp.getName())
                .host(activeSmtp.getHost())
                .port(activeSmtp.getPort())
                .username(activeSmtp.getUsername())
                .password(AppUtil.decodeBase64(activeSmtp.getPassword()))
                .isSsl(activeSmtp.isSsl())
                .isActive(activeSmtp.isActive())
                .build();
    }

    @Override
    public void sendEmail(EmailRequestDto emailRequestDTO) {
        Smtp smtp = getActiveSmtpEntity();
        JavaMailSenderImpl mailSender = configureMailSender(smtp);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, TRUE, UTF_8.name());

            helper.setFrom(smtp.getUsername());
            helper.setTo(emailRequestDTO.toEmail());
            helper.setSubject(emailRequestDTO.subject());
            helper.setText(emailRequestDTO.bodyHtml(), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new SmtpException("Failed to send email", e);
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String htmlBody) {
        Smtp smtp = getActiveSmtpEntity();
        JavaMailSenderImpl mailSender = configureMailSender(smtp);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, TRUE, UTF_8.name());

            helper.setFrom(smtp.getUsername());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new SmtpException("Failed to send email", e);
        }
    }

    @Override
    public void sendEmail(String toEmail, EmailTemplate emailTemplate, String body) {
        Smtp smtp = getActiveSmtpEntity();
        JavaMailSenderImpl mailSender = configureMailSender(smtp);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, TRUE, UTF_8.name());

            helper.setFrom(smtp.getUsername());
            helper.setTo(toEmail);
            helper.setSubject(emailTemplate.getSubject());
            helper.setText(body, TRUE);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new SmtpException("Failed to send email", e);
        }
    }

    private void testSmtpConfiguration(SmtpDto.SmtpRequestDto smtpRequestDTO) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpRequestDTO.host());
        mailSender.setPort(smtpRequestDTO.port());
        mailSender.setUsername(smtpRequestDTO.username());
        mailSender.setPassword(smtpRequestDTO.password());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_AUTH, TRUE.toString());
        properties.put(MAIL_SMTP_STARTTLS, Boolean.toString(smtpRequestDTO.isSsl()));

        properties.put(MAIL_SMTP_CONNECTIONTIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));
        properties.put(MAIL_SMTP_TIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));
        properties.put(MAIL_SMTP_WRITETIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));

        try {
            mailSender.testConnection();
        } catch (MessagingException e) {
            throw new SmtpException("SMTP connection failed: " + e.getMessage(), e);
        }
    }

    private JavaMailSenderImpl configureMailSender(Smtp smtp) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtp.getHost());
        mailSender.setPort(smtp.getPort());
        mailSender.setUsername(smtp.getUsername());
        mailSender.setPassword(smtp.getPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_AUTH, TRUE.toString());
        properties.put(MAIL_SMTP_STARTTLS, Boolean.toString(smtp.isSsl()));
        properties.put(MAIL_SMTP_CONNECTIONTIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));
        properties.put(MAIL_SMTP_TIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));
        properties.put(MAIL_SMTP_WRITETIMEOUT, String.valueOf(SMTP_TIMEOUT_MS));

        return mailSender;
    }

}
