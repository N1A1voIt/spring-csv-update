package itu.labs.springcsvupdate.csvManip.headerExtraction;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class CSVHeaderExtractorService implements CSVHeaderExtractor {

    @Override
    public String extractHeaders(String path) throws Exception{
        File file = new File(path);
        return extractHeaders(file);
    }

    @Override
    public String extractHeaders(File file) throws Exception {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file supplied");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                return line.trim();
            } else {
                throw new IOException("File is empty. No header found.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract headers from CSV file", e);
        }
    }
}