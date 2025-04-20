package game.multiplayer;

import javax.swing.*;
import java.awt.Color;
import java.io.IOException;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Graphical user interface for controlling the chat server
public class ServerGUI extends JFrame {
  // Label displaying current server connection status
  private JLabel status;

  // Labels for input fields
  private String[] labels = {"Port #", "Timeout"};

  // Text field corresponding to each label
  private JTextField[] textFields = new JTextField[labels.length];

  // Test area showing server log output
  private JTextArea log;

  // Control buttons for server operations
  private JButton listen, close, stop, quit;

  // Underlying chat server instance
  private ChatServer server;

  // Constructor
  public ServerGUI() {
    super("Chat Server");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(450, 450);

    // Top panel
    JPanel north = new JPanel();
    north.add(new JLabel("Status:"));
    status = new JLabel("Not Connected");
    status.setForeground(Color.RED);
    north.add(status);
    add(north, BorderLayout.NORTH);

    // Center panel
    JPanel center = new JPanel(new BorderLayout());

    // Sub-panel
    JPanel centerNorth = new JPanel(new GridLayout(labels.length, 2, 5, 5));
    for (int i = 0; i < labels.length; i++) {
      centerNorth.add(new JLabel(labels[i], JLabel.RIGHT));
      textFields[i] = new JTextField(10);
      centerNorth.add(textFields[i]);
    }

    // Default values
    textFields[0].setText("8300");
    textFields[1].setText("500");
    center.add(centerNorth, BorderLayout.NORTH);

    // Log display wrapped in scoll panel
    log = new JTextArea(10, 35);
    log.setEditable(false);
    center.add(new JScrollPane(log), BorderLayout.CENTER);
    add(center, BorderLayout.CENTER);

    // South panel
    JPanel south = new JPanel();
    listen = new JButton("Listen");
    close  = new JButton("Close");
    stop   = new JButton("Stop");
    quit   = new JButton("Quit");
    south.add(listen);
    south.add(close);
    south.add(stop);
    south.add(quit);
    add(south, BorderLayout.SOUTH);

    // Initialize server and inject dependencies
    server = new ChatServer();
    server.setLog(log);
    server.setStatus(status);

    // Button event handlers
    EventHandler handler = new EventHandler();
    listen.addActionListener(handler);
    close.addActionListener(handler);
    stop.addActionListener(handler);
    quit.addActionListener(handler);

    setVisible(true); // Show GUI
  }

  // Inner class to handle button actions
  class EventHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      try {
        if (src == listen) {
          // Ensures inputs are provided
          if (textFields[0].getText().isEmpty() || textFields[1].getText().isEmpty()) {
            log.append("Port or timeout missing\n");
          } else {
            // Parse and set server parameters
            server.setPort(Integer.parseInt(textFields[0].getText()));
            server.setTimeout(Integer.parseInt(textFields[1].getText()));
            server.listen();
          }
        } else if (src == close) {
          server.close(); // Close server socket
        } else if (src == stop) {
          server.stopListening(); // Stop accepting new connections
        } else if (src == quit) {
          System.exit(0); // Exit
        }
      } catch (IOException ex) {
        log.append("Error: " + ex.getMessage() + "\n");
      }
    }
  }

  // Entry point to launch the server GUI
  public static void main(String[] args) {
    new ServerGUI();
  }
}