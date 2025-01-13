package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorStorage extends BaseStorage<Director> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTOR(NAME) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTOR SET DIRECTOR_ID = ?, NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Director> findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Director addDirector(Director director) {
        Integer id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName());
        return director;
    }

    public boolean deleteDirector(Integer directorId) {
        return delete(DELETE_QUERY, directorId);
    }
}
