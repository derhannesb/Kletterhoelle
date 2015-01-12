package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MovingPlatform extends GameObject {

	private Sprite sprite = null;
	
	private Vector2 pos = new Vector2();
	private Vector2 dir = new Vector2();
	private float dist = 0f;
	private float maxDist = 100;
	private float speed = 1f;
	
	public MovingPlatform(GameContext context, int x, int y, int width, int height, float maxDist) {
		super(context, GameObject.TYPE_GROUND);
		this.maxDist = maxDist;
		setBounds(x, y, width, height);
		sprite = new Sprite(new Texture("fenster.jpg"));
		
		BodyDef def = new BodyDef();
		def.type = BodyType.KinematicBody;
		def.position.set( (x-width/2)/GameContext.PIXELSPERMETER, (y+height/2)/GameContext.PIXELSPERMETER);
		body = context.getWorld().createBody(def);
		
		PolygonShape poly = new PolygonShape();
		poly.setAsBox( (width/2)/GameContext.PIXELSPERMETER, (height/2)/GameContext.PIXELSPERMETER );
		Fixture fixture = body.createFixture(poly, 1);
		fixture.setUserData(GameObject.TYPE_GROUND);
		poly.dispose();
		
		pos.x = x;
		pos.y = y;
		setX(x);
		setY(y);
		
		body.setUserData(this);
		dir.x = speed;
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		sprite.setBounds(body.getPosition().x*GameContext.PIXELSPERMETER - getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight()/2, getWidth(), getHeight());
		sprite.setAlpha(parentAlpha);
		sprite.draw(batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		dist += dir.len() * delta;
		if(dist > maxDist) {
			dir.x = dir.x * -1;
			dist = 0;
		}
		
		body.setLinearVelocity(dir);
		 
	}

}