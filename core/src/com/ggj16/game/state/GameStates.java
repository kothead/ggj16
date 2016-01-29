package com.ggj16.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.ggj16.game.screen.GameScreen;

/**
 * Created by kettricken on 30.01.2016.
 */
public enum GameStates implements State<GameScreen> {

    GAME() {
        @Override
        public void enter(GameScreen entity) {
            Gdx.app.log("Test", "GAME enter");
        }

        @Override
        public void update(GameScreen entity) {
            if (entity.hasEnded()) {
                entity.getStateMachine().changeState(GAME_OVER);
            }
        }

        @Override
        public boolean onMessage(GameScreen entity, Telegram telegram) {
            if (telegram.message == MessageType.BACK_PRESS) {
                entity.getStateMachine().changeState(GameStates.PAUSE);
            }
            return false;
        }
    },

    PAUSE() {
        @Override
        public void enter(GameScreen entity) {
            entity.getViewProcessor().showPauseTable();
        }

        @Override
        public void exit(GameScreen entity) {
            entity.getViewProcessor().hidePauseTable();
        }

        @Override
        public boolean onMessage(GameScreen entity, Telegram telegram) {
            if (telegram.message == MessageType.BACK_PRESS) {
                entity.getStateMachine().changeState(GameStates.GAME);
            }
            return false;
        }

    },

    GAME_OVER() {
        @Override
        public void enter(GameScreen entity) {
            entity.getViewProcessor().showGameOverTable();
        }

        @Override
        public void exit(GameScreen entity) {
            entity.getViewProcessor().hideGameOverTable();
        }

        @Override
        public boolean onMessage(GameScreen entity, Telegram telegram) {
            if (telegram.message == MessageType.TAP) {
                entity.restart();
                entity.getStateMachine().changeState(GameStates.GAME);
            } else if (telegram.message == MessageType.BACK_PRESS) {
                entity.getGame().exit();
            }
            return false;
        }
    };

    @Override
    public void enter(GameScreen entity) {
    }

    @Override
    public void update(GameScreen entity) {

    }

    @Override
    public void exit(GameScreen entity) {

    }

    @Override
    public boolean onMessage(GameScreen entity, Telegram telegram) {
        return false;
    }
}
