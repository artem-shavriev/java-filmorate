package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserDbStorage extends BaseStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"USER\"";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"USER\" WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO \"USER\"(USER_NAME, LOGIN, EMAIL, BIRTHDAY, PASSWORD)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE \"USER\" SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?, " +
            "PASSWORD = ? WHERE USER_ID = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM \"USER\" WHERE EMAIL = ?";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM \"USER\" WHERE LOGIN = ?";

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
        String login;
        if (user.getLogin() == null) {
            login = user.getUsername() + "-" + user.getEmail();
            user.setLogin(login);
            log.warn("Логин не передан, он будет сгенирирован автоматически.");
        }

        long id = insert(INSERT_QUERY,
                user.getUsername(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getPassword()
        );
        user.setId(id);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User update(User user) {
        String login;
        if (user.getLogin() == null) {
            login = user.getUsername() + "-" + user.getEmail();
            user.setLogin(login);
            log.warn("Логин не передан, он будет сгенирирован автоматически.");
        }
        update(UPDATE_QUERY,
                user.getUsername(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getPassword(),
                user.getId()
        );

        log.info("Пользователь c id: {} обновлен", user.getId());
        return user;
    }
}
