package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Dekoration extends Actor {
	
	private Sprite sprite = null;
	public Dekoration(GameContext context, String region, float x, float y) {
		sprite = new Sprite(context.getAtlas().findRegion(region));
		sprite.setPosition(x, y);
		setPosition(x, y);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.draw(batch);
	}

}
