package org.cubeville.cvchat.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import org.cubeville.cvchat.ranks.RankManager;

import org.cubeville.cvchat.playerdata.PlayerDataManager;

public abstract class CommandBase extends Command
{
    String usage;
    
    public CommandBase(String command) {
        super(command);
    }

    public CommandBase(String command, String permission, String... aliases) {
        super(command, permission, aliases);
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
    
    public abstract void execute(CommandSender sender, String[] args);

    
    public boolean verifyPermission(CommandSender sender, String permission) {
        if(!sender.hasPermission(permission)) {
            sender.sendMessage("§cNo permission.");
            return false;
        }
        return true;
    }
    
    public boolean verifyOutranks(CommandSender commandSender, UUID player) {
        if(!(commandSender instanceof ProxiedPlayer)) return false;
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        if(!getPDM().outranks(sender.getUniqueId(), player)) {
            sender.sendMessage("§cNo permission.");
            return false;
        }
        return true;
    }

    public boolean verifyOutranks(CommandSender sender, ProxiedPlayer player) {
        return verifyOutranks(sender, player.getUniqueId());
    }

    public boolean verifyOnline(CommandSender sender, String playerName) {
        if(ProxyServer.getInstance().getPlayer(playerName) == null) {
            sender.sendMessage("§cPlayer not found!");
            return false;
        }
        return true;
    }

    public ProxiedPlayer getPlayer(String playerName) {
        return ProxyServer.getInstance().getPlayer(playerName);
    }

    public boolean verifyNotLessArguments(CommandSender sender, String[] args, int min) {
        if(args.length < min) {
            sender.sendMessage("§cToo few arguments.");
            if(usage != null) sender.sendMessage(usage);
            return false;
        }
        return true;
    }

    public boolean verifyNotMoreArguments(CommandSender sender, String[] args, int max) {
        if(args.length > max) {
            sender.sendMessage("§cToo many arguments.");
            if(usage != null) sender.sendMessage(usage);
            return false;
        }
        return true;
    }

    public Collection<ProxiedPlayer> getAllPlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    public Collection<ProxiedPlayer> getAllPlayersWithPermission(String permission) {
        List<ProxiedPlayer> ret = new ArrayList<>();
        for(ProxiedPlayer player: getAllPlayers()) {
            if(player.hasPermission(permission)) {
                ret.add(player);
            }
        }
        return ret;
    }
    
    public void sendMessage(Collection<ProxiedPlayer> players, String message) {
        for(ProxiedPlayer player: players) {
            player.sendMessage(message);
        }
    }

    public void sendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(message);
    }
    
    public boolean verify(ProxiedPlayer sender, boolean check, String message) {
        if(!check) {
            sender.sendMessage(message);
            return false;
        }
        return true;
    }

    public String getPlayerName(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) sender).getDisplayName();
        }
        else {
            return "Console";
        }
    }

    public UUID getUUID(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) sender).getUniqueId();
        }
        else {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }

    public String joinStrings(String[] args, int offset) {
        String ret = "";
        for(int i = offset; i < args.length; i++) {
            if(ret.length() > 0) ret += " ";
            ret += args[i];
        }
        return ret;
    }

    public PlayerDataManager getPDM() {
        return PlayerDataManager.getInstance();
    }

    public boolean isPlayerEqual(CommandSender sender, ProxiedPlayer player) {
        if(!(sender instanceof ProxiedPlayer)) return false;
        ProxiedPlayer senderPlayer = (ProxiedPlayer) sender;
        return senderPlayer.getUniqueId().equals(player.getUniqueId());
    }
}
