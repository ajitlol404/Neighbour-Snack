package com.akn.ns.neighbour_snack_be.service;

import com.akn.ns.neighbour_snack_be.dto.EmailRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpResponseDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpToggleRequestDto;
import com.akn.ns.neighbour_snack_be.entity.Smtp;
import com.akn.ns.neighbour_snack_be.utility.EmailTemplate;

import java.util.List;

public interface SmtpService {

    SmtpResponseDto createSmtp(SmtpRequestDto smtpRequestDto);

    SmtpResponseDto updateSmtp(String code, SmtpRequestDto smtpRequestDto);

    void deleteSmtp(String code);

    List<SmtpResponseDto> getAllSmtps();

    SmtpResponseDto getSmtpByCode(String code);

    SmtpResponseDto getActiveSmtp();

    SmtpResponseDto toggleSmtpStatus(String code, SmtpToggleRequestDto smtpToggleRequestDto);

    Smtp getActiveSmtpEntity();

    void sendEmail(EmailRequestDto emailRequestDTO);

    void sendEmail(String toEmail, String subject, String htmlBody);

    void sendEmail(String toEmail, EmailTemplate emailTemplate, String body);

}