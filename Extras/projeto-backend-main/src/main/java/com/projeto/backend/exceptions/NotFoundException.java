package com.projeto.backend.exceptions;

import com.projeto.backend.util.MessageUtils;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super(MessageUtils.USER_NOT_FOUND);
    }
}
