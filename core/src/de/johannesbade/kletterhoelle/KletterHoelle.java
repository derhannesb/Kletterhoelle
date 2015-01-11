package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KletterHoelle extends ApplicationAdapter implements InputProcessor{
	private SpriteBatch batch;
	private OrthographicCamera camera = null;
	private GameContext context = null;
	private Fenster fenster1, fenster2, fenster3;
	private Stickman stickman1 = null;
	
	@Override
	public void create () {
		context = new GameContext();
		batch = new SpriteBatch();
		fenster1 = new Fenster(context);
		fenster1.setBounds(276, 766, 1950, 220);
		fenster2 = new Fenster(context);
		fenster2.setBounds(1111, 240,1140,220);
		fenster3 = new Fenster(context);
		fenster3.setBounds(-728, 110,1141,220);
		
		context.getStage().addActor(fenster1);
		context.getStage().addActor(fenster2);
		context.getStage().addActor(fenster3);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.y += 200;
		stickman1 = new Stickman(context);
		context.getStage().addActor(stickman1);
		stickman1.setY(1200);
		stickman1.setX(800);
		
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		context.getStage().getCamera().position.x = camera.position.x;
		context.getStage().getCamera().position.y = camera.position.y;
		
		context.getStage().act();	
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			context.getStage().draw();
		batch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode)
		{
			case Keys.LEFT: camera.position.x-=15;
			break;
			case Keys.RIGHT: camera.position.x+=15;
			break;
			case Keys.UP: camera.position.y+=15;
			break;
			case Keys.DOWN: camera.position.y-=15;
			break;
			case Keys.A: stickman1.move(Stickman.LEFT);
			break;
			case Keys.D: stickman1.move(Stickman.RIGHT);
			break;
			case Keys.W: stickman1.jump();
			break;
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode)
		{
			case Keys.A: stickman1.move(Stickman.STOP);
			break;
			case Keys.D: stickman1.move(Stickman.STOP);
			break;	
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
