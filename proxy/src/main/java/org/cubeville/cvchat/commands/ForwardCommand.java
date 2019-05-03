package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ForwardCommand extends CommandBase
{
    private String forwardCommand;
    
    public ForwardCommand(String command, String forwardCommand) {
        super(command);
        this.forwardCommand = forwardCommand;
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        player.chat(forwardCommand);
    }
}
