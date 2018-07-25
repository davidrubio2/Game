package com.mygdx.game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {

    private int iwidthScreen = 480;
    private int iheightScreen = 800;
    private Texture tBackImage;
	private Array<Rectangle> aBalloons;
	private long lLastBalloonTime;
	private Texture tBalloons;
	SpriteBatch batch;
	private OrthographicCamera camera;
    private Rectangle rTouch;
    private ScalingViewport viewport;
    private int score;
    private String yourScoreName;
    BitmapFont yourBitmapFontName;
	
	@Override
	public void create () {

        score = 0;
        yourScoreName = "score: 0";
        yourBitmapFontName = new BitmapFont();

        batch = new SpriteBatch();
		tBalloons = new Texture("Balloons.png");
		tBackImage = new Texture("BackGroundPlay.jpg");
		aBalloons = new Array<Rectangle>();
        rTouch = new Rectangle();
        spawnBalloons();

        camera = new OrthographicCamera();
        viewport = new FillViewport(iwidthScreen,iheightScreen,camera);
        viewport.apply();


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
        batch.draw(tBackImage, 0,0,iwidthScreen,iheightScreen);
        yourBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        yourBitmapFontName.draw(batch, yourScoreName, 25, 100); 
        for (Rectangle rBalloon : aBalloons) {
            batch.draw(tBalloons, rBalloon.x, rBalloon.y);
         }


        batch.end();


        if (TimeUtils.nanoTime() - lLastBalloonTime > 1000000000) spawnBalloons();

        for (Iterator<Rectangle> iter = aBalloons.iterator(); iter.hasNext(); ) {
            Rectangle rBalloon = iter.next();

            rBalloon.y += 100 * Gdx.graphics.getDeltaTime();

            rBalloon.x -= 10 * Gdx.graphics.getDeltaTime();



            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isTouched(i)) {
                    if (Gdx.input.justTouched()) {
                        Vector3 touchPos = new Vector3();

                        touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                        camera.unproject(touchPos);
                        if (rBalloon.contains(touchPos.x - 20, touchPos.y - 10)) {
                            Gdx.input.vibrate(100);
                            score++;
                            yourScoreName = "score: " + score;
                            iter.remove();
                        }

                    }
                }
            }

            if (rBalloon.y > iheightScreen) {
                iter.remove();
            }
        }
    }
	
	@Override
	public void dispose () {
		batch.dispose();
		tBackImage.dispose();
	}

    @Override
    public void resize(int width, int height){
        viewport.update(width,height);
       camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
    }
}
