package com.projeto.backend.exceptions;

import com.projeto.backend.util.MessageUtils;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super(MessageUtils.USER_ALREADY_EXISTS);
    }
}
