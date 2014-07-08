package com.alastar.game.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface GUIElement
{
   public Actor getElementAsActor();
   public String getName();
   public void Destroy();
   public void Hide();
   public void Show();
   public void Update(String val);
   public String getHandledVariable();
}
