package org.cubeville.cvchat.sanctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

    

}
