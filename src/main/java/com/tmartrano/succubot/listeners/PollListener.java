package com.tmartrano.succubot.listeners;

import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.PollEntry;
import com.tmartrano.succubot.model.PollValidationException;
import com.tmartrano.succubot.validation.ListenerMessageValidation;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollListener implements MessageCreateListener {

    private PollManager pollManager;

    private ListenerMessageValidation listenerMessageValidation;

    @Autowired
    public PollListener(final ListenerMessageValidation listenerMessageValidation,
                        final PollManager pollManager) {
        this.listenerMessageValidation = listenerMessageValidation;
        this.pollManager = pollManager;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent message) {
        String[] splitMessage = message.getMessageContent().split("\\s+");
        final String command = splitMessage[0];

        try {
            /*Creates a poll*/
            if (command.equalsIgnoreCase("!poll")) {

                listenerMessageValidation.validatePollGenerationRequest(splitMessage);

                final MovieCategory category = MovieCategory.forValue(splitMessage[1]);
                final List<PollEntry> pollEntries = pollManager.generateMoviePoll(category);
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Here's this weeks poll to vote enter !vote [entryNumber]:\n");
                for (PollEntry pollEntry : pollEntries) {
                    stringBuilder.append(pollEntry.getPollEntryNumber())
                            .append(": ")
                            .append(pollEntry.getPollEntryDescription())
                            .append("\n");
                }
                message.getChannel().sendMessage(stringBuilder.toString());
                //TODO figure out how to ping users
            }
            //Vote for an entry
            if (command.equalsIgnoreCase("!vote")) {
                listenerMessageValidation.validateVoteRequest(splitMessage);
                final String username = message.getMessageAuthor().getName();
                final int movieVoteEntry = Integer.parseInt(splitMessage[1]);

                pollManager.voteForMovie(username, movieVoteEntry);
                message.getChannel().sendMessage("Vote successfully recorded.");
            }
        } catch (PollValidationException ex) {
            message.getChannel().sendMessage(ex.getMessage());
        }
    }
}
