package ru.alastar.world;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alastar.game.Entity;
import ru.alastar.main.Main;
import ru.alastar.main.net.ConnectedClient;
import ru.alastar.main.net.Server;
import ru.alastar.main.net.responses.AddEntityResponse;
import ru.alastar.main.net.responses.AddTileResponse;
import ru.alastar.main.net.responses.ChatSendResponse;
import ru.alastar.main.net.responses.RemoveEntityResponse;
import ru.alastar.main.net.responses.RemoveTileResponse;

import com.alastar.game.Tile;
import com.alastar.game.enums.ModeType;
import com.alastar.game.enums.TileType;
import com.badlogic.gdx.math.Vector3;


public class ServerWorld {

	public int id = 0;
	public String name = "GenericWorld";

	public HashMap<Vector3, Tile> tiles = new HashMap<Vector3, Tile>();
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public int version = 0;
	public int zMin = -1;
	public int zMax = 10;

	public ServerWorld(int i, String n) {
		this.id = i;
		this.name = n;
		tiles = new HashMap<Vector3, Tile>();
		entities = new ArrayList<Entity>();
	}

	public void CreateTile(int x, int y, int z, TileType type, boolean p) {
		AddTile(new Tile(new Vector3(x, y, z), type, p));
	}

	public void AddTile(Tile t) {
		AddTileResponse r;
		tiles.put(t.position, t);
		for (int i = 0; i < entities.size(); ++i) {
			if (Server.getClientByEntity(entities.get(i)) != null) {
				r = new AddTileResponse();
				r.x = (int) t.position.x;
				r.y = (int) t.position.y;
				r.z = (int) t.position.z;
				r.type = t.type;
				r.mode = ModeType.World;
				Server.SendTo(Server.getClientByEntity(entities.get(i)).connection, r);
			}
		}
	}

	public void AddEntity(Entity e) {
		Entity ent;
		AddEntityResponse r;
		for (int i = 0; i < entities.size(); ++i) {
			ent = entities.get(i);
			r = new AddEntityResponse();
			r.caption = e.caption;
			r.id = e.id;
			r.x = (int) e.pos.x;
			r.y = (int) e.pos.y;
			r.z = (int) e.pos.z;
			r.type = e.type;
			Server.SendTo(Server.getClientByEntity(ent).connection, r);
		}
		entities.add(e);
	}

	public void RemoveEntity(Entity entity) {
		RemoveEntityResponse r;
		entities.remove(entity);
		for (int i = 0; i < entities.size(); ++i) {
			if (Server.getClientByEntity(entities.get(i)) != null) {
				r = new RemoveEntityResponse();
				r.id = entity.id;
				r.mode = ModeType.World;
				Server.SendTo(Server.getClientByEntity(entities.get(i)).connection, r);
			}
		}
	}

	public void RemoveTile(Tile t) {
		RemoveTileResponse r;
		for (int i = 0; i < entities.size(); ++i) {
			if (Server.getClientByEntity(entities.get(i)) != null) {
				r = new RemoveTileResponse();
				r.x = (int) t.position.x;
				r.y = (int) t.position.y;
				r.z = (int) t.position.z;
				r.mode = ModeType.World;
				Server.SendTo(Server.getClientByEntity(entities.get(i)).connection, r);
			}
		}
		try {
			tiles.remove(t.position);
		} finally {
		}
	}

	public Tile GetTile(int x, int y, int z) {
		return tiles.get(new Vector3(x, y, z));
	}

	public Tile GetTile(Vector3 xyz) {
		return tiles.get(xyz);
	}

	public void SendTiles(ConnectedClient c) {
	}

	public void SendEntities(ConnectedClient c) {
		Entity e;
		AddEntityResponse r;

		for (int i = 0; i < entities.size(); ++i) {
			e = entities.get(i);
			r = new AddEntityResponse();
			r.caption = e.caption;
			r.id = e.id;
			r.x = (int) e.pos.x;
			r.y = (int) e.pos.y;
			r.z = (int) e.pos.z;
			r.type = e.type;
			Main.Log("[DEBUG]","Send entity id: " + e.id + " caption: " + e.caption
					+ " pos: " + e.pos.toString());
			Server.SendTo(c.connection, r);
		}
	}

    public void sendAll(String msg, String caption)
    {
        ChatSendResponse r = new ChatSendResponse();
        r.msg = "\'" + msg + "\'";
        r.sender = caption;
        for (Entity e1 : entities)
        {
            Server.SendTo(Server.getClientByEntity(e1).connection, r);
        }
        Main.Log("[CHAT]", "(" + this.name + ")" + caption + ":" + msg);
    }
}
