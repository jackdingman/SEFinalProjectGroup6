package login.model;

// Represents a user entity in the system
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

    // Returns database ID
    public int getId() {
        return id;
    }

    // Sets the database ID
    public void setId(int id) {
        this.id = id;
    }

    // Returns database Username
    public String getUsername() {
        return username;
    }

    // Sets the database Username
    public void setUsername(int id) {
        this.username = username;
    }

    // Returns the database Hashed password
    public String getHashedPassword() {
        return hashedPassword;
    }

    // Sets the database hashed password
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    // Returns the database security question
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    // Sets the database security question
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    // Returns the database security answer
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    // Sets the database security answer
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    // Returns the database total score
    public int getTotalScore() {
        return totalScore;
    }

    // Sets the databases total score
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}