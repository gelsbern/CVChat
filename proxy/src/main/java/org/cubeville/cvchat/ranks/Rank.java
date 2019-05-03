package org.cubeville.cvchat.ranks;

public class Rank
{
    private String postfix;
    private String permission;
    private int priority;
    private String color;
    
    public Rank(String postfix, String permission, int priority, String color) {
        this.postfix = postfix;
        this.permission = permission;
        this.priority = priority;
        this.color = color;
    }

    public String getPermission() {
        return permission;
    }

    public String getPostfix() {
        return postfix;
    }

    public int getPriority() {
        return priority;
    }

    public String getColor() {
        return color;
    }
}
