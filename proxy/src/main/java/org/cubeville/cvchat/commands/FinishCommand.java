package org.cubeville.cvchat.commands;

import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.playerdata.PlayerDataManager;

public class FinishCommand extends Command
{
    public FinishCommand() {
        super("finish");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length > 0) {
        if(args.length < 1) {
            sender.sendMessage("§cToo many arguments.");
            return;
        }

        }
}
