package com.ggj16.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ggj16.game.GGJGame;
import com.ggj16.game.processor.InputProcessor;
import com.ggj16.game.processor.PriestProcessor;
import com.ggj16.game.processor.ViewProcessor;
import com.ggj16.game.state.GameStates;
import com.ggj16.game.view.Floor;
import com.ggj16.game.view.Player;

/**
 * Created by kettricken on 30.01.2016.
 */
public class GameScreen extends BaseScreen implements Telegraph {

    private Stage stage;
    private ViewProcessor viewProcessor;
    private StateMachine sm;

    float delay = 0; // delete after the real condition of game over is set

    private Floor floor;
    private Player player;

    private PriestProcessor priestProcessor;
    private ShapeRenderer shapeRenderer = new ShapeRenderer(); // delete if the final fog will not need this

    public GameScreen(GGJGame game) {
        super(game);
        stage = new Stage(getViewport());
        viewProcessor = new ViewProcessor(this, stage);

        sm = new DefaultStateMachine(this);
        sm.setInitialState(GameStates.GAME);

        floor = new Floor(16, 10);
        player = new Player();
        player.setX(floor.getWidthInPixels() / 2);
        player.setY(floor.getHeightInPixels() / 2);

        priestProcessor = new PriestProcessor(floor);
        priestProcessor.generatePriests(4);
    }

    @Override
    public void show() {
        super.show();
        viewProcessor.initViews();

        InputMultiplexer inputMultiplexer = new InputMultiplexer(new InputProcessor(GameScreen.this));
        inputMultiplexer.addProcessor(new ControlProcess());
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.setViewport(getViewport());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // process block
        sm.update();
        stage.act(delta);
        player.process(delta);
        updateCameraPosition();
        shapeRenderer.setProjectionMatrix(getCamera().combined);

        priestProcessor.update(delta);

        // drawing block
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch().begin();
        floor.draw(batch(), (int) (getCamera().position.x - getCamera().viewportWidth / 2),
                (int) (getCamera().position.y - getCamera().viewportHeight / 2),
                (int) getWorldWidth(), (int) getWorldHeight());
        player.draw(batch(), delta);
        batch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // draw line
        priestProcessor.draw(shapeRenderer);
        shapeRenderer.end();

        batch().begin();
        priestProcessor.draw(batch());
        batch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // draw fog
        floor.draw(shapeRenderer, 0, 0, (int) getWorldWidth(), (int) getWorldHeight());
        shapeRenderer.end();

        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return sm.handleMessage(msg);
    }

    @Override
    protected void layoutViewsLandscape(int width, int height) {

    }

    @Override
    protected void layoutViewsPortrait(int width, int height) {

    }

    public StateMachine getStateMachine() {
        return sm;
    }

    public ViewProcessor getViewProcessor() {
        return viewProcessor;
    }

    public void restart() {
        delay = 0;
    }

    public boolean hasEnded() {
        delay += Gdx.graphics.getDeltaTime();
        return false;
    }

    private void updateCameraPosition() {
        float x = player.getX();
        float y = player.getY();
        float halfWidth = getCamera().viewportWidth / 2f;
        float halfHeight = getCamera().viewportHeight / 2f;

        if (x - halfWidth < 0) {
            x = halfWidth;
        } else if (x + halfWidth > floor.getWidthInPixels()) {
            x = floor.getWidthInPixels() - halfWidth;
            //Gdx.app.log("WIDTH", floor.getWidthInPixels() + " " + halfWidth + " " + x);
        }

        if (y - halfHeight < 0) {
            y = halfHeight;
        } else if (y + halfHeight > floor.getHeightInPixels()) {
            y = floor.getHeightInPixels() - halfHeight;
        }

        getCamera().position.x = x;
        getCamera().position.y = y;
        getCamera().update();
        batch().setProjectionMatrix(getCamera().combined);
    }

    private class ControlProcess extends InputAdapter {

        private static final float DOUBLE_TAP_DELAY = 0.5f;

        private float tapTime;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Camera camera = getCamera();

//            float x = camera.position.x - camera.viewportWidth / 2
//                    + screenX / (float) Gdx.graphics.getWidth() * getWorldWidth();
//            float y = camera.position.y - camera.viewportHeight / 2
//                    + (Gdx.graphics.getHeight() - screenY) / (float) Gdx.graphics.getHeight() * getWorldHeight();
            Vector3 pos = new Vector3(screenX, screenY, 0);
            getCamera().unproject(pos);

            player.setTarget(Player.Action.GO, pos.x, pos.y);
            return super.touchDown(screenX, screenY, pointer, button);
        }

    }
}
