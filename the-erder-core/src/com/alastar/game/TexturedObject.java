package com.alastar.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface TexturedObject
{

    public Texture getTexture();
    public void setTexture();
    public Transform getTransform();
    public void Draw(SpriteBatch batch, int i, int j);

    
}
