package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILM WHERE FILM_ID = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM FILM WHERE NAME = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
            "VALUES (?, ?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ?," +
            " WHERE FILM_ID = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Film> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Optional<Film> findByName(String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film save(Film film) {

        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.from(Instant.from(film.getReleaseDate())),
                film.getDuration(),
                film.getMpaRateId()
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpaRateId(),
                film.getId()
        );
        return film;
    }
}
