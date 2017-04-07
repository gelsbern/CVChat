package org.cubeville.cvchat.textcommands;

import java.util.Set;
import java.util.List;

public class TextCommand
{
    Set<String> aliases;
    List<String> text;
    boolean mandatory;
    
    public TextCommand(Set<String> aliases, List<String> text, boolean mandatory) {
        this.aliases = aliases;
        this.text = text;
    }

    public boolean matches(String command) {
        String c = command.replaceAll(" +", " ");
        for(String s: aliases) {
            if(s.equals(c)) return true;
        }
        return false;
    }

    public List<String> getText() {
        return text;
    }
}
        
