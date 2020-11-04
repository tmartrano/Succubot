package com.tmartrano.succubot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Token {
    @Value(value = "${discord.api.token}")
    public String token;

    public String getToken()
    {
        return token;
    }
}
