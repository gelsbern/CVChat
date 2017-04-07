package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.tickets.TicketManager;

public class UnclaimCommand extends CommandBase
{
    TicketManager ticketManager;
    
    public UnclaimCommand(TicketManager ticketManager) {
        super("unclaim");
        this.ticketManager = ticketManager;
        // TODO: What about some permissions?
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(args.length == 1) {
            try {
                int id = Integer.valueOf(args[0]);
                ticketManager.unclaimTicket(commandSender, id);
            }
            catch (NumberFormatException e) {
                commandSender.sendMessage("§cInvalid ticket id.");                
            }
        }
        else {
            commandSender.sendMessage("§cWrong number of arguments.");
            commandSender.sendMessage("§c/unclaim <modreq-id>");
        }
    }
}
