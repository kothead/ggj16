package com.ggj16.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ggj16.game.GGJGame;
import com.ggj16.game.processor.InputProcessor;
import com.ggj16.game.processor.PriestProcessor;
import com.ggj16.game.processor.ViewProcessor;
import com.ggj16.game.state.GameStates;
import com.ggj16.game.view.*;

/**
 * Created by kettricken on 30.01.2016.
 */
public class GameScreen extends BaseScreen implements Telegraph {

    private static final float GET_LOOSE_COUNT = 10;

    private Stage stage;
    private OrthographicCamera stageCamera;
    private ViewProcessor viewProcessor;
    private StateMachine sm;

    float trappedCount = 0;

    private Floor floor;
    private Player player;
    private Portal portal;
    private Fog fog;

    private PriestProcessor priestProcessor;
    private ShapeRenderer shapeRenderer = new ShapeRenderer(); // delete if the final fog will not need this
    private int waveCount = 0;

    public GameScreen(GGJGame game) {
        super(game);
        stageCamera = new OrthographicCamera();
        Viewport viewport = new ExtendViewport(getWorldWidth(), getWorldHeight(), stageCamera);
        stage = new Stage(viewport);
        viewProcessor = new ViewProcessor(this, stage);

        sm = new DefaultStateMachine(this);
        //sm.setInitialState(GameStates.GAME);
        sm.changeState(GameStates.GAME);

        floor = new Floor(16, 10);
        player = new Player(this);
        player.setX(floor.getWidthInPixels() / 2 - player.getWidth() / 2);
        player.setY(floor.getHeightInPixels() / 2 - player.getHeight() / 2);

        priestProcessor = new PriestProcessor(floor, this);
        priestProcessor.startWave();

        portal = new Portal(this);
        fog = new Fog(this);
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
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // process block
        sm.update();
        stage.act(delta);

        if (sm.getCurrentState() == GameStates.GAME) {
            player.process(delta);
            floor.process(delta);
            updateCameraPosition();
            priestProcessor.update(delta);
        }

        shapeRenderer.setProjectionMatrix(getCamera().combined);

        // drawing block
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch().begin();
        floor.draw(batch(), (int) (getCamera().position.x - getCamera().viewportWidth / 2),
                (int) (getCamera().position.y - getCamera().viewportHeight / 2),
                (int) getWorldWidth(), (int) getWorldHeight());
        portal.draw(batch());
        player.draw(batch(), delta);
        batch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // draw line
        priestProcessor.draw(shapeRenderer);
        shapeRenderer.end();

        batch().begin();
        priestProcessor.draw(batch());
        fog.draw(batch(), delta);
        batch().end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // draw fog
//        floor.draw(shapeRenderer, 0, 0, (int) getWorldWidth(), (int) getWorldHeight());
//        shapeRenderer.end();

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

    public void onVisibleZoneCut() {
        fog.rescaleAllEffects();
    }

    public Floor getFloor() {
        return floor;
    }

    public Portal getPortal() {
        return portal;
    }

    public PriestProcessor getPriestProcessor() {
        return priestProcessor;
    }

    public StateMachine getStateMachine() {
        return sm;
    }

    public ViewProcessor getViewProcessor() {
        return viewProcessor;
    }

    public void restart() {
        waveCount = 0;
        floor.initFloor();
        priestProcessor.clear();
        priestProcessor.startWave();
        player.reset();
        fog.rescaleAllEffects();
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

    public Player getPlayer() {
        return player;
    }

    public int getWaveCount() {
        return waveCount;
    }

    public void incrementWave() {
        waveCount++;
    }

    private class ControlProcess extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (sm.getCurrentState() != GameStates.GAME) {
                return false;
            }

            if (player.getAction() == Player.Action.TRAPPED) {
                trappedCount++;
                if (trappedCount >= GET_LOOSE_COUNT) {
                    player.release();
                    trappedCount = 0;
                } else {
                    return false;
                }
            }

//            float x = camera.position.x - camera.viewportWidth / 2
//                    + screenX / (float) Gdx.graphics.getWidth() * getWorldWidth();
//            float y = camera.position.y - camera.viewportHeight / 2
//                    + (Gdx.graphics.getHeight() - screenY) / (float) Gdx.graphics.getHeight() * getWorldHeight();
            Vector3 pos = new Vector3(screenX, screenY, 0);
            getCamera().unproject(pos);

            int tileWidth = floor.getTileWidth();
            int tileHeight = floor.getTileHeight();

            Priest priest = priestProcessor.findTouchedPriest(pos.x, pos.y);

            if (priest != null) {
                pos.x = priest.getX() + priest.getWidth() / 2;
                pos.y = priest.getY() + priest.getHeight() / 2;

                float difx = pos.x - player.getX();
                float dify = pos.y - player.getY();

                int tilex = (int) (pos.x / tileWidth - (Math.abs(difx) > Math.abs(dify) ? Math.signum(difx) : 0));
                int tiley = (int) (pos.y / tileHeight - (Math.abs(dify) > Math.abs(difx) ? Math.signum(dify) : 0));

                player.setTarget(Player.Action.SCARE,
                        tilex * tileWidth + (tileWidth - player.getWidth()) / 2,
                        tiley * tileHeight + (tileHeight - player.getHeight()) / 2);
            } else if (!portal.getBoundingRectangle().contains(pos.x, pos.y)
                    && player.getBoundingBox().contains(pos.x, pos.y)) {
                int tilex = (int) (pos.x / tileWidth);
                int tiley = (int) (pos.y / tileHeight);

                player.setTarget(Player.Action.BREAK_FLOOR,
                        tilex * tileWidth + (tileWidth - player.getWidth()) / 2,
                        tiley * tileHeight + (tileHeight - player.getHeight()) / 2);
            } else {
                player.setTarget(Player.Action.GO,
                        pos.x - player.getWidth() / 2,
                        pos.y - player.getHeight() / 2);
            }
            return super.touchDown(screenX, screenY, pointer, button);
        }

    }
}
