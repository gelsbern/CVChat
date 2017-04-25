package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.plugin.Command;

import java.util.List;

import net.md_5.bungee.api.CommandSender;

public class NetherCommand extends Command
{
    public NetherCommand() {
        super("nether");
    }
    
    List<String> nether = getConfig().getStringList("Nether");
    public void execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(nether.get(0) + "\n" + nether.get(1) + "\n" + nether.get(2) + "\n" + nether.get(3) + "\n" + nether.get(4) + "\n" + nether.get(5) + "\n" + nether.get(6));
    
    }
    
    
}

