package com.alastar.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.alastar.game.enums.EntityType;
import com.alastar.game.enums.ModeType;
import com.alastar.game.enums.Race;
import com.alastar.game.enums.TileType;
import com.alastar.game.lang.Entry;
import com.alastar.game.lang.EntryManager;
import com.alastar.game.lang.Language;
import com.alastar.game.lang.LanguageManager;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class GameManager {

	public static int fontSize = 15;
	public static Texture grass;
	public static Texture stone;
	public static Texture swamp;
	public static Texture lava;
	public static Texture water;
	public static Texture brick;

	public static Texture elf;
	public static Texture human;
	public static Texture shaolin;
	public static Texture orc;
	public static Texture zombie;
	public static Texture skeleton;
	public static Texture wolf;

	public static Texture background;

	public static Skin skin;

	public static TextButtonStyle txtBtnStyle;
	public static LabelStyle labelStyle;
	public static TextFieldStyle txtFieldStyle;

	public static int textureResolution = 32;

	public static Mode[] modes = new Mode[10];
	public static String lang = "ru.txt";
	public static int fieldOfTransparency = 3;

	public static Mode getMode(ModeType t) {
		for (int i = 0; i < modes.length; ++i) {
			if (modes[i] != null) {
				if (modes[i].type == t)
					return modes[i];
			}
		}
		return null;
	}

	public static void LoadContent() {
		System.out.println("Loading languages...");
		LoadLanguage();
		modes[0] = new Mode(ModeType.World);
		modes[1] = new Mode(ModeType.Battle);
		ModeManager.PushMode(modes[0]);
		////////////
		//TEXTURES//
		////////////
		grass = new Texture(Gdx.files.internal("textures/tiles/grass.png"));
		water = new Texture(Gdx.files.internal("textures/tiles/Water.png"));
		stone = new Texture(Gdx.files.internal("textures/tiles/Stone.png"));
		lava = new Texture(Gdx.files.internal("textures/tiles/Lava.png"));
		swamp = new Texture(Gdx.files.internal("textures/tiles/Swamp.png"));
		brick = new Texture(Gdx.files.internal("textures/tiles/bricks.png"));

		shaolin = new Texture(
				Gdx.files.internal("textures/entities/shaolin.png"));
		human = new Texture(Gdx.files.internal("textures/entities/human.png"));
		orc = new Texture(Gdx.files.internal("textures/entities/orc.png"));
		elf = new Texture(Gdx.files.internal("textures/entities/elf.png"));
		wolf = new Texture(Gdx.files.internal("textures/entities/wolf.png"));
		skeleton = new Texture(
				Gdx.files.internal("textures/entities/skeleton.png"));
		zombie = new Texture(Gdx.files.internal("textures/entities/zombie.png"));

		background = new Texture(
				Gdx.files.internal("textures/gui/TheErderBackground.png"));

		////////////////
		//MUSIC/SOUNDS//
		////////////////
		//dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		//rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		//rainMusic.setLooping(true);

		/////////
		//SKINS//
		/////////
		Skin skin = new Skin();
		skin.add("lbutton", new Texture("textures/gui/lbutton.jpg"));
		skin.add("lwindow", new Texture("textures/gui/lwindow.png"));
		skin.add("ltextBox", new Texture("textures/gui/ltextBox.png"));

		txtBtnStyle = new TextButtonStyle();
		txtBtnStyle.font = getLocaleFont();
		txtBtnStyle.up = skin.getDrawable("lbutton");
		txtBtnStyle.down = skin.getDrawable("lbutton");

		labelStyle = new LabelStyle();
		labelStyle.font = getLocaleFont();

		txtFieldStyle = new TextFieldStyle();
		txtFieldStyle.font = getLocaleFont();
		txtFieldStyle.fontColor = new Color(1, 1, 1, 1);
		txtFieldStyle.background = skin.getDrawable("ltextBox");
		System.out.println("gameManager load content done!");

	}

	@SuppressWarnings("deprecation")
	private static void LoadLanguage() {
		try {
			FileHandle[] files = getLocaleDir();
			FileHandle[] fontTxtFiles = getFontTxtDir();
			FileHandle[] fontFiles = getFontDataDir();

			System.out.println("files amt: " + files.length);
			
			LanguageManager.relaunchLanguageManager(1);

			Language l;
			EntryManager eM;

			File fontTxtFile = null;
			FileHandle fontFile = null;
			File langFile;
			FileReader fr;
			BufferedReader br;
			BitmapFont nFont;

			String s = "";
			String allowedCharacters = "";
			String line = "";
			String fontName = "";

			final ArrayList<String> lines = new ArrayList<String>();
			System.out.println("txt fonts files: " + fontTxtFiles.length);

			for (int i = 0; i < files.length; ++i) {

				langFile = files[i].file();
				System.out.println("file name: " + langFile.getName());

				if (langFile.getName().equals(lang)) {
					for (int f = 0; f < fontTxtFiles.length; ++f) {
						System.out.println(fontTxtFiles[f].name());
						if (fontTxtFiles[f].name().equals(langFile.getName())) {
							fontTxtFile = fontTxtFiles[f].file();
							break;
						}
					}
					 System.out.println("file name -  " + langFile.getName());

					fr = new FileReader(langFile);
					br = new BufferedReader(fr);

					while ((s = br.readLine()) != null) {
						lines.add(s);
					}

					br.close();

					fr.close();

					fr = new FileReader(fontTxtFile);
					br = new BufferedReader(fr);
					allowedCharacters = br.readLine();
					fontName = br.readLine();

					br.close();
					fr.close();

					for (int f = 0; f < fontFiles.length; ++f) {
						if (fontFiles[f].name().equals(fontName)) {
							fontFile = fontFiles[f];
							break;
						}
					}

					// System.out.println("Allowed chars for " +
					// langFile.getName()
					// + " are " + allowedCharacters);
					eM = new EntryManager(lines.size());

					// /////////////////
					// LOADING ENTRIES//
					// /////////////////
					// System.out.println(lines.size());
					for (int j = 0; j < lines.size(); ++j) {
						line = lines.get(j);
						// System.out.println(line + " j = " + j);
						eM.addEntry(new Entry(line.split("=")[0], line
								.split("=")[1]));
					}
					FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
							fontFile);
					nFont = generator.generateFont(fontSize, allowedCharacters,
							false);
					l = new Language(langFile.getName(), eM, nFont);
					LanguageManager.addLang(l);
					System.out.println(l.langName + " added! ");
					lines.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BitmapFont getLocaleFont() {
		return LanguageManager.getLang(lang).getFont();
	}

	public static FileHandle[] getFontDataDir() {
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			return Gdx.files.absolute(
					System.getProperty("user.dir") + "\\bin\\data\\fonts\\")
					.list();
		} else if (Gdx.app.getType() == ApplicationType.Android) {
			return Gdx.files.internal("data/fonts/").list();
		}
		return null;
	}

	public static FileHandle[] getWorldsDataDir() {
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			return Gdx.files.absolute(
					System.getProperty("user.dir") + "\\bin\\data\\worlds\\")
					.list();
		} else if (Gdx.app.getType() == ApplicationType.Android) {
			return Gdx.files.internal("data/worlds/").list();
		}
		return null;
	}

	public static String getWorldsDataPath() {
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			return System.getProperty("user.dir") + "\\data\\worlds\\";
		} else if (Gdx.app.getType() == ApplicationType.Android) {
			return "data/worlds/";
		}
		return lang;
	}

	public static FileHandle[] getFontTxtDir() {
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			System.out.println(System.getProperty("user.dir")
					+ "\\bin\\fonts\\");
			return Gdx.files.absolute(
					System.getProperty("user.dir") + "\\bin\\fonts\\").list();
		} else if (Gdx.app.getType() == ApplicationType.Android) {
			return Gdx.files.internal("fonts/").list();
		}
		return null;
	}

	public static FileHandle[] getLocaleDir() {
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			System.out.println(System.getProperty("user.dir")
					+ "\\bin\\languages\\");
			return Gdx.files.absolute(
					System.getProperty("user.dir") + "\\bin\\languages").list();
		} else if (Gdx.app.getType() == ApplicationType.Android) {
			return Gdx.files.internal("languages/").list();
		}
		return null;
	}

	public static Texture getTexture(TileType t) {
		switch (t) {
		case Grass:
			return grass;
		case Brick:
			return brick;
		case Lava:
			return lava;
		case Stone:
			return stone;
		case Swamp:
			return swamp;
		case Water:
			return water;
		default:
			return grass;

		}
	}

	public static Texture getPlayerTexture(Race t) {
		return grass;
	}

	public static String getLocalizedMessage(int res) {
		try {
			return LanguageManager.getLang(lang).getLangStringById(res).strValue;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getLocalizedMessage(String res) {
		try {
			return LanguageManager.getLang(lang).getLangByString(res).strValue;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Vector3 vectorDifference(Vector3 f, Vector3 w) {
		return new Vector3(f.x - w.x, f.y - w.y, f.z - w.z);
	}

	public static Texture getEntityTexture(EntityType type) {
		switch (type) {
		case Human:
			return human;
		case Elf:
			return elf;
		case Orc:
			return orc;
		case Shaolin:
			return shaolin;
		case Skeleton:
			return skeleton;
		case Wolf:
			return wolf;
		case Zombie:
			return zombie;
		default:
			return human;
		}
	}

}