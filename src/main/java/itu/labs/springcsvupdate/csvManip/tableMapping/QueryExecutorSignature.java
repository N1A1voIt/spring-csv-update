package itu.labs.springcsvupdate.csvManip.tableMapping;

import itu.labs.springcsvupdate.csvManip.EndResult;
import itu.labs.springcsvupdate.csvProps.CSVElements;
import itu.labs.springcsvupdate.tableListing.MTable;

public interface QueryExecutorSignature {
    EndResult executeQuery(CSVElements csvElements, TableCSVMapping tableCSVMapping) throws Exception;
}
