package ru.yandex.practicum.filmorate.DbTests;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreStorage.class,
        GenreRowMapper.class,
})
public class GenreStorageTest {
    private final GenreStorage genreStorage;

    @Test
    public void testFindGenreById() {

        Optional<Genre> filmOptional = genreStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllGenres() {

        Optional<Genre> filmOptional = Optional.of(genreStorage.findAll().get(3));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 4)
                );
    }

}
