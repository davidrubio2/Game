package com.mygdx.game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
	private int iwidthScreen = 800;
	private int iheightScreen = 480;
	private Texture tBackImage;
	private Array<Rectangle> aBalloons;
	private long lLastBalloonTime;
	private Texture tBalloons;
	SpriteBatch batch;
	private OrthographicCamera camera;
    private Rectangle rTouch;

	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, iwidthScreen, iheightScreen);
		batch = new SpriteBatch();
		tBalloons = new Texture("Balloons.jpg");
		tBackImage = new Texture("BackGroundPlay.jpg");
		aBalloons = new Array<Rectangle>();
        rTouch = new Rectangle();
        spawnBalloons();
	}

	private void spawnBalloons() {
		Rectangle rBalloon = new Rectangle();
        rBalloon.x = MathUtils.random(0, iwidthScreen-64);
        rBalloon.y = -64;
        rBalloon.width = 64;
        rBalloon.height = 64;
        aBalloons.add(rBalloon);
		lLastBalloonTime = TimeUtils.nanoTime();
	 }

	@Override
	public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);


        batch.begin();
        batch.draw(tBackImage, 0, 0);
        for (Rectangle rBalloon : aBalloons) {

            batch.draw(tBalloons, rBalloon.x, rBalloon.y);
        }
        batch.end();




        if (TimeUtils.nanoTime() - lLastBalloonTime > 1000000000) spawnBalloons();

        for (Iterator<Rectangle> iter = aBalloons.iterator(); iter.hasNext(); ) {
            Rectangle rBalloon = iter.next();

            rBalloon.y += 200 * Gdx.graphics.getDeltaTime();

            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                if(rBalloon.contains(touchPos.x,touchPos.y)) {
                    iter.remove();
                }
            }


            if (rBalloon.y > 480) {
                iter.remove();
            }

        }
    }
	
	@Override
	public void dispose () {
		batch.dispose();
		tBackImage.dispose();
	}
}
