import java.sql.*;
import java.util.Optional;

// JDBC implementation of UserDao for CRUD operations on User entities
class UserDaoImpl implements UserDao {

    // Escapes single quotes in SQL strings to prevent syntax errors
    private String escape(String s) {
        return s.replace("'", "''");
    }

    // Checks whether a user with the given username exists
    @Override
    public boolean existsByUsername(String username) throws Exception {
        String safe = escape(username);
        String sql  = "SELECT 1 FROM users WHERE username = '" + safe + "'";
        try (Connection c = DBConnection.get();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    // Persists a new user record into the database
    @Override
    public void save(User user) throws SQLException {
        // Escape all string values to avoid SQL syntax issues
        String u = escape(user.getUsername());
        String p = escape(user.getHashedPassword());
        String q = escape(user.getSecurityQuestion());
        String a = escape(user.getSecurityAnswer());
        int t = user.getTotalScore();

        String sb = "INSERT INTO users " +
                "(username, hashed_password, security_question, security_answer, total_score) " +
                "VALUES ('" + u + "', " +
                "'" + p + "', " +
                "'" + q + "', " +
                "'" + a + "', " +
                t + ")";

        // Execute DML statement
        DBConnection.executeDML(sb);
    }

    // Retrieves a user by username
    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String safe = escape(username);
        String sql  = "SELECT * FROM users WHERE username = '" + safe + "'";

        try (Connection c = DBConnection.get();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next()) return Optional.empty();

            // Map result set into User object
            User u = new User(
                    rs.getString("username"),
                    rs.getString("hashed_password"),
                    rs.getString("security_question"),
                    rs.getString("security_answer")
            );
            u.setTotalScore(rs.getInt("total_score"));
            return Optional.of(u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}