package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserDbStorage extends BaseStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM USER";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USER WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO USER(NAME, LOGIN, EMAIL, BIRTHDAY)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE USER SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?," +
            " WHERE USER_ID = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM USER WHERE EMAIL = ?";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM USER WHERE LOGIN = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findByLogin(String login) {
        return findOne(FIND_BY_LOGIN_QUERY, login);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public User save(User user) {
        String name;
        if (user.getName() != null) {
            name = user.getName();
            log.warn("Имя не передано, его заменит логин пользователя.");
        } else {
            name = user.getLogin();
        }

        long id = insert(INSERT_QUERY,
                name,
                user.getLogin(),
                Date.from(Instant.from(user.getBirthday())),
                user.getEmail()
        );
        user.setId(id);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User update(User user) {
        String name;
        if (user.getName() != null) {
            name = user.getName();
            log.warn("Имя не передано, его заменит логин пользователя.");
        } else {
            name = user.getLogin();
        }
        update(UPDATE_QUERY,
                name,
                user.getLogin(),
                Date.from(Instant.from(user.getBirthday())),
                user.getEmail(),
                user.getId()
        );

        log.info("Пользователь c id: {} обновлен", user.getId());
        return user;
    }
}
