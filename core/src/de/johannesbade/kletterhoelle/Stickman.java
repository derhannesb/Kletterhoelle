package de.johannesbade.kletterhoelle;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Stickman extends GameObject{

	private Sprite sprite;
	private boolean ground = false;
	private GameContext context;

	private Iterator<GameObject> itGO;
	private GameObject tmpGO;
	
	private float speedX = 0;
	
	public static final int STOP = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	public static final float BASE_SPEED = 20;
	public static final float BASE_JUMP_ACC = 550;
	
	private float verticalVelocity = 0;
	
	public Stickman(GameContext context) {
		super(context, GameObject.TYPE_PLAYER);
		sprite = new Sprite(new Texture("stickman.png"));
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
		this.context = context;
		updateBounds();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setBounds(getX(), getY(), getWidth(),getHeight());
		sprite.draw(batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		verticalVelocity += GameContext.GRAVITY;
		if (ground)	verticalVelocity = 0;
		
		moveBy(speedX*delta, verticalVelocity*delta);
		
		itGO = context.getGameObjects().iterator();
		ground = false;
		while (itGO.hasNext())
			{
				tmpGO = itGO.next();
				
				if (checkCollision(tmpGO))
					{
						if (tmpGO.getType() == GameObject.TYPE_GROUND)
							{
								if (getY()-getHeight() > tmpGO.getY()+10)
								{
									ground = true;
								}
								else if (!ground)
								{
									if (getX()+getWidth() <= tmpGO.getX()+30) move(LEFT);
									if (getX() <= tmpGO.getX()+tmpGO.getWidth()+30) move(RIGHT);
									
								}
							}
					}
			}
	}
	
	public boolean checkCollision(GameObject go)
	{		
		if (go.getBounds().overlaps(getBounds()))
		{
			return true;
		}
		else return false;
	}
	
	public void move(int direction)
	{
		speedX = direction * BASE_SPEED*GameContext.GAME_SPEED;
	}

	public void jump()
	{
		if (ground)
		{
			ground = false;
			verticalVelocity += BASE_JUMP_ACC*GameContext.GAME_SPEED;
		}
		//if (verticalVelocity > BASE_JUMP_ACC*2*GameContext.GAME_SPEED) verticalVelocity = BASE_JUMP_ACC*2*GameContext.GAME_SPEED;
	}
}
