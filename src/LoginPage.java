import java.awt.*;
import javax.swing.*;
import java.util.Optional;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.event.ItemEvent;
import java.sql.PreparedStatement;


// Authentication screen for user login. Handles credentials validation,  password visibility toggle, and password recovery workflow.
public class LoginPage extends JFrame {
    private final JFrame welcomeParent;

    // Data access object for user operations
    private final UserDaoImpl userDao = new UserDaoImpl();

    // Form components
    private final JTextField userField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);
    private final JCheckBox showPass = new JCheckBox("show");
    private final JButton submitBtn = new JButton("Submit");

    public LoginPage(JFrame parent) {
        super("Login");
        this.welcomeParent = parent;
        initUI();
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Constructs the UI layout:
    private void initUI() {
        Color bg = new Color(240, 240, 240);
        Color btnBase = new Color(90, 90, 90);

        getContentPane().setBackground(bg);
        setLayout(new BorderLayout());  // Main layout manager

        // Configure top navigation bar
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        styleFlatNavButton(backBtn);
        backBtn.addActionListener(e -> {
            new WelcomePage().setVisible(true);  // Return to welcome screen
            dispose();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setOpaque(false);
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // Configure main form using GridBagLayout for precise positioning
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,10,10,10);

        // Add form title
        JLabel title = new JLabel("Login");
        title.setFont(loadHeadingFont().deriveFont(40f));
        title.setForeground(Color.DARK_GRAY);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 3;
        g.insets = new Insets(0,10,30,10);
        form.add(title, g);

        // Username input field
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
        g.gridy = 1; g.gridx = 0;
        form.add(new JLabel("Username:"), g);
        g.gridx = 1;
        form.add(userField, g);

        // Password input with visibility toggle
        g.gridy = 2; g.gridx = 0;
        form.add(new JLabel("Password:"), g);
        g.gridx = 1;
        form.add(passField, g);
        g.gridx = 2;
        form.add(showPass, g);

        // Submit button
        g.gridy = 3; g.gridx = 0; g.gridwidth = 3;
        g.anchor = GridBagConstraints.CENTER;
        styleElevatedButton(submitBtn, btnBase);
        form.add(submitBtn, g);

        // Password recovery button
        JButton recoverBtn = new JButton("Recover Password");
        styleElevatedButton(recoverBtn, btnBase);
        g.gridy = 4;
        form.add(recoverBtn, g);

        add(form, BorderLayout.CENTER);

        // Configure component behaviors
        showPass.addItemListener(e -> passField.setEchoChar(
                e.getStateChange() == ItemEvent.SELECTED ? (char)0 : '•'));

        submitBtn.addActionListener(e -> handleSubmit());
        recoverBtn.addActionListener(e -> handleRecovery());
    }

    private void handleSubmit() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        try {
            Optional<User> maybe = userDao.findByUsername(username);

            if (maybe.isEmpty() || !HashUtil.sha256(password)
                    .equals(maybe.get().getHashedPassword())) {
                showErrorDialog("Invalid username or password", "Login Failed");
                return;
            }

            // Show success message FIRST
            JOptionPane.showMessageDialog(this,
                    "Login Successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Close windows and launch game AFTER user acknowledges
            dispose(); // Close login window
            if (welcomeParent != null) {
                welcomeParent.dispose(); // Close welcome window
            }

            // Launch game
            SwingUtilities.invokeLater(() -> {
                new Game(username).setVisible(true);
            });

        } catch (SQLException ex) {
            showErrorDialog("Database error: " + ex.getMessage(), "Error");
        }
    }

    // Manages password recovery workflow:
    private void handleRecovery() {
        String uname = JOptionPane.showInputDialog(this, "Enter your username:");
        if (uname == null || uname.isBlank()) return;

        try {
            Optional<User> maybe = userDao.findByUsername(uname.trim());
            if (maybe.isEmpty()) {
                showErrorDialog("Username not found.", "Error");
                return;
            }

            User u = maybe.get();
            String answer = JOptionPane.showInputDialog(this, u.getSecurityQuestion());
            if (answer == null) return;

            if (!HashUtil.sha256(answer).equals(u.getSecurityAnswer())) {
                showErrorDialog("Security answer is incorrect.", "Error");
                return;
            }

            // Password reset validation
            String newPwd = JOptionPane.showInputDialog(this, "Enter new password:");
            if (newPwd == null || newPwd.isBlank()) return;

            String confirm = JOptionPane.showInputDialog(this, "Confirm new password:");
            if (!newPwd.equals(confirm)) {
                showErrorDialog("Passwords do not match.", "Error");
                return;
            }

            // Secure password update
            updatePasswordInDB(uname.trim(), newPwd);
            JOptionPane.showMessageDialog(this,
                    "Password reset successful!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showErrorDialog("Error during recovery: " + ex.getMessage(), "Error");
        }
    }

    // Updates user password in database using prepared statement
    private void updatePasswordInDB(String username, String newPassword)
            throws Exception {
        String newHash = HashUtil.sha256(newPassword);
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE users SET hashed_password = ? WHERE username = ?")) {
            ps.setString(1, newHash);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    // Displays standardized error dialogs
    private void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Configures navigation buttons with flat appearance
    private void styleFlatNavButton(JButton b) { /* Nothing */ }

    // Applies elevated button styling with hover effects
    private void styleElevatedButton(JButton ignoredB, Color ignoredBase) { /* Nothing */ }

    // Loads custom font for UI headings with fallback to system font
    private Font loadHeadingFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf")) {
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignore) {}
        return new Font("SansSerif", Font.BOLD, 40);  // Degradation
    }
}