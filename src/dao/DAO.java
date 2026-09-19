package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic Data Access Object (DAO) interface.
 * Provides basic CRUD operations for a specific entity type.
 * <p>
 * This interface should be implemented by concrete DAO classes for specific
 * entity types.
 * <p>
 * Usage example:
 * <pre>{@code
 * public class UserDAO implements DAO<User> {
 *     // Implement the CRUD methods for the User entity here.
 * }
 * }</pre>
 *
 * @param <T> the type of entity managed by this DAO.
 */
public interface DAO<T> {
    T insert(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> list() throws SQLException;
    T findById(int id) throws SQLException;
}
