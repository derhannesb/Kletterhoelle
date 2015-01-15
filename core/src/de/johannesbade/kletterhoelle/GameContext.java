package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class GameContext {
	
	public static final float GAME_SPEED =15;
	public static final float GRAVITY = -50f;
	public static final float PIXELSPERMETER = 100;
	public static final int CONTROLLER_KEYBOARD = 11111;
	public static final int POV_CENTER = -90000;
	private TextureAtlas atlas = null;
	private World world = null;
	private Stage stage = null;
	private Array<GameObject> gameObjects = null;
	private float timeElapsed = 0f;
	private BitmapFont font = null;
	
	
	public GameContext() {
		stage = new Stage();
		world = new World(new Vector2(0,GameContext.GRAVITY), true);
		gameObjects = new Array<GameObject>();
		atlas = new TextureAtlas("atlas/kletterhoelle.pack");
		timeElapsed = 0f;
		
		
		font = new BitmapFont(Gdx.files.internal("fonts/roboto.fnt"));
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 28;
		font = gen.generateFont(parameter);
		
        font.setColor(Color.GREEN);
        
		//font = new BitmapFont(Gdx.files.internal("fonts/SHOWG-24.fnt"));
		//font.setColor(Color.BLACK);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Array<GameObject> getGameObjects() {
		return gameObjects;
	}

	public void setGameObjects(Array<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public TextureAtlas getAtlas() {
		return atlas;
	}

	public void setAtlas(TextureAtlas atlas) {
		this.atlas = atlas;
	}

	public float getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(float timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public BitmapFont getFont() {
		return font;
	}

	public void setFont(BitmapFont font) {
		this.font = font;
	}

}
