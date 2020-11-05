package com.tmartrano.succubot.validation;

import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.PollValidationException;
import org.javacord.api.event.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListenerMessageValidation {

    private PollManager pollManager;

    @Autowired
    public ListenerMessageValidation(final PollManager pollManager) {
        this.pollManager = pollManager;
    }

    public void validateVoteRequest(final String[] splitMessage) throws PollValidationException {
        if (pollManager.isInactivePoll()) {
            throw new PollValidationException("Cannot vote, there's no active poll at the moment.");
        }
        if (splitMessage.length < 2) {
            throw new PollValidationException("Please supply an entry to vote for!");
        }
        try {
            Integer.parseInt(splitMessage[1]);
        } catch (NumberFormatException ex) {
            throw new PollValidationException("Invalid entry, please supply a number");
        }
    }

    public void validateAddMovieRequest(final String[] splitMessage) throws PollValidationException {
        validateCategoryExists(splitMessage);
        validateCategory(splitMessage);
        if (splitMessage.length < 3) {
            throw new PollValidationException("You don't seem to have all the required fields. Are you missing the movie category or title? Make sure to format your request as !add [category] [title]");
        }
    }

    public void validatePollGenerationRequest(final String[] splitMessage) throws PollValidationException {
        validateCategoryExists(splitMessage);
        validateCategory(splitMessage);

        if (!pollManager.isInactivePoll()) {
            throw new PollValidationException("A poll is already active! Unable to generate poll.");
        }
    }

    private void validateCategoryExists(String[] splitMessage) throws PollValidationException {
        if (splitMessage.length == 1) {
            String stringBuilder = "Please supply a category to generate a poll.\n" +
                    getListOfCategories();
            throw new PollValidationException(stringBuilder);
        }
    }

    private void validateCategory(String[] splitMessage) throws PollValidationException {
        final String category = splitMessage[1];

        final MovieCategory movieCategory = MovieCategory.forValue(category);
        if (movieCategory == null) {
            String stringBuilder = "Invalid category\n" +
                    getListOfCategories();
            throw new PollValidationException(stringBuilder);
        }
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
}
