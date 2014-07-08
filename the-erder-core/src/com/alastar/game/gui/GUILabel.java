package com.alastar.game.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class GUILabel implements GUIElement
{

    private String name;
    private Label label;
    private String variable = "";
    
    public GUILabel(String n, Label l, String var, int h)
    {
        this.name = n;
        this.label = l;
        this.variable = var;
        this.label.setName(n);
        this.label.setHeight(h);
    }
    
    public void Update(String s)
    {
        System.out.println("Updating variable");
        this.label.setText(name+s);
    }
    
    @Override
    public Actor getElementAsActor()
    {
        return label;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void Destroy()
    {
        label.remove();
    }
    
    @Override
    public void Hide()
    {
        label.setVisible(false);
    }

    @Override
    public void Show()
    {
        label.setVisible(true);
    }

    @Override
    public String getHandledVariable()
    {
        return variable;
    }

}
