package com.projeto.backend.exceptions;

import com.projeto.backend.util.MessageUtils;

public class ServicoAlreadyExistsException extends RuntimeException {

    public ServicoAlreadyExistsException() {
        super(MessageUtils.SEGUIDOR_ALREADY_EXISTS);
    }
}
