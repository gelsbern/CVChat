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

    public void execute(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("§c/ptr <player> <message>");
            return;
        }
        
        String playerName = args[0];
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if(player == null) {
            commandSender.sendMessage("§cPlayer not found!");
            return;
        }
        
        String message = joinStrings(args, 1);
        message = Util.translateAlternateColorCodes(message);
        sendMessage(player, message);
    }
}
