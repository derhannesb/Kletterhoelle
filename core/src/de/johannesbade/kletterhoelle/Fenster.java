package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Fenster extends GameObject{

	private Sprite sprite;
	
	public Fenster(GameContext context) {
		super(context, GameObject.TYPE_GROUND);
		sprite = new Sprite(new Texture("fenster.jpg"));
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
