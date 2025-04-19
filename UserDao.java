import java.sql.SQLException;
import java.util.Optional;

// Defines the contract for user-related database interactions while abstracting the underlying storage implementation. Follows the DAO pattern to:
public interface UserDao {

    // Checks for username existence in the persistent storage.
    boolean existsByUsername(String username) throws Exception;

    // Persists a new user entity to storage.
    void save(User user) throws SQLException;

    // Retrieves a user by their unique username.
    Optional<User> findByUsername(String username) throws SQLException;
}