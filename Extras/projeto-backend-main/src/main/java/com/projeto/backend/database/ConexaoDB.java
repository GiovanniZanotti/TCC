package com.projeto.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexaoDB {

    private Connection conexao;

    public Connection openConnection() {
        try {
            String password = "123456";
            String username = "root";
            String url = "jdbc:mysql://localhost:3306/tcc";
            this.conexao = DriverManager.getConnection(url, username, password);
            return this.conexao;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (!this.conexao.isClosed()) {
                this.conexao.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
