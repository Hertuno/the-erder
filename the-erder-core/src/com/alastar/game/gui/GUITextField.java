package com.alastar.game.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class GUITextField implements GUIElement
{

    private String name;
    private TextField field;
    
    
    public GUITextField(String n, TextField l)
    {
        this.name = n;
        this.field = l;
    }
    
    @Override
    public Actor getElementAsActor()
    {
        return field;
    }

    public String getText()
    {
        return field.getText();
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void Destroy()
    {
        field.remove();
    }
    
    @Override
    public void Hide()
    {
        field.setVisible(false);
    }

    @Override
    public void Show()
    {
        field.setVisible(true);
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
