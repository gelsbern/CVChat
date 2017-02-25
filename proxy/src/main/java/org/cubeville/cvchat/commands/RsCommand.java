package org.cubeville.cvchat.commands;

// TODO: This should obviously be integrated with RCommand
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RsCommand extends Command
{
    public RsCommand() {
        super("rs");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length < 1) {
            sender.sendMessage("§cToo few arguments.");
            sender.sendMessage("§c/rs <message...>");
            return;
        }

        UUID recipientId = MsgCommand.getLastMessageSent(sender.getUniqueId());
        if (recipientId == null) {
            sender.sendMessage("§cNo message sent yet.");
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
