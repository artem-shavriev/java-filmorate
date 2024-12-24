package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

@Repository
public class FilmGenreStorage extends BaseStorage<FilmGenre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM_GENRE";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM FILM_GENRE WHERE FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)" +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";

    public FilmGenreStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public FilmGenre addGenre(FilmGenre genre) {

        long id = insert(INSERT_QUERY, genre.getFilmId(), genre.getGenreId());
        genre.setId(id);
        return genre;
    }

    public boolean deleteGenre(long filmId, long genreId) {
        return delete(DELETE_QUERY, filmId, genreId);
    }

    public List<FilmGenre> findGenresByFilmId(long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }
}
