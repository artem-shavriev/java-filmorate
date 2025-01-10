package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikesFromUsersStorage likesFromUsersStorage;


    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setDuration(resultSet.getInt("DURATION"));

        Timestamp date = resultSet.getTimestamp("RELEASE_DATE");
        film.setReleaseDate(date.toLocalDateTime().toLocalDate());

        Mpa mpa = new Mpa();
        Integer mpaId = resultSet.getInt("MPA_ID");
        mpa.setId(mpaId);

        String mpaName = mpaStorage.findById(mpaId).get().getName();
        mpa.setName(mpaName);

        film.setMpa(mpa);

       if (filmGenreStorage.findGenresByFilmId(film.getId()) != null) {
            List<FilmGenre> listFilmGenre = filmGenreStorage.findGenresByFilmId(film.getId());
            ArrayList<Genre> genres = new ArrayList<>();
            for (FilmGenre filmGenre: listFilmGenre) {
                    Genre currentGenre = new Genre();
                    currentGenre.setId(filmGenre.getGenreId());
                    String genreName = genreStorage.findById(filmGenre.getGenreId()).get().getName();
                    currentGenre.setName(genreName);
                    genres.add(currentGenre);
                }
            film.setGenres(genres);
        }

        if (likesFromUsersStorage.findLikesByFilmId(film.getId()) != null) {
            List<LikesFromUsers> likes = likesFromUsersStorage.findLikesByFilmId(film.getId());
            Set<Integer> usersId = new HashSet<>();
            for (LikesFromUsers like : likes) {
                usersId.add(like.getUserId());
            }
            film.setLikesFromUsers(usersId);
        }
        return film;
    }
}
