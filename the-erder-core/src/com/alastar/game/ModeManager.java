package com.alastar.game;

import com.alastar.game.enums.ModeType;

public class ModeManager {

	public static Mode currentMode;

	public static void PushMode(Mode m) {
		if (currentMode != null) {
			currentMode.Off();
		}

		currentMode = m;
		System.out.println("Mode pushed! Mode: " + m.type.toString());
	}

	public static void removeEntity(int i, ModeType m) {
		Mode tM = GameManager.getMode(m);
		if (tM.getEntityById(i) != null) {
			tM.entities.remove(tM.getEntityById(i).position);
		}
		System.out.println("Entity removed! Id: " + i);

	}

	// public static void removeTile(int x, int y, ModeType m) {
	// Mode tM = GameManager.getMode(m);
	// tM.tiles.remove(t);
	// }

	public static void handleEntity(Entity p, ModeType m) {
		Mode tM = GameManager.getMode(m);
		tM.entities.add(p);
		System.out.println("Entity added! Id: " + p.id);
	}

	public static void handleTile(Tile t, ModeType m) {
		Mode tM = GameManager.getMode(m);
		tM.world.tiles.put(t.position, t);
	}

}