package com.alastar.game.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GUIButton implements GUIElement
{
    private String name;
    private TextButton button;

    public GUIButton(String n, TextButton l, EventListener e)
    {
        this.name = n;
        this.button = l;
        this.button.addListener(e);
    }
    
    @Override
    public Actor getElementAsActor()
    {
        return button;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void Destroy()
    {
        button.remove();
    }
    
    @Override
    public void Hide()
    {
        button.setVisible(false);
    }

    @Override
    public void Show()
    {
        button.setVisible(true);
    }

    @Override
    public void Update(String s)
    {
        
    }

    @Override
    public String getHandledVariable()
    {
        return "";
    }
}
