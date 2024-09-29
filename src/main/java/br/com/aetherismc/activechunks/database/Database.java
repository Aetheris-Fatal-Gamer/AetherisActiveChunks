package br.com.aetherismc.activechunks.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.aetherismc.activechunks.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Database {

    public boolean add(String playerNick, String type, int quantity, int days) {
        try {
            PreparedStatement ps;
            ps = Core.core.getSQL().connection.prepareStatement("INSERT INTO " + Core.databaseTable + " "
                    + "(nick, tipo, chunks, dias, data_ativacao) "
                    + "VALUES "
                    + "(?,?,?,?,DATETIME('NOW', 'localtime'))");
            ps.setString(1, playerNick.toLowerCase());
            ps.setInt(2, (type.equalsIgnoreCase("alwayson") ? 1 : 2));
            ps.setInt(3, quantity);
            ps.setInt(4, days);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean hasID(String playerNick, int chunkID) {
        try {
            PreparedStatement ps;
            ps = Core.core.getSQL().connection.prepareStatement("SELECT id FROM " + Core.databaseTable + " WHERE nick = ? AND id = ? AND removido = ?");
            ps.setString(1, playerNick.toLowerCase());
            ps.setInt(2, chunkID);
            ps.setString(3, "F");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                rs.close();
                return true;
            } else {
                ps.close();
                rs.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPlayer(String playerNick) {
        try {
            PreparedStatement ps;
            ps = Core.core.getSQL().connection.prepareStatement("SELECT id FROM " + Core.databaseTable + " WHERE nick = ? AND removido = ?");
            ps.setString(1, playerNick.toLowerCase());
            ps.setString(2, "F");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                rs.close();
                return true;
            } else {
                ps.close();
                rs.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int chunkID) {
        try {
            PreparedStatement readPs;
            readPs = Core.core.getSQL().connection.prepareStatement("SELECT nick, tipo, chunks FROM " + Core.databaseTable + " WHERE id = ?");
            readPs.setInt(1, chunkID);

            ResultSet rs = readPs.executeQuery();
            if (rs.next()) {
                PreparedStatement deletePs;
                deletePs = Core.core.getSQL().connection.prepareStatement("UPDATE " + Core.databaseTable + " SET removido = ? WHERE id = ?");
                deletePs.setString(1, "T");
                deletePs.setInt(2, chunkID);
                deletePs.executeUpdate();
                deletePs.close();

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aecl chunks remove " + rs.getString("nick") + " " + (rs.getString("tipo").equalsIgnoreCase("1") ? "alwayson" : "onlineonly") + " " + rs.getInt("chunks"));

                deletePs.close();
                readPs.close();
                rs.close();
                return true;
            } else {
                readPs.close();
                rs.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean list(CommandSender sender, String playerNick) {
        try {
            PreparedStatement ps;
            ps = Core.core.getSQL().connection.prepareStatement("SELECT "
                    + "id, "
                    + "CASE WHEN tipo = 1 THEN '24 horas' ELSE 'Somente Online' END AS tipo, "
                    + "dias, "
                    + "chunks, "
                    + "STRFTIME('%d/%m/%Y %H:%M:%S', data_ativacao) AS data_ativacao, "
                    + "STRFTIME('%d/%m/%Y %H:%M:%S', DATETIME(data_ativacao, '+' || dias || ' days')) AS data_final "
                    + "FROM " + Core.databaseTable + " WHERE nick = ? AND removido = ?");
            ps.setString(1, playerNick.toLowerCase());
            ps.setString(2, "F");

            ResultSet rs = ps.executeQuery();

            if (sender.getName().equalsIgnoreCase(playerNick)) {
                sender.sendMessage("\n§cSeus chunks comprados e ativos abaixo:\n");
            } else {
                sender.sendMessage("\n§cChunks do jogador §4" + playerNick + " §cabaixo:\n");
            }

            sender.sendMessage("§c§m====================================");
            while (rs.next()) {
                sender.sendMessage(
                        (sender.getName().equalsIgnoreCase(playerNick) ? "" : "\n§cID: §f" + rs.getInt("id")) +
                                "\n§cTipo: §f" + rs.getString("tipo") +
                                "\n§cChunks: §f" + rs.getInt("chunks") +
                                (rs.getInt("dias") == 0 ? "" : "\n§cDias: §f" + rs.getInt("dias")) +
                                "\n§cData ativação: §f" + rs.getString("data_ativacao") +
                                "\n§cData final: §f" + (rs.getInt("dias") == 0 ? "Lifetime" : rs.getString("data_final")) +
                                "\n§c§m====================================");
            }

            ps.close();
            rs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void checkTask() {
        try {
            PreparedStatement ps = Core.core.getSQL().connection.prepareStatement("SELECT "
                    + "id, nick, chunks, tipo "
                    + "FROM " + Core.databaseTable + " WHERE "
                    + "removido = ? AND "
                    + "dias > ? AND "
                    + "DATETIME(data_ativacao, '+' || dias || ' days') < DATETIME('NOW', 'localtime')");
            ps.setString(1, "F");
            ps.setInt(2, 0);
            ResultSet rs = ps.executeQuery();
            PreparedStatement updatePs = null;
            while (rs.next()) {
                updatePs = Core.core.getSQL().connection.prepareStatement("UPDATE " + Core.databaseTable + " SET removido = ? WHERE id = ?");
                updatePs.setString(1, "T");
                updatePs.setInt(2, rs.getInt("id"));
                updatePs.executeUpdate();

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aecl chunks remove " + rs.getString("nick") + " " + (rs.getString("tipo").equalsIgnoreCase("1") ? "alwayson" : "onlineonly") + " " + rs.getInt("chunks"));
            }
            if (updatePs != null) {
                updatePs.close();
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
