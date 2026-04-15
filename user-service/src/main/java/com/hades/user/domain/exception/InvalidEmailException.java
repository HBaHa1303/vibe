package com.hades.user.domain.exception;

public class InvalidEmailException extends UserDomainException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
