package com.akn.ns.neighbour_snack_be.service;

import com.akn.ns.neighbour_snack_be.dto.EmailRequestDto;
import com.akn.ns.neighbour_snack_be.dto.PaginationResponse;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpRequestDto;
import com.akn.ns.neighbour_snack_be.dto.SmtpDto.SmtpResponseDto;
import com.akn.ns.neighbour_snack_be.entity.Smtp;
import com.akn.ns.neighbour_snack_be.utility.EmailTemplate;

public interface SmtpService {

    SmtpResponseDto createSmtp(SmtpRequestDto smtpRequestDto);

    SmtpResponseDto updateSmtp(String code, SmtpRequestDto smtpRequestDto);

    void deleteSmtp(String code);

    PaginationResponse<SmtpResponseDto> getAllSmtps(SmtpDto.SmtpFilterRequest smtpFilterRequest);

    SmtpResponseDto getSmtpByCode(String code);

    SmtpResponseDto getActiveSmtp();

    SmtpResponseDto toggleSmtpStatus(String code);

    Smtp getActiveSmtpEntity();

    void sendEmail(EmailRequestDto emailRequestDTO);

    void sendEmail(String toEmail, String subject, String htmlBody);

    void sendEmail(String toEmail, EmailTemplate emailTemplate, String body);

}