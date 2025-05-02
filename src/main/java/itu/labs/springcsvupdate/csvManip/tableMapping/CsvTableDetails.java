package itu.labs.springcsvupdate.csvManip.tableMapping;

import itu.labs.springcsvupdate.csvProps.CSVElements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsvTableDetails {
    CSVElements csvElements;
    TableCSVMapping tableCSVMapping;
}
