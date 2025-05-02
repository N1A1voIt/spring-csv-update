package itu.labs.springcsvupdate.csvManip.tableMapping.typer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TypeConverter {

    @Autowired
    private ResourceLoader resourceLoader;

    private Map<String, Map<String, Object>> typeConversions = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:type-mapping.yaml");
            try (InputStream inputStream = resource.getInputStream()) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);

                // Extract conversions mapping
                typeConversions = (Map<String, Map<String, Object>>) data.get("conversions");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load type conversion mappings", e);
        }
    }

    /**
     * Converts a string value to the appropriate Java type based on the database column type.
     * Uses the type-mapping.yml configuration.
     *
     * @param stringValue The string value to convert
     * @param columnType The database column type (VARCHAR, INTEGER, etc.)
     * @return The converted value with the appropriate Java type
     */
    public Object convertStringToType(String stringValue, String columnType) {
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }

        // Normalize column type to uppercase and remove any size parameters
        String normalizedType = columnType.toUpperCase();
        if (normalizedType.contains("(")) {
            normalizedType = normalizedType.substring(0, normalizedType.indexOf("("));
        }

        // Get conversion configuration from YAML
        Map<String, Object> conversionConfig = typeConversions.get(normalizedType);
        if (conversionConfig == null) {
            // Default to String if no specific conversion is defined
            return stringValue;
        }

        String converter = (String) conversionConfig.get("converter");

        // Apply the appropriate conversion based on the converter type
        return applyConverter(converter, stringValue, conversionConfig);
    }

    private Object applyConverter(String converter, String value, Map<String, Object> config) {
        try {
            switch (converter) {
                case "toString":
                    return value;

                case "toInteger":
                    return Integer.parseInt(value);

                case "toLong":
                    return Long.parseLong(value);

                case "toBigDecimal":
                    return new BigDecimal(value);

                case "toFloat":
                    return Float.parseFloat(value);

                case "toDouble":
                    return Double.parseDouble(value);

                case "toBoolean":
                    return Boolean.parseBoolean(value);

                case "toDate":
                    return convertToDate(value, (List<String>) config.get("formats"));

                case "toTime":
                    return Time.valueOf(value);

                case "toTimestamp":
                    return convertToTimestamp(value, (List<String>) config.get("formats"));

                default:
                    return value;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error converting value '" + value + "' using converter '" +
                    converter + "': " + e.getMessage(), e);
        }
    }

    private Date convertToDate(String value, List<String> formats) throws ParseException {
        if (formats == null || formats.isEmpty()) {
            try {
                return Date.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ParseException("Invalid date format: " + value, 0);
            }
        }

        ParseException lastException = null;
        for (String format : formats) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                dateFormat.setLenient(false);
                java.util.Date parsedDate = dateFormat.parse(value);
                return new Date(parsedDate.getTime());
            } catch (ParseException e) {
                lastException = e;
                // Try next format
            }
        }

        // If we get here, none of the formats worked
        throw lastException;
    }

    private Timestamp convertToTimestamp(String value, List<String> formats) throws ParseException {
        if (formats == null || formats.isEmpty()) {
            try {
                return Timestamp.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ParseException("Invalid timestamp format: " + value, 0);
            }
        }

        ParseException lastException = null;
        for (String format : formats) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                dateFormat.setLenient(false);
                java.util.Date parsedDate = dateFormat.parse(value);
                return new Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                lastException = e;
                // Try next format
            }
        }

        // If we get here, none of the formats worked
        throw lastException;
    }
}
