package itu.labs.springcsvupdate.checking;

import itu.labs.springcsvupdate.dataprops.DataRow;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableFetcherService {

    public HashMap<String, Map<String, String>> fetchTable(String tableName, String primaryKey, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM " + tableName;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        HashMap<String, Map<String, String>> tableDataMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Object pkValue = row.get(primaryKey);
            if (pkValue != null) {
                String pk = pkValue.toString();
                Map<String, String> rowMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String columnName = entry.getKey();
                    Object value = entry.getValue();
                    rowMap.put(columnName, value != null ? value.toString() : null);
                }
                tableDataMap.put(pk, rowMap);
            }
        }

        return tableDataMap;
    }
}
