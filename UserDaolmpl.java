import java.sql.*;
import java.util.Optional;

// Data Access Object (DAO) implementation for user-related database operations.
class UserDaoImpl implements UserDao {

    private String escape(String s) {
        return s.replace("'", "''");
    }

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

    @Override
    public void save(User user) throws SQLException {
        String u = escape(user.getUsername());
        String p = escape(user.getHashedPassword());
        String q = escape(user.getSecurityQuestion());
        String a = escape(user.getSecurityAnswer());
        int    t = user.getTotalScore();

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO users ")
                .append("(username, hashed_password, security_question, security_answer, total_score) ")
                .append("VALUES ('").append(u).append("', ")
                .append("'").append(p).append("', ")
                .append("'").append(q).append("', ")
                .append("'").append(a).append("', ")
                .append(t).append(")");

        DBConnection.executeDML(sb.toString());
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String safe = escape(username);
        String sql  = "SELECT * FROM users WHERE username = '" + safe + "'";

        try (Connection c = DBConnection.get();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next()) return Optional.empty();

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