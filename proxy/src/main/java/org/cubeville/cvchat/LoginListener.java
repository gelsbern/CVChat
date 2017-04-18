package org.cubeville.cvchat;

import java.util.Collection;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import org.cubeville.cvchat.channels.ChannelManager;
import org.cubeville.cvchat.playerdata.PlayerDataManager;
import org.cubeville.cvchat.ranks.RankManager;
import org.cubeville.cvchat.tickets.TicketManager;

public class LoginListener implements Listener
{
    ChannelManager channelManager;
    TicketManager ticketManager;
    
    public LoginListener(ChannelManager channelManager, TicketManager ticketManager) {
        this.channelManager = channelManager;
        this.ticketManager = ticketManager;
    }

    @EventHandler
    public void onPreLogin(final PreLoginEvent event) {
        String playerName = event.getConnection().getName();
        Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
        for(ProxiedPlayer p: players) {
            if(playerName.equals(p.getName())) {
                event.setCancelled(true);
                event.setCancelReason("§cYou're already logged in to this server.");
                return;
            }
        }
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {        
        PlayerDataManager pdm = PlayerDataManager.getInstance();
        PendingConnection connection = event.getConnection();
        if(pdm.isBanned(connection.getUniqueId(), true)) {
            event.setCancelled(true);
            String type = pdm.isPermanentlyBanned(connection.getUniqueId()) ? "permanently" : "temporarily";
            event.setCancelReason("§cYou're " + type + " banned from this server.\n§eReason: §c" + pdm.getBanReason(connection.getUniqueId()));
        }
        //String[] whitelist = {"DoubleSammich", "Firesnuke", "LissaLaine", "_Tiffy_", "Raytheonx", "inutterable", "unixadmin", "pikeman327", "Nafartiti", "stldenise", "Herzz", "deanwin", "saemai", "A_cloud_ora", "Monoshish", "Monikorpse", "Clicki", "WallyDonkey", "BlueFirebolt", "Hjortshoej_", "Clicker27", "KitKathy", "Ptown16", "SeaGuy", "FrediW", "VicTheDonut", "Cade1390", "JimS996", "RandomRob13", "vic_torus", "Daugeas", "Sequoia", "SauceyTim", "CaveHuntress", "CreepingDespair", "_HunniB_", "TemperanceL", "Twilighta", "ACreepMaster"};
        //boolean whitelisted = false;
        //for(int i = 0; i < whitelist.length; i++) {
        //if(event.getConnection().getName().equals(whitelist[i])) {
        //      whitelisted = true;
        //      break;
        //  }
        //}
        //if(!whitelisted) {
        //    event.setCancelled(true);
        //    event.setCancelReason("§cPlease come back soon, we're gonna open momentarily.");
        //}

    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(final PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        channelManager.playerLogin(player);
        
        PlayerDataManager pdm = PlayerDataManager.getInstance();
        UUID playerId = player.getUniqueId();
        
        if(!player.hasPermission("cvchat.silent.join")) {
            if(!pdm.isPlayerKnown(playerId)) {
                sendWelcomeMessage(player.getName());
                pdm.addPlayer(playerId, player.getName(), getStrippedIpAddress(player));
                sendMessage(player.getDisplayName(), "joined");
            }
            else {
                if(pdm.getPlayerName(playerId).equals(player.getName())) {
                    sendMessage(player.getDisplayName(), "joined");
                }
                else {
                    sendMessage(player.getDisplayName() + " (formerly known as " + pdm.getPlayerName(playerId) + ") ", "joined");
                    pdm.changePlayerName(playerId, player.getName());
                }
            }
        }
        else {
            sendSilentMessage(player.getDisplayName(), "joined");
            if(!pdm.isPlayerKnown(playerId)) {
                pdm.addPlayer(playerId, player.getName(), getStrippedIpAddress(player));
            }
            else if(!pdm.getPlayerName(playerId).equals(player.getName())) {
                pdm.changePlayerName(playerId, player.getName());
            }
        }
        pdm.playerLogin(playerId, getStrippedIpAddress(player), RankManager.getInstance().getPriority(player));

        ticketManager.playerLogin(player);
        
        System.out.println("Check if player logging in has finished the tutorial.");
        if(!pdm.finishedTutorial(playerId)) {
            System.out.println("Setting login location to tutorial for player " + player.getDisplayName());
            channelManager.getIPC().sendMessage("newskylands", "xwportal|" + playerId + "|portal:TutorialSpawn|newskylands");
        }
        else {
            System.out.println("Player has already finished the tutorial.");
        }
    }

    private String getStrippedIpAddress(ProxiedPlayer player) {
        String ret = player.getAddress().getAddress().toString();
        ret = ret.substring(ret.indexOf("/") + 1);
        return ret;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        channelManager.playerDisconnect(player);
        if(!player.hasPermission("cvchat.silent.leave")) {
            sendMessage(player.getDisplayName(), "left");
        }
        else {
            sendSilentMessage(player.getDisplayName(), "left");
        }
        PlayerDataManager.getInstance().playerLogout(player.getUniqueId());
    }

    private void sendMessage(String playerName, String status) {
        for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
            p.sendMessage("§e" + playerName + "§e " + status + " the game.");
        }
    }

    private void sendSilentMessage(String playerName, String status) {
        for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
            if(p.hasPermission("cvchat.silent.view")) {
                p.sendMessage("§3" + playerName + "§3 " + status + " the game silently.");
            }
        }
    }
    
    private void sendWelcomeMessage(String playerName) {
        for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
            p.sendMessage("§eEveryone welcome Cubeville's newest player, " + playerName + "§e!");
        }        
    }
}
