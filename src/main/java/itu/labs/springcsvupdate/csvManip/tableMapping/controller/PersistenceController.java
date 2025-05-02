package itu.labs.springcsvupdate.csvManip.tableMapping.controller;

import itu.labs.springcsvupdate.apiresponse.ApiResponse;
import itu.labs.springcsvupdate.csvManip.EndResult;
import itu.labs.springcsvupdate.csvManip.tableMapping.PersistenceExecutor;
import itu.labs.springcsvupdate.csvManip.tableMapping.dto.CSVTableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/csv")
public class PersistenceController {
    @Autowired
    PersistenceExecutor persistenceExecutor;
    @PostMapping(value = "/persistence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> persist (
            @RequestPart("file") MultipartFile file,
            @RequestPart("csvRequest") CSVTableDTO csvTableDto
    ) {
        csvTableDto.setCsvFilePath(file.getOriginalFilename());
        ApiResponse apiResponse = new ApiResponse();
        try {
            EndResult endResult = persistenceExecutor.persist(csvTableDto);
            apiResponse.setStatus(200);
            apiResponse.setData(endResult);
            apiResponse.setExceptions(null);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setStatus(500);
            apiResponse.setData(null);
            apiResponse.setExceptions(List.of(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
