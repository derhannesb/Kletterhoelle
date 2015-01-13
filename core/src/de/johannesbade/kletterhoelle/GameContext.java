package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class GameContext {
	
	public static final float GAME_SPEED =15;
	public static final float GRAVITY = -50f;
	public static final float PIXELSPERMETER = 100;
	
	private TextureAtlas atlas = null;
	private World world = null;
	private Stage stage = null;
	private Array<GameObject> gameObjects = null;
	private float timeElapsed = 0f;
	
	public GameContext() {
		stage = new Stage();
		world = new World(new Vector2(0,GameContext.GRAVITY), true);
		gameObjects = new Array<GameObject>();
		atlas = new TextureAtlas("atlas/kletterhoelle.pack");
		timeElapsed = 0f;
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
	
	
	

}
