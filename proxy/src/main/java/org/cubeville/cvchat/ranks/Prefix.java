package org.cubeville.cvchat.ranks;


public class Prefix
{
    private String permission;
    private String prefix;

    public Prefix(String prefix, String permission) {
        this.prefix = prefix;
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public String getPrefix() {
        return prefix;
    }
}
