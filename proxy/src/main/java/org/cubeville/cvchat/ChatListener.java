package org.cubeville.cvchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    Channel localChannel;

    public ChatListener(Channel localChannel) {
        this.localChannel = localChannel;
    }
    
    @EventHandler
    public void onChat(final ChatEvent event) {
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        event.setCancelled(true);

        String message = Util.removeSectionSigns(event.getMessage());
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        localChannel.sendMessage(player, message);
    }

}
