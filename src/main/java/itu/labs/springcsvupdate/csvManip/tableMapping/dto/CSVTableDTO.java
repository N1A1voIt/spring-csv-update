package itu.labs.springcsvupdate.csvManip.tableMapping.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class CSVTableDTO {
    String tableName;
    String csvFilePath;
    HashMap<String, String> equivalents;
    String primaryKey;
}
