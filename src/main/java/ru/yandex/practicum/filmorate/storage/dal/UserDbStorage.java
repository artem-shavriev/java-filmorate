package ru.yandex.practicum.filmorate.storage.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@Slf4j
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM USER_";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USER_ WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO USER_(NAME, LOGIN, EMAIL, BIRTHDAY)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USER_ SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? " +
            "WHERE USER_ID = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM USER_ WHERE EMAIL = ?";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM USER_ WHERE LOGIN = ?";
    private static final String DELETE_QUERY = "DELETE FROM USER_ WHERE USER_ID = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(int id) {
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

    public User addUser(User user) {
        String name;
        if (user.getName() == null || user.getName().isBlank()) {
            name = user.getLogin();
            user.setName(name);
            log.warn("Имя не передано, оно будет сгенирировано автоматически.");
        }

        int id = insert(INSERT_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User updateUser(User user) {
        String name;
        if (user.getName() == null || user.getName().isBlank()) {
            name = user.getLogin();
            user.setName(name);
            log.warn("Имя не передано, оно будет сгенирировано автоматически.");
        }
        update(UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );

        log.info("Пользователь c id: {} обновлен", user.getId());
        return user;
    }

    public boolean deleteUserById(Integer userId) {
        return delete(DELETE_QUERY, userId);
    }
}
