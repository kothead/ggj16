package com.ggj16.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ggj16.game.GGJGame;
import com.ggj16.game.processor.InputProcessor;
import com.ggj16.game.processor.ViewProcessor;
import com.ggj16.game.state.GameStates;
import com.ggj16.game.view.Floor;

/**
 * Created by kettricken on 30.01.2016.
 */
public class GameScreen extends BaseScreen implements Telegraph {

    private Stage stage;
    private ViewProcessor viewProcessor;
    private StateMachine sm;

    float delay = 0; // delete after the real condition of game over is set

    private Floor floor;

    public GameScreen(GGJGame game) {
        super(game);
        stage = new Stage(getViewport());
        viewProcessor = new ViewProcessor(this, stage);

        sm = new DefaultStateMachine(this);
        sm.setInitialState(GameStates.GAME);

        floor = new Floor(16, 10);
    }

    @Override
    public void show() {
        super.show();
        viewProcessor.initViews();

        InputMultiplexer inputMultiplexer = new InputMultiplexer(new InputProcessor(GameScreen.this));
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        sm.update();

        batch().begin();
        floor.draw(batch(), 0, 0, (int) getWorldWidth(), (int) getWorldHeight());
        batch().end();

        stage.act(delta);
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
        if (delay >= 2) {
            return true;
        }
        return false;
    }
}
