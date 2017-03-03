package org.cubeville.cvchat.channels;

import java.util.Collection;

public class ChannelFactory
{
    public static Channel getChannel(String name, String type, String viewPermission, String sendPermission, String colorPermission, String leavePermission, String format, boolean isDefault, boolean autojoin, boolean listable, Collection<String> commands) {
        if(type != null && type.equals("group")) {
            return new GroupChannel(name,
                                    viewPermission,
                                    sendPermission,
                                    colorPermission,
                                    leavePermission,
                                    format,
                                    isDefault,
                                    autojoin,
                                    listable,
                                    commands);
        }
        else {
            return new Channel(name,
                               viewPermission,
                               sendPermission,
                               colorPermission,
                               leavePermission,
                               format,
                               isDefault,
                               autojoin,
                               listable,
                               commands);
        }
    }

}
