package com.tmartrano.succubot.dataaccess;

import com.tmartrano.succubot.model.PollEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<PollEntry, Long> {

    PollEntry findPollEntryByPollEntryNumber(final int pollEntryNumber);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PollEntry p SET p.voteTally = ?2 WHERE p.entryId = ?1")
    void setVoteTally(UUID entryId, int voteTally);

    @Modifying
    @Query("DELETE FROM PollEntry")
    void deleteAll();

    /*@Query("SELECT PollEntry MAX (p.voteTally) FROM PollEntry p")
    PollEntry getWinningMovie();*/

    PollEntry findTopByOrderByVoteTallyDesc();
}
