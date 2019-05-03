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
import org.cubeville.cvchat.ranks.RankManager;

public class WhoCommand extends CommandBase
{
    public WhoCommand() {
        super("who", null, "list", "playerlist", "online", "players");
    }

    public void execute(CommandSender sender, String[] args) {
        if(args.length > 1) {
            sender.sendMessage("§cToo many arguments.");
            sender.sendMessage("§c/who [filter]");
            return;
        }

        String list = "";
        int cnt = 0;
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers()) {
            if(args.length == 0 || player.getDisplayName().toUpperCase().indexOf(args[0].toUpperCase()) >= 0) {
                boolean vis = !Util.playerIsHidden(player);
                boolean hvis = true;
                if(sender instanceof ProxiedPlayer) {
                    ProxiedPlayer senderPlayer = (ProxiedPlayer) sender;
                    boolean outranks = getPDM().outranksOrEqual(senderPlayer.getUniqueId(), player.getUniqueId());
                    hvis = isPlayerEqual(senderPlayer, player) || outranks;
                }
                if(vis || hvis) {
                    if(list.length() > 0) list += "§r, ";
                    list += "§" + RankManager.getInstance().getColor(player);
                    if(!vis && hvis) list += "§o";
                    list += player.getDisplayName();
                    cnt++;
                }
            }
        }

        sender.sendMessage("§6Fort Serenity §a(" + cnt + ")§r: " + list);
        
    }

}
