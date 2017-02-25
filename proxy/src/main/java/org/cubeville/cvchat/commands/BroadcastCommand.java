package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;

public class BroadcastCommand extends Command
{
    public BroadcastCommand() {
        super("broadcast");
    }

    public void execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("Hello World, you sent a broadcast command!");
    }
}
