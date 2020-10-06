package com.tmartrano.succubot.model;

import lombok.Getter;

@Getter
public class PollValidationException extends Exception {
    private String message;

    public PollValidationException(String message) {
        this.message = message;
    }
}
