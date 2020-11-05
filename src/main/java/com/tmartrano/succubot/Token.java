package com.tmartrano.succubot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class Token {
    @Value(value = "${discord.api.token}")
    private String token;

    String getToken()
    {
        return token;
    }
}
