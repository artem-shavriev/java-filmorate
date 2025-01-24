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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Film> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
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

    /*public List<Integer> findFilmsLikedBySimilarUsers(Integer userId) {
        String sql = "SELECT DISTINCT l.FILM_ID " +
                "FROM LIKES_FROM_USERS l " +
                "WHERE l.USER_ID IN (" +
                "  SELECT DISTINCT l2.USER_ID " +
                "  FROM LIKES_FROM_USERS l2 " +
                "  WHERE l2.FILM_ID IN (SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ?) " +
                "  AND l2.USER_ID != ? " +
                ") " +
                "AND l.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ?)";

        return jdbc.queryForList(sql, Integer.class, userId, userId, userId);
    }

    public List<Film> getFilmsByIds(List<Integer> filmIds) {
        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String sqlQueryStatement = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.MPA_NAME, COUNT(l.USER_ID) AS likes " +
                "FROM FILM f " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN LIKES_FROM_USERS l ON f.FILM_ID = l.FILM_ID ";

        sqlQueryStatement += "WHERE f.FILM_ID IN (" + inClause + ") ";

        sqlQueryStatement += "GROUP BY f.FILM_ID ORDER BY likes DESC";

        List<Object> sqlArgs = new ArrayList<>(filmIds);

        return jdbc.query(sqlQueryStatement, mapper::mapRow, sqlArgs.toArray());
    }*/

    public List<Integer> findUserWithSimilarLikes(Integer userId) {
        String sqlGetLikes = "SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ?";

        List<Integer> currentUserLikes = jdbc.queryForList(sqlGetLikes, Integer.class, userId);

        String sqlFindSimilar = "SELECT l2.USER_ID, COUNT(*) AS common_likes " +
                "FROM LIKES_FROM_USERS l2 " +
                "WHERE l2.FILM_ID IN ( " +
                "  SELECT FILM_ID FROM LIKES_FROM_USERS WHERE USER_ID = ? " +
                ") " +
                "AND l2.USER_ID != ? " +
                "GROUP BY l2.USER_ID " +
                "ORDER BY common_likes DESC " +
                "LIMIT 1";

        List<Map<String, Object>> result = jdbc.queryForList(sqlFindSimilar, userId, userId);

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> row = result.get(0);
        Integer similarUserId = (Integer) row.get("USER_ID");

        List<Integer> similarUserLikes = jdbc.queryForList(sqlGetLikes, Integer.class, similarUserId);


        if (currentUserLikes.size() > similarUserLikes.size()) {
            return Collections.emptyList();
        }

        if (new HashSet<>(currentUserLikes).equals(new HashSet<>(similarUserLikes))) {
            return Collections.emptyList();
        }

        return Collections.singletonList(similarUserId);
    }


    public List<Film> getFilmsByIds(List<Integer> filmIds) {
        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String sqlQueryStatement = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.MPA_NAME, COUNT(l.USER_ID) AS likes " +
                "FROM FILM f " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN LIKES_FROM_USERS l ON f.FILM_ID = l.FILM_ID ";

        sqlQueryStatement += "WHERE f.FILM_ID IN (" + inClause + ") ";

        sqlQueryStatement += "GROUP BY f.FILM_ID ORDER BY likes DESC";

        List<Object> sqlArgs = new ArrayList<>(filmIds);

        return jdbc.query(sqlQueryStatement, mapper::mapRow, sqlArgs.toArray());
    }
}
