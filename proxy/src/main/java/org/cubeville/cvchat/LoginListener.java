package org.cubeville.cvchat;

import java.util.Collection;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {

    private Collection<Channel> channels;

    public LoginListener(Collection<Channel> channels) {
        this.channels = channels;
    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        for(Channel channel: channels) {
            channel.playerLogin(player);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        for(Channel channel: channels) {
            channel.playerDisconnect(player);
        }
    }
}
