package com.alastar.game;

import java.util.ArrayList;

import com.alastar.game.enums.ModeType;
import com.badlogic.gdx.math.Vector3;

public class Mode {

	public ModeType type;

	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public World world;

	public Mode(ModeType t) {
		this.type = t;
		entities = new ArrayList<Entity>();
	}

	public Entity getEntityById(int i) {
		for (Entity e : entities) {
			if (e.id == i) {
				return e;

			}
		}
		return null;
	}

	public void Off() {
	}

	public void handleUpdate(int id, Vector3 vec) {
		getEntityById(id).setPosition(vec);
	}

}
