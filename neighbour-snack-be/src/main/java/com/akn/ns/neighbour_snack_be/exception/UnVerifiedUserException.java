package com.akn.ns.neighbour_snack_be.exception;

public class UnVerifiedUserException extends ApplicationException {

    public UnVerifiedUserException(String message) {
        super(message);
    }

    public UnVerifiedUserException(String message, Throwable cause) {
        super(message, cause);
    }

}