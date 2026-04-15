package com.hades.user.domain.exception;

import com.hades.common.exception.DomainException;

public class UserDomainException extends DomainException {

    public UserDomainException(String message) {
        super(message);
    }

    public UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
