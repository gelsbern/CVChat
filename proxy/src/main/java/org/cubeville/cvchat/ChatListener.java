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
import org.cubeville.cvchat.tickets.TicketManager;

public class ChatListener implements Listener, IPCInterface {

    private Channel localChannel;
    private Set<String> commandWhitelist;
    private Set<String> commandWhitelistTutorial;
    private TextCommandManager textCommandManager;
    private Set<UUID> tutorialChatUnlocked;
    private Map<String, String> aliases;

    private TicketManager ticketManager;
    private CVIPC cvipc;
    
    public ChatListener(Channel localChannel, Set<String> commandWhitelist, Set<String> commandWhitelistTutorial, TextCommandManager textCommandManager, TicketManager ticketManager, CVIPC ipc) {
        this.localChannel = localChannel;
        this.commandWhitelist = commandWhitelist;
        this.commandWhitelistTutorial = commandWhitelistTutorial;
        this.textCommandManager = textCommandManager;
        this.ticketManager = ticketManager;
        tutorialChatUnlocked = new HashSet<>();
        this.cvipc = ipc;
        ipc.registerInterface("unlocktutorialchat", this);
        aliases = new HashMap<>();
        aliases.put("/rg claim", "/claim"); // TODO: welp...
        aliases.put("/region claim", "/claim");
        aliases.put("/rg subzone", "/subzone");
        aliases.put("/region subzone", "/subzone");
        aliases.put("/w ", "/msg ");
        aliases.put("/tell ", "/msg ");
        aliases.put("/kill", "/suicide");
        aliases.put("/instasmelt", "/smelt");
        aliases.put("/night", "/ns");
        aliases.put("/repair", "/rp");
        aliases.put("/hub", "/ptp hub");
        aliases.put("/tut", "/ptp tut");
        aliases.put("/we cui", "/activatewecui");
    }

    public void unlockTutorialChat(UUID playerId) {
        tutorialChatUnlocked.add(playerId);
    }

    public void process(String serverName, String channel, String message) {
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

        String serverName = player.getServer().getInfo().getName();
        cvipc.sendMessage(serverName, "afktrigger|" + player.getUniqueId());
        
        boolean finishedTutorial = PlayerDataManager.getInstance().finishedTutorial(player.getUniqueId());
        if(finishedTutorial == false && tutorialChatUnlocked.contains(player.getUniqueId()) == false && player.hasPermission("cvchat.bypasstutorial") == false) {
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
        if(Util.playerIsHidden(player)) {
            if(message.endsWith("/")) {
                message = message.substring(0, message.length() - 1);
                if(message.length() == 0) return;
            }
            else {
                player.sendMessage("§cAdd a / to speak in local chat when you're in /v.");
                return;
            }
        }

        localChannel.sendMessage(player, message);
    }

    @EventHandler
    public void onTabCompleteEvent(final TabCompleteEvent event) {
        if(!(event.getSender() instanceof ProxiedPlayer)) {
            event.setCancelled(true);
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        String c = event.getCursor();
        while(c.startsWith(" ")) c = c.substring(1);
        
        int lastSpace = c.lastIndexOf(' ');
        if(lastSpace != -1) {
            String lastWord = c.substring(lastSpace + 1);
            Set<String> players;
            if(player.hasPermission("cvchat.tabcompletion.modreq")) {
                players = ticketManager.getOpenTicketPlayerList();
            }
            else {
                players = new HashSet<>();
            }
            for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                if(!Util.playerIsHidden(p)) {
                    players.add(Util.removeColorCodes(p.getDisplayName()));
                }
            }
            for(String pname: players) {
                if(pname.toLowerCase().startsWith(lastWord.toLowerCase())) {
                    event.getSuggestions().add(pname);
                }
            }
        }
        else {
            if(c.startsWith("/")) {
                c = c.substring(1);
                for(String cmd: commandWhitelist) {
                    if(cmd.startsWith(c)) {
                        event.getSuggestions().add("/" + cmd + " ");
                    }
                }
            }
        }
        if(event.getSuggestions().size() == 0) {
            event.setCancelled(true);
        }
    }
}
