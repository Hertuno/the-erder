package com.alastar.game;

import java.io.Serializable;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;

public class World implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3791347989965503519L;
	public int version = 0;
	public HashMap<Vector3, Tile> tiles;

	public int xMax;
	public int yMax;
	public int zMax;
	public int xMin;
	public int yMin;
	public int zMin;

	public World(int ver, HashMap<Vector3, Tile> tiles, int xMax, int xMin,
			int yMax, int yMin, int zMax, int zMin) {
		this.version = ver;
		this.tiles = tiles;

		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;

		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
	}

	public boolean isUnderTile(Vector3 position) {
		for (int z = (int) (position.z + 1); z <= zMax; ++z) {
			if (tiles.get(new Vector3(position.x, position.y, z)) != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isHidden(Entity e) {
		for (int y = (int) e.position.y, z = (int) e.position.z; y < (int) e.position.y
				&& z < zMax; ++y, ++z) {
			if (tiles.get(new Vector3(e.position.x, y, z)) != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isHidingEntity(Entity e, Tile t) {
		if (e.position.y > t.position.y) {
			if (e.position.y <= t.position.y + t.position.z
					&& (e.position.x - GameManager.fieldOfTransparency < t.position.x && e.position.x
							+ GameManager.fieldOfTransparency > t.position.x)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Tile getTileAbove(Vector3 position) {
		for (int z = (int) (position.z + 1); z <= zMax; ++z) {
			if (tiles.get(new Vector3(position.x, position.y, z)) != null) {
				return tiles.get(new Vector3(position.x, position.y, z));
			}
		}
		return null;
	}

	public boolean CanBeSeen(Entity f, Entity e) {
		if (Vector3.dst(f.position.x, f.position.y, f.position.z, e.position.x,
				e.position.y, e.position.z) > GameManager.fieldOfTransparency) {
			return false;
		} else
			return true;
	}

}
