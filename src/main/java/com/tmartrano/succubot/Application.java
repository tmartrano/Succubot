package com.tmartrano.succubot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

    @Value("${discord.api.token}")
    private static String token;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join();
    }
}
