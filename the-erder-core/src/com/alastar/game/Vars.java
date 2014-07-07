package com.alastar.game;

import java.util.Hashtable;

public class Vars {
    
    public static Hashtable<String, Integer> integerVars = new Hashtable<String, Integer>();
    public static Hashtable<String, String> stringVars = new Hashtable<String, String>();

    public static void AddVar(String s, Integer i)
    {
        System.out.println("Added var " + s + ": "+ i );
        integerVars.put(s, i);
    }
    
    public static void AddVar(String s, String i)
    {
        System.out.println("Added var " + s + ": "+ i );
        stringVars.put(s, i);
    } 
    
    public static void setVar(String s, Integer i)
    {
        System.out.println("Set var " + s + ": "+ i );

        if(integerVars.containsKey(s))
        integerVars.remove(s);
        
        integerVars.put(s, i);
    }
    
    public static void setVar(String s, String i)
    {    
        System.out.println("Set var " + s + ": "+ i );

        if(stringVars.containsKey(s))
            stringVars.remove(s);
        
        stringVars.put(s, i);
    }
    
    public static Integer getInt(String s)
    {
        return integerVars.get(s);
    }
    
    public static String getStr(String s)
    {
        return stringVars.get(s);
    }
    
    public static String getVar(String s)
    {
        if(integerVars.containsKey(s))
            return Integer.toString(integerVars.get(s));
        else if(stringVars.containsKey(s))
            return stringVars.get(s);
        else
            return null;
    }
    
}
