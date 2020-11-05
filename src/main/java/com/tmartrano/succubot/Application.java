package com.tmartrano.succubot;

import com.tmartrano.succubot.listeners.HelpListener;
import com.tmartrano.succubot.listeners.MovieManagementListener;
import com.tmartrano.succubot.listeners.PollListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final ApplicationContext context = SpringApplication.run(Application.class, args);

        final Token tokenClass = context.getBean(Token.class);
        final String token = tokenClass.getToken();

        final DiscordApi api = new DiscordApiBuilder()
                .setWaitForServersOnStartup(false)
                .setToken(token)
                .login()
                .join();

        final PollListener pollListener = context.getBean(PollListener.class);
        final MovieManagementListener movieManagementListener = context.getBean((MovieManagementListener.class));
        final HelpListener helpListener = context.getBean(HelpListener.class);

        api.addMessageCreateListener(pollListener);
        api.addMessageCreateListener(movieManagementListener);
        api.addMessageCreateListener(helpListener);
    }
}
