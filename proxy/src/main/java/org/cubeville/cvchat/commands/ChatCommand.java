package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.Channel;
import org.cubeville.cvchat.Util;

public class ChatCommand extends Command
{
    Channel channel;
    
    public ChatCommand(String command, Channel channel) {
        super(command);
        this.channel = channel;
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return; // TODO: Let console chat?
        String message = Util.removeSectionSigns(Util.joinStrings(args, 0));
        channel.sendMessage((ProxiedPlayer) commandSender, message);
    }
}
