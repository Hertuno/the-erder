package com.alastar.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import ru.alastar.main.net.requests.InputRequest;
import ru.alastar.net.Client;
import ru.alastar.net.PacketGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {

	final ErderGame game;
	OrthographicCamera camera;
	public static String result = "";
	public static int tileView = 13;

	public GameScreen(final ErderGame gam, String worldName) {
		System.out.println("Game Screen");

		this.game = gam;
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {

				return true;
			}

			@Override
			public boolean keyUp(int keycode) {

				return true;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}

		});
		LoadWorld(worldName);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 750, 750);

	}

	private void LoadWorld(String worldName) {
		try {
			System.out.println("Load World");

			File file = null;
			for (int i = 0; i <= GameManager.getWorldsDataDir().length; ++i) {
				System.out.println(GameManager.getWorldsDataDir()[i].name());
				System.out.println(worldName);

				if (GameManager.getWorldsDataDir()[i].name().equals(
						worldName + ".bin")) {
					file = GameManager.getWorldsDataDir()[i].file();
					System.out.println("File have been found!");
					break;
				}

			}

			FileInputStream f_in = new FileInputStream(file);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);
			ModeManager.currentMode.world = (World) obj_in.readObject();
			System.out.println("Current world zMin: "
					+ ModeManager.currentMode.world.zMin + " zMax: "
					+ ModeManager.currentMode.world.zMax);
			System.out.println("File have been loaded!");

			obj_in.close();
			f_in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Client.controlledEntity != null) {
			// if(camera.position.dst(Client.controlledEntity.position) != 0)
			// camera.translate(GameManager.vectorDifference(camera.position,
			// Client.controlledEntity.position));
			camera.position.x = Client.controlledEntity.position.x * 32;
			camera.position.y = Client.controlledEntity.position.y * 32
					+ (Client.controlledEntity.position.z * 32);
		}

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		if (ModeManager.currentMode.world != null) {
			if (ModeManager.currentMode.world
					.isUnderTile(Client.controlledEntity.position)) {
				DrawTiles(
						ModeManager.currentMode.world.zMin,
						(int) ModeManager.currentMode.world
								.getTileAbove(Client.controlledEntity.position).position.z - 1,
						(int) camera.position.x / GameManager.textureResolution
								- tileView, (int) camera.position.x
								/ GameManager.textureResolution + tileView,
						(int) camera.position.y / GameManager.textureResolution
								- tileView, (int) camera.position.y
								/ GameManager.textureResolution + tileView);
				DrawEntities(ModeManager.currentMode.world.zMin,
						(int) Client.controlledEntity.position.z,
						(int) camera.position.x / GameManager.textureResolution
								- tileView, (int) camera.position.x
								/ GameManager.textureResolution + tileView,
						(int) camera.position.y / GameManager.textureResolution
								- tileView, (int) camera.position.y
								/ GameManager.textureResolution + tileView);
			} else {
				DrawTiles(ModeManager.currentMode.world.zMin,
						ModeManager.currentMode.world.zMax,
						(int) camera.position.x / GameManager.textureResolution
								- tileView, (int) camera.position.x
								/ GameManager.textureResolution + tileView,
						(int) camera.position.y / GameManager.textureResolution
								- tileView, (int) camera.position.y
								/ GameManager.textureResolution + tileView);
				DrawEntities(ModeManager.currentMode.world.zMin,
						ModeManager.currentMode.world.zMax,
						(int) camera.position.x / GameManager.textureResolution
								- tileView, (int) camera.position.x
								/ GameManager.textureResolution + tileView,
						(int) camera.position.y / GameManager.textureResolution
								- tileView, (int) camera.position.y
								/ GameManager.textureResolution + tileView);
			}
		}
		game.batch.end();
		InputRequest r = new InputRequest();
		if (Gdx.input.isKeyPressed(Keys.W)) {
			r.x = 0;
			r.y = 1;
			PacketGenerator.generatePacket(r);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			r.x = 0;
			r.y = -1;
			PacketGenerator.generatePacket(r);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			r.x = 1;
			r.y = 0;
			PacketGenerator.generatePacket(r);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			r.x = -1;
			r.y = 0;
			PacketGenerator.generatePacket(r);
		}
	}

	private void DrawEntities(int zMin, int zMax, int xMin, int xMax, int yMin,
			int yMax) {
		for (Entity e : ModeManager.currentMode.entities) {
			if (e != null
					&& (!ModeManager.currentMode.world.isHidden(e) || ModeManager.currentMode.world
							.isHidden(e))) {
				if (!(e.id == Client.controlledEntity.id)) {
					game.batch
							.draw(GameManager.getEntityTexture(e.type),
									e.position.x
											* GameManager.textureResolution,
									(e.position.y * GameManager.textureResolution)
											+ (e.position.z * GameManager.textureResolution));
					GameManager.getLocaleFont().draw(game.batch, e.caption,
							e.position.x * GameManager.textureResolution,
							e.position.y * GameManager.textureResolution - 20);

				}
			}

		}
		if (Client.controlledEntity != null) {

			game.batch
					.draw(GameManager
							.getEntityTexture(Client.controlledEntity.type),
							Client.controlledEntity.position.x
									* GameManager.textureResolution,
							(Client.controlledEntity.position.y * GameManager.textureResolution)
									+ (Client.controlledEntity.position.z * GameManager.textureResolution));
			GameManager.getLocaleFont().draw(
					game.batch,
					Client.controlledEntity.caption,
					Client.controlledEntity.position.x
							* GameManager.textureResolution,
					Client.controlledEntity.position.y
							* GameManager.textureResolution - 20);
		}
	}

	private void DrawTiles(int zMin, int zMax, int xMin, int xMax, int yMin,
			int yMax) {
		Tile t;
		for (int z = zMin; z <= zMax; ++z) {
			for (int x = xMin; x <= xMax; ++x) {
				for (int y = yMax; y >= yMin; --y) {

					t = ModeManager.currentMode.world.tiles.get(new Vector3(x,
							y, z));

					if (t != null) {
						if (!ModeManager.currentMode.world.isHidingEntity(
								Client.controlledEntity, t)) {
							game.batch.setColor(1, 1, 1, 1f);
							game.batch
									.draw(GameManager.getTexture(t.type),
											x * GameManager.textureResolution,
											(y * GameManager.textureResolution)
													+ (z * GameManager.textureResolution));
						} else {
							game.batch.setColor(1, 1, 1, 0.5f);

							game.batch
									.draw(GameManager.getTexture(t.type),
											x * GameManager.textureResolution,
											(y * GameManager.textureResolution)
													+ (z * GameManager.textureResolution));
						}
					}
				}
			}
		}

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		game.dispose();
	}

}
