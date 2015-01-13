package de.johannesbade.kletterhoelle;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class KletterHoelle extends ApplicationAdapter implements InputProcessor, ContactListener, ControllerListener{
	private SpriteBatch batch;
	private OrthographicCamera camera = null;
	private OrthographicCamera cameraHUD = null;
	private GameContext context = null;
	
	private Box2DDebugRenderer debugRenderer;
	
	private boolean key_left_pressed = false;
	private boolean key_right_pressed = false;
	private boolean key_up_pressed = false;
	private boolean key_down_pressed = false;
	
	private Iterator<Body> itBody = null;
	
	private Array<Vector2> spawnPositions = null;
	
	private Array<Stickman> stickmen = null;
	private Iterator<Stickman> itStickman = null;
	
	public void spawnCoin()
	{
		Vector2 newPos = spawnPositions.get(MathUtils.random(spawnPositions.size-1));
		context.getStage().addActor(new Coin(context, newPos.x, newPos.y));
	}
	
	public void addPlayer()
	{
		Stickman stickman = new Stickman(context,MathUtils.random(200,1700), 1200);
		//if (stickmen.size > 0) stickman.setKeys(Keys.J, Keys.L, Keys.I, "Keyboard");
		stickmen.add(stickman);
		context.getStage().addActor(stickman);
	}
	
	public void addPlayer(int left, int right, int jump, int controllerID)
	{
		Stickman stickman = new Stickman(context,MathUtils.random(200,1700), 1200);
		if (stickmen.size > 0) stickman.setKeys(left, right, jump, controllerID);
		stickmen.add(stickman);
		context.getStage().addActor(stickman);
	}
	
	
	@Override
	public void create () {
		context = new GameContext();
		batch = new SpriteBatch();
		stickmen = new Array<Stickman>();
		
		
		
		spawnPositions = new Array<Vector2>();
		spawnPositions.add(new Vector2(1100,160+1006));
		spawnPositions.add(new Vector2(500,160+979));
		spawnPositions.add(new Vector2(1680,160+974));
		spawnPositions.add(new Vector2(1600,160+486));
		spawnPositions.add(new Vector2(838,160+555));
		spawnPositions.add(new Vector2(590,160+490));
		spawnPositions.add(new Vector2(296,160+343));
		
		//Fenster der Kletterhalle
		context.getStage().addActor(new Fenster(context, 276, 766, 1950, 220));
		context.getStage().addActor(new Fenster(context, 1111, 240,1140,220));
		context.getStage().addActor(new Fenster(context,-728, 110,1141,220));
		
		//Zusaetzliche Plattformen
		context.getStage().addActor(new MovingPlatform(context,540, 120,111,220,2));
		context.getStage().addActor(new MovingPlatform(context,-100, 500,180,220,3));
		context.getStage().addActor(new MovingPlatform(context,910, 120,150,220, 2));
		
		spawnCoin();

		//Spieler
		
		addPlayer();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.y += 200;
		
		cameraHUD = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		context.getWorld().setContactListener(this);
		
		Gdx.input.setInputProcessor(this);
		Controllers.addListener(this);
		
		debugRenderer = new Box2DDebugRenderer();
		
		
	}
	
	public void box2dstuff()
	{
		Array<Body> bodies = new Array<Body>();
		if (context.getWorld().getBodyCount() > 0)
		{
			context.getWorld().getBodies(bodies);
			itBody = bodies.iterator();
			if (bodies != null)
			{
				while (itBody.hasNext())
				{
					Body tmpBody = itBody.next();
					if (tmpBody.getUserData() instanceof GameObject)
					{
						GameObject go = (GameObject) tmpBody.getUserData();
						if (go.getType() == GameObject.TYPE_REMOVE) 
							{
								context.getWorld().destroyBody(tmpBody);
								spawnCoin();
							}
						if (go.getType() == GameObject.TYPE_PLAYER &&  go.getBody().getPosition().y  < -100)
						{
							go.getBody().setTransform(1200/GameContext.PIXELSPERMETER, 1500/GameContext.PIXELSPERMETER, 0);
							go.getBody().setLinearVelocity(0, 0);
						}
					}
				}
			}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1,1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		context.setTimeElapsed(context.getTimeElapsed()+Gdx.graphics.getDeltaTime());
		
		camera.update();
		context.getStage().getCamera().position.x = camera.position.x;
		context.getStage().getCamera().position.y = camera.position.y;
		
		box2dstuff(); // vor world.step ausfuehren!
		
		context.getWorld().step(Gdx.graphics.getDeltaTime(), 6, 2);
		context.getStage().act();	
		
	    Matrix4 cam = context.getStage().getCamera().combined.cpy();
		
		if (key_left_pressed) camera.position.x -= 3;
		if (key_right_pressed) camera.position.x += 3;
		if (key_up_pressed) camera.position.y += 3;
		if (key_down_pressed) camera.position.y -= 3;
	    
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			context.getStage().draw();
			
		batch.end();
		
		cameraHUD.update();
		batch.setProjectionMatrix(cameraHUD.combined);
		batch.begin();
		
		itStickman = stickmen.iterator();
		int snr = 0;
		while (itStickman.hasNext())
		{
			Stickman sTmp = itStickman.next();
			context.getFont().draw(batch,"P " +(snr++)+": "+sTmp.getScore(), 30, cameraHUD.viewportHeight-10 - (24*snr) );
		}
			
		batch.end();
		
		//debugRenderer.render(context.getWorld(), cam.scl(GameContext.PIXELSPERMETER));
		
		
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode)
		{
			case Keys.LEFT: key_left_pressed = true;
			break;
			case Keys.RIGHT: key_right_pressed = true;
			break;
			case Keys.UP: key_up_pressed = true;
			break;
			case Keys.DOWN: key_down_pressed = true;
			break;
		}
		
		
		itStickman = stickmen.iterator();
		while (itStickman.hasNext())
		{
			itStickman.next().key(keycode, true);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		switch (keycode)
		{
			case Keys.LEFT: key_left_pressed = false;
			break;
			case Keys.RIGHT: key_right_pressed = false;
			break;
			case Keys.UP: key_up_pressed = false;
			break;
			case Keys.DOWN: key_down_pressed = false;
			break;
		}
		
		itStickman = stickmen.iterator();
		while (itStickman.hasNext())
		{
			itStickman.next().key(keycode, false);
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

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals( GameObject.TYPE_COIN )) 
		{
			Coin coin = (Coin) contact.getFixtureB().getBody().getUserData();
			
			if (!coin.isDestroyed() && contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals( GameObject.TYPE_PLAYER))
			{
				Stickman sTemp = (Stickman) contact.getFixtureA().getBody().getUserData();
				sTemp.score(1);
			}
			coin.markForRemoval();
		}
		if (contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals( GameObject.TYPE_COIN ))
		{
			Coin coin = (Coin) contact.getFixtureA().getBody().getUserData();
			
			if (!coin.isDestroyed() && contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals( GameObject.TYPE_PLAYER))
			{
				Stickman sTemp = (Stickman) contact.getFixtureB().getBody().getUserData();
				sTemp.score(1);
			}
			coin.markForRemoval();
		}
		
		if (contact.isTouching())
		{
			if (contact.getFixtureA().getUserData().equals( GameObject.TYPE_PLAYER))
			{
				Stickman sTemp = (Stickman) contact.getFixtureA().getBody().getUserData();
				if ( contact.getFixtureB().getUserData().equals(GameObject.TYPE_GROUND) && contact.getFixtureB().getBody().getPosition().y < sTemp.getBody().getPosition().y)
					{
						
						sTemp.setGrounded(true);
						sTemp.setGroundedPlattform( (GameObject) contact.getFixtureB().getBody().getUserData());
					}
				
			}
			if (contact.getFixtureB().getUserData().equals( GameObject.TYPE_PLAYER))
			{
				Stickman sTemp = (Stickman) contact.getFixtureB().getBody().getUserData();
				if (contact.getFixtureA().getUserData().equals(GameObject.TYPE_GROUND) && contact.getFixtureA().getBody().getPosition().y < sTemp.getBody().getPosition().y)
					{
						
						sTemp.setGrounded(true);
						sTemp.setGroundedPlattform( (GameObject) contact.getFixtureB().getBody().getUserData());
					}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
			
		/*
			if (contact.getFixtureA() == stickman1.getSensorFixture())
			{				
				//if ( contact.getFixtureB().getUserData().equals(GameObject.TYPE_GROUND)) stickman1.setGrounded(false);
			}
			if (contact.getFixtureB() == stickman1.getSensorFixture())
			{
				//if ( contact.getFixtureA().getUserData().equals(GameObject.TYPE_GROUND)) stickman1.setGrounded(false);
			}
		*/
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		itStickman = stickmen.iterator();
		while (itStickman.hasNext())
		{
			itStickman.next().button(controller.hashCode(), buttonCode, true);			
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		boolean controllerAssigned = false;

		itStickman = stickmen.iterator();
		while (itStickman.hasNext())
		{
			Stickman sTmp = itStickman.next(); 
			if (!controllerAssigned) controllerAssigned = sTmp.button(controller.hashCode(), buttonCode, false);
			if (controllerAssigned)
			{
				if (sTmp.getKey_left() < 0 && sTmp.getKey_jump() != buttonCode) sTmp.setKey_left(buttonCode);
				else if (sTmp.getKey_right() < 0 && sTmp.getKey_left() != buttonCode && sTmp.getKey_jump() != buttonCode) sTmp.setKey_right(buttonCode);
			}
			
		}
		if (!controllerAssigned) addPlayer(-1,-2, buttonCode, controller.hashCode());
		
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}
}
