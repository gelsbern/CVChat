package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.Util;

public class TrCommand extends CommandBase
{
    public TrCommand() {
        super("tr", "cvchat.tr");
    }

    public void execute(CommandSender commandSender, String[] args) {
        String message = joinStrings(args, 0);
        message = Util.translateAlternateColorCodes(message);
        sendMessage(getAllPlayers(), message);
    }
}
