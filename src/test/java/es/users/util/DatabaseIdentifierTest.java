package es.users.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseIdentifier class test")
class DatabaseIdentifierTest {

    @Mock
    private DataSource         dataSource;
    @Mock
    private Connection         connection;
    @Mock
    private DatabaseMetaData   metaData;
    private DatabaseIdentifier databaseIdentifier;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
    }


    // --- Helper Method to simulate initialization ---
    private void simulateInit(String dbProductName) throws SQLException {
        when(metaData.getDatabaseProductName()).thenReturn(dbProductName);
        databaseIdentifier = new DatabaseIdentifier(dataSource);
        databaseIdentifier.init();
    }


    @Test
    @DisplayName("init() should get product name in lower case")
    void init_shouldSetDatabaseProductNameLowerCase() throws SQLException {
        String productName = "PostgreSQL";
        simulateInit(productName);
        String actualProductName = databaseIdentifier.getDatabaseProductName();
        assertEquals(productName.toLowerCase(), actualProductName, "Product name should be in lowerCase");
        verify(dataSource).getConnection();
        verify(connection).getMetaData();
        verify(metaData).getDatabaseProductName();
        verify(connection).close();
    }


    @Test
    @DisplayName("init() should throw sqlexception if connection fails")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void shouldThrowSqlExceptionIfConnectionFails() throws SQLException {
        SQLException expectedException = new SQLException("Connection error simulated");
        when(dataSource.getConnection()).thenThrow(expectedException);
        databaseIdentifier = new DatabaseIdentifier(dataSource);
        SQLException thrown = assertThrows(SQLException.class, () -> {
            databaseIdentifier.init();
        }, "SQLException should be thrown if getConnection fails");
        assertEquals(expectedException.getMessage(), thrown.getMessage());
        verify(connection, never()).getMetaData();
        verify(connection, never()).close();
    }


    @Test
    @DisplayName("init() should throw sqlexception if get metadata fails")
    void shouldThrowSqlExceptionIfGetMetadataFails() throws SQLException {
        SQLException expectedException = new SQLException("Connection error simulated");
        when(connection.getMetaData()).thenThrow(expectedException);
        databaseIdentifier = new DatabaseIdentifier(dataSource);
        SQLException thrown = assertThrows(SQLException.class, () -> {
            databaseIdentifier.init();
        }, "SQLException should be thrown if getMetadata fails");
        assertEquals(expectedException.getMessage(), thrown.getMessage());
        verify(dataSource).getConnection();
        verify(connection).getMetaData();
        verify(connection).close();
        verify(metaData, never()).getDatabaseProductName();
    }


    @ParameterizedTest
    @CsvSource({
            "postgresql,   true,  false, false, false",
            "mysql,        false, true,  false, false",
            "oracle,       false, false, true,  false",
            "h2,           false, false, false, true",
            "unknown_db,    false, false, false, false"
    })
    @DisplayName("Should detect database type")
    void shouldDetectDatabaseType(String productName, boolean expectedPostgres, boolean expectedMySQL,
            boolean expectedOracle, boolean expectedH2) throws SQLException {
        simulateInit(productName);
        assertAll(
                () -> assertEquals(expectedPostgres, databaseIdentifier.isPostgres()),
                () -> assertEquals(expectedMySQL, databaseIdentifier.isMySQL()),
                () -> assertEquals(expectedOracle, databaseIdentifier.isOracle()),
                () -> assertEquals(expectedH2, databaseIdentifier.isH2()));
    }


    @ParameterizedTest
    @CsvSource({
            "h2,           public.constraint_index_4",
            "mysql,        constrainMysql",
            "oracle,       constrainOracle",
            "postgresql,   constrainPostgres"
    })
    @DisplayName("Shuld return correct constain map")
    void shouldReturnCorrectConstraintMap(String productName, String expectedKey) throws SQLException {
        simulateInit(productName);
        Map<String, String> constraints = databaseIdentifier.getConstraintMap();
        assertTrue(constraints.containsKey(expectedKey));
    }


    @Test
    @DisplayName("Should return empty map for unknown db")
    void shouldReturnEmptyMapForUnknownDatabase() throws SQLException {
        simulateInit("unknown_db");
        Map<String, String> constraints = databaseIdentifier.getConstraintMap();
        assertTrue(constraints.isEmpty());
    }
}
