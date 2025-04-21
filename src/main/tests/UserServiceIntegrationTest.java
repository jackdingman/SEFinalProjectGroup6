
import login.dao.UserDao;
import login.dao.UserDaolmpl;
import login.model.User;
import login.service.UserService;
import login.service.UserService.DuplicateUserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import game.database.DBConnection;

// Integration test for the UserService, UserDao, and DBConnection components.
public class UserServiceIntegrationTest {
    private UserService userService;
    private UserDao userDao;
    private String testUsername;

    @Before
    public void setUp() {
        userService = new UserService();
        userDao = new UserDaolmpl();

        // Generate a unique username for each test run to avoid conflicts
        testUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @After
    public void tearDown() {
        // Clean up test data after each test
        try (Connection conn = DBConnection.get();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM users WHERE username LIKE 'testuser_%'");
        } catch (Exception e) {
            System.err.println("Error cleaning up test data: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterAndFindUser() throws Exception {
        String password = "password123";
        String question = "What is your favorite color?";
        String answer = "blue";

        userService.register(testUsername, password, question, answer);
        Optional<User> foundUser = userDao.findByUsername(testUsername);

        assertTrue("User should exist in database", foundUser.isPresent());
        assertEquals("Username should match", testUsername, foundUser.get().getUsername());
        assertEquals("Security question should match", question, foundUser.get().getSecurityQuestion());

        assertNotEquals("Password should be hashed", password, foundUser.get().getHashedPassword());
        assertNotEquals("Security answer should be hashed", answer, foundUser.get().getSecurityAnswer());
    }

    @Test(expected = DuplicateUserException.class)
    public void testRegisterDuplicateUsername() throws Exception {
        String password = "password123";
        String question = "What is your favorite color?";
        String answer = "blue";

        userService.register(testUsername, password, question, answer);

        // Try to register another user with the same username (Throw exception)
        userService.register(testUsername, "differentPassword", "Different question?", "different answer");
    }

    @Test
    public void testUserExistsByUsername() throws Exception {
        String password = "password123";
        String question = "What is your favorite color?";
        String answer = "blue";

        // Check that the user doesn't exist yet
        assertFalse("User should not exist before registration", userDao.existsByUsername(testUsername));

        userService.register(testUsername, password, question, answer);
        assertTrue("User should exist after registration", userDao.existsByUsername(testUsername));
    }
}
