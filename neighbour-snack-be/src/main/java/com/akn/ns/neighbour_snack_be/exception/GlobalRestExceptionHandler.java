package com.akn.ns.neighbour_snack_be.exception;

import com.akn.ns.neighbour_snack_be.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponseDto handleAuthenticationExceptions(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponseDto.of(
                UNAUTHORIZED.value(),
                UNAUTHORIZED.getReasonPhrase(),
                "Invalid email or password",
                request.getRequestURI(),
                request.getMethod(),
                ex.getClass().getName()
        );
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponseDto handleApplicationException(ApplicationException ae, HttpServletRequest request) {
        return ErrorResponseDto.of(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                ae.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                ae.getClass().getName()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException iae, HttpServletRequest request) {
        return ErrorResponseDto.of(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                iae.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                iae.getClass().getName()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponseDto handleIllegalStateException(IllegalStateException ise, HttpServletRequest request) {
        return ErrorResponseDto.of(
                CONFLICT.value(),
                CONFLICT.getReasonPhrase(),
                ise.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                ise.getClass().getName()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGenericException(Exception e, HttpServletRequest request) {
        return ErrorResponseDto.of(
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI(),
                request.getMethod(),
                e.getClass().getName()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponseDto handleNoSuchElementException(NoSuchElementException nsee, HttpServletRequest request) {
        return ErrorResponseDto.of(
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                nsee.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                nsee.getClass().getName()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponseDto handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException hrmse, HttpServletRequest request) {
        return ErrorResponseDto.of(
                METHOD_NOT_ALLOWED.value(),
                METHOD_NOT_ALLOWED.getReasonPhrase(),
                hrmse.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                hrmse.getClass().getName()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponseDto handleValidationErrors(MethodArgumentNotValidException manve, HttpServletRequest request) {
        Map<String, List<String>> fieldErrors = manve.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                Collectors.toList()
                        )
                ));

        return ErrorResponseDto.of(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                request.getMethod(),
                manve.getClass().getName(),
                fieldErrors
        );
    }

}

