package com.tmartrano.succubot.listeners;

import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.PollEntry;
import com.tmartrano.succubot.model.PollValidationException;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollListener implements MessageCreateListener {

    private PollManager pollManager;

    @Autowired
    public PollListener(final PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent message) {
        String[] splitMessage = message.getMessageContent().split("\\s+");

        /*Creates a poll*/
        if (validatePollGenerationRequest(message, splitMessage)) {
            final MovieCategory category = MovieCategory.forValue(splitMessage[1]);
            try {
                final List<PollEntry> pollEntries = pollManager.generateMoviePoll(category);
            } catch (PollValidationException ex) {
                message.getChannel().sendMessage(ex.getMessage());
            }
        }
    }

    //region Helper Methods
    private boolean validatePollGenerationRequest(final MessageCreateEvent message, final String[] splitMessage) {
        if (splitMessage == null) {
            message.getChannel().sendMessage("How the fuck did you even get this to break like this");
            return false;
        }

        final String command = splitMessage[0];

        if (!command.equalsIgnoreCase("!poll")) {
            return false;
        }

        if (splitMessage.length < 2) {
            String stringBuilder = "Please supply a category to generate a poll.\n" +
                    getListOfCategories();
            message.getChannel().sendMessage(stringBuilder);
            return false;
        }

        final String category = splitMessage[1];

        if (!pollManager.isActivePoll()) {
            message.getChannel().sendMessage("A poll is already active! Unable to generate poll.");
            return false;
        }

        final MovieCategory movieCategory = MovieCategory.forValue(category);
        if (movieCategory == null) {
            String stringBuilder = "Invalid category\n" +
                    getListOfCategories();
            message.getChannel().sendMessage(stringBuilder);
            return false;
        }
        return true;
    }

    private String getListOfCategories() {
        final List<String> movieKeys = MovieCategory.getKeys();
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Here's a list of valid categories:\n");

        for (final String key : movieKeys) {
            stringBuilder.append("- ").append(key).append("\n");
        }
        return stringBuilder.toString();
    }
    //endRegion
}
