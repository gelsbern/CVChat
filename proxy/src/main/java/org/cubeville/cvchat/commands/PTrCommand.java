package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;

import org.cubeville.cvchat.Util;

public class PTrCommand extends CommandBase
{
    public PTrCommand() {
        super("ptr", "cvchat.ptr");
    }

    public void executeC(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("§c/ptr <player>[,player...] <message>");
            return;
        }
        
        String originalMessage = joinStrings(args, 1);
        String message = Util.translateAlternateColorCodes(originalMessage);

        String[] playerNames = args[0].split(",");
        String notFound = "";
        for(int i = 0; i < playerNames.length; i++) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerNames[i]);
            if(player == null) {
                if(notFound.length() > 0) notFound += ", ";
                notFound += playerNames[i];
            }
            sendMessage(player, message);
        }
        commandSender.sendMessage("ptr sent: " + message);

        if(notFound.length() > 0) {
            commandSender.sendMessage("§cPlayers not found: " + notFound);
        }
    }
}
