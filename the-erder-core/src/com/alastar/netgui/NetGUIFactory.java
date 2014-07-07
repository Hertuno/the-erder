package com.alastar.netgui;

import java.util.Hashtable;

import ru.alastar.main.net.requests.GUIRequest;
import ru.alastar.net.PacketGenerator;

import com.alastar.game.GameManager;
import com.alastar.game.MainScreen;
import com.alastar.game.Vars;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class NetGUIFactory
{

    public static Hashtable<String, GUIElement> elements = new  Hashtable<String, GUIElement>();
    
    public static void buildGUI(String name, NetGUIInfo info)
    {
       Actor w = null;
       switch(info.widgetType){
        case Button:
            w = new TextButton(info.caption, GameManager.getSkin(info.skinName), "button");
            break;
        case Label:
            w = new Label(info.caption, GameManager.getSkin(info.skinName), "label");
            break;
        case TextField:
            w = new TextField(info.caption, GameManager.getSkin(info.skinName), "textField");
            break;
        case Texture:
            break;
        case Window:
            w = new Window(info.caption, GameManager.getSkin(info.skinName), "window");
            break;
        default:
            break;
           
       }
       w.setHeight(info.height/ Vars.getInt("balancedScreenHeight"));
       w.setWidth(info.width/  Vars.getInt("balancedScreenWidth"));
       w.setName(info.name);
       
       final String name1 = w.getName();

       w.addListener(new EventListener(){
        @Override
        public boolean handle(Event event)
        {
            
            GUIRequest r = new GUIRequest();
            r.name = name1;
            PacketGenerator.generatePacket(r);
            return false;
        }});
       
       w.setPosition(info.position.x/ Vars.getInt("balancedScreenWidth"), info.position.y/ Vars.getInt("balancedScreenHeight"));
       
       GUIElement e = new GUIElement(name, info, w);
       e.trackedVar = info.handledVariable.toLowerCase();
       e.UpdateVar(Vars.getVar(e.trackedVar));
       elements.put(name, e);
       MainScreen.gui.addActor(w);
       
    }
    
    public static void notifyGUI(String nameVar, String val)
    {
        for(GUIElement el: elements.values())
        {
            if(el.trackedVar.toLowerCase() == nameVar.toLowerCase())
                el.UpdateVar(val);
        }
    }
    
    public static void destroyGUIElement(String name)
    {
        GUIElement e = getElement(name);
        if(e != null){
          destroyChildren(e);
          e.Destroy();
        }
    }
    

    private static void destroyChildren(GUIElement e)
    {
        for(GUIElement child: e.children)
        {
            if(child != null)
            {
                elements.remove(child.name);
            }
        }
    }

    public static void resetParents()
    {
        for(GUIElement element: elements.values())
        {
            if(!element.getInfo().parentName.isEmpty())
            getElement(element.getInfo().parentName).AddChild(element);
        }
    }
    
    public static GUIElement getElement(String s)
    {
        for(String elementName: elements.keySet())
        {
            if(elementName == s){
                return elements.get(elementName);}
        }
        return null;
    }
}
