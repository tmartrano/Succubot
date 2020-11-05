package com.tmartrano.succubot.listeners;

import com.tmartrano.succubot.logic.MovieListManager;
import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.MovieEntry;
import com.tmartrano.succubot.model.PollValidationException;
import com.tmartrano.succubot.validation.ListenerMessageValidation;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieManagementListener implements MessageCreateListener {

    private MovieListManager movieListManager;
    private ListenerMessageValidation listenerMessageValidation;

    @Autowired
    public MovieManagementListener(final MovieListManager movieListManager,
                                   final ListenerMessageValidation listenerMessageValidation) {
        this.movieListManager = movieListManager;
        this.listenerMessageValidation = listenerMessageValidation;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent message) {
        String[] splitMessage = message.getMessageContent().split("\\s+");
        final String command = splitMessage[0];
        final String username = message.getMessageAuthor().getName();

        try {
            //Add movie for a user
            if (command.equalsIgnoreCase("!add")) {
                listenerMessageValidation.validateAddMovieRequest(splitMessage);

                final MovieCategory movieCategory = MovieCategory.forValue(splitMessage[1]);
                StringBuilder title = new StringBuilder();
                for (int i = 2; i < splitMessage.length; i++) {
                    title.append(splitMessage[i]).append(" ");
                }

                movieListManager.addMovieEntryForUser(movieCategory, username, title.toString());
                message.getChannel().sendMessage("Movie successfully saved!\nTo view your saved movies type !myList");
            }
            //Get movies for a given user
            if (command.equalsIgnoreCase("!myList")) {
                List<MovieEntry> movieEntriesForUser = movieListManager.getAllMovieEntriesForUser(username);

                if (!movieEntriesForUser.isEmpty()) {
                    String stringBuilder = "Here's a list of all the movies for " +
                            username +
                            ":\n" +
                            buildMovieListStingByCategory(movieEntriesForUser);
                    message.getChannel().sendMessage(stringBuilder);
                } else {
                    message.getChannel().sendMessage("No movies yet! Start adding them with !add [category] [title]");
                }

            }
            //Get all movies from all users
            if (command.equalsIgnoreCase("!allMovies")) {
                List<String> allUsernames = movieListManager.getDistinctUsernames();
                List<MovieEntry> allMovieEntries = movieListManager.getAllMovieEntries();
                if (!allMovieEntries.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String user : allUsernames) {
                        List<MovieEntry> moviesByUser = allMovieEntries.stream()
                                .filter(e -> e.getUsername().equals(user))
                                .collect(Collectors.toList());

                        stringBuilder.append("Movies for ")
                                .append(user)
                                .append(":\n")
                                .append(buildMovieListStingByCategory(moviesByUser));
                    }
                    message.getChannel().sendMessage(stringBuilder.toString());
                } else {
                    message.getChannel().sendMessage("No movies yet! Start adding them with !add [category] [title]");
                }
            }
            //Delete a movie for a user
            if (command.equalsIgnoreCase("!delete")) {
                listenerMessageValidation.validateDeleteMovieRequest(username, splitMessage);

                final StringBuilder title = new StringBuilder();
                for (int i = 1; i < splitMessage.length; i++) {
                    title.append(splitMessage[i]).append(" ");
                }

                movieListManager.deleteMovieEntryForUser(username, title.toString());
                message.getChannel().sendMessage("Movie successfully deleted");
            }
        } catch (PollValidationException ex) {
            message.getChannel().sendMessage(ex.getMessage());
        }
    }

    private String buildMovieListStingByCategory(final List<MovieEntry> movieEntries) {
        final List<String> allCategories = MovieCategory.getKeys();
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String category : allCategories) {
            List<MovieEntry> moviesByCategory = movieEntries.stream()
                    .filter(e -> e.getCategory().toString().equals(category))
                    .collect(Collectors.toList());

            if (!moviesByCategory.isEmpty()) {
                stringBuilder.append("__")
                        .append(category)
                        .append("__")
                        .append("\n");

                for (final MovieEntry movieEntry : moviesByCategory) {
                    stringBuilder.append("<:mrtartar:666753603641278465> ")
                            .append(movieEntry.getMovieTitle())
                            .append("\n");
                }
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

}
