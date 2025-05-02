package itu.labs.springcsvupdate.csvManip.csvdataloader;

import itu.labs.springcsvupdate.csvProps.CSVElements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface CSVLoadingInterface {
    /**
     * equivalents looks like this : "Column1" : "id"
     * */
    CSVElements loadCSV(String csvFilePath, Map<String,String> equivalents, String primaryKey) throws Exception;
    CSVElements loadCSV(File csvFile, Map<String,String> equivalents,String primaryKey) throws Exception;
}
