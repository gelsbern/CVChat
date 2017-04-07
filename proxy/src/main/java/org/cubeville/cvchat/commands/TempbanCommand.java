package org.cubeville.cvchat.commands;

import java.util.UUID;

import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.sanctions.SanctionManager;

public class TempbanCommand extends CommandBase
{
    public TempbanCommand() {
        super("tempban", "cvchat.tempban");
        setUsage("/tempban <player> <amount> <unit> <reason>");
    }

    public void execute(CommandSender sender, String[] args) {
        String senderName = getPlayerName(sender);

        int offset = 0;
        boolean silent = false;
        if(args[0].equals("-s")) {
            silent = true;
            offset = 1;
        }

        if(!verifyNotLessArguments(sender, args, 4 + offset)) return;

        int amount;
        try {
            amount = Integer.valueOf(args[offset + 1]);
        }
        catch(NumberFormatException e) {
            sender.sendMessage("§cAmount parameter must be numeric.");
            return;
        }

        String unit = args[offset + 2];
        if("days".startsWith(unit)) {
            amount *= 86400;
        }
        else if("hours".startsWith(unit)) {
            amount *= 3600;
        }
        else if("minutes".startsWith(unit)) {
            amount *= 60;
        }
        else {
            sender.sendMessage("§cUnit bust be minutes, hours or days.");
            return;
        }

        String banReason = joinStrings(args, offset + 3);
        UUID bannedPlayerId = getPDM().getPlayerId(args[offset]);
        if(bannedPlayerId == null) {
            sender.sendMessage("§cUnknown player §e" + args[offset]);
            return;
        }
        if(!verifyOutranks(sender, bannedPlayerId)) return;

        String bannedPlayerName = getPDM().getPlayerName(bannedPlayerId);
        SanctionManager.getInstance().banPlayer(sender, bannedPlayerId, banReason, amount, silent);

        if(!silent) {
            sendMessage(getAllPlayers(), "§e" + bannedPlayerName + "§6 was temporarily banned by §e" + senderName + "§6. Reason: §e" + banReason);
        }
        else {
            sendMessage(getAllPlayersWithPermission("cvchat.ban.notifysilent"), "§c[Silent] §e" + bannedPlayerName + "§6 was temporarily banned by §e" + senderName + "§6. Reason: §e" + banReason);
        }
    }
}
