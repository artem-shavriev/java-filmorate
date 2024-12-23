package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final  FilmGenreStorage filmGenreStorage;
    private final LikesFromUsersStorage likes;


    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setDuration(resultSet.getInt("DURATION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("MPA_ID"));
        film.setMpaRate(mpa);

       if (filmGenreStorage.findAll() != null) {
            List<FilmGenre> listFilmGenre = filmGenreStorage.findGenresByFilmId(film.getId());
            ArrayList<Genre> genres = new ArrayList<>();
            for (FilmGenre filmGenre: listFilmGenre) {

                    Genre currentGenre = new Genre();
                    currentGenre.setId(filmGenre.getGenreId());
                    genres.add(currentGenre);
                }
            film.setGenres(genres);
        }

        /*if(likes.findAll() != null) {
            ArrayList<LikesFromUsers> likesFromUsers = (ArrayList<LikesFromUsers>) likes.findAll();
            likesFromUsers.stream()
                    .forEach(likes -> {
                        if (likes.getFilmId() == film.getId()) {
                            usersLikes.add(likes.getUserId());
                        }
                    });

            film.setLikesFromUsers(usersLikes);
        }*/

        return film;
    }
}
