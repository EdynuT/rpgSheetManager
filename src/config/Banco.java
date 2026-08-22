package src.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {
    private static final String URL = "jdbc:postgresql://localhost:5433/postgres";
    private static final String USER = "postgres";
    private static final String SENHA = "postgres";
    
    public static Connection conectar() throws SQLException {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, SENHA);
            System.out.println("Conectado ao banco!");
            return con;
        }
        catch(SQLException e) {
            System.out.println("Conexão com o banco de dados falhou!");
        }

        return con;
    }
}
