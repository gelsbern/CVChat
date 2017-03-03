package org.cubeville.cvchat.postfix;

public class Postfix
{
    private String postfix;
    private String permission;
    private int priority;
    
    public Postfix(String postfix, String permission, int priority) {
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
