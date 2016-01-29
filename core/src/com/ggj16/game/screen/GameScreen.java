package com.ggj16.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.GL20;
import com.ggj16.game.GGJGame;
import com.ggj16.game.state.GameStates;

/**
 * Created by kettricken on 30.01.2016.
 */
public class GameScreen extends BaseScreen {

    StateMachine sm;

    float delay = 0;

    public GameScreen(GGJGame game) {
        super(game);
        sm = new DefaultStateMachine(this);
        sm.setInitialState(GameStates.GAME);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        delay += delta;
        if (delay >= 10) {
            sm.changeState(GameStates.GAME_OVER);
        }
    }

    @Override
    protected void layoutViewsLandscape(int width, int height) {

    }

    @Override
    protected void layoutViewsPortrait(int width, int height) {

    }
}
