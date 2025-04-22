package game.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// Manages database connections using JDBC with MySQL.
public class DBConnection {
    // Database configuration parameters loaded once at class initialization
    private static final String url;
    private static final String user;
    private static final String pass;

    static {
        try {
            // Load database configuration from properties file
            Properties p = new Properties();
            p.load(DBConnection.class.getResourceAsStream("/db.properties"));

            // Extract connection parameters
            url  = p.getProperty("db.url");       // JDBC connection URL
            user = p.getProperty("db.user");      // Database username
            pass = p.getProperty("db.password");  // Database password

            // Load MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            // Convert to unchecked exception to prevent class initialization
            throw new ExceptionInInitializerError("Failed to initialize DB configuration: " + e);
        }
    }

    // Provides a new database connection using configured credentials
    public static Connection get() throws Exception {
        // Using DriverManager to create a new connection each time
        return DriverManager.getConnection(url, user, pass);
    }

    // Execute one INSERT/UPDATE/DELETE & return rows‑affected
    public static int executeDML(String sql) throws SQLException {
        try (Connection c = get();
             Statement stmt = c.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}