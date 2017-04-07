package org.cubeville.cvchat;

import java.util.Set;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import org.cubeville.cvchat.channels.Channel;
import org.cubeville.cvchat.log.Logger;
import org.cubeville.cvchat.playerdata.PlayerDataManager;
import org.cubeville.cvchat.textcommands.TextCommandManager;

public class ChatListener implements Listener {

    Channel localChannel;
    Set<String> commandWhitelist;
    Set<String> commandWhitelistTutorial;

    TextCommandManager textCommandManager;
    
    public ChatListener(Channel localChannel, Set<String> commandWhitelist, Set<String> commandWhitelistTutorial, TextCommandManager textCommandManager) {
        this.localChannel = localChannel;
        this.commandWhitelist = commandWhitelist;
        this.commandWhitelistTutorial = commandWhitelistTutorial;
        this.textCommandManager = textCommandManager;
    }
    
    @EventHandler
    public void onChat(final ChatEvent event) {
        String name;
        if(event.getSender() instanceof ProxiedPlayer) name = ((ProxiedPlayer) event.getSender()).getDisplayName();
        else name = "Console";
        Logger.getInstance().logWithHeader(name + ": " + event.getMessage());
        
        if (event.isCancelled()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer)event.getSender();

        if (event.isCommand()) {
            if(textCommandManager.executeTextCommand(player, event.getMessage())) {
                event.setCancelled(true);
                return;
            }
        
            if(player.hasPermission("cvchat.nowhitelist")) return;
            String cmd = event.getMessage();
            int idx = cmd.indexOf(" ");
            if(idx != -1) cmd = cmd.substring(0, idx);
            cmd = cmd.substring(1);
            boolean finishedTutorial = PlayerDataManager.getInstance().finishedTutorial(player.getUniqueId());
            if(commandWhitelist == null || commandWhitelistTutorial == null) {
                player.sendMessage("§cCommand verification problems, please tell a server administrator.");
                event.setCancelled(true);
                return;
            }
            if((finishedTutorial == true && commandWhitelist.contains(cmd)) || (commandWhitelistTutorial.contains(cmd))) {
                return;
            }
            else {
                player.sendMessage("§cNo permission.");
                event.setCancelled(true);
                return;
            }
        }
        
        event.setCancelled(true);

        String message = Util.removeSectionSigns(event.getMessage());
        localChannel.sendMessage(player, message);
    }

    @EventHandler
    public void onTabCompleteEvent(final TabCompleteEvent event) {
        System.out.println("Tabcompleteevent: " + event.getSender() + ", " + event.getReceiver());
        System.out.println("Cursor: " + event.getCursor());
        System.out.println("Suggestions:");
        for(String s: event.getSuggestions()) {
            System.out.println("- " + s);
        }
        int lastSpace = event.getCursor().lastIndexOf(' ');
        if(lastSpace != -1) {
            String lastWord = event.getCursor().substring(lastSpace + 1);
            if(event.getCursor().startsWith("/msg ") ||
               event.getCursor().startsWith("/tp") || // TODO: Check permission of commands for player or, at least the whitelist
               event.getCursor().startsWith("/bring") ||
               event.getCursor().startsWith("/profile") ||
               event.getCursor().startsWith("/ban") ||
               event.getCursor().startsWith("/tempban") ||
               event.getCursor().startsWith("/kick") ||
               event.getCursor().startsWith("/mute") ) {
                for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                    String pname = p.getDisplayName(); // TODO: Strip colours?
                    if(pname.toLowerCase().startsWith(lastWord.toLowerCase())) { // TODO: Hidden players
                        event.getSuggestions().add(pname);
                        System.out.println("Add suggestion " + pname);
                    }
                }
            }
        }
        if(event.getSuggestions().size() == 0) {
            System.out.println("Cancel suggestion event");
            event.setCancelled(true);
        }
    }
}
