package src.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
    T insert(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> list() throws SQLException;
    T findById(int id) throws SQLException;
}
