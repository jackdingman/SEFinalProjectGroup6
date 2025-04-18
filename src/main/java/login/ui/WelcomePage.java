package login.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

// Welcome page shown at application launch, providing entry points for login and account creation.
public class WelcomePage extends JFrame {

    // Navigation buttons
    private final JButton loginBtn  = new JButton("Login");             // Access existing account
    private final JButton createBtn = new JButton("Create Account");    // Start registration process

    public WelcomePage() {
        super("Welcome");

        initLookAndFeel();   // Match OS native styling for non-custom components
        initUI();            // Build visual hierarchy and apply custom styling

        // Window configuration
        setSize(1000, 600);
        setMinimumSize(new Dimension(1000, 600));  // Prevent resizing below initial size
        setLocationRelativeTo(null);               // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Terminate process on window close
    }

    // Initializes look for standard Swing components
    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}  // Fallback to default Swing look if unavailable
    }

    // Constructs the visual hierarchy:
    private void initUI() {
        // Base background color for the frame
        Color lightGrayBg = new Color(240, 240, 240);
        getContentPane().setBackground(lightGrayBg);

        // Vertical box layout for top-to-bottom component arrangement
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Application title with custom typography
        JLabel heading = new JLabel("Welcome", SwingConstants.CENTER);
        heading.setForeground(Color.DARK_GRAY);
        heading.setFont(loadHeadingFont());  // Attempt to load branded font
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));
        add(heading);

        // Flexible space that expands to push content vertically center
        add(Box.createVerticalGlue());

        // Horizontal button container with spacing
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button configuration
        configureButton(loginBtn, new Color(110, 110, 110));
        loginBtn.addActionListener(_ -> {
            new LoginPage(this).setVisible(true);  // Transition to auth screen
            dispose();  // Clean up current window
        });

        // Account creation button configuration
        configureButton(createBtn, new Color(110, 110, 110));
        createBtn.addActionListener(_ -> {
            new CreateAccountPage().setVisible(true);  // Transition to registration
            dispose();
        });

        // Add buttons to layout
        buttons.add(loginBtn);
        buttons.add(createBtn);
        add(buttons);

        // Bottom flexible space for vertical centering
        add(Box.createVerticalGlue());
    }

    // Loads custom font asset for branding purposes
    private Font loadHeadingFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf")) {
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(54f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ignore) {}  // Font loading failures are non-critical
        return new Font("SansSerif", Font.BOLD, 54);  // Degrade gracefully
    }

    // Applies consistent styling and behavior to navigation buttons
    private void configureButton(JButton b, Color base) {
        // Size and typography
        b.setPreferredSize(new Dimension(180, 60));
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setForeground(Color.WHITE);

        // Interactive elements
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMnemonic(b.getText().charAt(0));  // Alt-key shortcut for accessibility

        // Remove default Swing button styling
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);

        // Hover effect state management
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.putClientProperty("hover", true);
                b.repaint();  // Trigger visual update
            }
            @Override public void mouseExited(MouseEvent e) {
                b.putClientProperty("hover", false);
                b.repaint();
            }
        });

        // Install custom painter for button visuals
        b.setUI(new ElevatedButtonUI(base));
    }

    // Custom button renderer implementing:
    protected static class ElevatedButtonUI extends BasicButtonUI {
        private final Color baseColor;  // Base color for button variants

        ElevatedButtonUI(Color baseColor) {
            this.baseColor = baseColor;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();
            boolean hover = Boolean.TRUE.equals(c.getClientProperty("hover"));
            int inset = hover ? 2 : 4;  // Smaller inset creates raised appearance
            int arc = 20;              // Corner radius for rounded rectangles

            // Hover shadow effect
            if (hover) {
                g2.setComposite(AlphaComposite.SrcOver.derive(0.25f));
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(inset, inset, w - 2 * inset, h - 2 * inset, arc, arc);
            }

            // Button background
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(hover ? baseColor.brighter() : baseColor);
            g2.fillRoundRect(0, 0, w - 2 * inset, h - 2 * inset, arc, arc);

            // Text rendering with vertical centering
            String text = ((AbstractButton) c).getText();
            FontMetrics fm = c.getFontMetrics(c.getFont());
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(text,
                    (w - textW) / 2 - inset,  // Horizontal center
                    (h + textH) / 2 - fm.getDescent() - inset  // Vertical center
            );

            g2.dispose();
        }
    }
}