package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreStorage extends BaseStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT G.NAME, G.GENRE_ID FROM GENRE AS G " +
            "JOIN FILM_GENRE AS FG ON G.GENRE_ID = FG.GENRE_ID " +
            "WHERE FILM_ID = ?";

    public GenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Genre> findGenresByFilmId(Integer filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public List<Genre> findGenresByIds(List<Integer> genreIds) {
        String inClause = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String query = "SELECT * FROM GENRE WHERE GENRE_ID IN (" + inClause + ")";
        return findMany(query, genreIds.toArray());
    }
}
