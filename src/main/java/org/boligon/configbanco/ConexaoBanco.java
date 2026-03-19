package org.boligon.configbanco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL = "jdbc:h2:./data/observacao_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão com o banco", e);
        }
    }
}