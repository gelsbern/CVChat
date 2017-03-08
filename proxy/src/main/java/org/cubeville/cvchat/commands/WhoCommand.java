package org.cubeville.cvchat.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.Util;
import org.cubeville.cvchat.sanctions.SanctionManager;

public class WhoCommand extends Command
{
    public WhoCommand() {
        super("who", null, "list", "playerlist", "online", "players");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length > 1) {
            sender.sendMessage("§cToo many arguments.");
            sender.sendMessage("/who [filter]");
            return;
        }

        String list = "";
        int cnt = 0;
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers()) {
            if(args.length == 0 || player.getDisplayName().toUpperCase().indexOf(args[0].toUpperCase()) >= 0) {
                boolean vis = !Util.playerIsHidden(player);
                boolean hvis = (player.getUniqueId().equals(sender.getUniqueId())) || (sender.hasPermission("cvchat.showvanished"));
                if(vis || hvis) {
                    if(list.length() > 0) list += "§r, ";
                    if(!vis && hvis) list += "§o";
                    list += player.getDisplayName();
                    cnt++;
                }
            }
        }

        sender.sendMessage("§6Cubeville §a(" + cnt + ")§r: " + list);
        
    }

}
