package com.saf.utils;
import java.io.FileInputStream;
import java.util.Properties;

// Config reader for reading configuration properties
public class ConfigReader {
private static final Properties props = new Properties();
static {
    try {
        // Load config.properties from classpath
        FileInputStream fis = new FileInputStream(
            "src/test/resources/config.properties");
        props.load(fis);
        fis.close();
        System.out.println("[ConfigReader] config.properties loaded OK");
    } catch (Exception e) {
        // Log and rethrow as unchecked to fail fast if config can't be loaded
        System.err.println("[ConfigReader] ERROR: " + e.getMessage());
        throw new RuntimeException("Cannot load config.properties", e);
    }
}


// Get property value by key
public static String get(String key) {
    String val = props.getProperty(key, "");
    if (val.isEmpty()) {
        System.err.println("[ConfigReader] WARNING: key not found -> " + key);
    }
    return val;
}
}
