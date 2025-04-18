package login.model;

/**
 * Represents a user entity in the system, containing authentication credentials,
 * security challenge information, and gameplay progress.
 * <p>
 * All sensitive fields (password, security answer) are stored in hashed form
 * to ensure security best practices.
 */
public class User {
    private int id;                  // Database-generated unique identifier
    private String username;         // Unique username (natural key)
    private String hashedPassword;   // Password stored as SHA-256 hash
    private String securityQuestion; // Security challenge question for password recovery
    private String securityAnswer;   // Security answer stored as SHA-256 hash
    private int totalScore;          // Cumulative game score (default: 0) -- Don't know if we are going to use or not

    // Constructs a new user with core security credentials.
    public User(String username,
                String hashedPassword,
                String securityQuestion,
                String securityAnswer) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.totalScore = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(int id) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}