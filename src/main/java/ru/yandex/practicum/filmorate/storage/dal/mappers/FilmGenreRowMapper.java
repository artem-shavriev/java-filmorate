package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(resultSet.getLong("FILM_ID"));

        switch (resultSet.getInt("GENRE_ID")) {
            case 1:
                filmGenre.setGenre(Genre.КОМЕДИЯ);
                break;
            case 2:
                filmGenre.setGenre(Genre.ДРАМА);
                break;
            case 3:
                filmGenre.setGenre(Genre.МУЛЬТФИЛЬМ);
                break;
            case 4:
                filmGenre.setGenre(Genre.ТРИЛЛЕР);
                break;
            case 5:
                filmGenre.setGenre(Genre.ДОКУМЕНТАЛЬНЫЙ);
                break;
            case 6:
                filmGenre.setGenre(Genre.БОЕВИК);
                break;
        }

        return filmGenre;
    }
}
