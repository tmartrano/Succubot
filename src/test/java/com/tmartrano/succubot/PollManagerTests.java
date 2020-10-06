package com.tmartrano.succubot;

import com.tmartrano.succubot.dataaccess.PollRepository;
import com.tmartrano.succubot.dataaccess.UserVotesRepository;
import com.tmartrano.succubot.logic.PollManager;
import com.tmartrano.succubot.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableJpaRepositories
public class PollManagerTests {

    @Autowired
    private PollManager pollManager;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private UserVotesRepository userVotesRepository;

    private final String username = "Tealx";

    //region generateMoviePoll
    @Test
    @Transactional
    public void generatePoll_BadMovies_Successful() {
        testHelper.seedMovieRepositoryOneCategory();
        List<PollEntry> pollEntries=pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

        assertNotNull(pollEntries);
        assertFalse(pollEntries.isEmpty());
        assertEquals(5, pollEntries.size());

        final List<MovieEntry> pollMovieEntries = testHelper.getPollMovieEntries(pollEntries);
        assertNotNull(pollMovieEntries);
        assertFalse(pollEntries.isEmpty());

        List<MovieEntry> distinctMovieEntries =
                pollMovieEntries.stream()
                        .filter(distinctByKey(MovieEntry::getUsername))
                        .collect(Collectors.toList());

        assertNotNull(distinctMovieEntries);
        assertEquals(5, distinctMovieEntries.size());
    }

    @Test
    @Transactional
    public void generatePoll_MixOfCategories_ChooseOnlyGood() {
        testHelper.seedMovieRepositoryMultipleCategories();
        final List<PollEntry> pollEntries = pollManager.generateMoviePoll(MovieCategory.GOOD_MOVIE);

        assertNotNull(pollEntries);
        assertFalse(pollEntries.isEmpty());
        assertEquals(3, pollEntries.size());
    }
    //endregion

    //region voteForMovie
    @Test
    @Transactional
    public void voteForMovie_UserHasNoVotes_VoteRecorded() throws PollValidationException {
        testHelper.seedMovieRepositoryOneCategory();
        pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

        pollManager.voteForMovie(username, 3);

        PollEntry pollEntry = testHelper.getPollEntryByPollEntryNumber(3);
        assertNotNull(pollEntry);
        assertEquals(1, pollEntry.getVoteTally());

        List<UserVotes> userVotes = userVotesRepository.findAllUserVotesByUsername(username);
        assertNotNull(userVotes);
        assertEquals(1, userVotes.size());
    }

    @Test
    @Transactional
    public void voteForMovie_UserHasTwoVotes_VoteRecorded() throws PollValidationException {
        testHelper.seedMovieRepositoryOneCategory();
        pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

        pollManager.voteForMovie(username, 1);
        pollManager.voteForMovie(username, 2);

        pollManager.voteForMovie(username, 3);

        List<UserVotes> userVotes = userVotesRepository.findAllUserVotesByUsername(username);
        assertNotNull(userVotes);
        assertEquals(3, userVotes.size());

        final PollEntry pollEntry1 = pollRepository.findPollEntryByPollEntryNumber(1);
        assertEquals(1, pollEntry1.getVoteTally());

        final PollEntry pollEntry2 = pollRepository.findPollEntryByPollEntryNumber(2);
        assertEquals(1, pollEntry2.getVoteTally());

        final PollEntry pollEntry3 = pollRepository.findPollEntryByPollEntryNumber(3);
        assertEquals(1, pollEntry3.getVoteTally());
    }

    @Test(expected = PollValidationException.class)
    @Transactional
    public void voteForMovie_UserHasThreeVotes_VoteNotRecorded() throws PollValidationException {
        try {
            testHelper.seedMovieRepositoryOneCategory();
            pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

            pollManager.voteForMovie(username, 1);
            pollManager.voteForMovie(username, 2);
            pollManager.voteForMovie(username, 3);

            pollManager.voteForMovie(username, 4);
        } catch (PollValidationException ex) {
            assertNotNull(ex);
            assertEquals("User has exceeded max vote count", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = PollValidationException.class)
    @Transactional
    public void voteForMovie_UserVotingForSameMovie_VoteNotRecorded() throws PollValidationException {
        try {
            testHelper.seedMovieRepositoryOneCategory();
            pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

            pollManager.voteForMovie(username, 1);
            pollManager.voteForMovie(username, 1);
        } catch (PollValidationException ex) {
            assertNotNull(ex);
            assertEquals("User has already voted for this movie", ex.getMessage());
            throw ex;
        }

    }

    @Test(expected = PollValidationException.class)
    @Transactional
    public void voteForMovie_UserEnteredNumberNotOnList_VoteNotRecorded() throws PollValidationException {
        testHelper.seedMovieRepositoryOneCategory();
        pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

        try {
            pollManager.voteForMovie(username, 7);

        } catch (PollValidationException ex) {
            assertNotNull(ex);
            assertEquals("Movie does not exist", ex.getMessage());
            throw ex;
        }
    }

    //Multiple votes from different users on the same movie
    @Test
    @Transactional
    public void voteForMovie_DifferentUsers_SameMovie() throws PollValidationException {
        testHelper.seedMovieRepositoryOneCategory();
        pollManager.generateMoviePoll(MovieCategory.BAD_MOVIE);

        pollManager.voteForMovie("User1", 3);
        pollManager.voteForMovie("User2", 3);
        pollManager.voteForMovie("User3", 3);

        final PollEntry pollEntry = pollRepository.findPollEntryByPollEntryNumber(3);
        assertNotNull(pollEntry);
        assertEquals(3, pollEntry.getVoteTally());
    }

    //endregion

    //region closePoll

    //endregion

    //Get Movie poll winner and close poll (delete movie poll also delete user votes)

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}