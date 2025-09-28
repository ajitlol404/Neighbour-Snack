package com.akn.ns.neighbour_snack_be.dto;

public record MessageDto(MessageType type, String message) {
    public enum MessageType {
        DANGER, SUCCESS, WARNING, INFO
    }
}
