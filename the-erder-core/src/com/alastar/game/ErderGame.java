package com.alastar.game;

import java.util.HashMap;
import java.util.Hashtable;

import ru.alastar.net.Client;

import com.alastar.game.enums.MenuState;
import com.alastar.game.enums.ModeType;
import com.alastar.game.lang.LanguageManager;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ErderGame extends Game {

	SpriteBatch batch;
	public static int screenWidth;
	public static int screenHeight;

	@Override
	public void create() {

		batch = new SpriteBatch();

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		Vars.balancedScreenHeight = 1024 / screenHeight;
		Vars.balancedScreenWidth = 1280 / screenWidth;
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Gdx.graphics.setDisplayMode(800, 800, false);
			Gdx.graphics.setDisplayMode(
					Gdx.graphics.getDesktopDisplayMode().width,
					Gdx.graphics.getDesktopDisplayMode().height, false);
			Gdx.graphics.setTitle("The Erder");
			Gdx.graphics.setVSync(true);
		}
		Client.game = this;
		try {
			Client.StartClient();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GameManager.LoadContent();

		this.setScreen(new MainMenuScreen(this));
	}

	public void SwitchScreen(MenuState m) {
		((MainMenuScreen) this.getScreen()).state = m;
	}

	@Override
	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		LanguageManager.dispose();
	}
}
