package ru.alastar.game.systems.gui.hadlers;

import ru.alastar.main.Main;
import ru.alastar.main.net.ConnectedClient;

public class InvButtonGUIHandler implements GUIHandler
{
    @Override
    public void handle(String[] args, ConnectedClient c)
    {
        if(!c.controlledEntity.haveGUI("inventory"))
        {
            Main.Log("[DEBUG]","Inventory request!");
        }
    }
}
