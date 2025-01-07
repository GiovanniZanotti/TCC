package com.projeto.backend.exceptions;

import com.projeto.backend.util.MessageUtils;

public class SeguidorAlreadyExistsException extends RuntimeException {

    public SeguidorAlreadyExistsException() {
        super(MessageUtils.SEGUIDOR_ALREADY_EXISTS);
    }
}
