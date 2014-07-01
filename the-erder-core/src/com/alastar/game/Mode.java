package com.alastar.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.alastar.game.enums.ModeType;
import com.badlogic.gdx.math.Vector3;

public class Mode {

	public ModeType type;

	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public World world;

    public HashMap<Vector3, TexturedObject> render;

	public Mode(ModeType t) {
		this.type = t;
		entities = new ArrayList<Entity>();
		render = new HashMap<Vector3, TexturedObject>();
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
		try{
		TexturedObject t = render.get(getEntityById(id).position);
		render.remove(t.getTransform().position);
		render.put(vec, getEntityById(id));
		}catch(Exception e)
		{
		   e.printStackTrace();
		}
	}

	public void setWorld(World w)
	{
	    this.world = w;
	    render = world.tiles;
	}
	
    public void removeEntity(int i)
    {
        render.remove(getEntityById(i));
        entities.remove(getEntityById(i));
    }

    public void addEntity(Entity p)
    {
        entities.add(p);
        UpdateEntity(p);
    }

    private void UpdateEntity(Entity p)
    {
        if(render.containsKey(p.position))
        {
            render.remove(p.position);
            render.put(p.position, p);
        }
        else
        {
            render.put(p.position, p);
        }
    }

}
