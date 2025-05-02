package itu.labs.springcsvupdate.csvManip.csvdataloader;

import com.opencsv.CSVReader;
import itu.labs.springcsvupdate.csvManip.headerExtraction.CSVHeaderExtractor;
import itu.labs.springcsvupdate.csvManip.headerExtraction.CSVHeaderExtractorService;
import itu.labs.springcsvupdate.csvProps.CSVElements;
import itu.labs.springcsvupdate.dataprops.DataRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CSVLoadingService implements CSVLoadingInterface {
    @Autowired
    CSVHeaderExtractorService csvHeaderExtractorService;

    @Override
    public CSVElements loadCSV(String csvFilePath, Map<String, String> equivalents,String primaryKey) throws Exception {
        CSVElements csvElements = new CSVElements();
        List<DataRow> dataRows = readCSV(csvFilePath,equivalents);
        csvElements.setPrimaryKey(primaryKey);
        csvElements.setDataRows(dataRows);
        return csvElements;
    }

    @Override
    public CSVElements loadCSV(File csvFile, Map<String, String> equivalents,String primaryKey) throws Exception {
        CSVElements csvElements = new CSVElements();
        List<DataRow> dataRows = readCSV(csvFile.getAbsolutePath(),equivalents);
        csvElements.setPrimaryKey(primaryKey);
        csvElements.setDataRows(dataRows);
        return csvElements;
    }
    private List<DataRow> readCSV(String filePath,Map<String,String> equivalent) throws Exception {
        List<DataRow> dataRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] headers = reader.readNext(); // Read the header row
            String[] line;
            while ((line = reader.readNext()) != null) {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowMap.put(equivalent.get(headers[i]), line[i]);
                }
                DataRow dataRow = new DataRow();
                dataRow.setRow(rowMap);
                dataRows.add(dataRow);
            }
        }
        return dataRows;
    }
}

