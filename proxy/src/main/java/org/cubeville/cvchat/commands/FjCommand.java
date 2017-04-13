package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.CVChat;
import org.cubeville.cvchat.Util;

public class FjCommand extends CommandBase
{
    public FjCommand() {
        super("fj", "cvchat.fakejoinquit");
    }

    public void execute(CommandSender commandSender, String[] args) {
        String playerName = "Console";
        if(commandSender instanceof ProxiedPlayer) playerName = ((ProxiedPlayer) commandSender).getDisplayName();

        sendMessage(getAllPlayers(), "§e" + playerName + "§e joined the game.");
    }
}
