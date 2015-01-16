package de.johannesbade.kletterhoelle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.Group;
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
	
	private Array<Color> playerColors = null;

	private HashMap<String, Integer> hmPseudoButtons = null;
	
	private int[] coinDistribution = {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2
	};

	private Dekoration fahnenvorhang = null;
	
	private Group groupBackground = null;
	private Group groupForeground = null;
	
	public void spawnCoin()
	{
		Vector2 newPos = spawnPositions.get(MathUtils.random(spawnPositions.size-1));
		
		context.getStage().addActor(new Coin(context, newPos.x, newPos.y, Coin.CoinType.values()[coinDistribution[MathUtils.random(0, coinDistribution.length - 1)]]));
	}
	
	public void addPlayer()
	{
		addPlayer(Keys.A, Keys.D, Keys.W,GameContext.CONTROLLER_KEYBOARD);
	}
	
	public void addPlayer(int left, int right, int jump, int controllerID)
	{
		Color color = Color.BLACK;
		if (playerColors.size > 0) color = playerColors.pop();
		Stickman stickman = new Stickman(context, 500+MathUtils.random(0, 800), 1200, color);
		stickman.setKeys(left, right, jump, controllerID);
		stickmen.add(stickman);
		groupBackground.addActor(stickman);
	}
	
	
	@Override
	public void create () {
		context = new GameContext();
		batch = new SpriteBatch();
		stickmen = new Array<Stickman>();
		
		//Map POV-Inputs to Pseudobuttons
		hmPseudoButtons = new HashMap<String, Integer>();
		hmPseudoButtons.put(PovDirection.center.toString(), -90000);
		hmPseudoButtons.put(PovDirection.west.toString(), 90001);
		hmPseudoButtons.put(PovDirection.east.toString(), 90002);
		hmPseudoButtons.put(PovDirection.north.toString(), 90003);
		hmPseudoButtons.put(PovDirection.south.toString(), 90004);
		
		playerColors = new Array<Color>();
		playerColors.add(Color.GRAY);
		playerColors.add(Color.LIGHT_GRAY);
		playerColors.add(Color.MAROON);
		playerColors.add(Color.YELLOW);
		playerColors.add(Color.PINK);
		playerColors.add(Color.PURPLE);
		playerColors.add(Color.TEAL);
		playerColors.add(Color.NAVY);
		playerColors.add(Color.MAGENTA);
		playerColors.add(Color.RED);
		playerColors.add(Color.BLUE);
		playerColors.add(Color.GREEN);
		playerColors.add(Color.ORANGE);
		playerColors.add(Color.BLACK);
		
		spawnPositions = new Array<Vector2>();
		spawnPositions.add(new Vector2(1680,160+950));
		spawnPositions.add(new Vector2(1600,160+490));
		spawnPositions.add(new Vector2(590,160+490));
		spawnPositions.add(new Vector2(900,160+490));
		spawnPositions.add(new Vector2(838,160+555));
		
		groupBackground = new Group();
		groupForeground = new Group();
		
		context.getStage().addActor(groupBackground);
		context.getStage().addActor(groupForeground);
		
		//Fenster der Kletterhalle
		//context.getStage().addActor(new Fenster(context, 276, 766, 1950, 220));
		
		groupBackground.addActor(new Fenster(context, 276, 766, 860, 220));
		groupBackground.addActor(new Fenster(context, 276+900+100, 766, 976, 220));
		
		groupBackground.addActor(new Fenster(context, 1111, 220,1140,220));
		groupBackground.addActor(new Fenster(context,-728, 110,1141,220));
		
		//Zusaetzliche Plattformen
		groupBackground.addActor(new Fenster(context,280, 766,200,820));
		MovingPlatform mittelgross = new MovingPlatform(context,680, 120,111,220,1f, 1);
		mittelgross.getSprite().setRegion(context.getAtlas().findRegion("platformtwo"));
		groupBackground.addActor(mittelgross);
		
		groupBackground.addActor(new MovingPlatform(context,990, 120,150,220, 2, 0));
		
		groupBackground.addActor(new MovingPlatform(context,2350, 300,80,60,3, 2));
		
		groupBackground.addActor(new Dekoration(context, "pfeilrunter", 276+870+16, 1100, .25f, Color.CYAN));
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.y += 150;
		camera.position.x += 505;
		
		spawnCoin();

		//Spieler
		
		//addPlayer();

		cameraHUD = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		context.getWorld().setContactListener(this);
		
		Gdx.input.setInputProcessor(this);
		Controllers.addListener(this);
		
		fahnenvorhang = new Dekoration(context, "fenster", 1892, 441, 1, Color.WHITE);
		fahnenvorhang.setSize(250, 325);
		groupForeground.addActor(fahnenvorhang);
		
		debugRenderer = new Box2DDebugRenderer();
		
		restartGame();
	}
	
	public void restartGame()
	{
		context.setTimeElapsed(0);
		context.getWorld().clearForces();
		//Player zuruecksetzen
		for (GameObject go : context.getGameObjects())
		{
			switch (go.getType())
			{
				case GameObject.TYPE_PLAYER:
					Stickman stickman = (Stickman) go;
					stickman.kill();
					stickman.score(-1000000000);
				break;
			}
		}
		context.setGamestate(GameContext.GS_RUNNING);
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
						
						//Wenn der Spieler unten aus dem Bild f�llt respawnen:
						if (go.getType() == GameObject.TYPE_PLAYER) {
							if (go.getBody().getPosition().y  < -100 || ((Stickman) go).isDead()) {
								go.getBody().setTransform(800/GameContext.PIXELSPERMETER, 1600/GameContext.PIXELSPERMETER, 0);
								go.getBody().setLinearVelocity(0, 0);
								((Stickman) go).setAlive();
							}
						}
					}
				}
			}
		}
	}
	
	public void act()
	{
		context.getWorld().step(Gdx.graphics.getDeltaTime(), 6, 2);
		context.getStage().act();	
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
		
		if (context.getGamestate() == GameContext.GS_RUNNING) act();
		
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
		if (context.getGamestate() == GameContext.GS_RUNNING)
		{
		itStickman = stickmen.iterator();
		int snr = -1;
		String str;
		while (itStickman.hasNext())
		{
			Stickman stickman = itStickman.next();
			str = "Spieler " +((snr++)+1)+": "+stickman.getScore();
			context.getFont().setColor(Color.WHITE);
			context.getFont().draw(batch,str, 28, cameraHUD.viewportHeight-12 - (context.getFont().getCapHeight()+context.getFont().getLineHeight()*snr));
			context.getFont().setColor(stickman.getColor());
			context.getFont().draw(batch,str, 30, cameraHUD.viewportHeight-10 - (context.getFont().getCapHeight()+context.getFont().getLineHeight()*snr));
		}
				
		if (GameContext.TIME_UP-context.getTimeElapsed() <= 0)
			{
				context.setGamestate(GameContext.GS_HIGHSCORE);
				//restartGame();
				
			}
		if (GameContext.TIME_UP-context.getTimeElapsed() <= 10) context.getFont().setColor(Color.RED);	
			else context.getFont().setColor(Color.GREEN);	
		
		context.getFont().draw(batch, ""+ MathUtils.round(GameContext.TIME_UP-context.getTimeElapsed()), cameraHUD.viewportWidth/2, cameraHUD.viewportHeight-40);
		}
		
		if (context.getGamestate() == GameContext.GS_HIGHSCORE)
		{
			stickmen.sort(new Comparator<Stickman>() {

				@Override
				public int compare(Stickman o1, Stickman o2) {
					return o2.getScore()-o1.getScore();
				}
			});
			int snr = -1;
			for (Stickman stickman : stickmen)
			{
				String str = "Spieler " +((snr++)+1)+": "+stickman.getScore();
				context.getFontBig().setColor(Color.WHITE);
				context.getFontBig().draw(batch,str, cameraHUD.viewportWidth/2 - 28, cameraHUD.viewportHeight-22 - (context.getFont().getCapHeight()+context.getFont().getLineHeight()*1.5f*snr));
				context.getFontBig().setColor(stickman.getColor());
				context.getFontBig().draw(batch,str,  cameraHUD.viewportWidth/2 - 30, cameraHUD.viewportHeight-20 - (context.getFont().getCapHeight()+context.getFont().getLineHeight()*1.5f*snr));
			}
			
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
			case Keys.ENTER: 
				if (context.getGamestate() == GameContext.GS_HIGHSCORE) restartGame();
			break;
		
		}
		
		itStickman = stickmen.iterator();
		boolean keyAssigned = false;
		while (itStickman.hasNext())
		{
			Stickman sTmp = itStickman.next();
			keyAssigned = sTmp.key(keycode, false);
		}
		if (!keyAssigned)
		{
			//addPlayer(-1,-2, keycode, GameContext.CONTROLLER_KEYBOARD);
		}
		if (keycode == Keys.F1) addPlayer(Keys.A,Keys.D, Keys.W, GameContext.CONTROLLER_KEYBOARD);
		
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
				Stickman stickman = (Stickman) contact.getFixtureA().getBody().getUserData();
				
				stickman.beginContact(coin);
			}
		}
		if (contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals( GameObject.TYPE_COIN ))
		{
			Coin coin = (Coin) contact.getFixtureA().getBody().getUserData();
			
			if (!coin.isDestroyed() && contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals( GameObject.TYPE_PLAYER))
			{
				Stickman stickman = (Stickman) contact.getFixtureB().getBody().getUserData();
				
				stickman.beginContact(coin);
			}
		}
		
		if (contact.getFixtureA().getUserData().equals( GameObject.TYPE_PLAYER) && contact.getFixtureB().getUserData().equals( GameObject.TYPE_PLAYER))
		{
			Stickman smA = (Stickman) contact.getFixtureA().getBody().getUserData();
			Stickman smB = (Stickman) contact.getFixtureB().getBody().getUserData();
			
			smA.beginContact(smB);
			smB.beginContact(smA);
			
			smA.setGroundedPlattform(smB);
			smB.setGroundedPlattform(smA);
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
			Stickman stickman = itStickman.next();
			if (stickman.button(controller.hashCode(), buttonCode, true))
			{
				if (stickman.getKey_left() < 0 && stickman.getKey_jump() != buttonCode && stickman.getKey_left() != buttonCode) stickman.setKey_left(buttonCode);
				else if (stickman.getKey_right() < 0 && stickman.getKey_left() != buttonCode && stickman.getKey_jump() != buttonCode && stickman.getKey_right() != buttonCode) stickman.setKey_right(buttonCode);
				
			}
		
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
				if (buttonCode != GameContext.POV_CENTER)
				{
					if (sTmp.getKey_left() < 0 && sTmp.getKey_jump() != buttonCode && sTmp.getKey_left() != buttonCode) sTmp.setKey_left(buttonCode);
					else if (sTmp.getKey_right() < 0 && sTmp.getKey_left() != buttonCode && sTmp.getKey_jump() != buttonCode && sTmp.getKey_right() != buttonCode) sTmp.setKey_right(buttonCode);
				}
				sTmp.button(controller.hashCode(), buttonCode, false);
			}
			
		}
		if (!controllerAssigned && buttonCode != GameContext.POV_CENTER) addPlayer(-1,-2, buttonCode, controller.hashCode());
		
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
		if (hmPseudoButtons.get(value.toString()) != null)
		{
			int pseudoButton = hmPseudoButtons.get(value.toString());
			//pseudoButton += povCode*10000; //Falls es mehrere POV-Buttons gibt...
			buttonUp(controller, pseudoButton);
			buttonDown(controller, pseudoButton);
		}
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
