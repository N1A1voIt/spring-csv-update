package itu.labs.springcsvupdate.tableListing;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.HashMap;

public interface TableMappingSignature {
    public HashMap<String, MTable> provideTablesMetadata(JdbcTemplate jdbcTemplate) throws SQLException;
}
