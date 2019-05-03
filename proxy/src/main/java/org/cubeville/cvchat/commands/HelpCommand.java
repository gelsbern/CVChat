package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;

public class HelpCommand extends Command
{
    public HelpCommand() {
        super("help");
    }

    public void execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("Here would be some kind of help now.");
    }
}

