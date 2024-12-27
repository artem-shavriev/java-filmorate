package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaStorage.class,
        MpaRowMapper.class,
})

public class MpaStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    public void testFindAllMpa() {
        assertThat(mpaStorage.findAll().size()).isEqualTo(5);
    }

    @Test
    public void testFindMpaById() {
        assertThat(mpaStorage.findById(1)).isNotNull();
    }
}
