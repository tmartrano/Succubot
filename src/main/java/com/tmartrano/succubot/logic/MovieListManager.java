package com.tmartrano.succubot.logic;

import com.tmartrano.succubot.dataaccess.MovieRepository;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.MovieEntry;
import com.tmartrano.succubot.model.PollValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieListManager {

    private MovieRepository movieRepository;

    @Autowired
    public MovieListManager(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    List<MovieEntry> getMoviesByUsernameAndCategory(final String username, final MovieCategory movieCategory) {
        return movieRepository.findAllByUsernameAndCategory(username, movieCategory);
    }

    List<String> getDistinctUsernames() {
        return movieRepository.findDistinctUsername();
    }

    public List<MovieEntry> getAllMovieEntries() {
        return movieRepository.findAll();
    }

    public List<MovieEntry> getAllMovieEntriesForUser(final String username) {
        return movieRepository.findAllByUsername(username);
    }

    public void addMovieEntryForUser(final MovieCategory category, final String username, final String movieTitle) {
        final MovieEntry movieEntry = generateMovieEntry(category, username, movieTitle);
        movieRepository.save(movieEntry);
    }

    public void deleteMovieEntryForUser(final String username, final String movieTitle) throws PollValidationException {
        final MovieEntry movieEntry =
                movieRepository.findMovieEntryByMovieTitleAndUsername(username, movieTitle);

        if (movieEntry == null) {
            throw new PollValidationException("Cannot delete, movie entry" + movieTitle + "does not exist");
        }
        movieRepository.deleteMovieEntryByTitle(username, movieTitle);
    }

    //region Helper Methods
    private MovieEntry generateMovieEntry(final MovieCategory category, final String username, final String movieTitle) {
        return MovieEntry.builder()
                .category(category)
                .movieTitle(movieTitle)
                .username(username)
                .build();
    }
    //endregion
}
