package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.plugin.Command;

import java.util.List;

import net.md_5.bungee.api.CommandSender;

public class NetherCommand extends Command
{
    public NetherCommand() {
        super("rift");
    }
    List<String> rift1 = getConfig().getStringList("rift");
    List<String> rift2 = getConfig().getStringList("WhatIsRift");
    List<String> riftRules = getConfig().getStringList("Rules of Rift");
    
    public void execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(rift1.get(0) + "\n" + rift1.get(1) + "\n" + rift1.get(2) + "\n" + rift1.get(3) + "\n" + rift1.get(4) + "\n" + rift1.get(5) + "\n" + rift1.get(6) + "\n" + rift1.get(7));
        sleep();
        commandSender.sendMessage(rift2.get(0) + "\n" + rift2.get(1) + "\n" + rift2.get(2) + "\n" + rift3.get(3));
        sleep();
        commandSender.sendMessage(riftRules.get(0) + "\n" + riftRules.get(1) + "\n" + riftRules.get(2));
    }
    
    public void sleep() {
        try {
            Thread.sleep(3000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

