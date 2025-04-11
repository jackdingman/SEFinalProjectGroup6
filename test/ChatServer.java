package test;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ChatServer extends AbstractServer
{
  // Data fields for this chat server.
  private JTextArea log;
  private JLabel status;
  private boolean running = false;
  private Database database;  //  changed from DatabaseFile to Database

  // Constructor for initializing the server with default settings.
  public ChatServer()
  {
    super(12345);
    this.setTimeout(500);
  }

  // Getter that returns whether the server is currently running.
  public boolean isRunning()
  {
    return running;
  }

  // Setters for the data fields corresponding to the GUI elements.
  public void setLog(JTextArea log)
  {
    this.log = log;
  }
  public void setStatus(JLabel status)
  {
    this.status = status;
  }

  // Setter for the database
  public void setDatabase(Database database)
  {
    this.database = database;
  }

  // When the server starts, update the GUI.
  public void serverStarted()
  {
    running = true;
    status.setText("Listening");
    status.setForeground(Color.GREEN);
    log.append("Server started\n");
  }

  // When the server stops listening, update the GUI.
  public void serverStopped()
  {
    status.setText("Stopped");
    status.setForeground(Color.RED);
    log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
  }

  // When the server closes completely, update the GUI.
  public void serverClosed()
  {
    running = false;
    status.setText("Close");
    status.setForeground(Color.RED);
    log.append("Server and all current clients are closed - press Listen to restart\n");
  }

  // When a client connects or disconnects, display a message in the log.
  public void clientConnected(ConnectionToClient client)
  {
    log.append("Client " + client.getId() + " connected\n");
  }

  // When a message is received from a client, handle it.
  public void handleMessageFromClient(Object arg0, ConnectionToClient arg1)
  {
    if (arg0 instanceof LoginData)
    {
      LoginData data = (LoginData)arg0;
      Object result;
      if (database.verifyAccount(data.getUsername(), data.getPassword()))
      {
        result = "LoginSuccessful";
        log.append("Client " + arg1.getId() + " successfully logged in as " + data.getUsername() + "\n");
      }
      else
      {
        result = new Error("The username and password are incorrect.", "Login");
        log.append("Client " + arg1.getId() + " failed to log in\n");
      }

      try
      {
        arg1.sendToClient(result);
      }
      catch (IOException e)
      {
        return;
      }
    }

    else if (arg0 instanceof CreateAccountData)
    {
      CreateAccountData data = (CreateAccountData)arg0;
      Object result;
      if (database.createNewAccount(data.getUsername(), data.getPassword()))
      {
        result = "CreateAccountSuccessful";
        log.append("Client " + arg1.getId() + " created a new account called " + data.getUsername() + "\n");
      }
      else
      {
        result = new Error("The username is already in use.", "CreateAccount");
        log.append("Client " + arg1.getId() + " failed to create a new account\n");
      }

      try
      {
        arg1.sendToClient(result);
      }
      catch (IOException e)
      {
        return;
      }
    }
  }

  public void listeningException(Throwable exception) 
  {
    running = false;
    status.setText("Exception occurred while listening");
    status.setForeground(Color.RED);
    log.append("Listening exception: " + exception.getMessage() + "\n");
    log.append("Press Listen to restart server\n");
  }
}
