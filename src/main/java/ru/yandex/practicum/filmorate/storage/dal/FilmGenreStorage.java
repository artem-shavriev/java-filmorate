package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class FilmGenreStorage extends BaseStorage<FilmGenre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM_GENRE";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM FILM_GENRE WHERE FILM_ID = ?";
    private static final String INSERT_QUERY = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID)" +
            "VALUES (?, ?) returning id";
    private static final String DELETE_QUERY = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";

    public FilmGenreStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> findAll() {

        return findMany(FIND_ALL_QUERY);
    }

    public void addGenre(long filmId, long genreId) {
        long id = insert(INSERT_QUERY, filmId, genreId);
    }

    public boolean deleteGenre(long filmId, long genreId) {
        return delete(DELETE_QUERY, filmId, genreId);
    }

    public List<FilmGenre> findGenresByFilmId(long filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public void setFilmGenres(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                addGenre(film.getId(), genre.getId());
            }
        }
    }
}
