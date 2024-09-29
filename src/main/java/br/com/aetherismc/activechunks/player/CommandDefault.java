package br.com.aetherismc.activechunks.player;

import br.com.aetherismc.activechunks.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDefault implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

        if (sender.hasPermission("aetheris.chunks.admin")) {
            if (args.length == 0) {
                sender.sendMessage(this.getHelp());
                return false;
            }
            //====================================
            // ADD
            //====================================
            if (args[0].equalsIgnoreCase("add")) {
                int quantity = 0, days = 0;
                // Parâmetros
                if (args.length != 5) {
                    sender.sendMessage(this.getHelp());
                    return false;
                }
                // Tipo
                if (!args[2].equalsIgnoreCase("alwayson") && !args[2].equalsIgnoreCase("onlineonly")) {
                    sender.sendMessage("§4[!] §cTipo de chunk loader inválido.");
                    return false;
                }
                // Quantidade
                try {
                    quantity = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§4[!] §cA quantidade informada é inválida.");
                    return false;
                }
                if (quantity <= 0) {
                    sender.sendMessage("§4[!] §cA quantidade informada é inválida.");
                    return false;
                }
                // Dias
                try {
                    days = Integer.parseInt(args[4]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§4[!] §cOs dias informados é inválido.");
                    return false;
                }
                if (days < 0) {
                    sender.sendMessage("§4[!] §cOs dias informados é inválido.");
                    return false;
                }
                // Adiciona no player informado
                if (Core.core.getDatabase().add(args[1], args[2], quantity, days)) {
                    sender.sendMessage("§2[!] §aChunks adicionados com sucesso para o player §2" + args[1] + " §a.");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aecl chunks add " + args[1] + " " + args[2] + " " + quantity);
                } else {
                    sender.sendMessage("§4[!] §cNão foi possível salvar, tente novamente mais tarde.");
                }
                return true;
            }
            //====================================
            // DEL
            //====================================
            if (args[0].equalsIgnoreCase("del")) {
                int chunkID;
                // Par§metros
                if (args.length != 3) {
                    sender.sendMessage(this.getHelp());
                    return false;
                }
                // ID
                try {
                    chunkID = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§4[!] §cID está inválido.");
                    return false;
                }
                // Verifica se existe
                if (!Core.core.getDatabase().hasID(args[1], chunkID)) {
                    sender.sendMessage("§4[!] §cNão foi encontrado o ID do chunk loader para o player informado.");
                    return false;
                }
                // Deleta
                if (Core.core.getDatabase().delete(chunkID)) {
                    sender.sendMessage("§2[!] §aChunk deletado com sucesso do player §2" + args[1] + " §a.");
                } else {
                    sender.sendMessage("§4[!] §cNão foi possível deletar, tente novamente mais tarde.");
                }
                return true;
            }
            //====================================
            // LIST
            //====================================
            if (args[0].equalsIgnoreCase("list")) {
                if (Core.core.getDatabase().hasPlayer(args[1])) {
                    if (!Core.core.getDatabase().list(sender, args[1])) {
                        sender.sendMessage("§4[!] §cNão foi possível exibir os chunks loaders do jogador §4" + args[1] + "§c.");
                    }
                } else {
                    sender.sendMessage("§4[!] §cO player §4" + args[1] + " §cnão possi nenhum chunk loader.");
                }
                return true;
            }
            sender.sendMessage(this.getHelp());
        } else if (args.length > 0) {
            sender.sendMessage("§4[!] §cVocê não tem permissão para fazer isso.");
        } else if (sender instanceof Player) {
            if (Core.core.getDatabase().hasPlayer(sender.getName())) {
                if (!Core.core.getDatabase().list(sender, sender.getName())) {
                    sender.sendMessage("§4[!] §cNão foi possível exibir os seus chunks loaders.");
                }
            } else {
                sender.sendMessage("§4[!] §cVocê não tem chunks loaders comprados que estão ativos.");
            }
        }
        return false;
    }

    private String getHelp() {
        return "§c§m=======================§r§4 Chunks §c§m=======================\n"
                + "§c/chunks add §f<player> <alwayson, onlineonly> <Qtde> <Dias>\n"
                + "§c/chunks list §f<player>\n"
                + "§c/chunks del §f<player> <id>\n"
                + "§c/chunks help\n"
                + "§c§m=======================§r§4 Chunks §c§m=======================";
    }

}

