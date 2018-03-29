package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LocchatCommand extends CommandBase
{
    public LocchatCommand() {
        super("locchat", "cvchat.locchat");
    }

    public void executeC(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        String a = "";
        for(int i = 0; i < args.length; i++) {
            if(i > 0) a += " ";
            a += args[i];
        }
        player.chat(a);
    }
}
