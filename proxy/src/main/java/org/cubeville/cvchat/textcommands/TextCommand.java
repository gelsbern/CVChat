package org.cubeville.cvchat.textcommands;

import java.util.Set;
import java.util.List;

public class TextCommand
{
    int id;
    Set<String> aliases;
    List<String> text;
    boolean mandatory;
    
    public TextCommand(int id, Set<String> aliases, List<String> text, boolean mandatory) {
        this.id = id;
        this.aliases = aliases;
        this.text = text;
        this.mandatory = mandatory;
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

    public Integer getId() {
        return id;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
        
