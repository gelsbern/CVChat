package org.cubeville.cvchat.commands;

// TODO: This should obviously be integrated with RCommand
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.sanctions.SanctionManager;

public class RlCommand extends CommandBase
{
    public RlCommand() {
        super("rl");
        setUsage("§c/rl <message...>");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if(!verifyNotLessArguments(sender, args, 1)) return;

        UUID recipientId = MsgCommand.getLastMessageSent(sender.getUniqueId());
        if(!verify(sender, recipientId != null, "§cNo message sent yet."));

        ProxiedPlayer recipient = ProxyServer.getInstance().getPlayer(recipientId);
        if(!verify(sender, recipient != null, "§cPlayer left."));

        if(SanctionManager.getInstance().isPlayerMuted(sender)) {
            if(!verify(sender, recipient.hasPermission("cvchat.mute.staff"), "§cYou are muted. You can only send messages to staff members.")) return;
        }
        
        MsgCommand.sendMessage(sender, recipient, args, 0);
    }
}
