package org.cubeville.cvchat.commands;

import java.util.Map;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.Channel;

public class ChannelCommand extends Command
{
    Map<String, Channel> channels;

    public ChannelCommand(Map<String, Channel> channels) {
        super("ch", null, "cha", "chan", "chann", "channe", "channel");
        this.channels = channels;
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if(args.length > 0 && ((args[0].equals("l") && args.length == 1) || (args[0].length() > 1 && "list".startsWith(args[0])))) {
            if(args.length != 1) {
                player.sendMessage("§cToo many arguments.");
                player.sendMessage("§c/channel list");
                return;
            }
            String channelList = "";
            for(String channel: channels.keySet()) {
                if(channels.get(channel).canList(player)) {
                    if(channelList.length() > 0) channelList += "§a,§r ";
                    channelList += channel;
                }
            }
            player.sendMessage("§e----- §6Channel List§e -----------");
            player.sendMessage(channelList);
        }
        else if(args.length > 0 && ("join".startsWith(args[0]) || "leave".startsWith(args[0]))) {
            if(args.length != 2) {
                if(args.length == 1) player.sendMessage("§cToo few arguments.");
                else player.sendMessage("§cToo many arguments.");
                player.sendMessage("§c/channel " + args[0] + " <name>");
                return;
            }
            if(!channels.containsKey(args[1])) {
                player.sendMessage("§cThat channel doesn't exist.");
                return;
            }
            if(args[0].charAt(0) == 'l') channels.get(args[1]).leave(player);
            else channels.get(args[1]).join(player);
        }
        else {
            player.sendMessage("§c/channel <leave|join|list>");
        }
    }
}
