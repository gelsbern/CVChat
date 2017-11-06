package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;

public class ClearchatCommand extends CommandBase
{
    public ClearchatCommand() {
        super("clearchat", "cvchat.clearchat");
    }

    public void execute(CommandSender commandSender, String[] args) {
        String message = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        sendMessage(getAllPlayers(), message);
    }
}
