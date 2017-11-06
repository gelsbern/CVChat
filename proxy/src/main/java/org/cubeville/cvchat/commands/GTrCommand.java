package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.Util;

public class GTrCommand extends CommandBase
{
    public GTrCommand() {
        super("gtr", "cvchat.gtr");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("§c/gtr <permission> <message>");
            return;
        }
        
        String permission = args[0];
        
        String message = joinStrings(args, 1);
        message = Util.translateAlternateColorCodes(message);
        sendMessage(getAllPlayersWithPermission(permission), message);
    }
}
