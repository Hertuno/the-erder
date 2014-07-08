package com.alastar.game.gui;

import com.alastar.game.Vars;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class GUIWindow implements GUIElement
{

    private String name;
    private Window window; 
    
    public GUIWindow(String n, Window w, Vector2 vector2, Vector2 vector22, int i, int j, int k, int l)
    {
        this.name = n;
        this.window = w;
        this.window.setName(n);
        this.window.setPosition(vector2.x / Vars.getInt("balancedScreenWidth"), vector2.y / Vars.getInt("balancedScreenHeight"));
        this.window.setHeight(vector22.y / Vars.getInt("balancedScreenHeight"));
        this.window.setWidth(vector22.x / Vars.getInt("balancedScreenWidth"));
        window.defaults().padLeft(i);
        window.defaults().padRight(j);
        window.defaults().padTop(k);
        window.defaults().padBottom(l);
        window.defaults().minWidth(100);
        window.defaults().minHeight(30);

    }
    
    @Override
    public Actor getElementAsActor()
    {
        return window;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void Destroy()
    {
        window.remove();
    }

    public void AddControl(GUIElement element)
    {
        window.add(element.getElementAsActor());
        window.pack();
    }
    
    @Override
    public void Hide()
    {
        window.setVisible(false);
    }

    @Override
    public void Show()
    {
        window.setVisible(true);
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
