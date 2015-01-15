package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;



public class Coin extends GameObject{

	private Sprite sprite = null;
	
	private boolean destroyed = false;
	
	public Coin(GameContext context, float x, float y) {
		super(context, GameObject.TYPE_COIN);
		sprite = new Sprite(context.getAtlas().findRegion("coin")); 
		sprite.setScale(2);
		setBounds(x, y, sprite.getWidth(), sprite.getHeight());
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(getX()/GameContext.PIXELSPERMETER, getY()/GameContext.PIXELSPERMETER);
		body = context.getWorld().createBody(bodyDef);
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius((getWidth()/2)/GameContext.PIXELSPERMETER);
		circle.setPosition(new Vector2(0,(-getHeight()/2)/GameContext.PIXELSPERMETER));
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;

		// Create our fixture and attach it to the body
		Fixture fixture = body.createFixture(fixtureDef);
		fixture.setSensor(true);
		fixture.setUserData(GameObject.TYPE_COIN);
		circle.dispose();
		body.setUserData(this);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (!destroyed)
			sprite.rotate(delta*-40f);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (!destroyed)
		{
		sprite.setPosition(body.getPosition().x*GameContext.PIXELSPERMETER -getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight());
		sprite.draw(batch);
		}
		
	}
	
	public void markForRemoval()
	{
		destroyed = true;
		setDestroyed(true);
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
		setType(TYPE_REMOVE);
	}
	
	
	

}
