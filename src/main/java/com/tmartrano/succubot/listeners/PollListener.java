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
                stringBuilder.append("<:susanping:774007071884312636> @everyone Here's this weeks poll to vote enter !vote [entryNumber]:\n");
                for (PollEntry pollEntry : pollEntries) {
                    stringBuilder.append(pollEntry.getPollEntryNumber())
                            .append(": ")
                            .append(pollEntry.getPollEntryDescription())
                            .append("\n");
                }
                message.getChannel().sendMessage(stringBuilder.toString());
            }
            //Vote for an entry
            if (command.equalsIgnoreCase("!vote")) {
                listenerMessageValidation.validateVoteRequest(splitMessage);
                final String username = message.getMessageAuthor().getName();
                final int movieVoteEntry = Integer.parseInt(splitMessage[1]);

                pollManager.voteForMovie(username, movieVoteEntry);
                message.deleteMessage();
                message.getChannel().sendMessage(username+" has cast a vote.");
            }
            //Close the poll and return the winner
            if (command.equalsIgnoreCase("!closePoll")) {
                if (pollManager.isInactivePoll()) {
                    throw new PollValidationException("Cannot return winner, there's no active poll!");
                }

                final PollEntry winningEntry = pollManager.closePoll();
                message.getChannel().sendMessage("The winning movie is: " + winningEntry.getPollEntryDescription() + " <:mrtartar:666753603641278465>");
            }
        } catch (PollValidationException ex) {
            message.getChannel().sendMessage(ex.getMessage());
        }
    }
}
