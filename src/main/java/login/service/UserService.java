package login.service;

import login.dao.UserDao;
import login.dao.UserDaolmpl;
import login.model.User;
import login.util.HashUtil;

// Service handling user-related business logic, such as registration
public class UserService {
    // Data access object for user persistence
    private final UserDao dao = new UserDaolmpl();

    // Throw error when username is already taken.
    public static class DuplicateUserException extends Exception {

        // Constructs a DuplicateUserException with the given message
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    // Register a new user, throwing error if username exists.
    public void register(String username, String password,
                         String question, String answer)
            throws Exception {
        if (dao.existsByUsername(username)) {
            throw new DuplicateUserException("Username already taken");
        }
        // Hash password and answer using SHA-256
        String pHash = HashUtil.sha256(password);
        String aHash = HashUtil.sha256(answer);

        // Create and save the new user
        User u = new User(username, pHash, question, aHash);
        dao.save(u);
    }
}