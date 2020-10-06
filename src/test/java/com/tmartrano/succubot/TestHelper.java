package com.tmartrano.succubot;

import com.tmartrano.succubot.dataaccess.MovieRepository;
import com.tmartrano.succubot.dataaccess.PollRepository;
import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.MovieEntry;
import com.tmartrano.succubot.model.PollEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component(value = "com.tmartrano.succubot.TestHelper")
class TestHelper {
    @Autowired
    private PollManager pollManager;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PollRepository pollRepository;

    private static final Random random = new Random();

    void seedMovieRepositoryOneCategory() {
        final List<String> usernames = new ArrayList<>();
        usernames.add("Roy Carmine");
        usernames.add("Yuuki Terumi");
        usernames.add("Relius Clover");
        usernames.add("Jin Kisaragi");
        usernames.add("Almond Butter");

        for (String username : usernames) {
            final int moviesForUser = random.nextInt(5) + 1;
            for (int i = 0; i < moviesForUser; i++) {
                MovieEntry movieEntry = generateMovieEntry(username, MovieCategory.BAD_MOVIE);
                movieRepository.save(movieEntry);
            }
        }
    }

    void seedMovieRepositoryMultipleCategories() {
        MovieCategory category = MovieCategory.BAD_MOVIE;

        for (int i = 0; i < 7; i++) {
            final MovieEntry entry = generateMovieEntry("User" + UUID.randomUUID().toString(), category);
            movieRepository.save(entry);
            if (i % 2 == 0) {
                category = MovieCategory.GOOD_MOVIE;
            } else {
                category = MovieCategory.BAD_MOVIE;
            }
        }
    }

    List<MovieEntry> getPollMovieEntries(final List<PollEntry> pollEntries) {
        final List<MovieEntry> pollMovieEntries = new ArrayList<>();

        //Iterate through and make sure there's distinct movie for one distinct user
        for (PollEntry pollEntry : pollEntries) {
            final MovieEntry actualMovieEntry =
                    movieRepository.findMovieEntryByMovieTitle(pollEntry.getPollEntryDescription());
            pollMovieEntries.add(actualMovieEntry);
        }

        return pollMovieEntries;
    }

    PollEntry getPollEntryByPollEntryNumber(int entryNumber) {
        return pollRepository.findPollEntryByPollEntryNumber(entryNumber);
    }

    private MovieEntry generateMovieEntry(String username, MovieCategory category) {
        return MovieEntry.builder()
                .movieTitle("MovieName: " + UUID.randomUUID().toString())
                .username(username)
                .category(category)
                .build();
    }
}
