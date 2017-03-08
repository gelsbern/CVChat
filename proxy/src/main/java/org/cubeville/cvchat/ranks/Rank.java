package org.cubeville.cvchat.ranks;

public class Rank
{
    private String postfix;
    private String permission;
    private int priority;

    public Rank(String postfix, String permission, int priority) {
        this.postfix = postfix;
        this.permission = permission;
        this.priority = priority;
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

}
