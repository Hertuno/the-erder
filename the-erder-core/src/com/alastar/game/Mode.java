package com.alastar.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.alastar.game.enums.ModeType;
import com.badlogic.gdx.math.Vector3;

public class Mode {

	public ModeType type;

	public ArrayList<Entity> entities;
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
	    
		
		try{
		    
		TexturedObject t = render.get(getEntityById(id).position);
		
		if(render.containsKey(t.getTransform().position))
		render.remove(t.getTransform().position);
		
		render.put(vec, getEntityById(id));
	    getEntityById(id).setPosition(vec);
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
        render.remove(getEntityById(i).position);
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
            System.out.println("Entity removed from the render thread");
            render.remove(p.position);
        }
         render.put(p.position, p);
         System.out.println("Entity putted on the render thread");
    }

    public void UpdateRenders()
    {
        @SuppressWarnings("unchecked")
        ArrayList<Entity> ents = (ArrayList<Entity>) entities.clone();
        for(Entity e: ents)
        {
          if(render.containsKey(e.position)){
          render.remove(e.position);
          }
          
          render.put(e.position, e);
        }
        //System.out.println("Update renders");
    }

}
