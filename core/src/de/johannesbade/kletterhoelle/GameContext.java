package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class GameContext {
	
	public static final float GAME_SPEED =15;
	public static final float GRAVITY = -0.9f*GAME_SPEED;
	
	private Stage stage = null;
	private Array<GameObject> gameObjects = null;
	
	public GameContext() {
		stage = new Stage();
		gameObjects = new Array<GameObject>();
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
	
	

}
