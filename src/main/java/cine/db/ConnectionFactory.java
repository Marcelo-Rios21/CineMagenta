package cine.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
   private static String url;
   private static String user; 
   private static String pass;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties props = new Properties();
            try (var in = ConnectionFactory.class.getResourceAsStream("/application.properties")) {
                if (in == null) throw new IllegalStateException("No se encontro application.properties");
                props.load(in);
            }
            url  = props.getProperty("db.url");
            user = props.getProperty("db.user");
            pass = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar configuración de BD", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
