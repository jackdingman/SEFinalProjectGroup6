import java.sql.SQLException;

public class UserService {
    private final UserDao dao = new UserDaoImpl();

    // Throw error when username is already taken.
    public static class DuplicateUserException extends Exception {
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
        String pHash = HashUtil.sha256(password);
        String aHash = HashUtil.sha256(answer);
        User u = new User(username, pHash, question, aHash);
        dao.save(u);
    }
}