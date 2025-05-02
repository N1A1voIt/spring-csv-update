package itu.labs.springcsvupdate.csvManip.tableMapping;

import itu.labs.springcsvupdate.tableListing.MTable;
import itu.labs.springcsvupdate.tableListing.Variable;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Var;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TableCSVMapping {
    MTable table;
    HashMap<String, Map<String,String>> tableVariables;

    public String provideInsertQuery() {
        if (table == null || table.getTable() == null || table.getVariables() == null || table.getVariables().isEmpty()) {
            throw new IllegalStateException("Table and variables must not be null or empty to create an insert query.");
        }
        StringBuilder columnsPart = new StringBuilder();
        StringBuilder valuesPart = new StringBuilder();
        for(Map.Entry<String, Variable> entry:table.getVariables().entrySet()){
            Variable variable = entry.getValue();
            if (!table.getVariables().containsKey(variable.getVariableName())) {
                continue;
            }
            columnsPart.append(variable.getVariableName()).append(", ");
            valuesPart.append(":").append(variable.getVariableName()).append(", ");
        }
        if (columnsPart.length() > 0) {
            columnsPart.setLength(columnsPart.length() - 2);
            valuesPart.setLength(valuesPart.length() - 2);
        }
        return "INSERT INTO " + table.getTable() + " (" + columnsPart + ") VALUES (" + valuesPart + ")";
    }

    public String provideUpdateQuery() {
        if (table == null || table.getTable() == null || table.getVariables() == null || table.getVariables().isEmpty()) {
            throw new IllegalStateException("Table and variables must not be null or empty to create an update query.");
        }
        StringBuilder setPart = new StringBuilder();
        StringBuilder wherePart = new StringBuilder();
        for(Map.Entry<String, Variable> entry:table.getVariables().entrySet()){
            Variable variable = entry.getValue();
            if (!table.getVariables().containsKey(variable.getVariableName())) {
                continue;
            }
            if ("PRIMARY".equalsIgnoreCase(variable.getKeyType())) {
                wherePart.append(variable.getVariableName())
                        .append(" = :")
                        .append(variable.getVariableName())
                        .append(" AND ");
            } else {
                setPart.append(variable.getVariableName())
                        .append(" = :")
                        .append(variable.getVariableName())
                        .append(", ");
            }
        }
        if (setPart.length() > 0) {
            setPart.setLength(setPart.length() - 2);
        } else {
            throw new IllegalStateException("No columns to update.");
        }
        if (wherePart.length() > 0) {
            wherePart.setLength(wherePart.length() - 5);
        } else {
            throw new IllegalStateException("No primary key found for WHERE clause.");
        }
        return "UPDATE " + table.getTable() + " SET " + setPart + " WHERE " + wherePart;
    }

}