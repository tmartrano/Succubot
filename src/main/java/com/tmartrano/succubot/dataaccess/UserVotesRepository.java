package com.tmartrano.succubot.dataaccess;

import com.tmartrano.succubot.model.UserVotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVotesRepository extends JpaRepository<UserVotes, Long> {

    List<UserVotes> findAllUserVotesByUsername(String username);

    @Modifying
    @Query("DELETE FROM UserVotes")
    void deleteAll();
}
