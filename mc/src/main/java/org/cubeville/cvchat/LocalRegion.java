package org.cubeville.cvchat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("LocalRegion")
public class LocalRegion implements ConfigurationSerializable {

    private String worldName;
    private String regionName;
    private String chatPrefix;
    
    public LocalRegion(String worldName, String regionName, String chatPrefix) {
        this.worldName = worldName;
        this.regionName = regionName;
        this.chatPrefix = chatPrefix;
    }
    
    public LocalRegion(Map<String, Object> config) {
        worldName = (String) config.get("worldName");
        regionName = (String) config.get("regionName");
        chatPrefix = (String) config.get("chatPrefix");
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("worldName", worldName);
        ret.put("regionName", regionName);
        ret.put("chatPrefix", chatPrefix);
        return ret;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public String getRegionName() {
        return regionName;
    }
    
    public String getChatPrefix() {
        return chatPrefix;
    }
}
