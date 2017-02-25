package org.cubeville.cvchat.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.Util;

public class MsgCommand extends Command
{
    private static Map<UUID, UUID> lastMessageReceived;
    private static Map<UUID, UUID> lastMessageSent;
    
    public MsgCommand() {
        super("msg");
        lastMessageReceived = new HashMap<>();
        lastMessageSent = new HashMap<>();
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(args.length < 2) {
            sender.sendMessage("§cToo few arguments.");
            sender.sendMessage("§c/msg <target> <message...>");
            return;
        }

        ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(args[0]);
        if(recipient == null) {
            sender.sendMessage("§cNo player found!");
            return;
        }

        sendMessage(sender, recipient, args, 1);
    }

    protected static void sendMessage(ProxiedPlayer sender, ProxiedPlayer recipient, String[] args, int argsOffset) {
        String message = Util.removeSectionSigns(Util.joinStrings(args, argsOffset));

        sender.sendMessage("§7(To " + recipient.getDisplayName() + "§7): §r" + message);
        recipient.sendMessage("§7(From " + sender.getDisplayName() + "§7): §r" + message);

        lastMessageReceived.put(recipient.getUniqueId(), sender.getUniqueId());
        lastMessageSent.put(sender.getUniqueId(), recipient.getUniqueId());
    }

    protected static UUID getLastMessageReceived(UUID player) {
        return lastMessageReceived.get(player);
    }

    protected static UUID getLastMessageSent(UUID player) {
        return lastMessageSent.get(player);
    }
}
