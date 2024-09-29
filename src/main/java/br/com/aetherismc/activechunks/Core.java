package br.com.aetherismc.activechunks;

import br.com.aetherismc.activechunks.database.Database;
import br.com.aetherismc.activechunks.database.SQL;
import br.com.aetherismc.activechunks.player.CheckTask;
import br.com.aetherismc.activechunks.player.CommandDefault;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    public SQL sql;
    public Database database;

    public static Core core;
    public static String databaseTable;

    public Core() {
        Core.core = this;
        this.sql = new SQL(this);
        this.database = new Database();
    }

    public void onEnable() {
        Core.databaseTable = "chunks_keys";

        this.getSQL().startDatabase();
        this.getCommand("chunks").setExecutor(new CommandDefault());
        this.getDatabase().checkTask();
        (new CheckTask()).runTaskTimer(this, 1800L, 1800L);

        Bukkit.getConsoleSender().sendMessage("\n§2====== §aAetherisActiveChunks §2======" +
                "\n§2Status: §aIniciado." +
                "\n§2Servidor: §aAetherisMC" +
                "\n§2Autor: §aJoão Veiga" +
                "\n§2====== §aAetherisActiveChunks §2======");
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("\n§4====== §cAetherisActiveChunks §4======" +
                "\n§4Status: §cDesabilitado." +
                "\n§4Servidor: §cAetherisMC" +
                "\n§4Autor: §cJoão Veiga" +
                "\n§4====== §cAetherisActiveChunks §4======");
    }

    public SQL getSQL() {
        return this.sql;
    }

    public Database getDatabase() {
        return this.database;
    }
}
