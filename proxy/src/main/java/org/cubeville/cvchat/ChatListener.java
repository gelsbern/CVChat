package org.cubeville.cvchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import org.cubeville.cvipc.CVIPC;
import org.cubeville.cvipc.IPCInterface;

import org.cubeville.cvchat.channels.Channel;
import org.cubeville.cvchat.log.Logger;
import org.cubeville.cvchat.playerdata.PlayerDataManager;
import org.cubeville.cvchat.textcommands.TextCommandManager;

public class ChatListener implements Listener, IPCInterface {

    private Channel localChannel;
    private Set<String> commandWhitelist;
    private Set<String> commandWhitelistTutorial;
    private TextCommandManager textCommandManager;
    private Set<UUID> tutorialChatUnlocked;
    private Map<String, String> aliases;

    public ChatListener(Channel localChannel, Set<String> commandWhitelist, Set<String> commandWhitelistTutorial, TextCommandManager textCommandManager, CVIPC ipc) {
        this.localChannel = localChannel;
        this.commandWhitelist = commandWhitelist;
        this.commandWhitelistTutorial = commandWhitelistTutorial;
        this.textCommandManager = textCommandManager;
        tutorialChatUnlocked = new HashSet<>();
        ipc.registerInterface("unlocktutorialchat", this);
        aliases = new HashMap<>();
        aliases.put("/rg claim", "/claim");
        aliases.put("/region claim", "/claim");
        aliases.put("/rg subzone", "/subzone");
        aliases.put("/region subzone", "/subzone");
        aliases.put("/w ", "/msg ");
        aliases.put("/tell ", "/msg ");
        aliases.put("/kill", "/suicide");
        aliases.put("/instasmelt", "/smelt");
        aliases.put("/night", "/ns");
    }

    public void unlockTutorialChat(UUID playerId) {
        tutorialChatUnlocked.add(playerId);
    }

    public void process(String serverName, String channel, String message) {
        System.out.println("Unlock tutorial chat for player " + message);
        unlockTutorialChat(UUID.fromString(message));
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

        boolean finishedTutorial = PlayerDataManager.getInstance().finishedTutorial(player.getUniqueId());
        if(finishedTutorial == false && tutorialChatUnlocked.contains(player.getUniqueId()) == false) {
            player.sendMessage("§cNo permission. Please proceed first.");
            event.setCancelled(true);
            return;
        }

        if (event.isCommand()) {
            if(textCommandManager.executeTextCommand(player, event.getMessage())) {
                event.setCancelled(true);
                return;
            }

            for(String alias: aliases.keySet()) {
                if(event.getMessage().toLowerCase().startsWith(alias)) {
                    event.setMessage(aliases.get(alias) + event.getMessage().substring(alias.length()));
                    break;
                }
            }
            
            if(player.hasPermission("cvchat.nowhitelist")) return;

            String cmd = event.getMessage();
            int idx = cmd.indexOf(" ");
            if(idx != -1) cmd = cmd.substring(0, idx);
            cmd = cmd.substring(1);
            cmd = cmd.toLowerCase();
            if(commandWhitelist == null || commandWhitelistTutorial == null) {
                player.sendMessage("§cCommand verification problems, please tell a server administrator.");
                event.setCancelled(true);
                return;
            }
            if((finishedTutorial == true && commandWhitelist.contains(cmd)) || (commandWhitelistTutorial.contains(cmd))) {
                return;
            }
            else {
                if(finishedTutorial == false) player.sendMessage("§cYou have limited permissions, please finish the tutorial first.");
                else player.sendMessage("§cNo permission.");
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
        for(String s: event.getSuggestions()) {
            System.out.println("- " + s);
        }
        int lastSpace = event.getCursor().lastIndexOf(' ');
        if(lastSpace != -1) {
            String c = event.getCursor();
            String lastWord = c.substring(lastSpace + 1);
            if(c.startsWith("/msg ") ||
               c.startsWith("/tp") || // TODO: Check permission of commands for player or, at least the whitelist
               c.startsWith("/bring") ||
               c.startsWith("/profile") ||
               c.startsWith("/ban") ||
               c.startsWith("/tempban") ||
               c.startsWith("/kick") ||
               c.startsWith("/mute") ||
               c.startsWith("/unmute") ||
               c.startsWith("/oi") ||
               c.startsWith("/oe") ||
               c.startsWith("/note") ||
               c.startsWith("/doc")) {
                for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                    if(!Util.playerIsHidden(p)) {
                        String pname = Util.removeColorCodes(p.getDisplayName());
                        if(pname.toLowerCase().startsWith(lastWord.toLowerCase())) {
                            event.getSuggestions().add(pname);
                        }
                    }
                }
            }
        }
        if(event.getSuggestions().size() == 0) {
            event.setCancelled(true);
        }
    }
}
