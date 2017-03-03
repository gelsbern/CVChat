package org.cubeville.cvchat.postfix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class PostfixManager
{
    private List<Postfix> postfixes;
    private Map<UUID, String> playerCache;

    private static PostfixManager instance;
    public static PostfixManager getInstance() {
        return instance;
    }
    
    public PostfixManager(Configuration config) {
        instance = this;
        
        postfixes = new ArrayList<>();
        playerCache = new HashMap<>();
        
        Collection<String> postfixNames = config.getKeys();
        for(String postfixName: postfixNames) {
            Configuration postfixConfig = (Configuration) config.get(postfixName);
            Postfix postfix = new Postfix(postfixConfig.getString("postfix"),
                                          postfixConfig.getString("permission"),
                                          postfixConfig.getInt("priority"));
            postfixes.add(postfix);
        }
    }

    public String getPostfix(ProxiedPlayer player) {
        String postfix = playerCache.get(player.getUniqueId());
        if(postfix != null) return postfix;

        int prio = -1;
        String ret = "";
        for(Postfix p: postfixes) {
            if((p.getPermission().equals("default") || player.hasPermission(p.getPermission())) && p.getPriority() > prio) {
                ret = p.getPostfix();
                prio = p.getPriority();
            }
        }
        playerCache.put(player.getUniqueId(), ret);
        return ret;
    }
}
