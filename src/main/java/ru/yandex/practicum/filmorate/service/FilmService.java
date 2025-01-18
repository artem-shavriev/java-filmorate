package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesFromUsersStorage likesFromUsersStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {
        if (request.getMpa() != null) {
            List<Mpa> mpaList = mpaStorage.findAll();
            List<Integer> mpaIdsList = mpaList.stream().map(mpa -> mpa.getId()).toList();

            if (!mpaIdsList.contains(request.getMpa().getId())) {
                throw new ValidationException("У рейтинга несуществующий id");
            }

            String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();

            request.getMpa().setName(mpaName);
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getGenres() != null) {

            List<Genre> genresList = genreStorage.findAll();
            List<Integer> genresIdList = genresList.stream().map(Genre::getId).toList();

            request.getGenres().stream().forEach(genre -> {
                if (!genresIdList.contains(genre.getId())) {
                    log.error("У фильма с названием: {} жанр с несуществующим id: {}", request.getName(), genre.getId());
                    throw new ValidationException("У одного из жанров фильма несуществующий id");
                }
            });

            List<Integer> requestGenresId = request.getGenres().stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Genre> uniqueGenresList = uniqueGenresIds.stream().map(id -> genreStorage.findById(id).get()).toList();

            for (Genre genre : uniqueGenresList) {
                String genreName = genreStorage.findById(genre.getId()).get().getName();
                genre.setName(genreName);
            }
            request.setGenres(uniqueGenresList);
        }

        if (request.getDirectors() != null) {
            List<Integer> directorsIdList = request.getDirectors().stream().map(dir -> dir.getId()).toList();

            List<Director> directorsListWithName = directorsIdList.stream().map(id ->
                    directorStorage.findById(id).get()).toList();

            request.setDirectors(directorsListWithName);
        }

        Film film = FilmMapper.mapToFilm(request);

        film = filmDbStorage.addFilm(film);

        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                FilmGenre filmGenre = new FilmGenre();

                filmGenre.setFilmId(film.getId());
                filmGenre.setGenreId(genre.getId());

                filmGenreStorage.addGenre(filmGenre);
            }
        }

        if (film.getDirectors() != null) {
            List<Director> directorsList = film.getDirectors();

            for (Director director : directorsList) {
                FilmDirector filmDirector = new FilmDirector();

                filmDirector.setDirectorId(director.getId());
                filmDirector.setFilmId(film.getId());

                filmDirectorStorage.addFilmDirector(filmDirector);
            }
        }

        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {

        Optional<Film> existFilm = filmDbStorage.findById(request.getId());
        if (existFilm.isEmpty()) {
            log.error("Фильм с id = {} не найден", request.getId());
            throw new NotFoundException("Фильм с id = " + request.getId() + " не найден");
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getMpa() != null) {
            List<Mpa> mpaList = mpaStorage.findAll();
            List<Integer> mpaIdsList = mpaList.stream().map(mpa -> mpa.getId()).toList();

            if (!mpaIdsList.contains(request.getMpa().getId())) {
                throw new ValidationException("У рейтинга несуществующий id");
            }

            String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();

            request.getMpa().setName(mpaName);
        }

        if (request.getGenres() != null) {

            List<Genre> genresList = genreStorage.findAll();
            List<Integer> genresIdList = genresList.stream().map(Genre::getId).toList();

            request.getGenres().stream().forEach(genre -> {
                if (!genresIdList.contains(genre.getId())) {
                    log.error("У фильма с названием: {} жанр с несуществующим id: {}", request.getName(), genre.getId());
                    throw new ValidationException("У одного из жанров фильма несуществующий id");
                }
            });

            List<Integer> requestGenresId = request.getGenres().stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Genre> uniqueGenresList = uniqueGenresIds.stream().map(id -> genreStorage.findById(id).get()).toList();

            for (Genre genre : uniqueGenresList) {
                String genreName = genreStorage.findById(genre.getId()).get().getName();
                genre.setName(genreName);
            }
            request.setGenres(uniqueGenresList);
        }

        if (request.getDirectors() != null) {
            List<Integer> directorsIdList = request.getDirectors().stream().map(dir -> dir.getId()).toList();

            List<Director> directorsListWithName = directorsIdList.stream().map(id ->
                    directorStorage.findById(id).get()).toList();
            request.setDirectors(directorsListWithName);

            List<Director> directorsList = request.getDirectors();

            for (Director director : directorsList) {
                FilmDirector filmDirector = new FilmDirector();

                filmDirector.setDirectorId(director.getId());
                filmDirector.setFilmId(request.getId());

                filmDirectorStorage.addFilmDirector(filmDirector);
            }
        }

        Film updateFilm = filmDbStorage.findById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updateFilm = filmDbStorage.updateFilm(updateFilm);
        log.info("Фильм {} был обновлен.", updateFilm.getName());

        return FilmMapper.mapToFilmDto(updateFilm);
    }

    public List<FilmDto> getFilms() {
        log.info("Получен список фильмов.");
        return filmDbStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Integer filmId) {
        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не найден", filmId);
            throw  new NotFoundException("Фильм с данным id не найден");
        }

        return FilmMapper.mapToFilmDto(filmDbStorage.findById(filmId).get());
    }

    public FilmDto likeFilm(Integer filmId, Integer userId) {

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        LikesFromUsers like = likesFromUsersStorage.addLike(filmId, userId);
        getFilmById(filmId).getLikesFromUsers().add(like.getUserId());

        log.info("Пользовтель с idt {} лайкнул фильм с id {}.", userId, filmId);

        return getFilmById(filmId);
    }

    public FilmDto deleteLikeFromFilm(Integer filmId, Integer userId) {

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        Film film = filmDbStorage.findById(filmId).get();
        Set<Integer> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользовтель с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользовтаель еще не лайкал этот фильм.");
        }

        likesFromUsersStorage.deleteLike(filmId, userId);
        log.info("Лайк пользовтеля с id {} фильму с id {} был удален.", userId, filmId);

        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> filmsList = filmDbStorage.findAll();

        if (year != null) {
            filmsList = filmsList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        }

        if (genreId != null) {
            filmsList = filmsList.stream()
                    .filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
                    .toList();
        }

        if (count == null || count <= 0) {
            count = filmsList.size();
        }

        List<Film> sortedFilmsByLikes = new ArrayList<>(filmsList);

        Collections.sort(sortedFilmsByLikes, new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return Integer.compare(film2.getLikesFromUsers().size(), film1.getLikesFromUsers().size());
            }
        });

        if (sortedFilmsByLikes.size() > count) {
            sortedFilmsByLikes = sortedFilmsByLikes.subList(0, count);
        }

        List<FilmDto> listOfPopularFilms = new ArrayList<>();
        for (Film film : sortedFilmsByLikes) {
            FilmDto filmDto = getFilmById(film.getId());
            if (filmDto != null) {
                listOfPopularFilms.add(filmDto);
            }
        }

        log.info("Список наиболее популярных фильмов сформирован. Длина списка: {}", listOfPopularFilms.size());

        return listOfPopularFilms;
    }

    public List<FilmDto> getCommonFilms(Integer userId, Integer friendId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userDbStorage.findById(userId);
        if (existFriend.isEmpty()) {
            log.error("Переданный friendId не существует.");
            throw new NotFoundException("friendId не найден.");
        }

        List<LikesFromUsers> likesFromUsersList = likesFromUsersStorage.findLikesByUserId(userId);
        List<Integer> userFilmsIdList = likesFromUsersList.stream().map(x -> x.getFilmId()).toList();

        List<LikesFromUsers> likesFromFriendList = likesFromUsersStorage.findLikesByUserId(friendId);
        List<Integer> friendFilmsIdList = likesFromFriendList.stream().map(x -> x.getFilmId()).toList();

        List<Integer> commonFilmsId = new ArrayList<>();

        userFilmsIdList.stream().forEach(id -> {
            if (friendFilmsIdList.contains(id)) {
                commonFilmsId.add(id);
            }
        });

        List<FilmDto> commonFilms = commonFilmsId.stream().map(id -> getFilmById(id)).toList();

        return commonFilms;
    }

    public FilmDto deleteFilmById(Integer filmIdForDelete) {
        FilmDto filmForDelete = getFilmById(filmIdForDelete);

        filmGenreStorage.deleteFilmGenreByFilmId(filmIdForDelete);
        log.info("Удалены записи о жанрах удаляемого фильма");
        filmDirectorStorage.deleteFilmDirectorByFilmId(filmIdForDelete);
        log.info("Удалены записи о режиссерах удаляемого фильма");
        likesFromUsersStorage.deleteLikesFromUsersByFilmId(filmIdForDelete);
        log.info("Удалены записи о лайках удаляемого фильма");
        filmDbStorage.deleteFilmById(filmIdForDelete);
        log.info("Удален фильм с id: {}", filmIdForDelete);

        return filmForDelete;
    }

    public List<FilmDto> getFilmsByDirector(Integer directorId) {
        List<FilmDirector> filmDirectorsList = filmDirectorStorage.findFilmDirectorByDirectorId(directorId);
        List<Integer> findFilmIds = filmDirectorsList.stream().map(f -> f.getFilmId()).toList();

        List<FilmDto> findFilms = findFilmIds.stream().map(id -> getFilmById(id)).toList();

        return findFilms;
    }

    public List<FilmDto> sortedByLikes(List<FilmDto> filmsForSort) {
        List<FilmDto> sortedFilmsByLikes = new ArrayList<>(filmsForSort);

        Collections.sort(sortedFilmsByLikes, new Comparator<FilmDto>() {
            @Override
            public int compare(FilmDto film1, FilmDto film2) {
                return Integer.compare(film2.getLikesFromUsers().size(), film1.getLikesFromUsers().size());
            }
        });

        return sortedFilmsByLikes;
    }

    public List<FilmDto> sortedByYear(List<FilmDto> filmsForSort) {
        List<FilmDto> sortedFilmsByYear = new ArrayList<>(filmsForSort);

        Collections.sort(sortedFilmsByYear, new Comparator<FilmDto>() {
            @Override
            public int compare(FilmDto film1, FilmDto film2) {
                return film1.getReleaseDate().compareTo(film2.getReleaseDate());
            }
        });

        return sortedFilmsByYear;
    }

    public List<FilmDto> getRecommendations(Integer userId) {
        List<Integer> userFilms = likesFromUsersStorage.getLikedFilmsId(userId);
        if (userFilms.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Получаем фильмы, которые лайкнул текущий пользователь {}", userFilms);

        Map<Integer, List<Integer>> allUserLikes = getAllUserLikes();

        Integer similarTasteUserId = null;
        int maxCommonLikes = 0;

        for (Map.Entry<Integer, List<Integer>> entry : allUserLikes.entrySet()) {
            Integer otherUserId = entry.getKey();
            List<Integer> otherUserFilms = entry.getValue();

            if (!otherUserId.equals(userId)) {
                int commonLikes = (int) otherUserFilms.stream()
                        .filter(userFilms::contains)
                        .count();

                if (commonLikes > maxCommonLikes) {
                    maxCommonLikes = commonLikes;
                    similarTasteUserId = otherUserId;
                }
            }
        }

        if (similarTasteUserId == null) {
            log.info("Нет похожего пользователя на {}", userId);
            return List.of();
        }
        log.info("Похожий пользователь {}", similarTasteUserId);

        List<Integer> similarTasteUserFilms = allUserLikes.get(similarTasteUserId);
        return similarTasteUserFilms.stream()
                .filter(filmId -> !userFilms.contains(filmId))
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getSearch(String query, String by) {
        List<FilmDto> result;
        if (query == null || by == null) {
            result = filmDbStorage.findAll()
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
            return sortedByLikes(result);
        } else if (by.contains("director") || by.contains("title")) {
            return filmDbStorage.getSearch(query, by);
        } else {
            throw new InternalServerException("Произошла ошибка");
        }
    }

    private Map<Integer, List<Integer>> getAllUserLikes() {
        List<Integer> allUserIds = likesFromUsersStorage.getAllUserIds();

        Map<Integer, List<Integer>> userLikes = new HashMap<>();
        for (Integer userId : allUserIds) {
            userLikes.put(userId, likesFromUsersStorage.getLikedFilmsId(userId));
        }

        return userLikes;
    }
}

