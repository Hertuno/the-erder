package com.alastar.netgui;

import java.util.ArrayList;

import com.alastar.game.MainScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class GUIElement
{
    public String name;
    private NetGUIInfo info;
    private Actor widget;
    private boolean visible;
    public ArrayList<GUIElement> children;
    public String trackedVar;
    
    public GUIElement(String n, NetGUIInfo info, Actor w)
    {
        this.info = info;
        this.widget = w;
        this.name = n;
        this.children = new ArrayList<GUIElement>();
        this.setVisibility(info.visible);
        this.widget.setVisible(info.visible);
    }
    
    public Actor getWidget()
    {
       return this.widget;
    }
    
    public NetGUIInfo getInfo()
    {
        return this.info;
    }
    
    public boolean getVisibility()
    {
        return this.visible;
    }
    
    public void setVisibility(boolean v)
    {
        this.visible = v;
    }
    
    public void Destroy()
    {
       for(Actor a: MainScreen.gui.getActors())
       {
           if(a == widget)
           {
              a.remove();
           }
       }
        NetGUIFactory.elements.remove(name);
    }

    public void AddChild(GUIElement element)
    {
        children.add(element);
    }

    public void UpdateVar(String val)
    {
        switch(info.widgetType)
        {
            case Button:
                ((TextButton)widget).setText(info.caption.replace("<v>", val));;
                break;
            case Label:
                ((Label)widget).setText(info.caption.replace("<v>", val));;
                break;
            case TextField:
                ((TextField)widget).setText(info.caption.replace("<v>", val));;
                break;
            case Texture:
                break;
            case Window:
                ((Window)widget).setTitle(info.caption.replace("<v>", val));;
                break;
            default:
                break;
            
        }
    }
}
