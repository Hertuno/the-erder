package com.alastar.game;

import com.alastar.game.enums.EntityType;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("serial")
public class Item extends Entity {

	public Item(int id, Vector3 pos, String caption, EntityType type) {
		super(id, pos, caption, type);
	}

}
