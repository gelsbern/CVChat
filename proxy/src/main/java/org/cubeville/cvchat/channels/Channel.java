package org.cubeville.cvchat.channels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;

import org.cubeville.cvchat.Util;
import org.cubeville.cvchat.ranks.RankManager;
import org.cubeville.cvchat.sanctions.SanctionManager;

public class Channel
{
    protected String name;
    private String viewPermission;
    private String sendPermission;
    private String colorPermission;
    private String leavePermission;
    private String format;
    private boolean isDefault; // Channel is activated for new players
    private boolean autojoin; // Players join automatically when they log in (otherwise serialized)
    private boolean listable; // Shows up in /ch list
    private boolean filtered;
    private Collection<String> commands;
    
    Set<UUID> members;
    
    public Channel(String name, String viewPermission, String sendPermission, String colorPermission, String leavePermission, String format, boolean isDefault, boolean autojoin, boolean listable, boolean filtered, Collection<String> commands) {
        this.name = name;
        this.viewPermission = viewPermission;
        this.sendPermission = sendPermission;
        this.colorPermission = colorPermission;
        this.leavePermission = leavePermission;
        this.format = format;
        this.isDefault = isDefault;
        this.autojoin = autojoin;
        this.listable = listable;
        this.filtered = filtered;
        this.commands = commands;
        
        members = new HashSet<>();
    }

    public void playerLogin(ProxiedPlayer player, String configuration) {
        if (viewPermission.equals("default") || player.hasPermission(viewPermission)) {
            if(autojoin || (configuration == null && isDefault)) {
                members.add(player.getUniqueId());
            }
            else if (configuration != null && Util.getBooleanProperty(configuration)) {
                members.add(player.getUniqueId());
            }
        }
    }

    public String getConfigurationString(ProxiedPlayer player) {
        if(autojoin == false) {
            if(members.contains(player.getUniqueId())) return name + ":" + true;
            return name + ":" + false;
        }
        return null;
    }
    
    public void playerDisconnect(ProxiedPlayer player) {
        members.remove(player.getUniqueId());
    }
    
    public void sendMessage(CommandSender player, String message) {
        if(SanctionManager.getInstance().isPlayerMuted(player)) {
            player.sendMessage("§cYou are muted.");
            return;
        }
        
        if(((!sendPermission.equals("default")) && player.hasPermission(sendPermission) == false) || ((!viewPermission.equals("default")) && player.hasPermission(viewPermission) == false)) {
            player.sendMessage("§cPermission denied.");
            return;
        }
        if(player instanceof ProxiedPlayer) {
            if(!members.contains(((ProxiedPlayer) player).getUniqueId())) {
                player.sendMessage("§cYou're currently not a member of this channel. Join with /ch join " + name + ".");
                return;
            }
        }
        
        String formattedMessage = format;

        message.replace("§", "");
        if(colorPermission.equals("default") || player.hasPermission(colorPermission)) {
            message = Util.translateAlternateColorCodes(message);
        }
        // TODO: Strip paragraph chars, convert colours if has permission to do so
        formattedMessage = formattedMessage.replace("%message%", message);
        formattedMessage = formattedMessage.replace("%prefix%", "");
        if(formattedMessage.indexOf("%postfix%") > 0) {
            if(player instanceof ProxiedPlayer) {
                String postfix = RankManager.getInstance().getPostfix(player);
                formattedMessage = formattedMessage.replace("%postfix%", postfix);
            }
            else {
                formattedMessage = formattedMessage.replace("%postfix%", "CO");
            }
        }
        if(player instanceof ProxiedPlayer) {
            formattedMessage = formattedMessage.replace("%player%", ((ProxiedPlayer) player).getDisplayName());
        }
        else {
            formattedMessage = formattedMessage.replace("%player%", "Console");
        }
        Collection<ProxiedPlayer> recipientList = getRecipientList(player);
        if(recipientList == null) {
            sendFailureMessage(player);
            return;
        }
        for(ProxiedPlayer p: recipientList) {
            if(members.contains(p.getUniqueId())) {
                p.sendMessage(formattedMessage);
            }
        }
    }

    protected Collection<ProxiedPlayer> getRecipientList(CommandSender player) {
        return ProxyServer.getInstance().getPlayers();
    }

    protected void sendFailureMessage(CommandSender player) {
        player.sendMessage("§cNobody hears your message.");
    }
    
    private boolean hasViewPermission(ProxiedPlayer player) {
        if(viewPermission.equals("default")) return true;
        return player.hasPermission(viewPermission);
    }
    
    public boolean canList(ProxiedPlayer player) {
        if(!listable) return false;
        return hasViewPermission(player);
    }
    
    public boolean join(ProxiedPlayer player) {
        if(members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are already in that channel.");
        }
        else if(hasViewPermission(player)) {
            members.add(player.getUniqueId());
            player.sendMessage("§aYou have joined channel '" + name + "'.");
            return true;
        }
        else {
            player.sendMessage("§cYou do not have permission to join that channel.");
        }
        return false;
    }

    public boolean leave(ProxiedPlayer player) {
        if(!members.contains(player.getUniqueId())) {
            player.sendMessage("§cYou are not in that channel.");
        }
        else if(leavePermission.equals("default") || player.hasPermission(leavePermission)) {
            members.remove(player.getUniqueId());
            player.sendMessage("§aYou have left channel '" + name + "'.");
            return true;
        }
        else {
            player.sendMessage("§cYou can't leave this channel.");
        }
        return false;
    }
    
    public boolean isListable() {
        return listable;
    }

    public Collection<String> getCommands() {
        return commands;
    }
}
