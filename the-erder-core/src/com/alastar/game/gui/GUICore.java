package com.alastar.game.gui;

import java.util.Hashtable;

import com.alastar.game.gui.constructed.ConstructedGUI;

public class GUICore
{
    public static Hashtable<String, ConstructedGUI> uis = new Hashtable<String, ConstructedGUI>();

    public static void add(ConstructedGUI ui)
    {
        uis.put(ui.getName(), ui);
    }
    
    public static void remove(String s)
    {
        uis.remove(s);
    }
    
    public static void notifyVariableHandlers(String s, String val)
    {
        for(ConstructedGUI cgui: uis.values())
        {
            cgui.notifyAllElements(s, val);
        }
    }
}
