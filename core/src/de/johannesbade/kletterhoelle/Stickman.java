package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Stickman extends GameObject{

	private Sprite sprite;
		
	public static final int STOP = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	public static final float BASE_SPEED = 5;
	public static final float BASE_JUMP_ACC = 550;
	public static final float MAX_VELOCITY = BASE_SPEED*1.5f;
	
	private Fixture physicsFixture = null;
	private Fixture sensorFixture = null;
	
	private Vector2 vel = null;
	private Vector2 pos = null;
	
	private int key_left = Keys.A;
	private int key_right = Keys.D;
	private int key_jump = Keys.W;
	
	private boolean key_left_pressed = false;
	private boolean key_right_pressed = false;
	private boolean key_jump_pressed = false;
	
	private float stillTime = 0;
	
	private boolean grounded= false;

	
	public Stickman(GameContext context, float x, float y) {
		super(context, GameObject.TYPE_PLAYER);
		sprite = new Sprite(new Texture("stickman.png"));
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
		poly.setAsBox((getWidth()/2)/GameContext.PIXELSPERMETER, ((fgetHeight()-getWidth())/2)/GameContext.PIXELSPERMETER);
		physicsFixture = body.createFixture(poly, 1);
		poly.dispose();
		
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius((getWidth()/2)/GameContext.PIXELSPERMETER);
		circle.setPosition(new Vector2(0,(-getHeight()/2)/GameContext.PIXELSPERMETER));
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.friction = 0f;

		// Create our fixture and attach it to the body
		sensorFixture = body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
		
		body.setBullet(true);
		body.setFixedRotation(true);
		updateBounds();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setBounds(body.getPosition().x*GameContext.PIXELSPERMETER -getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight()/1.3f, getWidth(),getHeight());
		sprite.draw(batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		vel = body.getLinearVelocity();
		pos = body.getPosition();
		
		body.setAwake(true);
		
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			body.setLinearVelocity(vel.x, vel.y);
		}
		
		if (!key_left_pressed && !key_right_pressed)
		{
			stillTime += Gdx.graphics.getDeltaTime();
			body.setLinearVelocity(vel.x * 0.9f, vel.y);
		}
		else
		{
			stillTime = 0f;
		}
		
		if (!grounded) {
			physicsFixture.setFriction(0f);
			sensorFixture.setFriction(0f);
		}
		else
		{
			if (!key_left_pressed && !key_right_pressed && stillTime > 0.2f)
			{
				physicsFixture.setFriction(0f);
				sensorFixture.setFriction(0f);				
			}
			else
			{
				physicsFixture.setFriction(0.2f);
				sensorFixture.setFriction(0.2f);			
			}
			
			if (grounded)
			{
				body.applyLinearImpulse(0, -1, pos.x, pos.y, true);
			}
		}
		
		if (key_left_pressed && vel.x > -MAX_VELOCITY) body.applyLinearImpulse(-1f, 0, pos.x , pos.y, true);
		if (key_right_pressed && vel.x < MAX_VELOCITY) body.applyLinearImpulse(1f, 0, pos.x , pos.y, true);

		if (key_jump_pressed && grounded) {
			key_jump_pressed = false;
			body.setLinearVelocity(vel.x,0);
			body.setTransform(pos.x, pos.y + 0.01f, 0);
			body.applyLinearImpulse(0, 20, pos.x, pos.y, true);
		}
	}
	
	
	public void key(int keyCode, boolean pressed)
	{
		
		if (keyCode == key_left) key_left_pressed = pressed;
		if (keyCode == key_right) key_right_pressed = pressed;
		if (keyCode == key_jump) key_jump_pressed = pressed;		
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
	}
	
	
	
}
