package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILM WHERE FILM_ID = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM FILM WHERE NAME = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ?," +
            "RELEASE_DATE = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILM WHERE FILM_ID = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Film> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Optional<Film> findByName(String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film addFilm(Film film) {

        Integer id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }


    public Film updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getId()
        );
        return film;
    }

    public boolean deleteFilmById(Integer filmId) {
        return delete(DELETE_QUERY, filmId);
    }

    public List<FilmDto> getSearch(String query, String by) {
        String sql;
        String replaced = by.replace("director", "d.NAME").replace("title", "f.NAME");
        if (replaced.contains(",")) {
            String[] split = replaced.split(",");
            sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID FROM FILM AS f " +
                    "LEFT JOIN FILM_DIRECTOR  AS fd ON f.FILM_ID =  fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE " + split[0] + " ILIKE '%" + query + "%' OR " + split[1] + " ILIKE '%" + query + "%' " +
                    "GROUP BY f.FILM_ID";
            return jdbc.query(sql, mapper::mapRow)
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        } else {
            sql =  "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID FROM FILM AS f " +
                    "LEFT JOIN FILM_DIRECTOR  AS fd ON f.FILM_ID =  fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE " + replaced + " ILIKE '%" + query + "%' " +
                    "GROUP BY f.FILM_ID";
            return jdbc.query(sql, mapper::mapRow)
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        }
    }
}
