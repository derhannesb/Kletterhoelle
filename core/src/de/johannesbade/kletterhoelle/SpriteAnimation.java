package de.johannesbade.kletterhoelle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class SpriteAnimation {

	private Animation animation = null;
	
	private GameContext context;
	
	private Sprite sprite;
	
	public SpriteAnimation(GameContext context, String  region, float animDelay)
	{
		this.context = context;
		
		Array<AtlasRegion> frames = context.getAtlas().findRegions(region);
		
		animation = new Animation(animDelay, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		
		sprite = new Sprite(animation.getKeyFrame(0));
		
	}


	public void draw(Batch batch)
	{
		sprite.setRegion(animation.getKeyFrame(context.getTimeElapsed()));
		sprite.draw(batch);
	}
	
	public void setPosition(float x, float y)
	{
		sprite.setPosition(x, y);
	}
	
	public void rotate(float angle)
	{
		sprite.rotate(angle);
	}
	
	public void setScale(float scale)
	{
		sprite.setScale(scale);
	}
	
	public float getWidth()
	{
		return sprite.getWidth();
	}
	
	public float getHeight()
	{
		return sprite.getHeight();
	}
	
	
	
	

}
