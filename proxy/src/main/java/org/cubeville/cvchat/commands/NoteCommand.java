package org.cubeville.cvchat.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.Util;

import org.cubeville.cvchat.CVChat;
import org.cubeville.cvchat.playerdata.ProfileEntry;
import org.cubeville.cvchat.playerdata.ProfilesDao;

public class NoteCommand extends CommandBase
{
    CVChat plugin;

    public NoteCommand(CVChat plugin) {
        super("note", "cvchat.note");
        setUsage("§c/note <player> <comment>");
        this.plugin = plugin;
    }

    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        
        if(!verifyNotLessArguments(sender, args, 2)) return;
        
        UUID playerId = getPDM().getPlayerId(args[0]);
        if(playerId == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        ProfileEntry entry = new ProfileEntry(System.currentTimeMillis(), joinStrings(args, 1), sender.getUniqueId());
        ProfilesDao.getInstance().addProfileEntry(playerId, entry);
        sender.sendMessage("§aNote added.");
    }
}
