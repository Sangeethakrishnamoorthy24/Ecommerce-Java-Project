package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {
    private static final String PROPERTIES_FILE = "resources/db.properties";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(PROPERTIES_FILE));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to find db.properties");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection error");
        }
        return connection;
    }
}
