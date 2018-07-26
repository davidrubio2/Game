package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {

    public enum GameState{MENU,PLAY,SETTINGS}
    public static GameState eGameState;
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
    private  BitmapFont yourBitmapFontName;

    private Stage stage;
    private TextButton button;
    private TextButton.TextButtonStyle textButtonStyle;
    private BitmapFont font;
    TextButton button1;
    TextButton button2;

	@Override
	public void create () {
        Gdx.input.setCatchBackKey(true);

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

        eGameState = GameState.MENU;

        stage = new Stage(new ExtendViewport(iwidthScreen,iheightScreen));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
         stage.addActor(table);
        font = new BitmapFont();
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        button1  = new TextButton("Play", textButtonStyle);
        button2 = new TextButton("Settings", textButtonStyle);

        table.add(button1).space(50);
        table.row();

        table.add(button2).space(50);
        table.row();


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
    public void pause(){
        eGameState = GameState.MENU;
    }



    @Override
    public void resume()
    {
        eGameState = GameState.PLAY;
    }

	@Override
	public void render () {
        switch (eGameState) {
            case MENU:
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                camera.update();
                batch.setProjectionMatrix(camera.combined);
                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();
                batch.begin();
                batch.end();
                button1.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        eGameState = GameState.PLAY;
                    }
                });

                Gdx.input.setInputProcessor(stage);

                break;
            case PLAY:
                if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
                    pause();
                }
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                camera.update();
                batch.setProjectionMatrix(camera.combined);

                batch.begin();
                batch.draw(tBackImage, 0, 0, iwidthScreen, iheightScreen);
                yourBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                yourBitmapFontName.draw(batch, yourScoreName, 25, 100);
                for (Rectangle rBalloon : aBalloons) {
                    batch.draw(tBalloons, rBalloon.x, rBalloon.y);
                }
                batch.end();
                logica();
                break;
            case SETTINGS:
                break;

        }

    }
	
	@Override
	public void dispose () {
		batch.dispose();
		tBackImage.dispose();
       stage.dispose();
	}


	public void logica()
    {
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
    public void resize(int width, int height){
        viewport.update(width,height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
        stage.getViewport().update(width, height, true);
    }


}
