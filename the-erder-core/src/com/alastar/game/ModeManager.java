package com.alastar.game;

public class ModeManager {

	public static Mode currentMode;

	public static void PushMode(Mode m) {
		if (currentMode != null) {
			currentMode.Off();
		}

		currentMode = m;
		System.out.println("Mode pushed! Mode: " + m.type.toString());
	}

	public static void removeEntity(int i) {
		if (currentMode.getEntityById(i) != null) {
		    currentMode.removeEntity(i);
		}
		System.out.println("Entity removed! Id: " + i);

	}

	public static void handleEntity(Entity p) {
		try{
		currentMode.addEntity(p);
		System.out.println("Entity added! Id: " + p.id);}
		catch(Exception e)
		{
		    System.out.println("Failed to add entity beacuse mode is null! Id: " + p.id);
		    e.printStackTrace();
		}
	}

	public static void handleTile(Tile t) {
	    currentMode.world.tiles.put(t.position, t);
	}

    public static void handleWorld(World readObject)
    {
        currentMode.setWorld(readObject);
    }

}
