package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
import ru.yandex.practicum.filmorate.service.MpaRatingConverter;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    FilmGenreRepository filmGenreRepository;
    LikesFromUsersRepository likesFromUsersRepository;
    ArrayList<Genre> genres = new ArrayList<>();
    Set<Long> usersLikes = new HashSet<>();

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setDuration(resultSet.getInt("DURATION"));

        LocalDate releaseDate = resultSet.getDate("RELEASE_DATE").toLocalDate();
        film.setReleaseDate(releaseDate);

        ArrayList<FilmGenre> listFilmGenre = (ArrayList<FilmGenre>) filmGenreRepository.findAll();

        listFilmGenre.stream()
                        .forEach(filmGenre -> {
                            if (filmGenre.getFilmId() == film.getId()) {
                                genres.add(filmGenre.getGenre());
                            }
                        });

        film.setGenres(genres);

        ArrayList<LikesFromUsers> likesFromUsers = (ArrayList<LikesFromUsers>) likesFromUsersRepository.findAll();
        likesFromUsers.stream()
                .forEach(likes -> {
                    if (likes.getFilmId() == film.getId()) {
                        usersLikes.add(likes.getUserId());
                    }
                });

        film.setLikesFromUsers(usersLikes);

        int mpaID = resultSet.getInt("MPA_ID");
        film.setМpaRate(MpaRatingConverter.convertToMpa(mpaID));

        return film;
    }
}
