package com.akn.ns.neighbour_snack_be.dto;

public record EmailRequestDto(
        String toEmail,
        String subject,
        String bodyHtml
) {
}
