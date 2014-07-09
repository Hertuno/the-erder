package com.alastar.game.gui.net;

import java.util.Hashtable;

import ru.alastar.net.PacketGenerator;

import com.alastar.game.MainScreen;
import com.alastar.game.gui.GUIElement;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class NetGUICore
{

    public static Hashtable<String, GUIElement> netGUIElements = new Hashtable<String, GUIElement>();
    
    public static void createConstructedGUI(String name, String classpath)
    {
        
    }
    
    public static void createGUIElement(final NetGUIInfo info)
    {
        try
        {
            GUIElement o = (GUIElement) Class.forName(info.elementClasspath).newInstance();
            ChangeListener el = new ChangeListener(){
                

                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    NetGUIAnswer r = new NetGUIAnswer();
                    r.name = info.name;
                    r.value = "";
                    PacketGenerator.generatePacket(r);
                }
                
            };
                
            o.setName(info.name);
            o.setPosition(info.position);
            o.setWidth(info.scale.x);
            o.setHandledVariable(info.variable);
            o.setHeight(info.scale.y);
            o.setText(info.text);
            o.setEventListener(el);
            
            if(!info.parentName.isEmpty())
               getNetGUIElement(info.parentName).addChild(o);
            else
               MainScreen.gui.addActor(o.getElementAsActor());
            
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    public static GUIElement getNetGUIElement(String name)
    {
        return netGUIElements.get(name);
    }
    
    
}
