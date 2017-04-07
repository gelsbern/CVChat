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

public class MsgCommand extends CommandBase
{
    private static Map<UUID, UUID> lastMessageReceived;
    private static Map<UUID, UUID> lastMessageSent;
    
    public MsgCommand() {
        super("msg");
        setUsage("§c/msg <target> <message...>");
        lastMessageReceived = new HashMap<>();
        lastMessageSent = new HashMap<>();
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(!verifyNotLessArguments(sender, args, 2)) return;

        if(!verifyOnline(sender, args[0])) return;
        ProxiedPlayer recipient = getPlayer(args[0]);

        if(SanctionManager.getInstance().isPlayerMuted(sender)) {
            if(!recipient.hasPermission("cvchat.mute.staff")) {
                sender.sendMessage("§cYou are muted. You can only send messages to staff members.");
                return;
            }
        }
        
        sendMessage(sender, recipient, args, 1);
    }

    protected static void sendMessage(ProxiedPlayer sender, ProxiedPlayer recipient, String[] args, int argsOffset) {
        String message = Util.removeSectionSigns(Util.joinStrings(args, argsOffset));

        sender.sendMessage("§3(To " + recipient.getDisplayName() + "§3): §r" + message);
        recipient.sendMessage("§3(From " + sender.getDisplayName() + "§3): §r" + message);

        lastMessageReceived.put(recipient.getUniqueId(), sender.getUniqueId());
        lastMessageSent.put(sender.getUniqueId(), recipient.getUniqueId());
        lastMessageReceived.put(sender.getUniqueId(), recipient.getUniqueId());
    }

    protected static UUID getLastMessageReceived(UUID player) {
        return lastMessageReceived.get(player);
    }

    protected static UUID getLastMessageSent(UUID player) {
        return lastMessageSent.get(player);
    }
}
