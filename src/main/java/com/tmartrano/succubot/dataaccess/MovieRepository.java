package com.tmartrano.succubot.dataaccess;

import com.tmartrano.succubot.model.MovieCategory;
import com.tmartrano.succubot.model.MovieEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntry, Long> {

    @Query("SELECT DISTINCT a.username FROM MovieEntry a")
    List<String> findDistinctUsername();

    List<MovieEntry> findAllByUsername(String username);

    List<MovieEntry> findAllByUsernameAndCategory(String username, MovieCategory category);

    MovieEntry findMovieEntryByMovieTitle(String movieTitle);

    MovieEntry findMovieEntryByMovieTitleAndUsername(String movieTitle, String username);

    @Modifying
    @Query("DELETE MovieEntry p WHERE p.username = ?1 AND p.movieTitle = ?2")
    void deleteMovieEntryByTitle(String username, String movieTitle);
}
