package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;



public class Coin extends GameObject {
	public enum CoinType {
		NORMAL_COIN,
		KILL_ALL_COIN,
		LOW_GRAVITY_COIN
	}
	
	private CoinType coinType = CoinType.NORMAL_COIN;
	private SpriteAnimation sprite = null;
	
	private boolean destroyed = false;
	
	public Coin(GameContext context, float x, float y, CoinType coinType) {
		super(context, GameObject.TYPE_COIN);
		
		this.setCoinType(coinType);
		
		switch (coinType) {
			case NORMAL_COIN:
				sprite = new SpriteAnimation(context,"normal_coin", 0.25f);
				break;
				
			case KILL_ALL_COIN:
				sprite = new SpriteAnimation(context,"kill_all_coin", 0.125f);
				break;
				
			case LOW_GRAVITY_COIN:
				sprite = new SpriteAnimation(context,"low_gravity_coin", 0.25f);
				break;
		}
		
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
		
		if (!destroyed) {
			sprite.rotate(delta*-40f);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		if (!destroyed) {
			sprite.setPosition(body.getPosition().x*GameContext.PIXELSPERMETER -getWidth()/2, body.getPosition().y*GameContext.PIXELSPERMETER -getHeight());
			sprite.draw(batch);
		}
	}
	
	public void markForRemoval() {
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

	public CoinType getCoinType() {
		return coinType;
	}

	public void setCoinType(CoinType coinType) {
		this.coinType = coinType;
	}
}
