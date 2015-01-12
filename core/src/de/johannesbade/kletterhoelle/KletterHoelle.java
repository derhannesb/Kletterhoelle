package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class KletterHoelle extends ApplicationAdapter implements InputProcessor, ContactListener{
	private SpriteBatch batch;
	private OrthographicCamera camera = null;
	private GameContext context = null;
	private Fenster fenster1, fenster2, fenster3;
	private Stickman stickman1 = null;
	private Box2DDebugRenderer debugRenderer;
	
	@Override
	public void create () {
		context = new GameContext();
		batch = new SpriteBatch();
		fenster1 = new Fenster(context, 276, 766, 1950, 220);
		fenster2 = new Fenster(context, 1111, 240,1140,220);
		fenster3 = new Fenster(context,-728, 110,1141,220);
		
		context.getStage().addActor(fenster1);
		context.getStage().addActor(fenster2);
		context.getStage().addActor(fenster3);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.y += 200;
		stickman1 = new Stickman(context,1200, 1200);
		context.getStage().addActor(stickman1);

		context.getWorld().setContactListener(this);
		
		Gdx.input.setInputProcessor(this);
		
		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		context.getStage().getCamera().position.x = camera.position.x;
		context.getStage().getCamera().position.y = camera.position.y;
		context.getWorld().step(Gdx.graphics.getDeltaTime(), 6, 2);
		context.getStage().act();	
		
	    Matrix4 cam = context.getStage().getCamera().combined.cpy();
		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			context.getStage().draw();
			
		batch.end();
		
		debugRenderer.render(context.getWorld(), cam.scl(GameContext.PIXELSPERMETER));
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
		}
		
		stickman1.key(keycode, true);
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		stickman1.key(keycode, false);
		
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

	@Override
	public void beginContact(Contact contact) {
		if (contact.isTouching())
		{
			if (contact.getFixtureA() == stickman1.getSensorFixture())
			{
				if (contact.getFixtureB().getBody().getPosition().y < stickman1.getBody().getPosition().y) stickman1.setGrounded(true);
			}
			if (contact.getFixtureB() == stickman1.getSensorFixture())
			{
				if (contact.getFixtureA().getBody().getPosition().y < stickman1.getBody().getPosition().y) stickman1.setGrounded(true);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		if (contact.isTouching())
		{
			if (contact.getFixtureA() == stickman1.getSensorFixture())
			{
				if (contact.getFixtureB().getBody().getPosition().y <= stickman1.getBody().getPosition().y) stickman1.setGrounded(false);
			}
			if (contact.getFixtureB() == stickman1.getSensorFixture())
			{
				if (contact.getFixtureA().getBody().getPosition().y <= stickman1.getBody().getPosition().y) stickman1.setGrounded(false);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
