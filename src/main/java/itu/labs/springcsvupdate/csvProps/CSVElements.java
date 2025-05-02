package itu.labs.springcsvupdate.csvProps;

import itu.labs.springcsvupdate.dataprops.DataRow;
import itu.labs.springcsvupdate.tableListing.Variable;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class CSVElements {
    String csvHeaderName;
    String primaryKey;
    List<DataRow> dataRows;
}
