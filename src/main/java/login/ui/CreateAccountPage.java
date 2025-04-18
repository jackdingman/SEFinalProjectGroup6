package login.ui;

import login.service.UserService;
import login.service.UserService.DuplicateUserException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

// Registration screen for creating new user accounts. Handles:
public class CreateAccountPage extends JFrame {
    private final UserService svc = new UserService();  // Business logic handler

    // Form components
    private final JTextField userField = new JTextField(20);
    private final JTextField passField = new JTextField(20);  // Consider using JPasswordField
    private final JTextField confirmField = new JTextField(20);
    private final JComboBox<String> question = new JComboBox<>(new String[]{
            "What is your pet's name?", "What is your mother's maiden name?",
            "What was the name of your first school?", "What is your favorite book?"});
    private final JTextField answerField = new JTextField(20);
    private final JButton submitBtn = new JButton("Submit");

    public CreateAccountPage() {
        super("Create Account");
        initUI();  // Initialize UI components
        setSize(1000, 600);
        setMinimumSize(new Dimension(1000, 600));  // Prevent window resizing
        setLocationRelativeTo(null);  // Center window
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // Close only this window
    }

    // Constructs the UI:
    private void initUI() {
        Color bg = new Color(240,240,240);  // Background color
        Color btnBase = new Color(90,90,90);  // Button base color

        getContentPane().setBackground(bg);
        setLayout(new BorderLayout());  // Main layout

        // Configure top navigation
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        styleFlatNavButton(backBtn);
        backBtn.addActionListener(_ -> {
            new WelcomePage().setVisible(true);  // Return to welcome screen
            dispose();  // Clean up resources
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,10));
        topBar.setOpaque(false);
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // Main form using GridBagLayout for precise control
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,10,10,10);  // Component padding

        // Form title
        JLabel title = new JLabel("Create Account");
        title.setFont(loadFont());  // Custom font loading
        title.setForeground(Color.DARK_GRAY);
        g.gridx=0; g.gridy=0; g.gridwidth=2; g.anchor=GridBagConstraints.CENTER;
        g.insets = new Insets(0,10,30,10);  // Title spacing
        form.add(title, g);

        // Username field
        g.gridwidth=1; g.anchor=GridBagConstraints.WEST; g.insets=new Insets(10,10,10,10);
        g.gridy=1; g.gridx=0; form.add(new JLabel("Username:"), g);
        g.gridx=1; form.add(userField, g);

        // Password fields
        g.gridy=2; g.gridx=0; form.add(new JLabel("Password:"), g);
        g.gridx=1; form.add(passField, g);
        g.gridy=3; g.gridx=0; form.add(new JLabel("Confirm Password:"), g);
        g.gridx=1; form.add(confirmField, g);

        // Security components
        g.gridy=4; g.gridx=0; form.add(new JLabel("Security Question:"), g);
        g.gridx=1; form.add(question, g);
        g.gridy=5; g.gridx=0; form.add(new JLabel("Answer:"), g);
        g.gridx=1; form.add(answerField, g);

        // Submit button
        g.gridy=6; g.gridx=0; g.gridwidth=2; g.anchor=GridBagConstraints.CENTER;
        styleElevatedButton(submitBtn, btnBase);
        form.add(submitBtn, g);

        add(form, BorderLayout.CENTER);

        // Input validation
        submitBtn.setEnabled(false);
        DocumentListener validator = new DocumentListener() {
            private void update() {
                boolean ok = !userField.getText().trim().isEmpty()
                        && !passField.getText().trim().isEmpty()
                        && !confirmField.getText().trim().isEmpty()
                        && !answerField.getText().trim().isEmpty();
                submitBtn.setEnabled(ok);  // Enable only when all fields filled
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        // Attach validation to all input fields
        userField.getDocument().addDocumentListener(validator);
        passField.getDocument().addDocumentListener(validator);
        confirmField.getDocument().addDocumentListener(validator);
        answerField.getDocument().addDocumentListener(validator);

        submitBtn.addActionListener(_ -> handleSubmit());
    }

    // Handles form submission:
    private void handleSubmit() {
        String user = userField.getText().trim();
        String p1 = passField.getText();
        String p2 = confirmField.getText();

        // Client-side validation
        if (!p1.equals(p2)) {
            showErrorDialog("Passwords do not match.");
            return;
        }

        try {
            // Service layer handles password hashing and security answer encryption
            svc.register(user, p1,
                    (String)question.getSelectedItem(),
                    answerField.getText().trim());

            JOptionPane.showMessageDialog(this,
                    "Account created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new WelcomePage().setVisible(true);
            dispose();  // Close registration window
        } catch (DuplicateUserException dup) {
            showErrorDialog(dup.getMessage());
        } catch (Exception ex) {
            showErrorDialog("Database error: " + ex.getMessage());
        }
    }

    // Styles navigation buttons with hover effects
    private void styleFlatNavButton(JButton b) {
        b.setFocusPainted(false);  // Remove focus outline
        b.setBorder(BorderFactory.createEmptyBorder(4,12,4,12));  // Padding
        b.setBackground(new Color(210,210,210));  // Default color
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(180,180,180));  // Hover color
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(210,210,210));  // Restore color
            }
        });
    }

    // Applies custom styling to action buttons
    private void styleElevatedButton(JButton b, Color base) {
        b.setPreferredSize(new Dimension(180,50));
        b.setFont(new Font("SansSerif", Font.BOLD,18));
        b.setForeground(Color.WHITE);
        b.setBackground(base);
        b.setFocusPainted(false);       // Remove focus ring
        b.setBorderPainted(false);      // Remove default border
        b.setContentAreaFilled(false);  // Use custom painting
        b.setOpaque(false);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.putClientProperty("hover", true);
                b.repaint();  // Trigger custom UI update
            }
            public void mouseExited(MouseEvent e) {
                b.putClientProperty("hover", false);
                b.repaint();
            }
        });
        b.setUI(new WelcomePage.ElevatedButtonUI(base));  // Shared button style
    }

    // Loads custom font with fallback to system font
    private Font loadFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf")) {
            assert is != null;
            Font f = Font.createFont(Font.TRUETYPE_FONT,is).deriveFont(40f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f;
        } catch (Exception e) {
            return new Font("SansSerif", Font.BOLD, 40);  // Fallback font
        }
    }

    // Displays error dialogs with consistent styling
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}