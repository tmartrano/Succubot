package com.tmartrano.succubot.logic;

import com.tmartrano.succubot.dataaccess.PollRepository;
import com.tmartrano.succubot.dataaccess.UserVotesRepository;
import com.tmartrano.succubot.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component(value = "PollGenerationManager")
public class PollManager {

    private MovieListManager movieListManager;

    private PollRepository pollRepository;

    private UserVotesRepository userVotesRepository;

    private static final Random random = new Random();

    //TODO implement GET movies for user
    //TODO implement GET movies for all users
    //TODO delete winning movie from movie list

    @Autowired
    public PollManager(final MovieListManager movieListManager,
                       final PollRepository pollRepository,
                       final UserVotesRepository userVotesRepository) {
        this.movieListManager = movieListManager;
        this.pollRepository = pollRepository;
        this.userVotesRepository = userVotesRepository;
    }

    public List<PollEntry> generateMoviePoll(final MovieCategory movieCategory) throws PollValidationException {
        final List<String> usernames = movieListManager.getDistinctUsernames();
        if (usernames == null || usernames.isEmpty()) {
            throw new PollValidationException("There doesn't seem to be any movies yet! Cannot create poll.");
        }

        int pollEntryNumber = 0;

        for (final String username : usernames) {
            final List<MovieEntry> movieListForUser =
                    movieListManager.getMoviesByUsernameAndCategory(username, movieCategory);

            if (movieListForUser.size() != 0) {
                final int selection = random.nextInt(movieListForUser.size());
                final MovieEntry selectedMovie = movieListForUser.get(selection);

                pollEntryNumber++;
                final PollEntry pollEntry = generatePollEntry(selectedMovie, pollEntryNumber);
                pollRepository.save(pollEntry);
            }
        }
        return pollRepository.findAll();
    }

    public void voteForMovie(final String username, final int pollEntryNumberVote) throws PollValidationException {
        final PollEntry movieVotedFor =
                pollRepository.findPollEntryByPollEntryNumber(pollEntryNumberVote);

        if (movieVotedFor == null) {
            throw new PollValidationException("Movie does not exist");
        }

        validateVotingEligibility(username, movieVotedFor);

        int updatedVoteTally = movieVotedFor.getVoteTally() + 1;
        int updatedRows = pollRepository.setVoteTally(movieVotedFor.getEntryId(), updatedVoteTally);
        if (updatedRows == 0) {
            throw new PollValidationException("Something went wrong, no vote was recorded.");
        }

        userVotesRepository.save(generateUserVote(username, movieVotedFor));
    }

    public PollEntry closePoll() throws PollValidationException {
        final PollEntry winningPoll = pollRepository.findTopByOrderByVoteTallyDesc();

        pollRepository.deleteAll();
        userVotesRepository.deleteAll();

        movieListManager.deleteMovieEntry(winningPoll.getPollEntryDescription());

        return winningPoll;
    }

    public boolean isInactivePoll() {
        List<PollEntry> pollEntries = pollRepository.findAll();
        List<UserVotes> userVotes = userVotesRepository.findAll();

        return pollEntries.isEmpty() && userVotes.isEmpty();
    }

    //region Helper Methods
    private void validateVotingEligibility(
            final String username,
            final PollEntry movieVotedFor) throws PollValidationException {

        final List<UserVotes> userVotesByUsername =
                userVotesRepository.findAllUserVotesByUsername(username);

        for (final UserVotes userVotes : userVotesByUsername) {
            if (movieVotedFor.getEntryId().toString().equalsIgnoreCase(userVotes.getEntryId().toString())) {
                throw new PollValidationException("User has already voted for this movie");
            }
        }
        exceedVoteLimitCheck(userVotesByUsername);
    }

    private void exceedVoteLimitCheck(final List<UserVotes> userVotesList) throws PollValidationException {
        if (userVotesList != null) {
            if (userVotesList.size() >= 3) //TODO put this in a configurable properties file
                throw new PollValidationException("User has exceeded max vote count");
        }
    }

    private UserVotes generateUserVote(String username, PollEntry movieVotedFor) {
        return UserVotes.builder()
                .entryId(movieVotedFor.getEntryId())
                .username(username)
                .build();
    }

    private PollEntry generatePollEntry(final MovieEntry movieEntry, final int pollEntryNumber) {
        return PollEntry.builder()
                .entryId(UUID.randomUUID())
                .isActive(true)
                .pollEntryDescription(movieEntry.getMovieTitle())
                .pollEntryNumber(pollEntryNumber)
                .voteTally(0)
                .build();
    }

    private MovieEntry generateMovieEntry(String username, MovieCategory category) {
        return MovieEntry.builder()
                .movieTitle("MovieName: " + UUID.randomUUID().toString())
                .username(username)
                .category(category)
                .build();
    }
    //endregion
}
