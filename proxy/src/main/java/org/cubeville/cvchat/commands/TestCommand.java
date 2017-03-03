package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import codecrafter47.bungeetablistplus.BungeeTabListPlus;

public class TestCommand extends Command
{
    public TestCommand() {
        super("test");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        if (BungeeTabListPlus.isHidden(BungeeTabListPlus.getInstance().getConnectedPlayerManager().getPlayer(sender))) {
            sender.sendMessage("You are hidden!");
        }
        else {
            sender.sendMessage("You are visible!");
        }
    }

}
