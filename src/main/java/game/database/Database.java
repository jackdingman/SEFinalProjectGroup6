package game.database;

import java.sql.*;
import java.util.*;
import java.io.*;

public class Database
{
  private Connection conn;

  public Database() 
  {
    FileInputStream fis = null;
    Properties prop = new Properties();
    
    try 
    {
      fis = new FileInputStream("main/resources/db.properties");
      prop.load(fis);
      fis.close();
    } 
    catch (IOException e)
    {
      e.printStackTrace();
    }

    String url = prop.getProperty("url");
    String user = prop.getProperty("user");
    String pass = prop.getProperty("password");

    try
    {
      conn = DriverManager.getConnection(url, user, pass);
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
  }

  //  New method for verifying user login
  public boolean verifyAccount(String username, String password)
  {
    String query = "SELECT * FROM User WHERE username = ? AND password = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      ResultSet rs = stmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  //  New method for creating a new account
  public boolean createNewAccount(String username, String password)
  {
    String checkQuery = "SELECT * FROM User WHERE username = ?";
    String insertQuery = "INSERT INTO User (username, password) VALUES (?, ?)";
    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
      checkStmt.setString(1, username);
      ResultSet rs = checkStmt.executeQuery();
      if (rs.next()) {
        return false; // Username already exists
      }

      try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
        insertStmt.setString(1, username);
        insertStmt.setString(2, password);
        insertStmt.executeUpdate();
        return true;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // (Original helper methods can remain if needed for other labs)
  public ArrayList<String> query(String query)
  {
    ArrayList<String> list = new ArrayList<String>();
    String record = "";
    try 
    {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int noFields = rsmd.getColumnCount();

      while(rs.next())
      {
        record = "";
        for(int i = 0; i < noFields; i++)
        {
          record += rs.getString(i+1) + ",";
        }
        list.add(record);
      }

      if(list.isEmpty())
        return null;
    }
    catch(SQLException e)
    {
      return null;
    }
    return list;
  }

  public void executeDML(String dml) throws SQLException
  {
    Statement stmt = conn.createStatement();
    stmt.execute(dml);
  }
}
