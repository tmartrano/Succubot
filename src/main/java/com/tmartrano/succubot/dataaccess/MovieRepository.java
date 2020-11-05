package com.tmartrano.succubot.dataaccess;

import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.MovieEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntry, Long> {

    @Query("SELECT DISTINCT a.username FROM MovieEntry a")
    List<String> findDistinctUsername();

    List<MovieEntry> findAllByUsername(String username);

    List<MovieEntry> findAllByUsernameAndCategory(String username, MovieCategory category);

    @Query("SELECT e FROM MovieEntry e WHERE LOWER(e.movieTitle) = LOWER(?1)")
    MovieEntry findMovieEntryByMovieTitle(String movieTitle);

    @Modifying
    @Query("DELETE MovieEntry p WHERE LOWER(p.username) = LOWER(?1) AND LOWER(p.movieTitle) = LOWER(?2)")
    @Transactional
    int deleteMovieEntryByTitleForUser(String username, String movieTitle);

    @Modifying
    @Query("DELETE MovieEntry p WHERE LOWER(p.movieTitle) = LOWER(?1)")
    @Transactional
    void deleteMovieEntryByTitle(String movieTitle);
}
