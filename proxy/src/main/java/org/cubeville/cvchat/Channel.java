package org.cubeville.cvchat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Channel
{
    private String name;
    private String viewPermission;
    private String sendPermission;
    private String colorPermission;
    private String leavePermission;
    private String format;
    private boolean isDefault; // Channel is activated for new players
    private boolean autojoin; // Players join automatically when they log in (otherwise serialized)
    private boolean listable; // Shows up in /ch list
    
    Set<UUID> members;
    
    public Channel(String name, String viewPermission, String sendPermission, String colorPermission, String leavePermission, String format, boolean isDefault, boolean autojoin, boolean listable) {
        members = new HashSet<>();
        
        this.name = name;
        this.viewPermission = viewPermission;
        this.sendPermission = sendPermission;
        this.colorPermission = colorPermission;
        this.leavePermission = leavePermission;
        this.format = format;
        this.isDefault = isDefault;
        this.autojoin = autojoin;
        this.listable = listable;
    }

    public void playerLogin(ProxiedPlayer player) {
        if(autojoin && (viewPermission.equals("default") || player.hasPermission(viewPermission))) {
            members.add(player.getUniqueId());
        }
    }

    public void playerDisconnect(ProxiedPlayer player) {
        members.remove(player.getUniqueId());
    }
    
    public void sendMessage(ProxiedPlayer player, String message) {
        if(((!sendPermission.equals("default")) && player.hasPermission(sendPermission) == false) || ((!viewPermission.equals("default")) && player.hasPermission(viewPermission) == false)) {
            player.sendMessage("§cPermission denied.");
            return;
        }
        if(!members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou're currently not a member of this channel. Join with /ch join " + name + ".");
            return;
        }
        String formattedMessage = format;
        // TODO: Strip paragraph chars, convert colours if has permission to do so
        formattedMessage = formattedMessage.replace("%message%", message);
        formattedMessage = formattedMessage.replace("%prefix%", "");
        formattedMessage = formattedMessage.replace("%postfix%", "C");
        formattedMessage = formattedMessage.replace("%player%", player.getDisplayName());
        for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
            if(members.contains(p.getUniqueId())) {
                p.sendMessage(formattedMessage);
            }
        }
    }

    private boolean hasViewPermission(ProxiedPlayer player) {
        if(viewPermission.equals("default")) return true;
        return player.hasPermission(viewPermission);
    }
    
    public boolean canList(ProxiedPlayer player) {
        if(!listable) return false;
        return hasViewPermission(player);
    }
    
    public void join(ProxiedPlayer player) {
        if(members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are already in that channel.");
        }
        else if(hasViewPermission(player)) {
            members.add(player.getUniqueId());
            player.sendMessage("§aYou have joined channel '" + name + "'.");
        }
        else {
            player.sendMessage("§cYou do not have permission to join that channel.");
        }
    }

    public void leave(ProxiedPlayer player) {
        if(!members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are not in that channel.");
        }
        else if(leavePermission.equals("default") || player.hasPermission(leavePermission)) {
            members.remove(player.getUniqueId());
            player.sendMessage("§aYou have left channel '" + name + "'.");
        }
        else {
            player.sendMessage("§cYou can't leave this channel.");
        }
                                     }
    
    public boolean isListable() {
        return listable;
    }

}
