package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {
    public static String getPropertyString(String fileName) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
            StringBuilder connectionString = new StringBuilder();
            connectionString.append("jdbc:mysql://");
            connectionString.append(properties.getProperty("hostname")).append(":");
            connectionString.append(properties.getProperty("port")).append("/");
            connectionString.append(properties.getProperty("dbname"));
            connectionString.append("?user=").append(properties.getProperty("username"));
            connectionString.append("&password=").append(properties.getProperty("password"));
            return connectionString.toString();
        } catch (IOException e) {
            System.out.println("Error loading properties file: " + e.getMessage());
            return null;
        }
    }
}

