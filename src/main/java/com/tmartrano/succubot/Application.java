package com.tmartrano.succubot;

import com.tmartrano.succubot.listeners.PollListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        Token tokenClass = context.getBean(Token.class);
        String token = tokenClass.getToken();

        DiscordApi api = new DiscordApiBuilder()
                .setWaitForServersOnStartup(false)
                .setToken(token)
                .login()
                .join();


        PollListener pollListener = context.getBean(PollListener.class);
        api.addMessageCreateListener(pollListener);
    }
}
