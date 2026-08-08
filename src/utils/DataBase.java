package src.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    public static String url = "jdbc:postgresql://localhost:5432/postgres";
    public static String user = "postgres";
    public static String password = "postgres";
    
    public static Connection conectar() throws SQLException {
        Connection con = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to Data Base");

        return con;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String newUrl) {
        url = newUrl;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String newUser) {
        user = newUser;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String newPassword) {
        password = newPassword;
    }
}
