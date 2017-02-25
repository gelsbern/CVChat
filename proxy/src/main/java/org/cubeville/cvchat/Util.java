package org.cubeville.cvchat;

public class Util
{
    public static String joinStrings(String[] parts, int offset) {
        String ret = "";
        for(int i = offset; i < parts.length; i++) {
            if(i > offset) ret += " ";
            ret += parts[i];
        }
        return ret;
    }

    public static String removeSectionSigns(String text) {
        text.replace("§", "");
        return text;
    }
}
