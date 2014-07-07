package ru.alastar.game.systems;

import java.util.ArrayList;

import com.alastar.netgui.NetGUIInfo;
import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.ConnectedClient;
import ru.alastar.main.net.Server;


public class NetGUISystem
{
 public static ArrayList<GUIRequest> allowedRequests = new ArrayList<GUIRequest>();
 
 public static void HandleRequest(String name, ConnectedClient c)
 {
      for(GUIRequest r: allowedRequests){
          if(r.name == name && r.client == c)
          {
              r.handler.handle(r);
              break;
          }
      }
 }
 
 public static void sendOpenGUI(GUIRequest req, NetGUIInfo r, Connection c)
 {
     allowedRequests.add(req);
     Server.SendTo(c, r);
 }
 
}
