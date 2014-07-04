package com.alastar.game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.alastar.main.net.requests.AuthPacketRequest;
import ru.alastar.main.net.requests.CharacterChooseRequest;
import ru.alastar.main.net.requests.CreateCharacterRequest;
import ru.alastar.main.net.requests.RegistrationPacketRequest;
import ru.alastar.net.Client;
import ru.alastar.net.PacketGenerator;

import com.alastar.game.enums.MenuState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

	final ErderGame game;

	Stage LoginStage;
	Stage RegisterStage;

	Stage stageCreate;
	Stage stageChoose;
	public static Label nameLabel1;
	// public static Image back;
	public int id = 0;

	OrthographicCamera camera;
	MenuState state = MenuState.Login;

	public MainMenuScreen(final ErderGame gam) {

		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(new Runnable() {
			public void run() {
				Client.Connect();
			}
		});

		Gdx.app.log("Erder Game", "Main Menu Screen");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 750 / Vars.balancedScreenWidth, 400 / Vars.balancedScreenHeight);
		game = gam;

		LoginStage = new Stage();
		Gdx.input.setInputProcessor(LoginStage);

		// back = new Image(GameManager.background);
		final TextButton btn = new TextButton(
				GameManager.getLocalizedMessage("Login"),
				GameManager.txtBtnStyle);
		btn.setPosition(700 / Vars.balancedScreenWidth,
				700 / Vars.balancedScreenHeight);
		btn.setWidth(175 / Vars.balancedScreenWidth);
		btn.padLeft(5);

		final TextButton btnToReg = new TextButton(
				GameManager.getLocalizedMessage("Reg"), GameManager.txtBtnStyle);
		btnToReg.setPosition(700 / Vars.balancedScreenWidth,
				750 / Vars.balancedScreenHeight);
		btnToReg.setWidth(175 / Vars.balancedScreenWidth);
		btnToReg.padLeft(5);
		Label nameLabel = new Label(GameManager.getLocalizedMessage("Login")
				+ ":", GameManager.labelStyle);
		nameLabel.setPosition(700 / Vars.balancedScreenWidth,
				950 / Vars.balancedScreenHeight);

		final TextField nameText = new TextField("Alastar",
				GameManager.txtFieldStyle);
		nameText.setPosition(700 / Vars.balancedScreenWidth,
				900 / Vars.balancedScreenHeight);
		nameText.setWidth(175 / Vars.balancedScreenWidth);

		Label addressLabel = new Label(
				GameManager.getLocalizedMessage("Password") + ":",
				GameManager.labelStyle);
		addressLabel.setPosition(700 / Vars.balancedScreenWidth,
				850 / Vars.balancedScreenHeight);

		final TextField passwordText = new TextField("123",
				GameManager.txtFieldStyle);
		passwordText.setPosition(700 / Vars.balancedScreenWidth,
				800 / Vars.balancedScreenHeight);
		passwordText.setWidth(175 / Vars.balancedScreenWidth);

		LoginStage.addActor(nameLabel);
		LoginStage.addActor(nameText);
		LoginStage.addActor(addressLabel);
		LoginStage.addActor(passwordText);
		LoginStage.addActor(btn);
		LoginStage.addActor(btnToReg);

		btn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AuthPacketRequest r = new AuthPacketRequest();
				r.login = nameText.getText();
				r.pass = passwordText.getText();
				PacketGenerator.generatePacket(r);
			}
		});

		btnToReg.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = MenuState.Register;
				Gdx.input.setInputProcessor(RegisterStage);

			}
		});

		RegisterStage = new Stage();

		final TextButton btnToLog = new TextButton(
				GameManager.getLocalizedMessage("Login"),
				GameManager.txtBtnStyle);
		btnToLog.setPosition(700 / Vars.balancedScreenWidth,
				650 / Vars.balancedScreenHeight);
		btnToLog.setWidth(175 / Vars.balancedScreenWidth);

		final TextButton btnReg = new TextButton(
				GameManager.getLocalizedMessage("Reg"), GameManager.txtBtnStyle);
		btnReg.setPosition(700 / Vars.balancedScreenWidth,
				600 / Vars.balancedScreenHeight);
		btnReg.setWidth(175 / Vars.balancedScreenWidth);

		Label loginLabel = new Label(GameManager.getLocalizedMessage("Login")
				+ ":", GameManager.labelStyle);
		loginLabel.setPosition(700 / Vars.balancedScreenWidth,
				950 / Vars.balancedScreenHeight);

		final TextField loginText = new TextField("Login",
				GameManager.txtFieldStyle);
		loginText.setPosition(700 / Vars.balancedScreenWidth,
				900 / Vars.balancedScreenHeight);
		loginText.setWidth(175 / Vars.balancedScreenWidth);

		Label passLabel = new Label(GameManager.getLocalizedMessage("Password")
				+ ":", GameManager.labelStyle);
		passLabel.setPosition(700 / Vars.balancedScreenWidth,
				850 / Vars.balancedScreenHeight);

		final TextField passText = new TextField("Pass",
				GameManager.txtFieldStyle);
		passText.setPosition(700 / Vars.balancedScreenWidth,
				800 / Vars.balancedScreenHeight);
		passText.setWidth(175 / Vars.balancedScreenWidth);

		Label mailLabel = new Label("E-Mail:", GameManager.labelStyle);
		mailLabel.setPosition(700 / Vars.balancedScreenWidth,
				750 / Vars.balancedScreenHeight);

		final TextField mailText = new TextField("Mail",
				GameManager.txtFieldStyle);
		mailText.setPosition(700 / Vars.balancedScreenWidth,
				700 / Vars.balancedScreenHeight);
		mailText.setWidth(175 / Vars.balancedScreenWidth);

		RegisterStage.addActor(btnToLog);
		RegisterStage.addActor(btnReg);
		RegisterStage.addActor(loginLabel);
		RegisterStage.addActor(loginText);
		RegisterStage.addActor(passLabel);
		RegisterStage.addActor(passText);
		RegisterStage.addActor(mailLabel);
		RegisterStage.addActor(mailText);

		btnToLog.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = MenuState.Login;
				Gdx.input.setInputProcessor(LoginStage);

			}
		});
		btnReg.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RegistrationPacketRequest r = new RegistrationPacketRequest();
				r.login = loginText.getText();
				r.pass = passText.getText();
				r.mail = mailText.getText();

				PacketGenerator.generatePacket(r);
			}
		});
		stageChoose = new Stage();

		String n = "";

		nameLabel1 = new Label(n, GameManager.labelStyle);
		nameLabel1.setPosition(700 / Vars.balancedScreenWidth,
				960 / Vars.balancedScreenHeight);

		final TextButton btnN = new TextButton(">", GameManager.txtBtnStyle);
		btnN.setPosition(765 / Vars.balancedScreenWidth,
				900 / Vars.balancedScreenHeight);

		final TextButton btnP = new TextButton("<", GameManager.txtBtnStyle);
		btnP.setPosition(725 / Vars.balancedScreenWidth,
				900 / Vars.balancedScreenHeight);

		final TextButton btnCh = new TextButton(
				GameManager.getLocalizedMessage("Choose"),
				GameManager.txtBtnStyle);
		btnCh.setPosition(700 / Vars.balancedScreenWidth,
				850 / Vars.balancedScreenHeight);
		btnCh.setWidth(175 / Vars.balancedScreenWidth);

		final TextButton btnCr = new TextButton(
				GameManager.getLocalizedMessage("Create"),
				GameManager.txtBtnStyle);
		btnCr.setPosition(700 / Vars.balancedScreenWidth,
				800 / Vars.balancedScreenHeight);
		btnCr.setWidth(175 / Vars.balancedScreenWidth);

		final TextButton btnDel = new TextButton(
				GameManager.getLocalizedMessage("Delete"),
				GameManager.txtBtnStyle);
		btnDel.setPosition(700 / Vars.balancedScreenWidth,
				750 / Vars.balancedScreenHeight);
		btnDel.setWidth(175 / Vars.balancedScreenWidth);

		stageChoose.addActor(nameLabel1);

		stageChoose.addActor(btnP);

		stageChoose.addActor(nameLabel1);

		stageChoose.addActor(btnN);

		stageChoose.addActor(btnCr);
		stageChoose.addActor(btnDel);
		stageChoose.addActor(btnCh);

		btnCh.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Client.characters.size() > 0) {
					CharacterChooseRequest r = new CharacterChooseRequest();
					r.nick = nameLabel1.getText().toString();
					PacketGenerator.generatePacket(r);
					dispose();
				}
			}
		});

		btnP.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Client.characters.size() > 0) {

					if ((id - 1) < 0) {
						id = Client.characters.size() - 1;
					} else {
						--id;
					}

					nameLabel1.setText((String)Client.characters.keySet().toArray()[id]);
				}
			}
		});

		btnN.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Client.characters.size() > 0) {
					if ((id + 1) >= Client.characters.size()) {
						id = 0;
					} else {
						++id;
					}

					nameLabel1.setText((String)Client.characters.keySet().toArray()[id]);
				}
			}
		});

		btnCr.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = MenuState.CharacterCreate;
			}
		});

		stageCreate = new Stage();

		Label nickLabel = new Label(GameManager.getLocalizedMessage("Name")
				+ ":", GameManager.labelStyle);
		nickLabel.setPosition(700 / Vars.balancedScreenWidth,
				900 / Vars.balancedScreenHeight);

		final TextField nickText = new TextField("Alastar",
				GameManager.txtFieldStyle);
		nickText.setPosition(700 / Vars.balancedScreenWidth,
				850 / Vars.balancedScreenHeight);
		nickText.setWidth(175 / Vars.balancedScreenWidth);

		final Label raceLabel1 = new Label(com.alastar.game.enums.EntityType.values()[0].name(),
				GameManager.labelStyle);
		raceLabel1.setPosition(700 / Vars.balancedScreenWidth,
				800 / Vars.balancedScreenHeight);

		final TextButton btnRP = new TextButton("<", GameManager.txtBtnStyle);
		btnRP.setPosition(725 / Vars.balancedScreenWidth,
				750 / Vars.balancedScreenHeight);

		final TextButton btnRN = new TextButton(">", GameManager.txtBtnStyle);
		btnRN.setPosition(765 / Vars.balancedScreenWidth,
				750 / Vars.balancedScreenHeight);

		final TextButton btnCreate = new TextButton(
				GameManager.getLocalizedMessage("Confirm"),
				GameManager.txtBtnStyle);
		btnCreate.setPosition(700 / Vars.balancedScreenWidth,
				700 / Vars.balancedScreenHeight);
		btnCreate.setWidth(175 / Vars.balancedScreenWidth);

		final TextButton btnBack = new TextButton(
				GameManager.getLocalizedMessage("Back"),
				GameManager.txtBtnStyle);
		btnBack.setPosition(700 / Vars.balancedScreenWidth,
				650 / Vars.balancedScreenHeight);
		btnBack.setWidth(175 / Vars.balancedScreenWidth);

		stageCreate.addActor(nickLabel);
		stageCreate.addActor(nickText);
		stageCreate.addActor(btnRP);
		stageCreate.addActor(raceLabel1);
		stageCreate.addActor(btnRN);
		stageCreate.addActor(btnCreate);
		stageCreate.addActor(btnBack);

		btnRP.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if ((id - 1) < 1)
					id = com.alastar.game.enums.EntityType.values().length;
				else
					--id;

				raceLabel1.setText((com.alastar.game.enums.EntityType.values()[id - 1]).name());
			}
		});

		btnRN.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if ((id + 1) > com.alastar.game.enums.EntityType.values().length)
					id = 1;
				else
					++id;

				raceLabel1.setText((com.alastar.game.enums.EntityType.values()[id - 1]).name());
			}
		});

		btnBack.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				state = MenuState.CharacterChoose;
			}
		});

		btnCreate.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				CreateCharacterRequest r = new CreateCharacterRequest();
				r.nick = nickText.getText().toString();
				r.type = com.alastar.game.enums.EntityType.valueOf(raceLabel1.getText().toString());
				PacketGenerator.generatePacket(r);
				id = 0;
				state = MenuState.CharacterChoose;

			}
		});
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
	//	game.batch.begin();
	//	game.batch.draw(GameManager.background, -100, -100,
	//			GameManager.background.getWidth(),
	//			GameManager.background.getHeight());
	//	game.batch.end();
		switch (state) {
		case Register:
			RegisterStage.act(Gdx.graphics.getDeltaTime());
			RegisterStage.draw();
			Table.drawDebug(RegisterStage);
			break;
		case Login:
			LoginStage.act(Gdx.graphics.getDeltaTime());
			LoginStage.draw();
			Table.drawDebug(LoginStage);
			break;
		case CharacterChoose:
			stageChoose.act(Gdx.graphics.getDeltaTime());
			stageChoose.draw();
			Gdx.input.setInputProcessor(stageChoose);
			Table.drawDebug(stageChoose);
			break;
		case CharacterCreate:
			stageCreate.act(Gdx.graphics.getDeltaTime());
			stageCreate.draw();
			Gdx.input.setInputProcessor(stageCreate);
			Table.drawDebug(stageCreate);
			break;
		default:
			break;
		}

	}

	@Override
	public void resize(int width, int height) {
		switch (state) {
		case Register:
			RegisterStage.getViewport().update(width, height, true);
			break;
		case Login:
			LoginStage.getViewport().update(width, height, true);
			break;
		case CharacterChoose:
			stageChoose.getViewport().update(width, height, true);
			break;
		case CharacterCreate:
			stageCreate.getViewport().update(width, height, true);
			break;
		default:
			break;
		}
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
		LoginStage.dispose();
		RegisterStage.dispose();
		stageCreate.dispose();
		stageChoose.dispose();
	}

}
