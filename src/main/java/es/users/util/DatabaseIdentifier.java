package es.users.util;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseIdentifier {

    private final DataSource dataSource;
    private String           databaseProductName;

    public DatabaseIdentifier(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @PostConstruct
    public void init() throws SQLException {
        try (var conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            this.databaseProductName = metaData.getDatabaseProductName().toLowerCase();
        }
    }


    public String getDatabaseProductName() {
        return databaseProductName;
    }


    public boolean isPostgres() {
        return databaseProductName.contains("postgresql");
    }


    public boolean isMySQL() {
        return databaseProductName.contains("mysql");
    }


    public boolean isOracle() {
        return databaseProductName.contains("oracle");
    }


    public boolean isH2() {
        return databaseProductName.contains("h2");
    }


    public Map<String, String> getConstraintMap() {
        if (isH2()) {
            return Map.of("public.constraint_index_4", "nif", "public.constraint_index_4d", "email");
        }
        if (isMySQL()) {
            return Map.of("constrainMysql", "valueMysql");
        }
        if (isOracle()) {
            return Map.of("constrainOracle", "valueOracle");
        }
        if (isPostgres()) {
            return Map.of("constrainPostgres", "valuepostgres");
        }
        return Map.of();
    }
}
