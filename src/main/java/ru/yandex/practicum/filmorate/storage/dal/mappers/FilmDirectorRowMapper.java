package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmDirectorRowMapper implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmDirector filmDirector = new FilmDirector();

        filmDirector.setDirectorId(resultSet.getInt("DIRECTOR_ID"));
        filmDirector.setFilmId(resultSet.getInt("FILM_ID"));

        return filmDirector;
    }
}
