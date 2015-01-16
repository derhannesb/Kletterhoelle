package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Dekoration extends Actor {
	
	private Sprite sprite = null;
	
	private Animation animation = null;
	private GameContext context;
	
	
	public Dekoration(GameContext context, String region, float x, float y, float animDelay, Color color) {
		super();
		sprite = new Sprite(context.getAtlas().findRegion(region));
		sprite.setPosition(x, y);
		sprite.setColor(color);
		setPosition(x, y);
		this.context = context;
		Array<AtlasRegion> frames = context.getAtlas().findRegions(region);
		animation = new Animation(animDelay, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setRotation(getRotation());
		sprite.setRegion(animation.getKeyFrame(context.getTimeElapsed()));
		sprite.draw(batch);
	}


}
