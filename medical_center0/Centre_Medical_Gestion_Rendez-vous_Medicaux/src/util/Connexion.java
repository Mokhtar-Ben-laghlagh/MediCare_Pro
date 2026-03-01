package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Connexion {
    private static Connexion instance;
    private Connection conn;

    private static Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("base.properties")) {
            props.load(fis);
            String driver = props.getProperty("jdbc.driver");
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur chargement base.properties", e);
        }
    }

    private Connexion() throws SQLException {
        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.username");
        String pass = props.getProperty("jdbc.password");

        conn = DriverManager.getConnection(url, user, pass);
        conn.setAutoCommit(true);
    }

    public static synchronized Connexion getInstance() throws SQLException {
        if (instance == null || instance.conn.isClosed()) {
            instance = new Connexion();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
