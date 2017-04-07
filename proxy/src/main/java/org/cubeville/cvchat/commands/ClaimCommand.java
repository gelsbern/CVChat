package org.cubeville.cvchat.commands;

import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.tickets.TicketManager;

public class ClaimCommand extends CommandBase
{
    TicketManager ticketManager;
    
    public ClaimCommand(TicketManager ticketManager) {
        super("claim", "cvchat.ticket");
        this.ticketManager = ticketManager;
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(args.length == 1) {
            try {
                int id = Integer.valueOf(args[0]);
                ticketManager.claimTicket(commandSender, id);
            }
            catch (NumberFormatException e) {
                commandSender.sendMessage("§cInvalid ticket id.");                
            }
        }
        else {
            commandSender.sendMessage("§cWrong number of arguments.");
            commandSender.sendMessage("§c/claim <modreq-id>");
        }
    }
}
