package login.dao;

import login.database.DBConnection;
import login.model.User;
import java.sql.*;
import java.util.Optional;

// Data Access Object (DAO) implementation for user-related database operations.
public class UserDaoImpl implements UserDao {

    // Checks if a username already exists in the database.
    @Override
    public boolean existsByUsername(String username) throws SQLException {
        // Efficient existence check using SQL EXISTS would be better, but this works
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                // Returns true if at least one record exists
                return rs.next();
            }
        } catch (Exception e) {
            // Wrap non-SQL exceptions to maintain interface contract
            throw new SQLException("Username check failed", e);
        }
    }

    // Persists a new user to the database.
    @Override
    public void save(User user) throws SQLException {
        String sql = """
            INSERT INTO users
              (username, hashed_password, security_question, security_answer, total_score)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            // Set parameters for secure insertion
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getSecurityQuestion());
            ps.setString(4, user.getSecurityAnswer());
            ps.setInt   (5, user.getTotalScore());

            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException dup) {
            // Specifically propagate duplicate username errors
            throw dup;
        } catch (Exception e) {
            throw new SQLException("User save operation failed", e);
        }
    }

    // Retrieves a user by their username.
    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                // Map ResultSet to User object
                User u = new User(
                        rs.getString("username"),
                        rs.getString("hashed_password"),
                        rs.getString("security_question"),
                        rs.getString("security_answer")
                );
                u.setTotalScore(rs.getInt("total_score"));

                // If we want to add ID and/or timestamp fields we can
                return Optional.of(u);
            }
        } catch (Exception e) {
            throw new SQLException("User lookup failed", e);
        }
    }
}