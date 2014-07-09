package ru.alastar.game.systems.gui;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.ConnectedClient;
import ru.alastar.main.net.Server;

public class NetGUISystem
{
   
    public static void sendGUIElement(NetGUIInfo info, ConnectedClient c)
    {
        Server.SendTo(c.connection, info);
    }
    
    public static NetGUIInfo CreateGUIInfo(String  name,
    Vector2 position,
    Vector2 scale,
    String  parentName,
    String  elementClasspath,
    String  variable,
    String  text)
    {
        NetGUIInfo r = new NetGUIInfo();
        r.name = name;
        r.position = position;
        r.parentName = parentName;
        r.scale = scale;
        r.text = text;
        r.variable = variable;
        r.elementClasspath = elementClasspath;
        return r;
        
    }

    public static void OpenGUI(NetGUIInfo info, ConnectedClient c)
    {
        c.controlledEntity.AddGUI(info);
        sendGUIElement(info, c);
    }
    
    

    public static void handleAnswer(NetGUIAnswer r, Connection connection)
    {
        ConnectedClient c = Server.getClient(connection);
        c.controlledEntity.invokeGUIHandler(r, c);    
    }
}
