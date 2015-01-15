package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Fenster extends GameObject{

	private Sprite sprite = null;
	
	public Fenster(GameContext context, int x, int y, int width, int height) {
		super(context, GameObject.TYPE_GROUND);
		setBounds(x, y, width, height);
		sprite = new Sprite(context.getAtlas().findRegion("fenster"));
		sprite.setColor(Color.BLACK);
		// Create our body definition
		BodyDef bodyDef =new BodyDef();  
		// Set its world position
		bodyDef.position.set(new Vector2( (getX()+getWidth()/2)/GameContext.PIXELSPERMETER, (getY()+getHeight()/2)/GameContext.PIXELSPERMETER));  

		// Create a body from the defintion and add it to the world
		body = context.getWorld().createBody(bodyDef);  

		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();  
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		groundBox.setAsBox(getWidth()/2/GameContext.PIXELSPERMETER, getHeight()/2/GameContext.PIXELSPERMETER);
		// Create a fixture from our polygon shape and add it to our ground body  
		Fixture fixture = body.createFixture(groundBox, 0.0f); 
		fixture.setUserData(GameObject.TYPE_GROUND);
		// Clean up after ourselves
		groundBox.dispose();
		
		body.setUserData(this);
		updateBounds();
	}
		
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setBounds(getX(), getY(), getWidth(), getHeight());
		sprite.setAlpha(parentAlpha);
		sprite.draw(batch);
	}

}
