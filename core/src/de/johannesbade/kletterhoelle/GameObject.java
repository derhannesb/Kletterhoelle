package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GameObject extends Actor{

	public static final int TYPE_REMOVE = -1;
	public static final int TYPE_GROUND = 0;
	public static final int TYPE_PLAYER = 1;
	public static final int TYPE_COIN = 2;
	
	private Rectangle bounds = null;
	private int type = -1;
	private GameContext context = null;
	protected Body body = null;
	
	
	public GameObject(GameContext context, int type) {
		bounds = new Rectangle();
		this.context = context;
		this.type = type;
		context.getGameObjects().add(this);
	}
	
	public void updateBounds(float x, float y, float w, float h)
	{
		bounds.set(x, y, w, h);
	}

	public void updateBounds()
	{
		updateBounds(getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		updateBounds(getX(), getY(), getWidth(), getHeight());
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public GameContext getContext() {
		return context;
	}

	public void setContext(GameContext context) {
		this.context = context;
	}
	
	

	
}
