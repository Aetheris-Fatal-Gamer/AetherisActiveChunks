package br.com.aetherismc.activechunks.database;

import br.com.aetherismc.activechunks.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

    public Core core;
    public Connection connection;

    public SQL(Core core) {
        this.core = core;
    }

    public void setConnection() {
        try {
            File file = new File(this.core.getDataFolder().getAbsolutePath() + File.separator);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.core.getDataFolder().getAbsolutePath() + File.separator + "database.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void startDatabase() {
        try {
            this.setConnection();
            Statement stmt = this.connection.createStatement();
            /*
             * tipo: 1 - 24 horas / 2 - Somente online
             */
            stmt.execute("CREATE TABLE IF NOT EXISTS " + Core.databaseTable + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nick VARCHAR(255) NOT NULL, "
                    + "tipo ENUM(1,2) NOT NULL, "
                    + "chunks INTEGER NOT NULL, "
                    + "dias INTEGER NOT NULL, "
                    + "data_ativacao DATETIME NOT NULL, "
                    + "removido VARCHAR(1) NOT NULL DEFAULT 'F')");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
