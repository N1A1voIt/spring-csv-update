package itu.labs.springcsvupdate.csvManip.experimental;

import itu.labs.springcsvupdate.apiresponse.ApiResponse;
import itu.labs.springcsvupdate.checking.TableFetcherService;
import itu.labs.springcsvupdate.csvManip.EndResult;
import itu.labs.springcsvupdate.csvManip.csvdataloader.CSVLoadingInterface;
import itu.labs.springcsvupdate.csvManip.csvdataloader.CSVLoadingService;
import itu.labs.springcsvupdate.csvManip.headerExtraction.CSVHeaderExtractor;
import itu.labs.springcsvupdate.csvManip.headerExtraction.CSVHeaderExtractorService;
import itu.labs.springcsvupdate.csvManip.tableMapping.PersistenceExecutor;
import itu.labs.springcsvupdate.csvManip.tableMapping.dto.CSVTableDTO;
import itu.labs.springcsvupdate.csvProps.CSVElements;
import itu.labs.springcsvupdate.tableListing.TableMappingService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
public class CSVLoaderController {
    @Autowired
    PersistenceExecutor persistenceExecutor;
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

    @PostMapping("/api/v1/experiment/extract-header")
    public ResponseEntity<?> extractHeader(@RequestParam("file") MultipartFile file) {
        CSVHeaderExtractor csvHeaderExtractor = csvHeaderExtractorService;
        ApiResponse apiResponse = new ApiResponse();
        try {
            File tempFile = File.createTempFile("uploaded_csv_", ".csv");
            file.transferTo(tempFile);
            String ret = csvHeaderExtractor.extractHeaders(tempFile);
            apiResponse.setStatus(200);
            apiResponse.setData(ret);
            apiResponse.setExceptions(null);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e){
            apiResponse.setStatus(400);

            apiResponse.setData(null);
            apiResponse.setExceptions(List.of(e));
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
    @PostMapping("/api/v1/experiment/extract-table")
    public ResponseEntity<?> fetchTable(@RequestBody TableRequest table) {
        return ResponseEntity.ok(tableFetcherService.fetchTable(table.getTable(),"etu",jdbcTemplate));
    }

    @PostMapping(value = "/api/v1/experiment/load-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> fetchCSV(
            @RequestPart("file") MultipartFile file,
            @RequestPart("csvRequest") CSVRequest csvRequest) {
        try {
            File tempFile = File.createTempFile("uploaded_csv_", ".csv");
            file.transferTo(tempFile);
            CSVElements result = csvLoadingService.loadCSV(tempFile, csvRequest.getEquivalents(), csvRequest.getPrimaryKey());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing CSV file");
        }
    }
    @GetMapping(value = "/api/v1/experiment/table-metadata")
    public ResponseEntity<?> metaData() throws Exception{
        return ResponseEntity.ok(tableMappingService.provideTablesMetadata(jdbcTemplate));
    }
    @PostMapping(value = "/api/v1/experiment/persistence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> persist(
            @RequestPart("file") MultipartFile file,
            @RequestPart("csvRequest") CSVTableDTO csvRequest){
        csvRequest.setCsvFilePath(file.getOriginalFilename());
        try {
            EndResult endResult = persistenceExecutor.persist(csvRequest);
            return ResponseEntity.ok(endResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing CSV file");
        }
    }
}

@Getter
@Setter
class CSVRequest {
    String csvFilePath;
    String tableName;
    HashMap<String, String> equivalents;
    String primaryKey;
}
class TableRequest {
    private String table;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
