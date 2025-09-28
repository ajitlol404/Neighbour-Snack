package com.akn.ns.neighbour_snack_be.exception;

public class ProductException extends ApplicationException {

    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }

}