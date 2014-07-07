package ru.alastar.game.systems;

import ru.alastar.game.netgui.handlers.GUIRequestHandler;
import ru.alastar.main.net.ConnectedClient;

public class GUIRequest
{
  public ConnectedClient client;
  public String name;
  public GUIRequestHandler handler;
}
