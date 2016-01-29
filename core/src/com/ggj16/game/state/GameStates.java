package com.ggj16.game.state;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.ggj16.game.GGJGame;

/**
 * Created by kettricken on 30.01.2016.
 */
public enum GameStates implements State<GGJGame> {

    GAME() {

    },

    PAUSE() {

    },

    GAME_OVER() {

    };

    @Override
    public void enter(GGJGame entity) {

    }

    @Override
    public void update(GGJGame entity) {

    }

    @Override
    public void exit(GGJGame entity) {

    }

    @Override
    public boolean onMessage(GGJGame entity, Telegram telegram) {
        return false;
    }
}
