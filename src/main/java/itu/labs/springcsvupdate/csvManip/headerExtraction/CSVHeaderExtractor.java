package itu.labs.springcsvupdate.csvManip.headerExtraction;

import itu.labs.springcsvupdate.apiresponse.ApiResponse;

import java.io.File;

public interface CSVHeaderExtractor {
    String extractHeaders(String path) throws Exception;
    String extractHeaders(File file) throws Exception;
}
