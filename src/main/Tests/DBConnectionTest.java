import game.database.DBConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DBConnectionTest {
    private Connection mockConnection;
    private Statement mockStatement;

    @Before
    public void setUp() {
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
    }

    @Test
    public void testGetConnection() throws Exception {
        // Use Mockito's MockedStatic to mock the static DriverManager.getConnection method
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // Call the method under test
            Connection result = DBConnection.get();

            assertNotNull("Connection should not be null", result);
            assertEquals("Should return the mock connection", mockConnection, result);

            mockedDriverManager.verify(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString()));
        }
    }

    @Test
    public void testExecuteDML() throws Exception {
        String testSql = "INSERT INTO test_table VALUES (1, 'test')";
        int expectedRowsAffected = 1;

        // Use Mockito's MockedStatic to mock the static DBConnection.get method
        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            mockedDBConnection.when(DBConnection::get).thenReturn(mockConnection);

            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeUpdate(testSql)).thenReturn(expectedRowsAffected);

            // Call the method under test (we need to call the real method)
            mockedDBConnection.when(() -> DBConnection.executeDML(testSql)).thenCallRealMethod();
            int result = DBConnection.executeDML(testSql);

            assertEquals("Should return the expected rows affected", expectedRowsAffected, result);
            verify(mockStatement).executeUpdate(testSql);
        }
    }

    @Test
    public void testExecuteDMLWithException() throws Exception {
        String testSql = "INSERT INTO test_table VALUES (1, 'test')";

        // Use Mockito's MockedStatic to mock the static DBConnection.get method
        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            // Configure the mock to throw an exception
            mockedDBConnection.when(DBConnection::get).thenThrow(new SQLException("Test exception"));

            mockedDBConnection.when(() -> DBConnection.executeDML(testSql)).thenCallRealMethod();

            // The method should throw a RuntimeException
            try {
                DBConnection.executeDML(testSql);
                fail("Should have thrown RuntimeException");
            } catch (RuntimeException e) {
                assertTrue(e.getCause() instanceof SQLException);
                assertEquals("Test exception", e.getCause().getMessage());
            }
        }
    }
}