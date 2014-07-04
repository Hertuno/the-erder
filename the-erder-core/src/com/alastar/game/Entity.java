package com.alastar.game;

import com.alastar.game.enums.EntityType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("serial")
public class Entity extends Transform implements TexturedObject{

	public int id = 0;
	public String caption = "generic Entity";
	public EntityType type;

	public Entity(int i, Vector3 pos, String c, EntityType t) {
		super(pos);
		this.id = i;
		this.caption = c;
		this.type = t;
	}

    @Override
    public Texture getTexture()
    {
        return GameManager.getEntityTexture(type);
    }

    @Override
    public void setTexture()
    {
        
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
        GameManager.getLocaleFont().draw(batch, caption, i - caption.length() * 2, j + GameManager.textureResolution * 2 + 5); 
    }

}
