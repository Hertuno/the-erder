package ru.alastar.mapeditor.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.JFileChooser;

import com.alastar.game.enums.*;
import com.alastar.game.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class EditorScreen implements Screen {
	OrthographicCamera camera;
     
	HashMap<Vector3, Tile> tiles = new HashMap<Vector3, Tile>();
	World world;
	public TileType selectedType = TileType.Grass;
	MapEditor map;
	int index = 0;
    BitmapFont font;
	public boolean switchPos = false;
	public Vector3 fPos = null;
	public Vector3 sPos = null;
	final JFileChooser load;
	final JFileChooser save;
	public boolean passableState = true;
	public boolean physiXView = false;

	public EditorScreen(MapEditor map)
	{
		this.map = map;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 500, 500);
		font = GameManager.getFont();
		Gdx.input.setCursorCatched(true);
		Gdx.input.setInputProcessor(new InputProcessor(){
        
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.S)
				{
					int ret = save.showSaveDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {					
						SaveWorld(save.getCurrentDirectory()+ "/world.bin");
					}
				}
				if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.I)
				{
					int ret = load.showOpenDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
					    LoadWorld(load.getSelectedFile());
					}	
				}
				if(keycode == Keys.P)
				{
					physiXView = !physiXView;
				}
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
				if(amount > 0)
				{
					if(index + 1 <= TileType.values().length){
					++index;}
					else
						index = 1;
				}
				else if(amount < 0)
				{
					if(index - 1 > 0)
					--index;
					else
						index = TileType.values().length;
				}
				selectedType = TileType.values()[index - 1];
				System.out.println(selectedType);
				return true;
			}
			
		});

	    load = new JFileChooser();
		save = new JFileChooser();

	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = map.positionX;
        camera.position.y = map.positionY;
        camera.position.z = 0;
		camera.update();
		map.batch.setProjectionMatrix(camera.combined);
		map.batch.begin();
		Vector3 v;
	    
		for(int z = zMin; z < zMax; ++z){
		for(int x = xMin; x < xMax; ++x)
		{
			for(int y = yMin; y < yMax; ++y)
			{
			    v = new Vector3(x,y,z);
				if(tiles.get(v) != null){
				    map.batch.draw(GameManager.getTexture(tiles.get(v).type), x * GameManager.texWidth, y * GameManager.texHeight);
				}
		    }
		}
		}
		if(physiXView){
			for(int x = 0; x < 1000; ++x)
			{
				for(int y = 0; y < 1000; ++y)
				{
					if(tiles[x][y] != null){
			if(tiles[x][y].passable)
				map.batch.draw(GameManager.notpassable, tiles[x][y].position.x * 8, tiles[x][y].position.y * 8);
			else
				map.batch.draw(GameManager.passable, tiles[x][y].position.x * 8, tiles[x][y].position.y * 8);
		}}}
		}
		Vector3 tr = camera.unproject(new Vector3((int)Gdx.input.getX(), (int)Gdx.input.getY(), 0));
		map.batch.draw(GameManager.getTexture(selectedType), (int)tr.x - 4,(int)tr.y - 4);
		
		if(switchPos)
		{
			font.draw(map.batch, "Second Pos(x:" + (int)tr.x / 8 + ",y:" + (int)tr.y /8 + ") P: " + passableState, tr.x - 4, tr.y - 4);
		}
		else
		{
			font.draw(map.batch, "First Pos(x:" + (int)tr.x / 8 + ",y:" + (int)tr.y /8 + ") P: " + passableState, tr.x - 4, tr.y - 4);

		}
		map.batch.end();
		

			Vector3 tr1 = camera.unproject(new Vector3((int)Gdx.input.getX(), (int)Gdx.input.getY(), 0));

			if(Gdx.input.isButtonPressed(Buttons.LEFT)){
				if(tr1.x > 0 && tr1.y > 0){
			       tiles[(int) (tr1.x / 8)][(int) (tr1.y / 8)] = (new Tile(new Vector2((int)tr1.x / 8,(int)tr1.y / 8), selectedType, passableState));
		          System.out.println("Set tile at " + tr1.x / 8 + " " + tr1.y / 8);
				}
			}

			if(Gdx.input.justTouched())
			{
			if(Gdx.input.isButtonPressed(Buttons.RIGHT))
			{
				if(fPos == null){
					fPos = tr1;
					switchPos = true;
					}
				else if(sPos == null){
					sPos = tr1;
                    Fill();
					sPos = null;
					fPos = null;
					switchPos = false;
				}
					
			}
			if(Gdx.input.isButtonPressed(Buttons.MIDDLE))
			{
			 passableState = !passableState;	
			}
		}
	}
	
	public void Fill()
	{
		Vector3 up = sPos;
		Vector3 down = sPos;
		Vector3 left = sPos;
		Vector3 right = sPos;
		if(fPos.y > sPos.x)
		{
			up = fPos;
			down = sPos;
		}
		else if(fPos.y < sPos.x)
		{
			up = sPos;
			down = fPos;
		}

		if(fPos.x > sPos.x)
		{
			right = fPos;
			left = sPos;
		}
		else if(fPos.x < sPos.x)
		{
			right = sPos;
			left = fPos;
		}

		for(int x = (int) left.x; x <= right.x; ++x)
		{
			for(int y = (int)down.y; y <= up.y; ++y)
			{
				if(x > 0 && y > 0)
				tiles[x / 8][y / 8] = (new Tile(new Vector2(x / 8,y / 8), selectedType, passableState));
			}
		}
	}
	
	private void LoadWorld(File selectedFile) {
		try {
			FileInputStream f_in = new FileInputStream(selectedFile);
			ObjectInputStream obj_in = new ObjectInputStream (f_in);
			   tiles = (Tile[][])obj_in.readObject();
               obj_in.close();
               f_in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private void SaveWorld(String path)
	{
		try {

		FileOutputStream f_out = new FileOutputStream(path);
		ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
				obj_out.writeObject(tiles);
				System.out.println("Write tiles array list");
				obj_out.close();
				f_out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

}
