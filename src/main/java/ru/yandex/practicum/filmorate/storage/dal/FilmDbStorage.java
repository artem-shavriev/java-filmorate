package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, " +
            "f.DURATION, f.RELEASE_DATE, f.MPA_ID, M.MPA_NAME, " +
            "COUNT(l.USER_ID) AS likes " +
            "FROM FILM AS f " +
            "LEFT JOIN MPA AS M ON f.MPA_ID = M.MPA_ID " +
            "LEFT JOIN FILM_DIRECTOR AS fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID " +
            "GROUP BY f.FILM_ID " +
            "ORDER BY likes DESC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILM AS F JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
            "WHERE F.FILM_ID = ?";
    private static final String FIND_BY_DIRECTOR_SORT_YEAR = "SELECT * FROM FILM AS F " +
            "JOIN FILM_DIRECTOR AS FD ON FD.FILM_ID = F.FILM_ID " +
            "JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
            "WHERE FD.DIRECTOR_ID = ? " +
            "GROUP BY F.FILM_ID " +
            "ORDER BY RELEASE_DATE";

    private static final String FIND_BY_DIRECTOR_SORT_LIKE = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, " +
            "f.DURATION, f.RELEASE_DATE, f.MPA_ID, M.MPA_NAME, " +
            "COUNT(l.USER_ID) AS likes " +
            "FROM FILM AS f " +
            "LEFT JOIN MPA AS M ON f.MPA_ID = M.MPA_ID " +
            "LEFT JOIN FILM_DIRECTOR AS fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID " +
            "WHERE FD.DIRECTOR_ID = ? " +
            "GROUP BY f.FILM_ID " +
            "ORDER BY likes DESC";

    private static final String INSERT_QUERY = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ?," +
            "RELEASE_DATE = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILM WHERE FILM_ID = ?";

    private static final String RECOMMENDATION_QUERY =
            "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, M.MPA_ID, M.MPA_NAME " +
                    "FROM FILM AS F " +
                    "JOIN MPA AS M ON M.MPA_ID = F.MPA_ID " +
                    "JOIN LIKES_FROM_USERS L ON L.FILM_ID = F.FILM_ID " +
                    "WHERE L.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ?) " +
                    "AND L.USER_ID IN (SELECT USER_ID FROM LIKES_FROM_USERS WHERE " +
                    "FILM_ID IN (SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ?) " +
                    "AND USER_ID != ? " +
                    "GROUP BY USER_ID " +
                    "ORDER BY COUNT(FILM_ID) DESC " +
                    "LIMIT 10)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Film> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public List<Film> recommendationForUser(Integer userId) {
        return findMany(RECOMMENDATION_QUERY, userId);
    }

    public List<Film> findByDirectorSortByYear(Integer directorId) {
        return findMany(FIND_BY_DIRECTOR_SORT_YEAR, directorId);
    }

    public List<Film> findByDirectorSortByLike(Integer directorId) {
        return findMany(FIND_BY_DIRECTOR_SORT_LIKE, directorId);
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
            sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, M.MPA_NAME, " +
                    "COUNT(l.USER_ID) AS likes " +
                    "FROM FILM AS f " +
                    "LEFT JOIN MPA AS M ON f.MPA_ID = M.MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR AS fd ON f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID " +
                    "WHERE " + split[0] + " ILIKE ? OR " + split[1] + " ILIKE ? " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY likes DESC";
            return jdbc.query(sql, mapper::mapRow, "%" + query + "%", "%" + query + "%")
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        } else {
            sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, M.MPA_NAME, " +
                    "COUNT(l.USER_ID) AS likes " +
                    "FROM FILM AS f " +
                    "LEFT JOIN MPA AS M ON f.MPA_ID = M.MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR AS fd ON f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID " +
                    "WHERE " + replaced + " ILIKE ? " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY likes DESC";
            return jdbc.query(sql, mapper::mapRow, "%" + query + "%")
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        }
    }

    public List<Film> findPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Object> sqlArgs = new ArrayList<>();
        List<String> sqlConditions = new ArrayList<>();

        if (year != null) {
            sqlConditions.add("EXTRACT(YEAR FROM f.RELEASE_DATE) = ?");
            sqlArgs.add(year);
        }

        if (genreId != null) {
            sqlConditions.add("g.GENRE_ID = ?");
            sqlArgs.add(genreId);
        }

        String sqlQueryStatement = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.MPA_NAME, COUNT(l.USER_ID) AS likes " +
                "FROM FILM AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_GENRE AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID ";

        String sqlCondition = String.join(" AND ", sqlConditions);
        if (!sqlCondition.isEmpty()) {
            sqlQueryStatement += " WHERE " + sqlCondition;
        }

        sqlQueryStatement += " GROUP BY f.FILM_ID ORDER BY likes DESC LIMIT ?";
        sqlArgs.add(count);

        return jdbc.query(sqlQueryStatement, mapper, sqlArgs.toArray());
    }

    /*public List<Film> findPopularFilms(Integer count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder(
                "SELECT f.*, m.MPA_NAME, COUNT(l.USER_ID) AS likes " +
                        "FROM FILM AS f " +
                        "LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                        "LEFT JOIN FILM_GENRE AS fg ON f.FILM_ID = fg.FILM_ID " +
                        "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " +
                        "LEFT JOIN LIKES_FROM_USERS AS l ON f.FILM_ID = l.FILM_ID "
        );

        List<Object> result = new ArrayList<>();

        String additionalParam;
        if (sql.toString().contains("WHERE")) {
            additionalParam = " AND ";
        } else {
            additionalParam = " WHERE ";
        }

        if (year != null) {
            sql.append(additionalParam).append("YEAR(f.RELEASE_DATE) = ?");
            result.add(year);
            additionalParam = " AND ";
        }

        if (genreId != null) {
            sql.append(additionalParam).append("g.GENRE_ID = ?");
            result.add(genreId);
        }

        sql.append(" GROUP BY f.FILM_ID ")
                .append(" ORDER BY likes DESC ");

        if (count != null && count > 0) {
            sql.append(" LIMIT ?");
            result.add(count);
        }

        return jdbc.query(sql.toString(), mapper, result.toArray());
    }*/
}
