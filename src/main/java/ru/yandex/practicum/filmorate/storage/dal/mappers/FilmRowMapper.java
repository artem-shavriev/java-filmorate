package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final LikesFromUsersStorage likesFromUsersStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;


    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setDuration(resultSet.getInt("DURATION"));

        Timestamp date = resultSet.getTimestamp("RELEASE_DATE");
        film.setReleaseDate(date.toLocalDateTime().toLocalDate());

        Mpa mpa = new Mpa(resultSet.getInt("MPA_ID"), resultSet.getString("MPA_NAME"));
        film.setMpa(mpa);

       if (filmGenreStorage.findGenresByFilmId(film.getId()) != null) {
            List<Genre> filmGenresList = genreStorage.findGenresByFilmId(film.getId());
            film.setGenres(filmGenresList);
        }

        if (likesFromUsersStorage.findLikesByFilmId(film.getId()) != null) {
            List<LikesFromUsers> likes = likesFromUsersStorage.findLikesByFilmId(film.getId());
            Set<Integer> usersId = new HashSet<>();
            for (LikesFromUsers like : likes) {
                usersId.add(like.getUserId());
            }
            film.setLikesFromUsers(usersId);
        }

        if (filmDirectorStorage.findFilmDirectorByFilmId(film.getId()) != null) {
            List<Director> directors = directorStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(directors);
        }
        return film;
    }
}
