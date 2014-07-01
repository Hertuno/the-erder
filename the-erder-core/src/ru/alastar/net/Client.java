package ru.alastar.net;

import java.io.IOException;
import java.util.ArrayList;

import com.alastar.game.Entity;
import com.alastar.game.ErderGame;
import com.alastar.game.GameScreen;

public class Client {
	private static String host = "10.0.0.2";
	private static int port = 25565;
	public static com.esotericsoftware.kryonet.Client client = null;
	public static ErderGame game = null;
	public static int id = 0;
	public static Entity controlledEntity = null;
	public static ArrayList<String> characters = new ArrayList<String>();

	public static void StartClient() throws Exception {
		client = new com.esotericsoftware.kryonet.Client();
		client.start();
		client.addListener(new ClientListener(client));
	}

	public static void Connect() {
		try {
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
		game.setScreen(new GameScreen(game, string));
	}
}