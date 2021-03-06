package com.alastar.game;

import java.io.Serializable;

import com.alastar.game.enums.TileType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Tile extends Transform implements Serializable, TexturedObject {

	private static final long serialVersionUID = 7420787875382412198L;
	public TileType type;
	public boolean passable = false;

	public Tile(Vector3 pos, TileType t, boolean p) {
		super(pos);
		this.type = t;
		this.passable = p;
	}

    @Override
    public Texture getTexture()
    {
        return GameManager.getTexture(type);
    }

    @Override
    public void setTexture()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Transform getTransform()
    {
        return (Transform)this;
    }

    @Override
    public void Draw(SpriteBatch batch, int i, int j)
    {
        batch.draw(this.getTexture(), i, j);
    }

}
