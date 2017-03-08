package org.cubeville.cvchat.channels;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import org.cubeville.cvchat.CVChat;
import org.cubeville.cvchat.Util;

public class ChannelManager
{
    private Map<String, Channel> channels;
    private File statusFolder;

    Channel localChannel; // TODO: no no no no

    private static ChannelManager instance;
    public static ChannelManager getInstance() {
        return instance;
    }
    
    public ChannelManager(Configuration config, File statusFolder) {
        instance = this;
        
        this.statusFolder = statusFolder;

        channels = new HashMap<>();
        
        Collection<String> channelNames = config.getKeys();
        for(String channelName: channelNames) {
            Configuration channelConfig = (Configuration) config.get(channelName);
            String type = channelConfig.getString("type");
            Channel channel =
                ChannelFactory.getChannel(channelName, type,
                                          channelConfig.getString("permission"),
                                          channelConfig.getString("sendpermission"),
                                          channelConfig.getString("colorpermission"),
                                          channelConfig.getString("leavepermission"),
                                          channelConfig.getString("format"),
                                          channelConfig.getBoolean("default"),
                                          channelConfig.getBoolean("autojoin"),
                                          channelConfig.getBoolean("listable"),
                                          channelConfig.getBoolean("filtered"),
                                          (List<String>) channelConfig.getList("commands"));
            channels.put(channelName, channel);
            if(type.equals("local")) localChannel = channel;
        }
        
        
    }

    public void saveStatus(ProxiedPlayer player) {
        List<String> list = new ArrayList<>();
        for(Channel channel: channels.values()) {
            String s = channel.getConfigurationString(player);
            if(s != null) list.add(s);
        }
        UUID uuid = player.getUniqueId();
        ProxyServer.getInstance().getScheduler()
            .runAsync(CVChat.getInstance(),
                      new Runnable() {
                          public void run() {
                              // this feels like it needs a lock, but then also feels like it doesn't?
                              Util.saveFile(new File(statusFolder, uuid.toString()), list);
                          }
                      });
    }

    public void playerLogin(ProxiedPlayer player) {
        File file = new File(statusFolder, player.getUniqueId().toString());
        Map<String, String> config = new HashMap<>();
        if(file.exists()) {
            List<String> t = Util.readFile(file);
            for(String s: t) {
                config.put(Util.getPropertyName(s), s);
            }
        }
        for(String channel: channels.keySet()) {
            channels.get(channel).playerLogin(player, config.get(channel));
        }
        // I was very sure that I wanted to save if it's a new player, but
        // I absolutely can't remember why.
    }

    public void playerDisconnect(ProxiedPlayer player) {
        for(Channel channel: channels.values()) {
            channel.playerDisconnect(player);
        }
    }
    
    public GroupChannel getGroupChannel() {
        for(String channel: channels.keySet()) {
            if(channels.get(channel) instanceof GroupChannel) {
                return (GroupChannel) channels.get(channel);
            }
        }
        return null;
    }

    public Channel getLocalChannel() {
        return localChannel;
    }
    //public LocalChannel getLocalChannel() {
        // TODO
        //for(String channel: channels.keySet()) {
        //if(channels.get(channel) instanceof LocalChannel) return channels.get(localChannel);
        //}
        //return null;
    //}
    
    public Collection<Channel> getChannels() {
        return channels.values();
    }

    public Map<String, Channel> getChannelMap() {
        return channels;
    }
}
