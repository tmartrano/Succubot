package com.tmartrano.succubot.listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Component;

@Component
public class HelpListener implements MessageCreateListener {

    //!help command
    //r
    //!add [category] [title] - Add a movie to the given users list, validates the category and title
    //!delete [title] - Deletes a movie based on it's title, ignores case, checks user deleting the title to make sure it matches
    //!myList - Shows all movies for a given user
    //!allMovies - Shows all movies from all users

    @Override
    public void onMessageCreate(MessageCreateEvent message) {
        String[] splitMessage = message.getMessageContent().split("\\s+");
        final String command = splitMessage[0];

        if (command.equalsIgnoreCase("!help")) {
            String helpString = "Here's a list of commands: \n" +
                    "<:mrtartar:666753603641278465> **!poll [category]** - Generates a new poll based on a category, will check if a poll is already active, if it is, do not create a poll. Please let Teal be the only one to run this command until proper role checking is in place!\n" +
                    "<:mrtartar:666753603641278465> **!closePoll** - closes the poll and returns the winning vote. Please let only Teal run this until proper role checking is in place!\n" +
                    "<:mrtartar:666753603641278465> **!vote [poll entry number]** - Vote for a given poll entry\n" +
                    "<:mrtartar:666753603641278465> **!add [category] [title]** - Add a movie to the given users list\n" +
                    "<:mrtartar:666753603641278465> **!delete [title]** - Deletes a movie based on it's title\n" +
                    "<:mrtartar:666753603641278465> **!myList** - Shows all movies for a given user\n" +
                    "<:mrtartar:666753603641278465> **!allMovies** - Shows all movies from all users";
            message.getChannel().sendMessage(helpString);
        }
    }
}
