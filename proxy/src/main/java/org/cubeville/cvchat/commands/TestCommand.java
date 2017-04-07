package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

//import codecrafter47.bungeetablistplus.BungeeTabListPlus;

import org.cubeville.cvchat.CVChat;
import org.cubeville.cvchat.Util;

public class TestCommand extends Command
{
    public TestCommand() {
        super("test", "cvchat.testcommand.supersecret");
    }

    public void execute(CommandSender commandSender, String[] args) {
        
        commandSender.sendMessage("Sleeping for 10 seconds now.");
        try {Thread.sleep(10000); } catch(Exception e) {}
        commandSender.sendMessage("Done sleeping.");
        
        // if (BungeeTabListPlus.isHidden(BungeeTabListPlus.getInstance().getConnectedPlayerManager().getPlayer(sender))) {
        //     sender.sendMessage("You are hidden!");
        // }
        // else {
        //     sender.sendMessage("You are visible!");
        // }
    }

}
