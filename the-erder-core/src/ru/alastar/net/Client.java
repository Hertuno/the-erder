package ru.alastar.net;

import java.io.IOException;
import java.util.Hashtable;

import com.alastar.game.Entity;
import com.alastar.game.ErderGame;
import com.alastar.game.Item;
import com.alastar.game.Vars;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;

public class Client {
	private static String host = "127.0.0.1";
	private static int port = 25565;
	public static com.esotericsoftware.kryonet.Client client = null;
	public static ErderGame game = null;
	public static int id = 0;
	public static Entity controlledEntity = null;
	public static Hashtable<String, String> characters = new Hashtable<String, String>();
    public static Hashtable<String, Stat> stats = new Hashtable<String, Stat>();
    public static Hashtable<String, Skill> skills = new Hashtable<String, Skill>();
    public static Hashtable<Integer, Item> inventory = new Hashtable<Integer, Item>();

	
	public static void StartClient() throws Exception {
		client = new com.esotericsoftware.kryonet.Client();
		client.start();
		client.addListener(new ClientListener(client));
	}

	public static void Connect() {
		try {
		    if(Gdx.app.getType() == ApplicationType.Android)
		        host = "10.0.0.2";
			client.connect(100, host, port, port + 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Log(String s) {
		System.out.println(s);
	}

	public static void LoadWorld(String string) {
		System.out.println("Client: Load World");
		ErderGame.LoadWorld(string);
	}

    public static void handleStat(String name, int sValue, int mValue)
    {
        if(stats.containsKey(name.toLowerCase())){
            stats.remove(name.toLowerCase());
        }
        if(Vars.integerVars.containsKey(name.toLowerCase()+"_value")){
            Vars.setVar(name.toLowerCase()+"_value", sValue);
        }
        else
            Vars.AddVar(name.toLowerCase()+"_value", sValue);
        if(Vars.integerVars.containsKey(name.toLowerCase()+"_max")){
            Vars.setVar(name.toLowerCase()+"_max", mValue);
        }
        else
            Vars.AddVar(name.toLowerCase()+"_max", mValue);
        
        stats.put(name.toLowerCase(), new Stat(name, sValue, mValue));
    }

    public static void handleSkill(String name, int sValue, int mValue)
    {
        if(skills.containsKey(name.toLowerCase())){
            skills.remove(name.toLowerCase());
        }
        if(Vars.integerVars.containsKey(name.toLowerCase()+"_value")){
            Vars.setVar(name.toLowerCase()+"_value", sValue);
        }
        else
            Vars.AddVar(name.toLowerCase()+"_value", sValue);
        if(Vars.integerVars.containsKey(name.toLowerCase()+"_max")){
            Vars.setVar(name.toLowerCase()+"_max", mValue);
        }
        else
            Vars.AddVar(name.toLowerCase()+"_max", mValue);
        
        skills.put(name.toLowerCase(), new Skill(name, sValue, mValue));  
    }

    public static void handleInv(Item item)
    {
        if(inventory.containsKey(item.id))
            inventory.remove(item.id);
        inventory.put(item.id, item);
    }

    public static void handleChar(String name, String type)
    {
        characters.put(name, type);
    }
}