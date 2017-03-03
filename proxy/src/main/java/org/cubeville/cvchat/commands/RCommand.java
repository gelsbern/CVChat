package org.cubeville.cvchat.commands;

import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RCommand extends Command
{
    public RCommand() {
        super("r");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length < 1) {
            sender.sendMessage("§cToo few arguments.");
            sender.sendMessage("§c/r <message...>");
            return;
        }

        UUID recipientId = MsgCommand.getLastMessageReceived(sender.getUniqueId());
        if (recipientId == null) {
            sender.sendMessage("§cNo message to reply to.");
            return;
        }

        ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(recipientId);
        if(recipient == null) {
            sender.sendMessage("§cPlayer left.");
            return;
        }

        MsgCommand.sendMessage(sender, recipient, args, 0);
    }
}