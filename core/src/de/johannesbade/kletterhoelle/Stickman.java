package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Stickman extends GameObject {
	public enum Capability {
		NO_CAPABILITY,
		KILL_ALL_CAPABILITY,
		HOVER_CAPABILITY
	}
	
	private Capability capability = Capability.NO_CAPABILITY;
	private Sprite sprite;
	
	private int score = 0;
	private boolean deadness = false;
		
	public static final int STOP = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	public static final float BASE_SPEED = 1.5f;
	public static final float BASE_JUMP_ACC = 550;
	public static final float MAX_VELOCITY = BASE_SPEED*3f;
	public static final float DAMPENING = .7f;
	
	private Fixture physicsFixture = null;
	private Fixture sensorFixture = null;
	
	private Vector2 vel = null;
	private Vector2 pos = null;
	
	private ControllerConfig cconf = new ControllerConfig();

	private float stillTime = 0;
	private long lastGroundTime = 0;
	
	private float killAllStartTime = -1;
	
	private boolean grounded= false;
	
	private boolean jump = false;
	
	private GameObject groundedPlattform = null;
	
	private Animation animStand = null;
	private Animation animWalk = null; 
	private Animation animJump = null;
	
	private Animation animation = null;

	private SpriteAnimation effectSprite = null;
	
	public Stickman(GameContext context, float x, float y, Color color) {
		super(context, GameObject.TYPE_PLAYER);
	    effectSprite = new SpriteAnimation(context, "invincible", 0.125f);
		sprite = new Sprite(context.getAtlas().findRegion("stickmanwalk"));
		effectSprite.setPosition(x, y);
		Array<AtlasRegion> frames = context.getAtlas().findRegions("stickmanwalk");
		animWalk = new Animation(0.1f, frames);
		animWalk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		frames = context.getAtlas().findRegions("stickmanjump");
		animJump = new Animation(0.25f, frames);
		animJump.setPlayMode(Animation.PlayMode.NORMAL);
		
		frames = context.getAtlas().findRegions("stickmanstand");
		animStand = new Animation(0.5f, frames);
		animStand.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		setColor(color);
		animation = animStand;
		
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
		setX(x);
		setY(y);
		
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(getX()/GameContext.PIXELSPERMETER, getY()/GameContext.PIXELSPERMETER);

		// Create our body in the world using our body definition
		body = context.getWorld().createBody(bodyDef);

		PolygonShape poly = new PolygonShape();
		poly.setAsBox((getWidth()/4)/GameContext.PIXELSPERMETER, ((getHeight()-getWidth())/2)/GameContext.PIXELSPERMETER);
		physicsFixture = body.createFixture(poly, 2);
		physicsFixture.setUserData(GameObject.TYPE_PLAYER);
		poly.dispose();
		
		
		CircleShape circle = new CircleShape();
		circle.setRadius((getWidth()/2)/GameContext.PIXELSPERMETER);
		circle.setPosition(new Vector2(0,(-getHeight()/2)/GameContext.PIXELSPERMETER));
		sensorFixture = body.createFixture(circle,0);
		sensorFixture.setUserData(GameObject.TYPE_PLAYER);

		circle.dispose();
		
		body.setBullet(true);
		body.setFixedRotation(true);
		body.setUserData(this);
		updateBounds();
		
		sprite.setColor(color);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setRegion(animation.getKeyFrame(getContext().getTimeElapsed()) );
		sprite.setBounds(body.getPosition().x*GameContext.PIXELSPERMETER -getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight()/1.3f, getWidth(),getHeight());
		sprite.draw(batch);
		

		
		if (capability == Capability.KILL_ALL_CAPABILITY)
		{
			effectSprite.setPosition(sprite.getX(), sprite.getY());
			effectSprite.draw(batch);
		}
	}
	
	private void handleCoinCollision(Coin coin)
	{
		switch (coin.getCoinType()) {
			case NORMAL_COIN:
				if (!coin.isDestroyed()) {
					score(1);
					coin.markForRemoval();
				}
				
				break;
			
			case KILL_ALL_COIN:
				if (!coin.isDestroyed())
				{
					System.out.println("KILLERCOIN");
					setCapability(Stickman.Capability.KILL_ALL_CAPABILITY);
					setKillAllStartTime(getContext().getTimeElapsed());
					coin.markForRemoval();
				}
				
				break;
			
			case LOW_GRAVITY_COIN:
				break;
			
			case COLOR_SWITCH_COIN:
				if (!coin.isDestroyed())
				{
					System.out.println("COLORSWITCHER");
					
					Array<ControllerConfig> cconfs = new Array<ControllerConfig>();
					
					for (GameObject go : getContext().getGameObjects()) {
						if (go.getType() == TYPE_PLAYER) {
							cconfs.add(((Stickman) go).getControllerConfig());
							System.out.println("old order: " + ((Stickman) go).getControllerConfig());
						}
					}
					
					cconfs.shuffle();
					
					int i = 0;
					
					for (GameObject go : getContext().getGameObjects()) {
						if (go.getType() == TYPE_PLAYER) {
							((Stickman) go).setControllerConfig(cconfs.get(i));
							System.out.println("new order: " + cconfs.get(i));
							i++;
						}
					}
					
					coin.markForRemoval();
				}
				
				break;
		}
	}
	
	private void handlePlayerCollision(Stickman stickman) {
		switch (capability) {
			case NO_CAPABILITY:
				if (stickman.getCapability() != Capability.NO_CAPABILITY) {
					//stickman.beginContact(this);
				}
				
				break;
			
			case KILL_ALL_CAPABILITY:
				if (stickman.getCapability() == Capability.KILL_ALL_CAPABILITY) {
					setCapability(Capability.NO_CAPABILITY);
					stickman.setCapability(Capability.NO_CAPABILITY);
				} else {
					stickman.kill();
				}
				
				break;
			
			case HOVER_CAPABILITY:
				break;
		}
	}
	
	public void beginContact(GameObject o) {
		switch (o.getType()) {
			case TYPE_COIN:
				handleCoinCollision((Coin) o);
				break;
			
			case TYPE_PLAYER:
				handlePlayerCollision((Stickman) o);
				break;
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		body.setAwake(true);
		
		vel = body.getLinearVelocity();
		pos = body.getPosition();
		
		if(grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if(System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}
		
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			body.setLinearVelocity(vel.x, vel.y);
		}
		
		// calculate stilltime & damp
		if (!cconf.key_left_pressed && !cconf.key_right_pressed)
		{
			stillTime += Gdx.graphics.getDeltaTime();
			body.setLinearVelocity(vel.x * DAMPENING, vel.y);
			animation = animStand;
		}
		else
		{
			stillTime = 0;
		}
		
		// disable friction while jumping
		if (groundedPlattform == null) {
			physicsFixture.setFriction(0f);
			sensorFixture.setFriction(0f);
		}
		else
		{

			if (!cconf.key_left_pressed && !cconf.key_right_pressed && stillTime > 0.2f)
			{
				physicsFixture.setFriction(100f);
				sensorFixture.setFriction(100f);	
			}
			else
			{
				physicsFixture.setFriction(0.2f);
				sensorFixture.setFriction(0.2f);
				animation = animWalk;
			}
			
			
			if (groundedPlattform != null)
			{
				//body.applyLinearImpulse(0, -24, pos.x, pos.y, true);
				
			}
			
		}
		
		// apply left impulse, but only if max velocity is not reached yet
		if (cconf.key_left_pressed && vel.x > -MAX_VELOCITY) body.applyLinearImpulse(-BASE_SPEED, 0, pos.x , pos.y, true);
		// apply right impulse, but only if max velocity is not reached yet
		if (cconf.key_right_pressed && vel.x < MAX_VELOCITY) body.applyLinearImpulse(BASE_SPEED, 0, pos.x , pos.y, true);

		if (jump) {
			jump = false;
			
			if (groundedPlattform != null)
			{
				animation = animJump;
				groundedPlattform = null;
				body.setLinearVelocity(vel.x,0);
				body.setTransform(pos.x, pos.y + 0.01f, 0);
				body.applyLinearImpulse(0, 9, pos.x, pos.y, true);
			}
		}
		
		if (vel.y > 0.1f && groundedPlattform == null) animation = animJump;

		
		if (capability == Capability.NO_CAPABILITY) sprite.setAlpha(1f);		
		if (capability == Capability.KILL_ALL_CAPABILITY && getContext().getTimeElapsed() - killAllStartTime > Coin.KILL_ALL_DURATION)
		{
			setKillAllStartTime(-1);
			setCapability(Capability.NO_CAPABILITY);
			
			sprite.setAlpha(1f);
		}
		
		if (capability == Capability.KILL_ALL_CAPABILITY) sprite.setAlpha(0.75f+0.25f*MathUtils.sin(getContext().getTimeElapsed()*10));

	}
	
	
	public boolean  key(int keyCode, boolean pressed)
	{
		if (keyCode == cconf.key_left) cconf.key_left_pressed = pressed;
		if (keyCode == cconf.key_right) cconf.key_right_pressed = pressed;
		if (keyCode == cconf.key_jump) {
			if (pressed && grounded) jump = true;		
		}
		if (keyCode == cconf.key_left || keyCode == cconf.key_right || keyCode == cconf.key_jump) return true;
			else return false;
	}
	
	public boolean button(int controllerID, int button, boolean pressed)
	{
		if (controllerID == this.cconf.controllerID)
		{
			if (button != GameContext.POV_CENTER)
			{
				if (button == cconf.key_left) cconf.key_left_pressed = pressed;
				if (button == cconf.key_right) cconf.key_right_pressed = pressed;
				if (button == cconf.key_jump) {
					if (pressed && grounded) jump = true;		
				}
			}
			else
			{
				cconf.key_left_pressed = false;
				cconf.key_right_pressed = false;
			}
			return true;
		}
		return false;
	}


	public GameObject getGroundedPlattform() {
		return groundedPlattform;
	}

	public void setGroundedPlattform(GameObject groundedPlattform) {
		if (groundedPlattform != null) grounded = true;
		this.groundedPlattform = groundedPlattform;
	}

	public Fixture getPhysicsFixture() {
		return physicsFixture;
	}

	public void setPhysicsFixture(Fixture physicsFixture) {
		this.physicsFixture = physicsFixture;
	}

	public Fixture getSensorFixture() {
		return sensorFixture;
	}

	public void setSensorFixture(Fixture sensorFixture) {
		this.sensorFixture = sensorFixture;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
		if (!grounded) groundedPlattform = null;
	}

	public int getScore() {
		return score;
	}

	public void score(int i)
	{
		score+=i;
		if (score < 0) score = 0;
	}
	
	public void setKeys(int left, int right, int jump, int controllerID)
	{
		cconf.key_left = left;
		cconf.key_right = right;
		cconf.key_jump = jump;
		this.cconf.controllerID = controllerID; 
	}

	public int getKey_left() {
		return cconf.key_left;
	}

	public void setKey_left(int key_left) {
		this.cconf.key_left = key_left;
	}

	public int getKey_right() {
		return cconf.key_right;
	}

	public void setKey_right(int key_right) {
		this.cconf.key_right = key_right;
	}

	public int getKey_jump() {
		return cconf.key_jump;
	}

	public void setKey_jump(int key_jump) {
		this.cconf.key_jump = key_jump;
	}

	public Capability getCapability() {
		return capability;
	}

	public void setCapability(Capability capability) {
		this.capability = capability;
	}

	public float getKillAllStartTime() {
		return killAllStartTime;
	}

	public void setKillAllStartTime(float killAllStartTime) {
		this.killAllStartTime = killAllStartTime;
	}
	
	public boolean isDead() {
		return deadness;
	}
	
	public void kill() {
		deadness = true;
	}
	
	public void setAlive() {
		killAllStartTime = -1;
		capability = Capability.NO_CAPABILITY;
		deadness = false;
	}
	
	public ControllerConfig getControllerConfig() {
		return cconf;
	}

	public void setControllerConfig(ControllerConfig cconf) {
		this.cconf = cconf;
	}
}
