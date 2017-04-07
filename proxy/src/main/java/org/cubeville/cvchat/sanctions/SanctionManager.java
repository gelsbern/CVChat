package org.cubeville.cvchat.sanctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.cubeville.cvchat.playerdata.PlayerDataManager;

public class SanctionManager
{
    Map<UUID, Long> mutedPlayers;
    List<String> filterTerms;
    
    private static SanctionManager instance;
    public static SanctionManager getInstance() {
        return instance;
    }

    public SanctionManager(List<String> filterTerms) {
        instance = this;
        mutedPlayers = new HashMap<>();
        this.filterTerms = filterTerms;
    }

    public void mutePlayer(ProxiedPlayer player) {
        mutedPlayers.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void unmutePlayer(ProxiedPlayer player) {
        mutedPlayers.remove(player.getUniqueId());
    }
    
    public boolean isPlayerMuted(CommandSender player) {
        if(player instanceof ProxiedPlayer) {
            return mutedPlayers.containsKey(((ProxiedPlayer) player).getUniqueId());
        }
        else {
            return false;
        }
    }

    public void banPlayer(CommandSender sender, UUID bannedPlayerId, String banReason, long duration, boolean silent) {
        UUID senderId = null;
        if(sender instanceof ProxiedPlayer) senderId = ((ProxiedPlayer) sender).getUniqueId();
        PlayerDataManager pdm = PlayerDataManager.getInstance();
        pdm.banPlayer(bannedPlayerId, senderId, banReason, duration * 1000);
        {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(bannedPlayerId);
            if(player != null) {
                String kname = "Console";
                if(sender instanceof ProxiedPlayer) kname = ((ProxiedPlayer) sender).getDisplayName();
                player.disconnect("§6You have been " + (duration != 0 ? "temporarily " : "") + "banned by §e" + kname + "§6.\nReason: §e" + banReason);
            }
        }
    }

    public String unbanPlayer(CommandSender sender, String bannedPlayerName, boolean silent) {
        PlayerDataManager pdm = PlayerDataManager.getInstance();
        UUID bannedPlayerId = pdm.getPlayerId(bannedPlayerName);
        if(bannedPlayerId == null) return null;
        pdm.unbanPlayer(bannedPlayerId);
        return pdm.getPlayerName(bannedPlayerId);
    }
}
