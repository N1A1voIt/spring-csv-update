package itu.labs.springcsvupdate.tableListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TableMappingService implements TableMappingSignature{
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @Override
    public HashMap<String, MTable> provideTablesMetadata(JdbcTemplate jdbcTemplate) throws SQLException {
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});
        HashMap<String , MTable> tabMapping = new HashMap<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            MTable tableDescriptor = new MTable();
            tableDescriptor.setTable(tableName);
            ResultSet columnResultSet = metaData.getColumns(null, null, tableName, null);
            tableDescriptor.setVariables(tableElements(columnResultSet,metaData,tableName));
            tabMapping.put(tableName,tableDescriptor);
        }
        return tabMapping;
    }
    private HashMap<String,Variable> tableElements(ResultSet columnResultSet, DatabaseMetaData metaData, String tableName) throws SQLException {
//        List<Variable> variables = new ArrayList<>();
        HashMap<String,Variable> variableMap = new HashMap<>();
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
        List<String> primaryKeyColumns = new ArrayList<>();
        while (primaryKeys.next()) {
            primaryKeyColumns.add(primaryKeys.getString("COLUMN_NAME"));
        }
        while (columnResultSet.next()) {
            Variable variable = new Variable();
            String columnName = columnResultSet.getString("COLUMN_NAME");
            String columnType = columnResultSet.getString("TYPE_NAME");
            variable.setVariableName(columnName);
            variable.setDatabaseType(columnType);
            if (primaryKeyColumns.contains(columnName)) {
                variable.setKeyType("PRIMARY");
            } else {
                variable.setKeyType("NONE");
            }
            variableMap.put(columnName,variable);
//            variables.add(variable);
        }
        return variableMap;
    }

}
