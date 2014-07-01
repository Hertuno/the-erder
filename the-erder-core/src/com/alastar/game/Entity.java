package com.alastar.game;

import com.alastar.game.enums.EntityType;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("serial")
public class Entity extends Transform {

	public int id = 0;
	public String caption = "generic Entity";
	public EntityType type;

	public Entity(int i, Vector3 pos, String c, EntityType t) {
		super(pos);
		this.id = i;
		this.caption = c;
		this.type = t;
	}

}
