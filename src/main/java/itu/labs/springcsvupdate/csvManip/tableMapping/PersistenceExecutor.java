package itu.labs.springcsvupdate.csvManip.tableMapping;

import itu.labs.springcsvupdate.checking.TableFetcherService;
import itu.labs.springcsvupdate.csvManip.EndResult;
import itu.labs.springcsvupdate.csvManip.csvdataloader.CSVLoadingService;
import itu.labs.springcsvupdate.csvManip.headerExtraction.CSVHeaderExtractorService;
import itu.labs.springcsvupdate.csvManip.tableMapping.dto.CSVTableDTO;
import itu.labs.springcsvupdate.csvManip.tableMapping.typer.TypeConverter;
import itu.labs.springcsvupdate.csvProps.CSVElements;
import itu.labs.springcsvupdate.dataprops.DataRow;
import itu.labs.springcsvupdate.tableListing.MTable;
import itu.labs.springcsvupdate.tableListing.TableMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PersistenceExecutor {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    TableFetcherService tableFetcherService;
    @Autowired
    CSVHeaderExtractorService csvHeaderExtractorService;
    @Autowired
    CSVLoadingService csvLoadingService;
    @Autowired
    TableMappingService tableMappingService;
    @Autowired
    private TypeConverter typeConverter;

    private CsvTableDetails loadDetails(CSVTableDTO csvTableDTO) throws Exception {
        CsvTableDetails csvTableDetails = new CsvTableDetails();
        csvTableDetails.setCsvElements(csvLoadingService.loadCSV(csvTableDTO.getCsvFilePath(),csvTableDTO.getEquivalents(),csvTableDTO.getPrimaryKey()));
        MTable mTable = tableMappingService.provideTablesMetadata(jdbcTemplate).get(csvTableDTO.getTableName());
        HashMap<String, Map<String,String>> rows = tableFetcherService.fetchTable(csvTableDTO.getTableName(),csvTableDTO.getPrimaryKey(),jdbcTemplate);
        TableCSVMapping tableCSVMapping = new TableCSVMapping();
        tableCSVMapping.setTableVariables(rows);
        tableCSVMapping.setTable(mTable);
        csvTableDetails.setTableCSVMapping(tableCSVMapping);
        return csvTableDetails;
    }
    public EndResult persist(CSVTableDTO csvTableDTO) throws Exception{
        CsvTableDetails csvTableDetails = loadDetails(csvTableDTO);
        CSVElements csvElements = csvTableDetails.getCsvElements();
        TableCSVMapping tableCSVMapping = csvTableDetails.getTableCSVMapping();
        for (DataRow dataRow : csvElements.getDataRows()) {
            if (tableCSVMapping.getTableVariables().containsKey(dataRow.getRow().get(csvTableDTO.getPrimaryKey()))) {
                String query = tableCSVMapping.provideUpdateQuery();

            } else {
                String query = tableCSVMapping.provideInsertQuery();
            }
        }
        return null;
    }
}
