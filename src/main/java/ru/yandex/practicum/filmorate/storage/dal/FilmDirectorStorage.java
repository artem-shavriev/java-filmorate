package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;

@Repository
public class FilmDirectorStorage extends BaseStorage<FilmDirector> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM_DIRECTOR";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM FILM_DIRECTOR WHERE FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID)" +
            "VALUES (?, ?)";

    public FilmDirectorStorage (JdbcTemplate jdbc, RowMapper<FilmDirector> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmDirector> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public FilmDirector addFilmDirector(FilmDirector director) {
        update(INSERT_QUERY, director.getFilmId(), director.getDirectorId());

        return director;
    }

    public List<FilmDirector> findFilmDirectorByFilmId(Integer filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }
}
