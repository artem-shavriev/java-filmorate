package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.ArrayList;
import java.util.List;

public class FilmGenreRepository extends BaseRepository<FilmGenre>{
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM_GENRE";
    private static final String UPDATE_QUERY = "UPDATE FILM_GENRE SET FILM_ID = ?, GENRE_ID = ? WHERE ID = ?";
    private static final String INSERT_QUERY = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID)" +
            "VALUES (?, ?) returning id";
    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public void saveFilmGenres(Film film) {
        ArrayList<Long> genres = film.getGenresIds();
        for (Long genreId : genres) {
            long id = insert(INSERT_QUERY,
                    film.getId(),
                    genreId
            );
        }
    }


}
