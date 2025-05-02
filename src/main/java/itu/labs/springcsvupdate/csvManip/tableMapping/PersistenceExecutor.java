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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersistenceExecutor {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        System.out.println(mTable.getVariables());
        csvTableDetails.setTableCSVMapping(tableCSVMapping);
        return csvTableDetails;
    }

    @Transactional
    public EndResult persist(CSVTableDTO csvTableDTO) throws Exception{
        CsvTableDetails csvTableDetails = loadDetails(csvTableDTO);
        EndResult endResult = new EndResult();
        
        List<String> before = new ArrayList<>();
        List<String> after = new ArrayList<>();
        List<String> modified = new ArrayList<>();
        List<String> newRows = new ArrayList<>();

        CSVElements csvElements = csvTableDetails.getCsvElements();
        TableCSVMapping tableCSVMapping = csvTableDetails.getTableCSVMapping();
        String query;
        for (DataRow dataRow : csvElements.getDataRows()) {
            if (tableCSVMapping.getTableVariables().containsKey(dataRow.getRow().get(csvTableDTO.getPrimaryKey()))) {
                query = tableCSVMapping.provideUpdateQuery();
            } else {
                query = tableCSVMapping.provideInsertQuery();
            }
            Map<String,Object> arg = convertDataRow(dataRow,tableCSVMapping.getTable());
            namedParameterJdbcTemplate.update(query,arg);
        }
        return null;
    }
    private Map<String, Object> convertDataRow(DataRow dataRow,MTable mTable){
        Map<String, Object> paramMap = new HashMap<>();
        for (Map.Entry<String,String> element : dataRow.getRow().entrySet()){
//            System.out.println(element.getKey() + " " + element.getValue());
            Object temp = typeConverter.convertStringToType(element.getValue(),mTable.getVariables().get(element.getKey()).getDatabaseType());
            paramMap.put(element.getKey(),temp);
        }
        return paramMap;
    }
    private List<Map<String, Object>> convertDataRows(List<DataRow> dataRows,MTable mTable){
        List<Map<String, Object>> rows = new ArrayList<>();
        for (DataRow dataRow : dataRows) {
            Map<String, Object> paramMap = new HashMap<>();
            for (Map.Entry<String,String> element : dataRow.getRow().entrySet()){
                Object temp = typeConverter.convertStringToType(element.getValue(),mTable.getVariables().get(element.getKey()).getDatabaseType());
                paramMap.put(element.getKey(),temp);
            }
            rows.add(paramMap);
        }
        return rows;
    }
}
