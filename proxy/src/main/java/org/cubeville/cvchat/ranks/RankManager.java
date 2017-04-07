package org.cubeville.cvchat.ranks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class RankManager
{
    private List<Rank> ranks;
    //private Map<UUID, String> playerPostfix; // TODO Postfix cache disabled for now, needs to be cleared once in a while
    //private Map<UUID, Integer> playerPriority;
    
    private static RankManager instance;
    public static RankManager getInstance() {
        return instance;
    }
    
    public RankManager(Configuration config) {
        instance = this;
        
        ranks = new ArrayList<>();
        //playerPostfix = new HashMap<>();
        //playerPriority = new HashMap<>();
        
        Collection<String> rankNames = config.getKeys();
        for(String rankName: rankNames) {
            Configuration rankConfig = (Configuration) config.get(rankName);
            Rank rank = new Rank(rankConfig.getString("postfix"),
                                 rankConfig.getString("permission"),
                                 rankConfig.getInt("priority"));
            ranks.add(rank);
        }
    }

    public String getPostfix(CommandSender sender) {
        if(!(sender instanceof ProxiedPlayer)) return "CO";
        ProxiedPlayer player = (ProxiedPlayer) sender;
        //String postfix = playerPostfix.get(player.getUniqueId());
        //if(postfix != null) return postfix;

        int prio = -1;
        String ret = "";
        for(Rank p: ranks) {
            if((p.getPermission().equals("default") || player.hasPermission(p.getPermission())) && p.getPriority() > prio) {
                ret = p.getPostfix();
                prio = p.getPriority();
            }
        }
        //playerPostfix.put(player.getUniqueId(), ret);
        return ret;
    }

    public int getPriority(ProxiedPlayer player) {
        //Integer priority = playerPriority.get(player.getUniqueId());
        //if(priority != null) return priority;

        int prio = 0;
        for(Rank rank: ranks) {
            if((rank.getPermission().equals("default") || player.hasPermission(rank.getPermission())) && rank.getPriority() > prio) {
                prio = rank.getPriority();
            }
        }
        //playerPriority.put(player.getUniqueId(), prio);
        return prio;
    }
}
