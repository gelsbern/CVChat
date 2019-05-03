package org.cubeville.cvchat.commands;

import java.util.Map;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.ranks.RankManager;
import org.cubeville.cvchat.playerdata.PlayerDataManager;

public class PrefixCommand extends Command
{
    public PrefixCommand() {
        super("prefix");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length != 1) {
            if(args.length > 1) sender.sendMessage("§cToo many arguments.");
            sender.sendMessage("§c/prefix <list|rank>");
            return;
        }

        Map<String, String> p = RankManager.getInstance().getPossiblePrefixes(sender);
        
        if(args[0].equals("list")) {
            String l = "";
            for(String s: p.keySet()) {
                if(l.length() > 0) l += ", ";
                l += s;
            }
            sender.sendMessage("§e---------- §rPrefix list §e----------");
            sender.sendMessage(l);
            return;
        }

        else {
            if(p.keySet().contains(args[0])) {
                PlayerDataManager.getInstance().changePrefix(sender.getUniqueId(), p.get(args[0]));
                sender.sendMessage("§aPrefix changed.");
            }
            else {
                sender.sendMessage("§cInvalid prefix.");
            }
        }
    }
}
