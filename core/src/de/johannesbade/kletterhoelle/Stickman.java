package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Stickman extends GameObject{

	private Sprite sprite;
	
	private int score = 0;
		
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
	
	private int key_left = Keys.A;
	private int key_right = Keys.D;
	private int key_jump = Keys.W;
	
	private boolean key_left_pressed = false;
	private boolean key_right_pressed = false;
	//private boolean key_jump_pressed = false;
	
	private float stillTime = 0;
	long lastGroundTime = 0;
	
	private boolean grounded= false;
	
	private boolean jump = false;
	
	private GameObject groundedPlattform = null;
	
	private Animation animStand = null;
	private Animation animWalk = null; 
	private Animation animJump = null;
	
	private Animation animation = null;
	
	public Stickman(GameContext context, float x, float y) {
		super(context, GameObject.TYPE_PLAYER);
		sprite = new Sprite(context.getAtlas().findRegion("stickmanwalk"));
		Array<AtlasRegion> frames = context.getAtlas().findRegions("stickmanwalk");
		animWalk = new Animation(0.1f, frames);
		animWalk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
		frames = context.getAtlas().findRegions("stickmanjump");
		animJump = new Animation(0.25f, frames);
		animJump.setPlayMode(Animation.PlayMode.NORMAL);
		
		frames = context.getAtlas().findRegions("stickmanstand");
		animStand = new Animation(0.5f, frames);
		animStand.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		
				
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
		
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setRegion(animation.getKeyFrame(getContext().getTimeElapsed()) );
		sprite.setBounds(body.getPosition().x*GameContext.PIXELSPERMETER -getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight()/1.3f, getWidth(),getHeight());
		sprite.draw(batch);
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
		if (!key_left_pressed && !key_right_pressed)
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

			if (!key_left_pressed && !key_right_pressed && stillTime > 0.2f)
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
		if (key_left_pressed && vel.x > -MAX_VELOCITY) body.applyLinearImpulse(-BASE_SPEED, 0, pos.x , pos.y, true);
		// apply right impulse, but only if max velocity is not reached yet
		if (key_right_pressed && vel.x < MAX_VELOCITY) body.applyLinearImpulse(BASE_SPEED, 0, pos.x , pos.y, true);

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
		 
	}
	
	
	public void key(int keyCode, boolean pressed)
	{
		
		if (keyCode == key_left) key_left_pressed = pressed;
		if (keyCode == key_right) key_right_pressed = pressed;
		if (keyCode == key_jump) {
			if (pressed && grounded) jump = true;		
		}
	}


	public GameObject getGroundedPlattform() {
		return groundedPlattform;
	}

	public void setGroundedPlattform(GameObject groundedPlattform) {
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
	
	public void setKeys(int left, int right, int jump)
	{
		key_left = left;
		key_right = right;
		key_jump = jump;
	}
	
	
}
