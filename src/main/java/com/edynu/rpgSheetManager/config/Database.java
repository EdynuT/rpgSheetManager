package com.edynu.rpgSheetManager.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:postgresql://ep-cold-unit-ac1k67x0-pooler.sa-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_QI6TgzxdOl5E&sslmode=require&channelBinding=require";

    public static Connection connect() throws SQLException {
        try {
            Connection con = DriverManager.getConnection(URL);
            System.out.println("Conectado ao banco de dados!");
            return con;
        } catch(SQLException e) {
            System.out.println("Falha ao conectar ao banco de dados!");
            throw e;
        }
    }
}
